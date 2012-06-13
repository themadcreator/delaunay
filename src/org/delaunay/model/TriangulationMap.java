package org.delaunay.model;

import org.delaunay.algorithm.Triangulation;
import org.delaunay.algorithm.Triangulation.InvalidVertexException;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * A map that stores the models on vertices of a Delaunay Triangulation.
 */
public class TriangulationMap<T> {
	private final BiMap<Vertex, T> map = HashBiMap.create();
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

	public Vertex put(double x, double y, T value) {
		Vertex vert = new Vertex(x, y);
		triangulation.addVertex(vert);
		map.put(vert, value);
		return vert;
	}

	public void triangulate() throws InvalidVertexException {
		triangulation.triangulate();
	}
	
	public Triangulation getTriangulation() {
		return triangulation;
	}
	
	public Vertex getVertex(T value){
		return map.inverse().get(value);
	}

	public T get(Vertex v) {
		return map.get(v);
	}

	public T locate(double x, double y) {
		Vertex vert = triangulation.locateNearestVertex(new Vector(x, y));
		return get(vert);
	}
}
