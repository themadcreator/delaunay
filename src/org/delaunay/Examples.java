package org.delaunay;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.delaunay.algorithm.Triangulations;
import org.delaunay.dtfe.BasicDensityModel;
import org.delaunay.dtfe.ColorScales;
import org.delaunay.dtfe.DtfeTriangulationMap;
import org.delaunay.dtfe.interpolation.InterpolationStrategies;
import org.delaunay.dtfe.painters.DtfePainter;
import org.delaunay.dtfe.painters.DtfePainterModel;
import org.delaunay.dtfe.painters.TriangulationPainter;
import org.delaunay.dtfe.painters.TriangulationPainterModel;
import org.delaunay.model.Vertex;

public class Examples {
	private static final int WIDTH = 800;
	private static final int HEIGHT = 800;

	public static void createInterpolationExamples() throws Exception {
		// Generate random distribution of points
		DtfeTriangulationMap<BasicDensityModel> dtfe = new DtfeTriangulationMap<BasicDensityModel>();
		for (Vertex v : Triangulations.randomGaussian(1000, WIDTH, HEIGHT)) {
			dtfe.put(v.x, v.y, new BasicDensityModel());
		}
		dtfe.triangulate();

		// Draw overall triangulation
		BufferedImage img = new TriangulationPainter(new TriangulationPainterModel()
				.setEdgeColor(Color.BLACK)
				.setEdgeStrokeWidth(1.5f)).paint(
						dtfe.getTriangulation(),
						new Dimension(WIDTH, HEIGHT),
						new Rectangle(0, 0, WIDTH, HEIGHT));
		ImageIO.write(img, "png", new File("examples/dtfe_triangulation_full.png"));

		// Draw individual interpolations
		DtfePainterModel model = new DtfePainterModel()
			.setDensityScalar(35)
			.setColorScale(ColorScales.PURPLE_TO_GREEN_LINEAR);
		model.setInterpolationStrategy(InterpolationStrategies.createMean());
		createSampleImages(dtfe, model, "mean");
		model.setInterpolationStrategy(InterpolationStrategies.createNearestNeighbor());
		createSampleImages(dtfe, model, "near_neighbor");
		model.setInterpolationStrategy(InterpolationStrategies.createBarycentricLinear());
		createSampleImages(dtfe, model, "linear");
		model.setInterpolationStrategy(InterpolationStrategies.createNaturalNeighbor());
		createSampleImages(dtfe, model, "natural_neighbor");
	}

	private static void createSampleImages(DtfeTriangulationMap<BasicDensityModel> dtfe,
			DtfePainterModel model, String name) throws IOException {
		Rectangle fullRect = new Rectangle(0, 0, WIDTH, HEIGHT);
		Rectangle zoomRect = new Rectangle(WIDTH / 2 + 50, HEIGHT / 2 - 100, 200, 200);

		ImageIO.write(new DtfePainter(model).paint(dtfe, new Dimension(WIDTH, HEIGHT), fullRect),
				"png", new File("examples/dtfe_interp_" + name + "_full.png"));
		ImageIO.write(new DtfePainter(model).paint(dtfe, new Dimension(WIDTH, HEIGHT), zoomRect),
				"png", new File("examples/dtfe_interp_" + name + "_zoom.png"));
	}

	public static void main(String[] args) throws Exception {
		createInterpolationExamples();
	}
}
