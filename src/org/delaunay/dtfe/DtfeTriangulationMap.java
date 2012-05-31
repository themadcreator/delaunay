package org.delaunay.dtfe;

import org.delaunay.algorithm.Triangulation.InvalidVertexException;
import org.delaunay.algorithm.Utils;
import org.delaunay.model.Triangle;
import org.delaunay.model.TriangulationMap;
import org.delaunay.model.Vector;
import org.delaunay.model.Vertex;

import com.google.common.base.Function;

/**
 * Computes the Delaunay Tesselation Field Estimator (DTFE):
 * http://en.wikipedia.org/wiki/Delaunay_tessellation_field_estimator
 * 
 * This method produces very good heatmaps from discrete data.
 * 
 * @param <T> a model that implements the DensityModel interface
 */
public class DtfeTriangulationMap<T extends DtfeTriangulationMap.DensityModel> extends TriangulationMap<T> {
	public static interface DensityModel {
		public double getDensity();
		public void setDensity(double d);
		public double getWeight();
	}

	public static enum ScaleType {
		LINEAR, LOG;
	}

	@Override
	public void triangulate() throws InvalidVertexException {
		super.triangulate();
		computeDtfe();
	}

	/**
	 * Returns the density of the vertex.
	 */
	public double getDensity(Vertex v) {
		T model = get(v);
		if (model == null) {
			return 0.0;
		} else {
			return model.getDensity();
		}
	}
	
	public double getInterpolatedDensity(Vector v) {
		Triangle tri = getTriangulation().locate(v);
		
		// Do not report density for triangles outside the convex hull of map
		// vertices.
		if (tri == null || getTriangulation().touchesSuperVertex(tri)) {
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
		double sum = lambdaA * getDensity(a) + lambdaB * getDensity(b) + lambdaC * getDensity(c);
		return sum;
	
		// This method uses vector math
//		Vertex[] verts = new Vertex[]{a,b,c};
//		Vector an = tri.b.normalTo(tri.c).normalize();
//		Vector bn = tri.c.normalTo(tri.a).normalize();
//		Vector cn = tri.a.normalTo(tri.b).normalize();
//		Vector[] norms = new Vector[]{an, bn, cn};
//
//		double aMax = an.dot(tri.b.subtract(tri.a));
//		double bMax = bn.dot(tri.c.subtract(tri.b));
//		double cMax = cn.dot(tri.a.subtract(tri.c));
//		double[] maxes = new double[]{aMax, bMax, cMax};
//
//		double sum = 0.0;
//		for(int i = 0; i < 3; i++){
//			double dist = norms[i].dot(v.subtract(verts[i]));
//			double coeff = 1.0 - dist / maxes[i];
//			double dens = coeff * getDensity(verts[i]);
//			sum += dens;
//		}
//		return sum;
	}
	
	private Double maxDensity = null;
	
	/**
	 * Returns the maximum density value for all vertices
	 */
	public double getMaxDensity() {
		if (maxDensity == null) {
			maxDensity = Utils.maxValue(getTriangulation().getVertices(),
					new Function<Vertex, Double>() {
						@Override
						public Double apply(Vertex v) {
							return getDensity(v);
						}
					});
		}
		return maxDensity;
	}
	
	/**
	 * Returns a value of 0.0 to 1.0, where 1.0 represents the maximum density
	 * value. This can be plugged directly into a {@link ColorScale} object.
	 */
	public double getRelativeDensity(double d, ScaleType scaleType) {
		if(d == 0) return 0;
		return scaleType == ScaleType.LOG ?
				Math.log10(d) / Math.log10(getMaxDensity()) :
				d / getMaxDensity();
	}

	private void computeDtfe() {
		for (Vertex v : getTriangulation().getVertices()) {
			double area = 0.0;
			for (Triangle tri : v.getNeighborTriangles()) {
				area += tri.getArea();
			}
			T model = get(v);
			model.setDensity(area == 0 ? 0.0 : model.getWeight() / area);
		}
	}
}