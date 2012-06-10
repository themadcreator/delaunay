package org.delaunay.dtfe.painters;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Set;

import org.delaunay.algorithm.Triangulation;
import org.delaunay.model.Edge;
import org.delaunay.model.Triangle;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class TriangulationPainter {
	private final TriangulationPainterModel model;

	public TriangulationPainter(TriangulationPainterModel model) {
		this.model = model;
	}

	public BufferedImage paint(
			Triangulation triangulation,
			Dimension imageSize,
			Rectangle bounds) {
		BufferedImage img = new BufferedImage(imageSize.width, imageSize.height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = (Graphics2D) img.getGraphics();

		// Draw Edges
		if (model.getEdgeColor() != null) {
			g.setColor(model.getEdgeColor());
			g.setStroke(new BasicStroke(model.getEdgeStrokeWidth()));
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			for (Edge e : getPaintableEdges(triangulation)) {
				double ax = (e.a.x - bounds.getMinX()) * imageSize.width / bounds.getWidth();
				double ay = (e.a.y - bounds.getMinY()) * imageSize.height / bounds.getHeight();
				double bx = (e.b.x - bounds.getMinX()) * imageSize.width / bounds.getWidth();
				double by = (e.b.y - bounds.getMinY()) * imageSize.height / bounds.getHeight();
				g.drawLine((int) ax, (int) ay, (int) bx, (int) by);
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
