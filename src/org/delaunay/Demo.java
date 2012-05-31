package org.delaunay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.delaunay.algorithm.Triangulation;
import org.delaunay.algorithm.Triangulation.DebugLogger;
import org.delaunay.algorithm.Triangulation.InvalidVertexException;
import org.delaunay.algorithm.Triangulations;
import org.delaunay.dtfe.BasicDensityModel;
import org.delaunay.dtfe.ColorScale;
import org.delaunay.dtfe.Colorscales;
import org.delaunay.dtfe.DensityModel;
import org.delaunay.dtfe.DtfeTriangulationMap;
import org.delaunay.dtfe.DtfeTriangulationMap.ScaleType;
import org.delaunay.dtfe.InterpolationStrategies;
import org.delaunay.model.Edge;
import org.delaunay.model.Triangle;
import org.delaunay.model.Vector;
import org.delaunay.model.Vertex;

public class Demo {
	private static final int WIDTH = 800;
	private static final int HEIGHT = 800;
	
	public static void drawTriangulation(Triangulation t, int w, int h,  String filename) {
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, h);
		
		g.setColor(new Color(0x2222AA));
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setStroke(new BasicStroke(1.0f));
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
			int w, int h,
			String filename) {
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, h);

		for (int y = 0; y < h; y++) {
			double[] scanline = new double[w];
			for (int x = 0; x < w; x++) {
				scanline[x] = scalar * dtfe.getInterpolatedDensity(
						new Vector(x, y),
						InterpolationStrategies.BARYCENTRIC_LINEAR);
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

	public static void threeLiner() throws Exception {
		Triangulation t = new Triangulation();
		t.triangulate(Triangulations.randomVertices(1000, 400, 400));
		Demo.drawTriangulation(t, 400, 400, "triangulation.png");
	}

	private static void fullDemo() throws InvalidVertexException {
		// Generate vertices
		long start = System.nanoTime();
		int n = 10000;
		List<Vertex> verts = Triangulations.randomVertices(n, WIDTH, HEIGHT);
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
		for (Vertex v : Triangulations.randomGaussian(1000, WIDTH, HEIGHT)) {
			dtfe.put(v.x, v.y, new BasicDensityModel());
		}
		dtfe.triangulate();
		System.out.println(String.format("Time to create DTFE: %d msec.", 
				TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS)));
		
		// Draw Results
		System.out.println("Creating images");
		drawTriangulation(t, WIDTH, HEIGHT, "triangulation.png");
		start = System.nanoTime();
		drawDtfe(dtfe, Colorscales.PURPLE_TO_GREEN_LINEAR, 50, WIDTH, HEIGHT, "dtfe.png");
		System.out.println(String.format("Time to draw DTFE: %d msec.", 
				TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS)));
		System.out.println("Done");
	}

	public static void main(String[] args) throws Exception {
		//threeLiner();
		fullDemo();
	}
}
