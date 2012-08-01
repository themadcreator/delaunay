package org.delaunay.dtfe.painters;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Set;

import org.delaunay.algorithm.Triangulation;
import org.delaunay.model.Edge;
import org.delaunay.model.Triangle;
import org.delaunay.model.Vertex;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class TriangulationPainter {
	private final TriangulationPainterModel model;

	public TriangulationPainter(TriangulationPainterModel model) {
		this.model = model;
	}

	public BufferedImage paint(
			Triangulation triangulation,
			PaintTransform transform) {
		BufferedImage img = new BufferedImage(transform.getWidth(), transform.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = (Graphics2D) img.getGraphics();

		// Draw Edges
		if (model.getEdgeColor() != null) {
			g.setColor(model.getEdgeColor());
			g.setStroke(new BasicStroke(model.getEdgeStrokeWidth()));
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			for (Edge e : getPaintableEdges(triangulation)) {
				Point a = transform.toImagePoint(e.a);
				Point b = transform.toImagePoint(e.b);
				g.drawLine(a.x, a.y, b.x, b.y);
			}
		}

		if (model.getVertexDotColor() != null) {
			g.setColor(model.getVertexDotColor());
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			float r = model.getVertexDotRadius();

			for (Vertex v : triangulation.getVertices()) {
				Point p = transform.toImagePoint(v);
				g.fillOval((int)(p.x - r), (int)(p.y - r), (int)(r * 2), (int)(r * 2));
			}
		}
		
		return img;
	}

	public static Set<Edge> getPaintableEdges(Triangulation triangulation) {
		Set<Edge> allEdges = Sets.newHashSet();
		for (Triangle tri : triangulation.getTriangles()) {
			if (triangulation.touchesSuperVertex(tri)) {
				continue;
			}
			Iterables.addAll(allEdges, tri.getEdges());
		}
		return allEdges;
	}
}
