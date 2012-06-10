package org.delaunay.model;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class Voronoi extends ConvexPolygon {
	public static Voronoi createFromTriangulation(Vertex v) {
		return create(v, v.getNeighborVertices(), ImmutableList.copyOf(v.getNeighborTriangles()));
	}
	
	public static Voronoi create(
			final Vertex vert,
			Iterable<Vertex> neighborVertices,
			Iterable<Triangle> neighborTriangles) {
		
		// Sort neighbors.
		// TODO For some reason, sorting a list was not working, so we use a treeset
		Comparator<Vertex> comp = new Comparator<Vertex>() {
			public int compare(Vertex o1, Vertex o2) {
				return o2.orientation(o1, vert);
			}
		};
		TreeSet<Vertex> sortedSet = Sets.newTreeSet(comp);
		sortedSet.addAll(Lists.newArrayList(neighborVertices));
		List<Vertex> sortedNeighbors = Lists.newArrayList(sortedSet);

		// Connect circum-centers
		List<Vector> vertices = Lists.newArrayList();
		for (int i = 0; i < sortedNeighbors.size(); i++) {
			final Vector a = i == 0 ? sortedNeighbors.get(sortedNeighbors.size() - 1) : sortedNeighbors.get(i - 1);
			final Vector b = sortedNeighbors.get(i);
			Triangle t = Iterables.getFirst(Iterables.filter(neighborTriangles,
					new Predicate<Triangle>() {
						public boolean apply(Triangle t) {
							return t.getVertices().contains(a) && t.getVertices().contains(b);
						}
					}), null);
			if (t == null) {
				continue;
				// TODO this shouldn't be happening, debug this.
				// We appear to be getting an extra vertex? Are we missing a neighbor triangle?
			}
			vertices.add(t.getCircumCenter());
		}

		return new Voronoi(vertices, sortedNeighbors);
	}

	private final Iterable<Vertex> neighborVertices;

	private Voronoi(Iterable<? extends Vector> vertices, Iterable<Vertex> neighborVertices) {
		super(vertices);
		this.neighborVertices = neighborVertices;
	}

	public Iterable<Vertex> getNeighborVertices() {
		return neighborVertices;
	}
}
