package org.delaunay.algorithm;

import java.awt.Point;
import java.awt.geom.Rectangle2D;


public class ScaledHilbertIndex {
	private final Rectangle2D scales;
	private final HilbertTableIndex index;
	
	public ScaledHilbertIndex(int order, Rectangle2D bbox) {
		long side = 1 << order;
		this.scales = new Rectangle2D.Double(
				bbox.getMinX(),
				bbox.getMinY(),
				side / bbox.getWidth(),
				side / bbox.getHeight());
		this.index = new HilbertTableIndex(order);
	}
	
	public int toIndex(double x, double y){
		return index.getIndex(new Point(
				(int)((x - scales.getX()) * scales.getWidth()),
				(int)((y - scales.getY()) * scales.getHeight())));
	}
}
