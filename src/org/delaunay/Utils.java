package org.delaunay;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.TreeMap;

import org.delaunay.model.Vector;
import org.delaunay.model.Vertex;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

public class Utils {

	public static <T> double sum(Iterable<Double> values) {
		double sum = 0.0;
		for (double d : values) {
			sum += d;
		}
		return sum;
	}

	public static <T> double maxValue(Iterable<T> values, Function<? super T, Double> valueFunction) {
		TreeMap<Double, T> map = valueMap(values, valueFunction);
		if (map.isEmpty()) {
			return 0.0;
		}
		return map.lastKey();
	}


	public static <T> double minValue(Iterable<T> values, Function<? super T, Double> valueFunction) {
		TreeMap<Double, T> map = valueMap(values, valueFunction);
		if (map.isEmpty()) {
			return 0.0;
		}
		return map.firstKey();
	}

	public static <T> T maxObject(Iterable<T> values, Function<? super T, Double> valueFunction) {
		TreeMap<Double, T> map = valueMap(values, valueFunction);
		if (map.isEmpty()) {
			return null;
		}
		return map.lastEntry().getValue();
	}
	
	public static <T> T minObject(Iterable<T> values, Function<? super T, Double> valueFunction) {
		TreeMap<Double, T> map = valueMap(values, valueFunction);
		if (map.isEmpty()) {
			return null;
		}
		return map.firstEntry().getValue();
	}

	private static <T> TreeMap<Double, T> valueMap(
			Iterable<T> values,
			Function<? super T, Double> valueFunction) {
		TreeMap<Double, T> map = Maps.newTreeMap();
		for (T value : values) {
			Double d = valueFunction.apply(value);
			if (d != null) {
				map.put(d, value);
			}
		}
		return map;
	}

	public static Iterable<Vertex> toVertices(Iterable<? extends Vector> vectors) {
		return Iterables.transform(vectors, new Function<Vector, Vertex>() {
			public Vertex apply(Vector vector) {
				return new Vertex(vector.x, vector.y);
			}
		});
	}
	
	public static Iterable<Point2D> toPoints(Iterable<? extends Vector> vectors) {
		return Iterables.transform(vectors, new Function<Vector, Point2D>() {
			public Point2D apply(Vector vector) {
				return new Point2D.Double(vector.x, vector.y);
			}
		});
	}

	public static Path2D pathFromPoints(Iterable<? extends Point2D> points) {
		Path2D path = new Path2D.Double();
		int i = 0;
		for (Point2D point : points) {
			if (i++ == 0) {
				path.moveTo(point.getX(), point.getY());
			} else {
				path.lineTo(point.getX(), point.getY());
			}
		}
		path.closePath();
		return path;
	}
}
