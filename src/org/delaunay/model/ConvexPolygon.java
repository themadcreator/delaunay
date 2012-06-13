package org.delaunay.model;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public strictfp class ConvexPolygon {
	private Double area = null;
	private final List<Vector> vertices;

	public ConvexPolygon(Iterable<? extends Vector> vertices) {
		this.vertices = ImmutableList.copyOf(vertices);
	}

	public List<Vector> getVertices() {
		return vertices;
	}

	public double getArea() {
		if (area != null) {
			return area;
		}
		area = 0.0;
		
		if(vertices.size() > 0){
			Vector c = vertices.get(0);

			for (int i = 1; i < vertices.size(); i++) {
				// compute area of triangle defined by vectors a, b, c
				Vector a = vertices.get(i - 1);
				Vector b = vertices.get(i);
				if (c == a) {
					continue;
				}

				area += Math.abs(a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y)) / 2.0;
			}
		}
		return area;
	}

	public ConvexPolygon intersect(ConvexPolygon o) {
		List<Vector> output = Lists.newArrayList(o.getVertices());

		// http://en.wikipedia.org/wiki/Sutherland%E2%80%93Hodgman_algorithm
		for (int i = 0; i < vertices.size(); i++) {
			Vector a = i == 0 ? vertices.get(vertices.size() - 1) : vertices.get(i - 1);
			Vector b = vertices.get(i);

			List<Vector> input = output;
			output = Lists.newArrayList();

			for (int j = 0; j < input.size(); j++) {
				Vector c = j == 0 ? input.get(input.size() - 1) : input.get(j - 1);
				Vector d = input.get(j);

				if (c.orientation(a, b) >= 0) {
					output.add(c);
					if (d.orientation(a, b) < 0) {
						output.add(intersection(a, b, c, d));
					}
				} else if (d.orientation(a, b) >= 0) {
					output.add(intersection(a, b, c, d));
				}
			}
		}
		return new ConvexPolygon(output);
	}

	public Vector intersection(Vector a, Vector b, Vector c, Vector d) {
		Vector p = a;
		Vector r = b.subtract(a);
		Vector q = c;
		Vector s = d.subtract(c);
		double t = (q.subtract(p)).cross(s) / r.cross(s);
		return p.add(r.multiply(t));
	}
}
