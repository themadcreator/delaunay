package org.delaunay.dtfe.painters;

import java.awt.Color;

import org.delaunay.dtfe.ColorScale;
import org.delaunay.dtfe.ColorScales;
import org.delaunay.dtfe.DtfeTriangulationMap.ScaleType;
import org.delaunay.dtfe.interpolation.InterpolationStrategies;
import org.delaunay.dtfe.interpolation.InterpolationStrategy;

public class DtfePainterModel {
	private ColorScale colorScale = ColorScales.PURPLE_TO_GREEN_LINEAR;
	private double densityScalar = 1.0;
	private Color edgeColor = null;
	private InterpolationStrategy interpolationStrategy = InterpolationStrategies.createNaturalNeighbor();
	private float edgeStrokeWidth = 1.0f;
	private ScaleType scaleType = ScaleType.LINEAR;

	public ScaleType getScaleType() {
		return scaleType;
	}

	public DtfePainterModel setScaleType(ScaleType scaleType) {
		this.scaleType = scaleType;
		return this;
	}

	public float getEdgeStrokeWidth() {
		return edgeStrokeWidth;
	}

	public DtfePainterModel setEdgeStrokeWidth(float edgeStrokeWidth) {
		this.edgeStrokeWidth = edgeStrokeWidth;
		return this;
	}

	public Color getEdgeColor() {
		return edgeColor;
	}

	public DtfePainterModel setEdgeColor(Color edgeColor) {
		this.edgeColor = edgeColor;
		return this;
	}

	public ColorScale getColorScale() {
		return colorScale;
	}

	public DtfePainterModel setColorScale(ColorScale colorScale) {
		this.colorScale = colorScale;
		return this;
	}

	public double getDensityScalar() {
		return densityScalar;
	}

	public DtfePainterModel setDensityScalar(double densityScalar) {
		this.densityScalar = densityScalar;
		return this;
	}

	public InterpolationStrategy getInterpolationStrategy() {
		return interpolationStrategy;
	}

	public DtfePainterModel setInterpolationStrategy(InterpolationStrategy interpolationStrategy) {
		this.interpolationStrategy = interpolationStrategy;
		return this;
	}
}