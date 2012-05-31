package org.delaunay.dtfe;

import org.delaunay.model.Triangle;
import org.delaunay.model.Vector;
import org.delaunay.model.Vertex;

public class InterpolationStrategies {
	public static final InterpolationStrategy BARYCENTRIC_LINEAR = new InterpolationStrategy() {
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
	};
	
	public static final InterpolationStrategy ALGEBRAIC_LINEAR = new InterpolationStrategy() {
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
	};
	
	public static final InterpolationStrategy NEAREST_NEIGHBOR = new InterpolationStrategy() {
		public double getDensity(DtfeTriangulationMap<?extends DensityModel> dtfe, Vector v) {	
			Vertex vert = dtfe.getTriangulation().locateNearestVertex(v);

			// Do not report density for triangles outside the convex hull of
			// map vertices.
			if (vert == null || dtfe.getTriangulation().neighborsSuperVertex(vert)) {
				return 0.0;
			}
		
			return dtfe.getDensity(vert);
		}
	};
}
