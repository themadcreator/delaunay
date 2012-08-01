package org.delaunay.algorithm;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.delaunay.Utils;
import org.delaunay.dtfe.painters.PaintTransform;
import org.delaunay.dtfe.painters.TriangulationPainter;
import org.delaunay.dtfe.painters.TriangulationPainterModel;
import org.delaunay.model.Triangle;
import org.delaunay.model.Vertex;

import com.google.common.base.Function;

public class TriangulationLocateTest {
	
	public static void main(String[] args) throws Exception {
		int D = 400;

		Triangulation t = new Triangulation();
		t.addAllVertices(Triangulations.randomVertices(200, D, D));
		t.setKeepSuperTriangle(true);
		t.triangulate();
		
		int right = 0;
		int wrong = 0;
		
		for(final Vertex v : Triangulations.randomVertices(400, D, D)){
			Vertex nearestNaive = Utils.minObject(t.getVertices(),
					new Function<Vertex, Double>() {
						public Double apply(Vertex vert) {
							return vert.subtract(v).lengthSquared();
						}
					});
			
			Triangle nearestTri = t.locate(v);
			Vertex nearestVertex = t.locateNearestVertex(v);
			
			if (nearestNaive != nearestVertex) {
				System.out.println("WRONG");
				System.out.println(v);
				System.out.println(nearestNaive);
				System.out.println(nearestVertex);
				System.out.println(nearestTri != null && nearestTri.getVertices().contains(nearestNaive));
				
				System.out.println();
				
				
				wrong++;

				TriangulationPainter painter = new TriangulationPainter(
						new TriangulationPainterModel().setEdgeColor(Color.DARK_GRAY));
				BufferedImage img = painter.paint(t, new PaintTransform(D, D));
				
				Graphics2D g = (Graphics2D)img.getGraphics();
				int r = 4;
				g.setColor(Color.RED);
				g.fillOval((int)(v.x - r / 2), (int)(v.y - r / 2), r, r);
				g.setColor(Color.GREEN);
				g.fillOval((int)(nearestNaive.x - r / 2), (int)(nearestNaive.y - r / 2), r, r);
				if (nearestVertex != null) {
					g.setColor(Color.BLUE);
					g.fillOval((int) (nearestVertex.x - r / 2), (int) (nearestVertex.y - r / 2), r, r);
				}
				ImageIO.write(img, "png", new File(String.format("wrongs/wrong_%04d.png", wrong)));
			} else{
				right++;
			}
		}

		System.out.println("Right: " + right + " / " + (right + wrong));
		System.out.println("Wrong: " + wrong + " / " + (right + wrong));
	}

}
