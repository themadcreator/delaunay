package org.delaunay.dtfe.interpolation;

import org.delaunay.dtfe.DensityModel;
import org.delaunay.dtfe.DtfeTriangulationMap;
import org.delaunay.model.Triangle;
import org.delaunay.model.Vector;
import org.delaunay.model.Vertex;

public final class BarycentricLinearInterpolationStrategy implements
		InterpolationStrategy {
	public double getDensity(DtfeTriangulationMap<? extends DensityModel> dtfe, Vector v) {
		Triangle tri = dtfe.getTriangulation().locate(v);

		// Do not report density for triangles outside the convex hull of
		// map vertices.
		if (tri == null || dtfe.getTriangulation().touchesSuperVertex(tri)) {
			return 0.0;
		}

		Vertex a = tri.a;
		Vertex b = tri.b;
		Vertex c = tri.c;

		// This method uses barycentric coordinates.
		// The only danger here is a divide by zero, which we check.
		double det = (b.y - c.y) * (a.x - c.x) + (c.x - b.x) * (a.y - c.y);
		if (det == 0.0) {
			return 0.0;
		}
		double lambdaA = ((b.y - c.y) * (v.x - c.x) + (c.x - b.x) * (v.y - c.y)) / det;
		double lambdaB = ((c.y - a.y) * (v.x - c.x) + (a.x - c.x) * (v.y - c.y)) / det;
		double lambdaC = 1.0 - lambdaA - lambdaB;
		double sum = lambdaA * dtfe.getDensity(a) + lambdaB * dtfe.getDensity(b) + lambdaC * dtfe.getDensity(c);
		return sum;
	}
}