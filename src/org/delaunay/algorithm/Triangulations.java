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
import org.delaunay.dtfe.BasicDensityModel;
import org.delaunay.dtfe.ColorScale;
import org.delaunay.dtfe.Colorscales;
import org.delaunay.dtfe.DensityModel;
import org.delaunay.dtfe.DtfeTriangulationMap;
import org.delaunay.dtfe.DtfeTriangulationMap.ScaleType;
import org.delaunay.model.Edge;
import org.delaunay.model.Triangle;
import org.delaunay.model.Vector;
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
	
	public static List<Vertex> randomGaussian(int n) {
		Random random = new Random(System.currentTimeMillis());
		List<Vertex> rands = Lists.newArrayList();
		for (int i = 0; i < n; i++) {
			double x = random.nextGaussian();
			double y = random.nextGaussian();
			rands.add(new Vertex(x * 100 + 400, y * 100 + 400));
		}
		return rands;
	}

	public static void drawTriangulation(Triangulation t,  String filename) {
		int w = 800;
		int h = 800;
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, h);
		
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

	public static void drawDtfe(
			DtfeTriangulationMap<? extends DensityModel> dtfe,
			ColorScale colorScale,
			double scalar,
			String filename) {
		int w = 800;
		int h = 800;
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, h);

		for (int y = 0; y < h; y++) {
			double[] scanline = new double[w];
			for (int x = 0; x < w; x++) {
				scanline[x] = scalar * dtfe.getInterpolatedDensity(new Vector(x, y));
			}
			int[] rgb = new int[w];
			for (int x = 0; x < w; x++) {
				double scale = dtfe.getRelativeDensity(scanline[x], ScaleType.LINEAR);
				rgb[x] = colorScale.get(scale).getRGB();
			}
			img.setRGB(0, y, w, 1, rgb, 0, w);
		}
		
		g.setColor(new Color(0x0A000000, true));
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setStroke(new BasicStroke(1.0f));
		for (Triangle tri : dtfe.getTriangulation().getTriangles()) {
			if (dtfe.getTriangulation().touchesSuperVertex(tri)) {
				continue;
			}
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
		// Generate vertices
		long start = System.nanoTime();
		int n = 10000;
		List<Vertex> verts = Triangulations.randomVertices(n);
		System.out.println(String.format("Time to create %,d vertices: %d msec.", n,
				TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS)));
		
		// Triangulate
		start = System.nanoTime();
		Triangulation t = new Triangulation();
		t.setDebugLogger(new DebugLogger() {
			public void debug(String str) {
				System.out.println(str);
			}
		});
		t.triangulate(verts);
		System.out.println(String.format("Time to triangulate %,d vertices: %d msec.", n,
				TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS)));
		System.out.println(String.format("Average hops per locate: %.2f", (float)t.getHopCount() / t.getLocateCount()));

		// DTFE
		start = System.nanoTime();
		DtfeTriangulationMap<BasicDensityModel> dtfe = new DtfeTriangulationMap<BasicDensityModel>();
		for (Vertex v : Triangulations.randomGaussian(1000)) {
			dtfe.put(v.x, v.y, new BasicDensityModel());
		}
		dtfe.triangulate();
		System.out.println(String.format("Time to create DTFE: %d msec.", 
				TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS)));
		
		// Draw Results
		System.out.println("Creating images");
		Triangulations.drawTriangulation(t, "triangualation.png");
		start = System.nanoTime();
		Triangulations.drawDtfe(dtfe, Colorscales.PURPLE_TO_GREEN_LINEAR, 50, "dtfe.png");
		System.out.println(String.format("Time to draw DTFE: %d msec.", 
				TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS)));
		System.out.println("Done");
	}
}
