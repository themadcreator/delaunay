package org.delaunay.algorithm;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.delaunay.algorithm.Triangulation.DebugLogger;
import org.delaunay.model.Edge;
import org.delaunay.model.Triangle;
import org.delaunay.model.Vertex;

import com.google.common.collect.Lists;

public class Triangulations {
	public static List<Vertex> randomVertices(int n) {
		Random random = new Random(System.currentTimeMillis());
		List<Vertex> rands = Lists.newArrayList();
		for (int i = 0; i < n; i++) {
			double x = random.nextDouble();
			double y = random.nextDouble();
			x = x*x;
			y = y*y;
			rands.add(new Vertex(x * 800.0, y * 800.0));
		}
		return rands;
	}

	public static void drawTriangulation(Triangulation t,  String filename) {
		BufferedImage img = new BufferedImage(800, 800, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		g.setColor(new Color(0x2222AA));
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setStroke(new BasicStroke(2.0f));
		for (Triangle tri : t.getTriangles()) {
			for (Edge e : tri.getEdges()) {
				g.drawLine((int) e.a.x, (int) e.a.y, (int) e.b.x, (int) e.b.y);
			}
		}
		
		try {
			File file = new File(filename);
			ImageIO.write(img, "png", file);
		} catch (IOException e) {
		}
	}

	public static void main(String[] args) throws Exception {
		Triangulation t = new Triangulation();
		t.setDebugLogger(new DebugLogger() {
			public void debug(String str) {
				System.out.println(str);
			}
		});
		long start = System.nanoTime();
		int n = 10000;
		List<Vertex> verts = Triangulations.randomVertices(n);
		System.out.println(String.format("Time to create %,d vertices: %d msec.", n,
				TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS)));
		start = System.nanoTime();
		t.triangulate(verts);
		System.out.println(String.format("Time to triangulate %,d vertices: %d msec.", n,
				TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS)));
		System.out.println(String.format("Average hops per locate: %.2f", (float)t.getHopCount() / t.getLocateCount()));
		System.out.println("Writing image");
		Triangulations.drawTriangulation(t, "img.png");
		System.out.println("Done");
	}
}
