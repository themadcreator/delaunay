package org.delaunay.dtfe.interpolation;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.delaunay.algorithm.Triangulation;
import org.delaunay.algorithm.Triangulation.InvalidVertexException;
import org.delaunay.algorithm.Triangulation.NonDelaunayException;
import org.delaunay.dtfe.DensityModel;
import org.delaunay.dtfe.DtfeTriangulationMap;
import org.delaunay.model.Triangle;
import org.delaunay.model.Vector;
import org.delaunay.model.Vertex;
import org.delaunay.model.Voronoi;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class NaturalNeighborInterpolationStrategy implements InterpolationStrategy {
	private final Map<Vertex, Voronoi> voronoi;

	public NaturalNeighborInterpolationStrategy() {
		this.voronoi = Maps.newLinkedHashMap();
	}

	public Voronoi getVoronoi(Vertex v) {
		if (voronoi.containsKey(v)) {
			return voronoi.get(v);
		}

		Voronoi cell = Voronoi.createFromTriangulation(v);
		voronoi.put(v, cell);
		return cell;
	}

	public Voronoi getSecondOrderVoronoi(Triangulation triangulation, Vertex v) throws NonDelaunayException, InvalidVertexException {
		Collection<Triangle> cavity = triangulation.getCircumcircleTriangles(v);
		List<Triangle> tris = triangulation.createTriangles(triangulation.getEdgeSet(cavity), v);
		Set<Vertex> verts = Sets.newHashSet();
		for (Triangle tri : tris) {
			verts.addAll(tri.getVertices());
		}
		verts.remove(v);
		return Voronoi.create(v, verts, tris);
	}

	public double getDensity(DtfeTriangulationMap<? extends DensityModel> dtfe, Vector v) {
		Triangle tri = dtfe.getTriangulation().locate(v);
		if (tri == null || dtfe.getTriangulation().touchesSuperVertex(tri)) {
			return 0.0;
		}

		try {
			Voronoi vor = getSecondOrderVoronoi(dtfe.getTriangulation(), new Vertex(v.x, v.y));
			double area = 0;
			for (Vertex vert : vor.getNeighborVertices()) {
				if (dtfe.getTriangulation().neighborsSuperVertex(vert)) {
					continue;
				}
				Voronoi vertVor = getVoronoi(vert);
				if (vertVor != null) {
					area += vor.intersect(vertVor).getArea() * dtfe.getDensity(vert);
				}
			}
			return area / vor.getArea();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

}
