package org.delaunay.algorithm;

import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.delaunay.model.Edge;
import org.delaunay.model.Triangle;
import org.delaunay.model.Vector;
import org.delaunay.model.Vectors;
import org.delaunay.model.Vertex;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * A fast Delaunay Triangulation implementation.
 */
@SuppressWarnings("serial")
public strictfp class Triangulation {

	public static class NonDelaunayException extends RuntimeException {
	}

	public static class InvalidVertexException extends Exception {
	}
	
	public static interface DebugLogger{
		public void debug(String str);
	}
	
	public static enum VertexExceptionStrategy{
		THROW_EXCEPTION,
		CATCH_AND_DROP_VERTEX,
		;
	}
	
	private Vertex[] superVerts = new Vertex[]{};
	private LinkedHashSet<Triangle> triangles = Sets.newLinkedHashSet();
	private LinkedHashSet<Vertex> inputVertices = Sets.newLinkedHashSet();
	private LinkedHashSet<Vertex> vertices = Sets.newLinkedHashSet();
	private Triangle lastLocatedTriangle = null;
	private int hopCount = 0;
	private int locateCount = 0;
	private boolean keepSuperTriangle = false;
	private VertexExceptionStrategy vertexExceptionStrategy = VertexExceptionStrategy.THROW_EXCEPTION; 

	/*
	 * The hilbert order determines the granularity of the hilbert curve. For
	 * example, a value of 16 produces a square with with length and width 2^16,
	 * resulting in 2^16 * 2^16 = 2^32 cells. This is typically good enough for
	 * a triangulation up to 4 Billion vertices. Running time for coordinate
	 * conversion would be O(16).
	 */
	private int hilbertOrder = 16;

	/*
	 * Determines the scale of the super triangle. Increase this number if you
	 * need to vertex locates from farther out from the bounding box of the
	 * vertices.
	 */
	private double superTriangleScale = 2.0;
	
	private DebugLogger log = new DebugLogger() {
		public void debug(String str) {
			// null implementation
		}
	};

	public int getHopCount() {
		return hopCount;
	}

	public int getLocateCount() {
		return locateCount;
	}

	public void setDebugLogger(DebugLogger log) {
		this.log = log;
	}

	public Vertex addVertex(double x, double y) {
		Vertex vertex = new Vertex(x, y);
		inputVertices.add(vertex);
		return vertex;
	}

	public void addVertex(Vertex v) {
		inputVertices.add(v);
	}

	public void addAllVertices(Iterable<Vertex> vs) {
		Iterables.addAll(inputVertices, vs);
	}

	public LinkedHashSet<Vertex> getInputVertices() {
		return inputVertices;
	}
	
	public LinkedHashSet<Vertex> getVertices() {
		return vertices;
	}

	public List<Vertex> getVerticesInBounds(final Rectangle2D rect) {
		return Lists.newArrayList(Iterables.filter(getVertices(), new Predicate<Vertex>() {
			public boolean apply(Vertex v) {
				return rect.contains(v.toPoint());
			}
		}));
	}

	public LinkedHashSet<Triangle> getTriangles() {
		return triangles;
	}

	/**
	 * If set to true, the supertriangle will not be removed at the end of the
	 * triangulation method. This allows points outside the convex hull of
	 * vertices to be located.
	 */
	public void setKeepSuperTriangle(boolean keepSuperTriangle) {
		this.keepSuperTriangle = keepSuperTriangle;
	}

	public Vertex locateNearestVertex(Vector v) {
		Triangle located = locate(v);
		if (located == null) {
			return null;
		}
		
		Vertex bestVertex = null;
		double dist = Double.MAX_VALUE;
		
		for (Triangle tri : getCircumcircleTriangles(v, located)) {
			for (Vertex vert : tri.getVertices()) {
				double d = vert.subtract(v).lengthSquared();
				if (d < dist) {
					bestVertex = vert;
					dist = d;
				}
			}
		}
		return bestVertex;
	}
	
	public Set<Vertex> getVerticesInRadius(Vertex v, double radius) {
		Set<Vertex> checked = Sets.newHashSet(v);
		Set<Vertex> inRadius = Sets.newHashSet(v);
		Set<Vertex> toCheck = Sets.newHashSet(v.getNeighborVertices());

		while (toCheck.size() > 0) {
			Vertex check = Iterables.getFirst(toCheck, null);
			toCheck.remove(check);
			checked.add(check);

			if (v.subtract(check).length() < radius) {
				inRadius.add(check);
				toCheck.addAll(check.getNeighborVertices());
				toCheck.removeAll(checked);
			}
		}

		return inRadius;
	}

	/**
	 * Creates a Delaunay Triangulation of the {@link Vertex}s.
	 * 
	 * This implementation uses a simple iterative approach, but with some
	 * pre-processing to make the real-world performance fast.
	 * 
	 * First, the vertices are sorted using a Hilbert Space-Filling curve. Since
	 * our locate method walks the triangulation, linearizing the points with a
	 * space-filling curve gives us some pretty good locality when adding each
	 * vertex, thus greatly reducing the number of hops required to locate the
	 * vertex. The sort is O(n log n), but is fast since hilbert indices are
	 * computed in O(h) (where h is a small constant), and results in a
	 * triangulation asymptotic running time of O(n) for non-diabolical cases.
	 * For more info, see: {@link http://en.wikipedia.org/wiki/Hilbert_curve}
	 * 
	 * For each vertex, we walk to the enclosing triangle. We create a cavity
	 * from that triangle and all neighboring triangles for which the vertex is
	 * in their circumcircle.
	 * 
	 * Then, we create new triangles between the edges of the cavity and the
	 * vertex.
	 * 
	 * We guarantee that a triangle will be located for each vertex by first
	 * creating a "super triangle" that is at least twice as large as the bounds
	 * of the vertices. If this triangulation will be used for point location,
	 * you will want to call {@link #setKeepSuperTriangle(true)} so that points
	 * outside the convex hull of vertices may also be located.
	 * 
	 * Basic incremental triangulation method inspired by Paul Bourke's notes
	 * and psuedocode. See: {@link http://paulbourke.net/papers/triangulate/}
	 * 
	 * @throws InvalidVertexException
	 *             if any two vertices have the same location or if any three
	 *             points are co-linear.
	 */
    public void triangulate() throws InvalidVertexException {
    	/*
    	 * Reset triangulation state
    	 */
    	resetTriangulation();
		
		if (Iterables.isEmpty(inputVertices)) {
			return;
		}

		/*
		 * Determine the supertriangle.
		 */
		createSuperTriangle(inputVertices);

		/*
		 * Sort vertices using hilbert curve to linearize triangulation
		 * performance.
		 */
		log.debug("Linearizing with Hilbert Space-filling Curve");
		List<Vertex> sortedVertices = getHilbertSortedVertices(inputVertices);

		/*
		 * Add vertices one at a time, updating the triangulation as we go.
		 */
		log.debug("Building Triangulation");
		for (Vertex vertex : sortedVertices) {
			try {
				addVertexToTriangulation(vertex);
			} catch (InvalidVertexException e) {
				if (vertexExceptionStrategy == VertexExceptionStrategy.THROW_EXCEPTION) {
					throw e;
				} else {
					// ignore
				}
			}
		}
		
		/*
		 * Cleanup
		 */
		clearLocator();
		if (!keepSuperTriangle) {
			removeSuperTriangle();
		}

		log.debug("Triangulation Complete");
	}

	public void clear() {
		resetTriangulation();
		inputVertices = Sets.newLinkedHashSet();
	}
	
	private void resetTriangulation() {
		triangles = Sets.newLinkedHashSet();
		vertices = Sets.newLinkedHashSet();
		clearLocator();
	}

	private List<Vertex> getHilbertSortedVertices(Iterable<? extends Vertex> verts) {
		Rectangle2D bbox = Vectors.boundingBox(Lists.newArrayList(superVerts));
		ScaledHilbertIndex  hilbert = new ScaledHilbertIndex(hilbertOrder, bbox);
		for (Vertex v : verts) {
			v.setHilbertIndex(hilbert.toIndex(v.x, v.y));
		}
		List<Vertex> sortedVertices = Lists.newArrayList(verts);
		Collections.sort(sortedVertices, new Comparator<Vertex>() {
			public int compare(Vertex v1, Vertex v2) {
				return v1.getHilbertIndex().compareTo(v2.getHilbertIndex());
			}
		});
		return sortedVertices;
	}

	public void addVertexToTriangulation(Vertex vertex) throws InvalidVertexException {
		Collection<Triangle> toRemove = null, toAdd = null;

		try {
			/*
			 * Get the set of triangles for which the vertex lies in its
			 * circumcircle.
			 */
			toRemove = getCircumcircleTriangles(vertex);
		} catch (NonDelaunayException e) {
			/*
			 * Unfortunately, we cannot recover from this state since the
			 * triangulation is already non-delaunay. It was probably caused
			 * by overlapping vertices, so we throw an invalid vertex
			 * exception.
			 */
			throw new InvalidVertexException();
		} catch (InvalidVertexException e) {
			log.debug(String.format("Dropping vertex %s because it outside the triangulation!\nMaybe something went wrong when computing the super triangle?", vertex));
			return;
		}

		/*
		 * Compute the set of edges that represent the convex hull of the
		 * cavity left by removing the triangles.
		 */
		List<Edge> edgeSet = getEdgeSet(toRemove);
		
		/*
		 * Remove the triangles.
		 */
		removeTriangles(toRemove);
		
		try {
			/*
			 * Create and add triangles created from the cavity convex hull
			 * edges and the vertex.
			 */
			toAdd = createTriangles(edgeSet, vertex);
			addTriangles(toAdd);
			vertices.add(vertex);
		} catch (NonDelaunayException e) {
			log.debug(String.format("Dropping vertex %s because it causes degeneracy.\nYou may need to use exact math on this vertex.", vertex));
			removeTriangles(toAdd);
			addTriangles(toRemove);
		}
	}
    
	public void createSuperTriangle(Iterable<? extends Vertex> verts) {
		createSuperTriangle(Vectors.boundingBox(verts));
	}

	public void createSuperTriangle(Rectangle2D rect) {
		double dmax = Math.max(rect.getWidth(), rect.getHeight());
		double xmid = rect.getCenterX();
		double ymid = rect.getCenterY();

		superVerts = new Vertex[] {
				new Vertex(xmid - dmax * superTriangleScale, ymid - dmax),
				new Vertex(xmid, ymid + dmax * superTriangleScale),
				new Vertex(xmid + dmax * superTriangleScale, ymid - dmax)
				};

		triangles = Sets.newLinkedHashSet();
		triangles.add(new Triangle(superVerts[0], superVerts[1], superVerts[2]));
	}

	public void removeSuperTriangle() {
		Set<Triangle> touching = Sets.newHashSet();
		for (Vertex v : superVerts) {
			touching.addAll(v.getNeighborTriangles());
		}
		removeTriangles(touching);
		superVerts = new Vertex[] {};
	}

	public boolean touchesSuperVertex(Triangle tri) {
		for (Vertex v : superVerts) {
			if (tri.getVertices().contains(v)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean neighborsSuperVertex(Vertex vert) {
		for (Triangle tri : vert.getNeighborTriangles()) {
			if (touchesSuperVertex(tri)) {
				return true;
			}
		}
		if (Sets.newHashSet(superVerts).contains(vert)) {
			return true;
		}
		return false;
	}

	private void clearLocator() {
		lastLocatedTriangle = null;
	}

	public Collection<Triangle> getCircumcircleTriangles(Vector vertex) throws InvalidVertexException, NonDelaunayException {
		Triangle t = locate(vertex);
		if (t == null) {
			throw new InvalidVertexException();
		}
		return getCircumcircleTriangles(vertex, t);
	}

	private Collection<Triangle> getCircumcircleTriangles(Vector vertex, Triangle t) {
		Set<Triangle> checked = Sets.newHashSet(t);
		Set<Triangle> inCircum = Sets.newHashSet(t);

		// Initialize "to check" set with neighbors
		Set<Triangle> toCheck = Sets.newHashSet(t.getNeighbors());

		// For first triangle in "to check" set, check if
		// the vertex is in its circum circle.
		while (toCheck.size() > 0) {
			t = Iterables.getFirst(toCheck, null);
			toCheck.remove(t);

			if (t.isInCircum(vertex)) {
				inCircum.add(t);
				// If it is, add *its* neighbors to the "to check" set.
				toCheck.addAll(t.getNeighbors());
				toCheck.removeAll(checked);
			}
			checked.add(t);
		}

		return inCircum;
	}
	
	/*
	 * Walks the triangulation toward the vertex. Returns the triangle in which
	 * the vertex resides. If the vertex is outside the current triangulation,
	 * nil is returned.
	 * 
	 * It is possible that if the triangulation breaks due to floating point
	 * errors, it will cause errors during locate. In this case, we throw a
	 * NonDelaunayException.
	 * 
	 * If the vertices are near each other, such as when iterating over a
	 * hilbert linearization or running a scanline of locations, this should be
	 * pretty fast.
	 */
	public Triangle locate(Vector v) throws NonDelaunayException {
		locateCount += 1;
		Triangle t = lastLocatedTriangle == null ? Iterables.getFirst(triangles, null) : lastLocatedTriangle;
		if (t == null) {
			return null;
		}
		boolean done = false;

		Set<Triangle> seen = Sets.<Triangle> newHashSet();
		while (!done) {
			hopCount += 1;
			lastLocatedTriangle = t;
			if (seen.contains(t)) {
				throw new NonDelaunayException();
			}
			seen.add(t);
			Triangle tNext = t.nextWalk(v);
			if (tNext == null) {
				return null;
			}
			done = (tNext == t);
			t = tNext;
		}

		/*
		 * During triangulation the located triangle is immediately removed.
		 * But, it can be useful to store this if we are locating points in the
		 * triangulation after it's constructed.
		 */
		lastLocatedTriangle = t;
		return t;
	}

	public List<Edge> getEdgeSet(Collection<Triangle> tris) {
		List<Edge> edges = Lists.newArrayList();
		for (Triangle t : tris) {
			for (Edge e : t.getEdges()) {
				if (edges.contains(e)) {
					edges.remove(e);
				} else {
					edges.add(e);
				}
			}
		}
		return edges;
	}

	public List<Triangle> createTriangles(Iterable<Edge> edgeSet, final Vertex vertex) {
		return Lists.newArrayList(Iterables.transform(edgeSet, new Function<Edge, Triangle>() {
			public Triangle apply(Edge e) {
				return new Triangle(vertex, e.a, e.b);
			}
		}));
	}

	private void addTriangles(Iterable<Triangle> tris) throws NonDelaunayException {
		for (Triangle t : tris) {
			for (Vertex v : t.getVertices()) {
				v.addTriangle(t);
			}
			triangles.add(t);
		}
		lastLocatedTriangle = Iterables.getFirst(tris, null);

		// uncomment to debug robustness issues at the cost of performance
		// triangles.each { |t| raise NonDelaunayException unless t.delaunay? }
	}

	private void removeTriangles(Iterable<Triangle> tris) {
		for (Triangle t : tris) {
			for (Vertex v : t.getVertices()) {
				v.removeTriangle(t);
			}
			triangles.remove(t);
		}
		lastLocatedTriangle = null;
	}
}
