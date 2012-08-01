package org.delaunay.algorithm.samples;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import org.delaunay.model.Vector;

public class SampleFunctions {

	public static interface SampleFunction {
		Shape getBoundingShape();
		Vector createSampleIn(Shape shape);
		Vector createSampleNear(Vector v);
		double getMimimumDistance(Vector v);
	}
	
	public static abstract class BoundSampleFunction implements SampleFunction {
		protected Random random = new Random(System.currentTimeMillis());
		protected final Shape shape;

		public BoundSampleFunction(Shape shape) {
			this.shape = shape;
		}

		public Shape getBoundingShape() {
			return shape;
		}

		public Vector createSampleIn(Shape shape) {
			Rectangle2D b = shape.getBounds2D();
			double x = b.getMinX() + random.nextDouble() * b.getWidth();
			double y = b.getMinY() + random.nextDouble() * b.getHeight();
			return new Vector(x, y);
		}
	}

	public static class PoissonDiscSampleFunction extends BoundSampleFunction {
		private final double minDist;
		private final double maxDist;

		public PoissonDiscSampleFunction(Shape shape, double minDist) {
			this(shape, minDist, minDist * 2);
		}

		public PoissonDiscSampleFunction(Shape shape, double minDist, double maxDist) {
			super(shape);
			this.minDist = minDist;
			this.maxDist = maxDist;
		}

		public Vector createSampleNear(Vector v) {
			double rr = Math.sqrt(random.nextDouble()); // sqrt to compensate for area change
			double radius = minDist + (rr) * (maxDist - minDist);
			double angle = 2 * Math.PI * random.nextDouble();
			double x = v.x + radius * Math.cos(angle);
			double y = v.y + radius * Math.sin(angle);
			return new Vector(x, y);
		}

		public double getMimimumDistance(Vector v) {
			return minDist;
		}
	}
	
	public static abstract class VariablePoissonDiscSampleFunction extends BoundSampleFunction {
		public VariablePoissonDiscSampleFunction(Shape shape) {
			super(shape);
		}

		public Vector createSampleNear(Vector v) {
			double minDist = getMimimumDistance(v);
			double rr = Math.sqrt(random.nextDouble()); // sqrt to compensate for area change
			double radius = minDist + (rr + 1);
			double angle = 2 * Math.PI * random.nextDouble();
			double x = v.x + radius * Math.cos(angle);
			double y = v.y + radius * Math.sin(angle);
			return new Vector(x, y);
		}
	}
}
