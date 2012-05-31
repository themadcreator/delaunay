package org.delaunay.dtfe;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.delaunay.dtfe.DtfeTriangulationMap.ScaleType;
import org.delaunay.model.Edge;
import org.delaunay.model.Triangle;
import org.delaunay.model.Vector;
import org.delaunay.model.Vertex;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class IsolineBuilder {

	private final DtfeTriangulationMap<? extends DensityModel> dtfe;
	private ScaleType scaleType = ScaleType.LOG;
	
	public IsolineBuilder(DtfeTriangulationMap<? extends DensityModel> dtfe) {
		this.dtfe = dtfe;
	}
	
	public IsolineBuilder setScaleType(ScaleType scaleType) {
		this.scaleType = scaleType;
		return this;
	}

	/**
	 * Returns a list of list of vectors. Each list of vectors is a continuous
	 * isoline at the specified value.
	 */
	public List<List<Vector>> getIsoLines(double value) {
		Set<Segment> segments = Sets.newLinkedHashSet();
		Multimap<Edge, Segment> segmentsMap = ArrayListMultimap.create();
		
		// Compute segments from triangles that pass through value
		for (Triangle tri : dtfe.getTriangulation().getTriangles()) {
			// Compare vertex density with iso value
			List<Vertex> above = Lists.newArrayList();
			List<Vertex> below = Lists.newArrayList();
			for (Vertex v : tri.getVertices()) {
				if (dtfe.getRelativeDensity(dtfe.getDensity(v), scaleType) > value) {
					above.add(v);
				} else {
					below.add(v);
				}
			}

			// All vertices above or below value
			if (above.size() == 3 || below.size() == 3 || above.size() + below.size() != 3) {
				continue;
			}
			// One vertex above value (2 below)
			else if (above.size() == 1) {
				Segment segment = new Segment(Iterables.getOnlyElement(above), below.get(0), below.get(1), value);
				segmentsMap.put(segment.e0, segment);
				segmentsMap.put(segment.e1, segment);
				segments.add(segment);
			}
			// One vertex below value (2 above)
			else if (below.size() == 1) {
				Segment segment = new Segment(Iterables.getOnlyElement(below), above.get(0), above.get(1), value);
				segmentsMap.put(segment.e0, segment);
				segmentsMap.put(segment.e1, segment);
				segments.add(segment);
			}
		}

		// Connect line segments into lists of vectors
		List<List<Vector>> paths = Lists.newArrayList();
		while (!segments.isEmpty()) {
			// Poll any segment
			Segment segment = Iterables.getFirst(segments, null);
			segments.remove(segment);

			Edge startEdge = segment.e0;
			Edge headEdge = segment.e1;
			List<Vector> vectors = Lists.newArrayList(segment.v0, segment.v1);

			while (segment != null) {
				// Find other segment that touches the same edge
				Collection<Segment> segs = segmentsMap.get(headEdge);
				segs.remove(segment);
				Segment nextSegment = Iterables.getFirst(segs, null);

				if (nextSegment == null) {
					// TODO log error
					break;
				}

				segments.remove(nextSegment);

				// Connect the segment (switch direction of segment if necessary)
				if (headEdge.equals(nextSegment.e0)) {
					vectors.add(nextSegment.v1);
					headEdge = nextSegment.e1;
				} else {
					vectors.add(nextSegment.v0);
					headEdge = nextSegment.e0;
				}
				segment = nextSegment;

				// If we reach the beginning, this path is complete
				if (headEdge.equals(startEdge)) {
					paths.add(vectors);
					break;
				}
			}
		}
		return paths;
	}

	private class Segment {
		final Vector v0;
		final Edge e0;
		final Vector v1;
		final Edge e1;

		private Segment(Vertex vCommon, Vertex v0, Vertex v1, double value) {
			// Compute the location of the segment endpoints based on the
			// density of the vertices and the iso value
			double dCommon = dtfe.getRelativeDensity(dtfe.getDensity(vCommon), scaleType);
			double d0 = dtfe.getRelativeDensity(dtfe.getDensity(v0), scaleType);
			double d1 = dtfe.getRelativeDensity(dtfe.getDensity(v1), scaleType);
			double t0 = (value - dCommon) / (d0 - dCommon);
			double t1 = (value - dCommon) / (d1 - dCommon);

			this.e0 = new Edge(vCommon, v0);
			this.v0 = v0.subtract(vCommon).multiply(t0).add(vCommon);
			this.e1 = new Edge(vCommon, v1);
			this.v1 = v1.subtract(vCommon).multiply(t1).add(vCommon);
		}
	}
}
