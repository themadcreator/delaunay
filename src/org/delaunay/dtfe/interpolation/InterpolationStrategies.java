package org.delaunay.dtfe.interpolation;

import org.delaunay.algorithm.Triangulation;

public class InterpolationStrategies {
	public static BarycentricLinearInterpolationStrategy createBarycentricLinear() {
		return new BarycentricLinearInterpolationStrategy();
	}

	public static AlgebraicLinearInterpolationStrategy createAlgebraicLinear() {
		return new AlgebraicLinearInterpolationStrategy();
	}

	public static NearestNeighborInterpolationStrategy createNearestNeighbor() {
		return new NearestNeighborInterpolationStrategy();
	}

	public static MeanInterpolationStrategy createMean() {
		return new MeanInterpolationStrategy();
	}

	public static NaturalNeighborInterpolationStrategy createNaturalNeighbor(Triangulation triangulation) {
		return new NaturalNeighborInterpolationStrategy(triangulation);
	}
}
