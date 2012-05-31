package org.delaunay.model;


public class Edge {
	public final Vertex a, b;

	public Edge(Vertex a, Vertex b) {
		this.a = a;
		this.b = b;
	}
	
	@Override
	public int hashCode() {
		return a.hashCode() ^ b.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Edge) {
			Edge o = (Edge) obj;
			return (a == o.a && b == o.b) || (a == o.b && b == o.a);
		}
		return false;
	}
}