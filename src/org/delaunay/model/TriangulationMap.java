package org.delaunay.model;

import java.util.Map;

import org.delaunay.algorithm.Triangulation;
import org.delaunay.algorithm.Triangulation.InvalidVertexException;

import com.google.common.collect.Maps;

/**
 * A map that stores the models on vertices of a Delaunay Triangulation.
 */
public class TriangulationMap<T> {
	private final Map<Vertex, T> map = Maps.newLinkedHashMap();
	private final Triangulation triangulation = new Triangulation();

	public TriangulationMap() {
		triangulation.setKeepSuperTriangle(true);
	}

	public void clear() {
		map.clear();
		triangulation.clear();
	}

	public boolean contains(double x, double y) {
		return map.containsKey(new Vertex(x, y));
	}

	public void put(double x, double y, T value) {
		Vertex vert = new Vertex(x, y);
		map.put(vert, value);
	}

	public void triangulate() throws InvalidVertexException {
		triangulation.triangulate(map.keySet());
	}
	
	public Triangulation getTriangulation() {
		return triangulation;
	}

	public T get(Vertex v) {
		return map.get(v);
	}

	public T locate(double x, double y) {
		Vertex vert = triangulation.locateNearestVertex(new Vector(x, y));
		return get(vert);
	}
}
