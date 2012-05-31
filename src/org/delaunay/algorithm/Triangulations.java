package org.delaunay.algorithm;

import java.util.List;
import java.util.Random;

import org.delaunay.model.Vertex;

import com.google.common.collect.Lists;

public class Triangulations {
	public static List<Vertex> randomVertices(int n, int width, int height) {
		Random random = new Random(System.currentTimeMillis());
		List<Vertex> rands = Lists.newArrayList();
		for (int i = 0; i < n; i++) {
			double x = random.nextDouble();
			double y = random.nextDouble();
			x = x*x;
			y = y*y;
			rands.add(new Vertex(x * width, y * height));
		}
		return rands;
	}
	
	public static List<Vertex> randomGaussian(int n, int width, int height) {
		Random random = new Random(System.currentTimeMillis());
		List<Vertex> rands = Lists.newArrayList();
		for (int i = 0; i < n; i++) {
			double x = random.nextGaussian();
			double y = random.nextGaussian();
			rands.add(new Vertex(x * width / 8 + width / 2, y * width / 8 + height / 2));
		}
		return rands;
	}
}
