package org.delaunay.dtfe.painters;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import org.delaunay.dtfe.ColorScale;

public class ColorScaleLegendPainter {
	public static enum Orientation{
		HORIZONTAL,
		VERTICAL;
	}

	public static class ColorScalePainterModel {
		private Orientation orientation = Orientation.HORIZONTAL;
		private Rectangle bounds = new Rectangle(0, 0, 300, 20);

		public Orientation getOrientation() {
			return orientation;
		}

		public ColorScalePainterModel setOrientation(Orientation orientation) {
			this.orientation = orientation;
			return this;
		}

		public Rectangle getBounds() {
			return bounds;
		}

		public ColorScalePainterModel setBounds(Rectangle bounds) {
			this.bounds = bounds;
			return this;
		}
	}
	
	private final ColorScalePainterModel model;
	
	public ColorScaleLegendPainter(ColorScalePainterModel model) {
		this.model = model;
	}

	public BufferedImage createLegendImage(ColorScale scale){
		BufferedImage img = new BufferedImage(model.getBounds().width, model.getBounds().height, BufferedImage.TYPE_4BYTE_ABGR);
		paintLegendImage(scale, img);
		return img;
	}

	public void paintLegendImage(ColorScale scale, BufferedImage img) {
		Rectangle bounds = model.getBounds();
		double s0 = scale.getMinStop();
		double s1 = scale.getMaxStop();
		double ds = s1 - s0;
		
		if (model.getOrientation() == Orientation.VERTICAL) {
			int[] rgb = new int[bounds.width];
			for (int y = 0; y < bounds.height; y++) {
				double value = 1.0 - (double) y / bounds.height;
				value = s0 + ds * value;
				Arrays.fill(rgb, scale.get(value).getRGB());
				img.setRGB(bounds.x, bounds.y + y, bounds.width, 1, rgb, 0, bounds.height);
			}
		} else {
			int[] rgb = new int[bounds.width];
			for (int x = 0; x < bounds.width; x++) {
				double value = (double) x / bounds.width;
				value = s0 + ds * value;
				rgb[x] = scale.get(value).getRGB();
			}

			for (int y = 0; y < bounds.height; y++) {
				img.setRGB(bounds.x, bounds.y + y, bounds.width, 1, rgb, 0, bounds.height);
			}
		}
	}
}
