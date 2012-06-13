package org.delaunay.dtfe.painters;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.delaunay.model.Vector;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class PaintTransform {
	private final int width;
	private final int height;
	private final Rectangle2D dtfeWindow;

	public PaintTransform(int width, int height) {
		this(width, height, new Rectangle(0, 0, width, height));
	}
	
	public PaintTransform(int width, int height, Rectangle2D dtfeWindow) {
		this.width = width;
		this.height = height;
		this.dtfeWindow = dtfeWindow;
	}

	public List<PaintTransform> createSlices(int slices) {
		int heightPerSlice = height / slices;
		List<PaintTransform> transforms = Lists.newArrayList();
		for(int i = 0; i < slices; i++){
			transforms.add(new PaintTransform(
					width,
					heightPerSlice,
					new Rectangle2D.Double(
						dtfeWindow.getMinX(),
						dtfeWindow.getMinY() + i * dtfeWindow.getHeight() / slices,
						dtfeWindow.getWidth(),
						dtfeWindow.getHeight() / slices)));
		}
		return transforms;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Rectangle2D getDtfeWindow() {
		return dtfeWindow;
	}
	
	public Vector toDtfeVector(int x, int y){
		return new Vector(
			dtfeWindow.getMinX() + dtfeWindow.getWidth() * x / width,
			dtfeWindow.getMinY() + dtfeWindow.getHeight() * y / height);
	}

	public Point toImagePoint(Vector v) {
		double x = (v.x - dtfeWindow.getMinX()) * width / dtfeWindow.getWidth();
		double y = (v.y - dtfeWindow.getMinY()) * height / dtfeWindow.getHeight();
		return new Point((int) x, (int) y);
	}
	
	public Iterable<Point2D> toImagePoints(Iterable<? extends Vector> vectors){
		return Iterables.transform(vectors, new Function<Vector, Point2D>() {
			public Point2D apply(Vector vector) {
				return toImagePoint(vector);
			}
		});
	}
}