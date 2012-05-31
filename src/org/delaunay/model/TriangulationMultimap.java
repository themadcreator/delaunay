package org.delaunay.model;

import java.util.List;

import org.delaunay.algorithm.Triangulation;
import org.delaunay.algorithm.Triangulation.InvalidVertexException;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;

public class TriangulationMultimap<T> {
	private final ArrayListMultimap<Vertex, T> map = ArrayListMultimap.create();
	private final Triangulation triangulation = new Triangulation();
	
	public TriangulationMultimap() {
		triangulation.setKeepSuperTriangle(true);
	}

	public void clear(){
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

	public List<T> locate(double x, double y) {
		Vertex vert = triangulation.locateNearestVertex(new Vector(x, y));
		if (vert == null) {
			return Lists.newArrayList();
		}
		return map.get(vert);
	}
}
