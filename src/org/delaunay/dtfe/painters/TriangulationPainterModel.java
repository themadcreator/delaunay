package org.delaunay.dtfe.painters;

import java.awt.Color;

public class TriangulationPainterModel {
	private Color edgeColor = null;
	private float edgeStrokeWidth = 1.0f;

	private Color vertexDotColor = null;
	private float vertexDotRadius = 2.0f;

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

	public Color getVertexDotColor() {
		return vertexDotColor;
	}

	public TriangulationPainterModel setVertexDotColor(Color vertexColor) {
		this.vertexDotColor = vertexColor;
		return this;
	}

	public float getVertexDotRadius() {
		return vertexDotRadius;
	}

	public TriangulationPainterModel setVertexDotRadius(float vertexDotRadius) {
		this.vertexDotRadius = vertexDotRadius;
		return this;
	}
	
	
}