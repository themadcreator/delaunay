package org.delaunay.dtfe.painters;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.delaunay.dtfe.DensityModel;
import org.delaunay.dtfe.DtfeTriangulationMap;
import org.delaunay.dtfe.DtfeTriangulationMap.ScaleType;
import org.delaunay.model.Edge;
import org.delaunay.model.Vector;

public class DtfePainter {
	private final DtfePainterModel model;

	public DtfePainter(DtfePainterModel model) {
		this.model = model;
	}

	public BufferedImage paint(
			DtfeTriangulationMap<? extends DensityModel> dtfe,
			Dimension imageSize,
			Rectangle bounds) {
		BufferedImage img = new BufferedImage(imageSize.width, imageSize.height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = (Graphics2D) img.getGraphics();
		
		// Fill scanlines
		for (int y = 0; y < imageSize.height; y++) {
			double[] scanline = new double[imageSize.width];
			for (int x = 0; x < imageSize.width; x++) {
				Vector v = new Vector(
						bounds.getMinX() + bounds.getWidth() * x / imageSize.width,
						bounds.getMinY() + bounds.getHeight() * y / imageSize.height);
				scanline[x] = model.getDensityScalar() * dtfe.getInterpolatedDensity(v, model.getInterpolationStrategy());
			}
			int[] rgb = new int[imageSize.width];
			for (int x = 0; x < imageSize.width; x++) {
				double scale = dtfe.getRelativeDensity(scanline[x], ScaleType.LINEAR);
				rgb[x] = model.getColorScale().get(scale).getRGB();
			}
			img.setRGB(0, y, imageSize.width, 1, rgb, 0, imageSize.width);
		}	

		// Draw Edges
		if (model.getEdgeColor() != null) {
			g.setColor(model.getEdgeColor());
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setStroke(new BasicStroke(model.getEdgeStrokeWidth()));

			for (Edge e : TriangulationPainter.getPaintableEdges(dtfe.getTriangulation())) {
				double ax = (e.a.x - bounds.getMinX()) * imageSize.width / bounds.getWidth();
				double ay = (e.a.y - bounds.getMinY()) * imageSize.height / bounds.getHeight();
				double bx = (e.b.x - bounds.getMinX()) * imageSize.width / bounds.getWidth();
				double by = (e.b.y - bounds.getMinY()) * imageSize.height / bounds.getHeight();
				g.drawLine((int) ax, (int) ay, (int) bx, (int) by);
			}
		}
		return img;
	}
}