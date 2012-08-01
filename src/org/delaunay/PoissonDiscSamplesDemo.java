package org.delaunay;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.delaunay.algorithm.Triangulation;
import org.delaunay.algorithm.samples.LocateStrategies.TriangulationLocateStrategy;
import org.delaunay.algorithm.samples.SampleBuilder;
import org.delaunay.algorithm.samples.SampleFunctions.PoissonDiscSampleFunction;
import org.delaunay.algorithm.samples.SampleFunctions.SampleFunction;
import org.delaunay.algorithm.samples.SampleFunctions.VariablePoissonDiscSampleFunction;
import org.delaunay.dtfe.painters.PaintTransform;
import org.delaunay.dtfe.painters.TriangulationPainter;
import org.delaunay.dtfe.painters.TriangulationPainterModel;
import org.delaunay.model.Vector;

/**
 * Generates an image of point samples.
 * 
 * Each new sample is computed using a poisson disc distribution around an
 * existing sample point.
 * 
 * @see http://en.wikipedia.org/wiki/Supersampling
 */
public class PoissonDiscSamplesDemo {
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		// Create poisson disc sample function
		Rectangle bounds = new Rectangle(0, 0, 400, 400);
		SampleFunction poissonFunction = new PoissonDiscSampleFunction(bounds, 10);
		SampleFunction variableFunction = new VariablePoissonDiscSampleFunction(bounds) {
			public double getMimimumDistance(Vector v) {
				return 10 + v.x / 5;
			}
		};
		
		// Generate samples to fill bounds
		Iterable<Vector> samples = new SampleBuilder()
				.setLocateStrategy(new TriangulationLocateStrategy())
				.fill(poissonFunction)
				.getSamples();
				
		// Triangulate and paint
		Triangulation t = new Triangulation();
		t.addAllVertices(Utils.toVertices(samples));
		t.triangulate();

		TriangulationPainter painter = new TriangulationPainter(
				new TriangulationPainterModel().setVertexDotColor(Color.BLACK));
		BufferedImage img = painter.paint(t, new PaintTransform(bounds.width, bounds.height));
		ImageIO.write(img, "png", new File("poisson.png"));
	}
}
