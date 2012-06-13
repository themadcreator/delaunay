package org.delaunay.dtfe.painters;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.delaunay.dtfe.DensityModel;
import org.delaunay.dtfe.DtfeTriangulationMap;
import org.delaunay.model.Edge;
import org.delaunay.model.Vector;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class DtfePainter {

	private final DtfePainterModel model;

	public DtfePainter(DtfePainterModel model) {
		this.model = model;
	}

	public Iterable<BufferedImage> paintSlices(
			final DtfeTriangulationMap<? extends DensityModel> dtfe,
			PaintTransform transform,
			int slices) {

		return Iterables.transform(transform.createSlices(slices),
				new Function<PaintTransform, BufferedImage>() {
					public BufferedImage apply(PaintTransform pattern) {
						return paint(dtfe, pattern);
					}
				});
	}


	public BufferedImage paint(
			DtfeTriangulationMap<? extends DensityModel> dtfe,
			PaintTransform transform) {
		BufferedImage img = new BufferedImage(transform.getWidth(), transform.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = (Graphics2D) img.getGraphics();
		
		// Fill scanlines
		for (int y = 0; y < transform.getHeight(); y++) {
			double[] scanline = new double[transform.getWidth()];
			for (int x = 0; x < transform.getWidth(); x++) {
				Vector v = transform.toDtfeVector(x, y);
				scanline[x] = dtfe.getInterpolatedDensity(v, model.getInterpolationStrategy());
			}
			int[] rgb = new int[transform.getWidth()];
			for (int x = 0; x < transform.getWidth(); x++) {
				double scale = model.getDensityScalar() * dtfe.getRelativeDensity(scanline[x], model.getScaleType());
				rgb[x] = model.getColorScale().get(scale).getRGB();
			}
			img.setRGB(0, y, transform.getWidth(), 1, rgb, 0, transform.getWidth());
		}	

		// Draw Edges
		if (model.getEdgeColor() != null) {
			g.setColor(model.getEdgeColor());
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setStroke(new BasicStroke(model.getEdgeStrokeWidth()));

			for (Edge e : TriangulationPainter.getPaintableEdges(dtfe.getTriangulation())) {
				Point a = transform.toImagePoint(e.a);
				Point b = transform.toImagePoint(e.b);
				g.drawLine(a.x, a.y, b.x, b.y);
			}
		}
		return img;
	}
}