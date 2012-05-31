package org.delaunay.dtfe;

import org.delaunay.model.Vector;

public interface InterpolationStrategy {
	public double getDensity(DtfeTriangulationMap<? extends DensityModel> dtfe, Vector v);
}