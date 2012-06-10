package org.delaunay.dtfe.interpolation;

import org.delaunay.dtfe.DensityModel;
import org.delaunay.dtfe.DtfeTriangulationMap;
import org.delaunay.model.Triangle;
import org.delaunay.model.Vector;
import org.delaunay.model.Vertex;

public final class AlgebraicLinearInterpolationStrategy implements
		InterpolationStrategy {
	public double getDensity(DtfeTriangulationMap<?extends DensityModel> dtfe, Vector v) {	
		Triangle tri = dtfe.getTriangulation().locate(v);

		// Do not report density for triangles outside the convex hull of
		// map vertices.
		if (tri == null || dtfe.getTriangulation().touchesSuperVertex(tri)) {
			return 0.0;
		}
	
		Vertex[] verts = new Vertex[] { tri.a, tri.b, tri.c };
		Vector an = tri.b.normalTo(tri.c).normalize();
		Vector bn = tri.c.normalTo(tri.a).normalize();
		Vector cn = tri.a.normalTo(tri.b).normalize();
		Vector[] norms = new Vector[]{an, bn, cn};

		double aMax = an.dot(tri.b.subtract(tri.a));
		double bMax = bn.dot(tri.c.subtract(tri.b));
		double cMax = cn.dot(tri.a.subtract(tri.c));
		double[] maxes = new double[]{aMax, bMax, cMax};

		double sum = 0.0;
		for(int i = 0; i < 3; i++){
			double dist = norms[i].dot(v.subtract(verts[i]));
			double coeff = 1.0 - dist / maxes[i];
			double dens = coeff * dtfe.getDensity(verts[i]);
			sum += dens;
		}
		return sum;
	}
}