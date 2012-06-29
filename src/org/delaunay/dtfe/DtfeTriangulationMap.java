package org.delaunay.dtfe;

import org.delaunay.Utils;
import org.delaunay.algorithm.Triangulation.InvalidVertexException;
import org.delaunay.dtfe.interpolation.InterpolationStrategy;
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
public class DtfeTriangulationMap<T extends DensityModel> extends TriangulationMap<T> {
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
		} else if (getTriangulation().neighborsSuperVertex(v)) {
			return 0.0;
		} else {
			return model.getDensity();
		}
	}
	
	public double getInterpolatedDensity(Vector v, InterpolationStrategy strategy) {
		return strategy.getDensity(this, v);
	}
	
	private Double maxDensity = null;
	
	/**
	 * Returns the maximum density value for all vertices
	 */
	public double getMaxDensity() {
		if (maxDensity == null) {
			maxDensity = Utils.maxValue(getTriangulation().getVertices(),
					new Function<Vertex, Double>() {
						public Double apply(Vertex v) {
							return Math.abs(getDensity(v));
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
		boolean neg = (d < 0); 
		d = Math.abs(d);
		double relativeDensity = scaleType == ScaleType.LOG ?
				Math.log10(1 + d) / Math.log10(1 + getMaxDensity()) :
				d / getMaxDensity();
		return relativeDensity * (neg ? -1 : 1);
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