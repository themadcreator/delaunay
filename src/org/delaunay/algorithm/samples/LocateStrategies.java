package org.delaunay.algorithm.samples;

import java.awt.geom.Rectangle2D;
import java.util.List;

import org.delaunay.Utils;
import org.delaunay.algorithm.Triangulation;
import org.delaunay.algorithm.Triangulation.InvalidVertexException;
import org.delaunay.model.Vector;
import org.delaunay.model.Vertex;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class LocateStrategies {
	public static interface LocateStrategy {
		void initialize(Iterable<Vector> samples, Rectangle2D bounds);
		boolean addSample(Vector v);
		Vector getNearest(Vector v);
	}

	public static class NaiveLocateStrategy implements LocateStrategy {
		private List<Vector> locatable = Lists.newArrayList();

		public void initialize(Iterable<Vector> samples, Rectangle2D bounds) {
			locatable = Lists.newArrayList(samples);
		}

		public boolean addSample(Vector v) {
			locatable.add(v);
			return true;
		}

		public Vector getNearest(final Vector v) {
			return Utils.minObject(locatable, new Function<Vector, Double>() {
				public Double apply(Vector vert) {
					return vert.subtract(v).lengthSquared();
				}
			});
		}
	}

	public static class TriangulationLocateStrategy implements LocateStrategy {
		private Triangulation triangulation = new Triangulation();

		public void initialize(Iterable<Vector> samples, Rectangle2D bounds) {
			triangulation = new Triangulation();
			triangulation.createSuperTriangle(bounds);
			for (Vector v : samples) {
				addSample(v);
			}
		}

		public boolean addSample(Vector v) {
			try {
				triangulation.addVertexToTriangulation(new Vertex(v.x, v.y));
				return true;
			} catch (InvalidVertexException e) {
				return false;
			}
		}

		public Vector getNearest(final Vector v) {
			return triangulation.locateNearestVertex(v);
		}
	}
}
