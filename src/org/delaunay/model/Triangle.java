package org.delaunay.model;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public strictfp class Triangle {
	
	public final Vertex a, b, c;
	private final Vector circumCenter;
	private final double rSquared;
	private final LinkedHashSet<Vertex> vertices;
	private final List<Edge> edges;
	
	public Triangle(Vertex v_a, Vertex v_b, Vertex v_c) {
		// Enforce winding rule
		boolean swap = v_c.orientation(v_a, v_b) > 0;

		this.a = v_a;
		this.b = swap ? v_c : v_b;
		this.c = swap ? v_b : v_c;

		this.vertices = Sets.newLinkedHashSet(Lists.newArrayList(a, b, c));
		this.edges = Lists.newArrayList(
				new Edge(a, b),
				new Edge(c, a),
				new Edge(b, c));

		// compute circum center
		// http://www.ics.uci.edu/~eppstein/junkyard/circumcenter.html    
		double d = ((a.x - c.x) * (b.y - c.y) - (b.x - c.x) * (a.y - c.y)) * 2;
		double c_x = (((a.x - c.x) * (a.x + c.x) + (a.y - c.y) * (a.y + c.y)) * (b.y - c.y) -
				((b.x - c.x) * (b.x + c.x) + (b.y - c.y) * (b.y + c.y)) * (a.y - c.y))
				/ d;
		double c_y = (((b.x - c.x) * (b.x + c.x) + (b.y - c.y) * (b.y + c.y)) * (a.x - c.x) -
				((a.x - c.x) * (a.x + c.x) + (a.y - c.y) * (a.y + c.y)) * (b.x - c.x))
				/ d;
		this.circumCenter = new Vector(c_x, c_y);
		this.rSquared = (c.x - c_x) *  (c.x - c_x) + (c.y - c_y) *  (c.y - c_y);
	}

	public LinkedHashSet<Vertex> getVertices() {
		return vertices;
	}
	
	public Vector getCircumCenter() {
		return circumCenter;
	}
	
	public boolean isInCircum(Vector v){
		v = v.subtract(circumCenter);
		return v.lengthSquared() <= rSquared;
	}
	
	public Triangle nextWalk(Vector v) {
		if (v.orientation(b, c) > 0) {
			validateOpposites();
			return oppositeBC;
		} else if (v.orientation(c, a) > 0) {
			validateOpposites();
			return oppositeCA;
		} else if (v.orientation(a, b) > 0) {
			validateOpposites();
			return oppositeAB;
		}
		return this;
	}

	public Triangle opposite(Vertex a, Vertex b) {
		for (Triangle t : a.getNeighborTriangles()) {
			if (t != this && t.vertices.contains(b)) {
				return t;
			}
		}
		return null;
	}

	// Cache opposites for performance
	private Triangle oppositeAB = null;
	private Triangle oppositeBC = null;
	private Triangle oppositeCA = null;
	private boolean oppositesValid = false;

	private void validateOpposites() {
		if (oppositesValid) {
			return;
		}
		oppositeAB = opposite(a, b);
		oppositeBC = opposite(b, c);
		oppositeCA = opposite(c, a);
		oppositesValid = true;
	}

	public void invalidateNeighbors() {
		oppositesValid = false;
		neighborsValid = false;
	}

	private Set<Triangle> neighbors = null;
	private boolean neighborsValid = false;

	public Set<Triangle> getNeighbors() {
		if (neighborsValid) {
			return neighbors;
		}
		neighbors = Sets.newHashSet();
		neighbors.addAll(a.getNeighborTriangles());
		neighbors.addAll(b.getNeighborTriangles());
		neighbors.addAll(c.getNeighborTriangles());
		neighbors.remove(this);
		neighborsValid = true;
		return neighbors;
	}

	public Iterable<Edge> getEdges() {
		return edges;
	}
	
	private Double area = null;
	
	public double getArea() {
		if (area == null) {
			area = (c.subtract(a)).cross((b.subtract(a))) / 2.0;
		}
		return area;
	}
	
	@Override
	public String toString() {
		return String.format("%s -> %s -> %s", a, b, c);
	}
}
