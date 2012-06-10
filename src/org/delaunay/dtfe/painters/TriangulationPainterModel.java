package org.delaunay.dtfe.painters;

import java.awt.Color;

public class TriangulationPainterModel {
	private Color edgeColor = null;
	private float edgeStrokeWidth = 1.0f;

	public Color getEdgeColor() {
		return edgeColor;
	}

	public TriangulationPainterModel setEdgeColor(Color edgeColor) {
		this.edgeColor = edgeColor;
		return this;
	}

	public float getEdgeStrokeWidth() {
		return edgeStrokeWidth;
	}

	public TriangulationPainterModel setEdgeStrokeWidth(float edgeStrokeWidth) {
		this.edgeStrokeWidth = edgeStrokeWidth;
		return this;
	}
}