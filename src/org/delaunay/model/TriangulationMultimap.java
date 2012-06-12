package org.delaunay.model;

import java.util.List;
import java.util.Map;

import org.delaunay.algorithm.Triangulation;
import org.delaunay.algorithm.Triangulation.InvalidVertexException;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class TriangulationMultimap<T> {
	private final ArrayListMultimap<Vertex, T> map = ArrayListMultimap.create();
	private final Map<Vertex, Vertex> canonicalKeys = Maps.newHashMap();
	private final Triangulation triangulation = new Triangulation();
	
	public TriangulationMultimap() {
		triangulation.setKeepSuperTriangle(true);
	}

	public void clear(){
		map.clear();
		triangulation.clear();
	}
	
	public Triangulation getTriangulation() {
		return triangulation;
	}
	
	public boolean contains(double x, double y) {
		return map.containsKey(new Vertex(x, y));
	}

	public Vertex put(double x, double y, T value) {
		return put(new Vertex(x, y), value);
	}

	public Vertex put(Vertex vert, T value) {
		if (canonicalKeys.containsKey(vert)) {
			vert = canonicalKeys.get(vert);
		} else {
			triangulation.addVertex(vert);
			canonicalKeys.put(vert, vert);
		}
		map.put(vert, value);
		return vert;
	}
	
	public Vertex getKey(double x, double y) {
		return canonicalKeys.get(new Vertex(x, y));
	}

	public List<T> get(Vertex key) {
		return map.get(key);
	}

	public void triangulate() throws InvalidVertexException {
		triangulation.triangulate();
	}

	public List<T> locate(double x, double y) {
		Vertex vert = triangulation.locateNearestVertex(new Vector(x, y));
		if (vert == null) {
			return Lists.newArrayList();
		}
		return map.get(vert);
	}
}
