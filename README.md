delaunay
========

Delaunay Triangulation with Hilbert Curve linearization and Delaunay Tesselation Field Estimator (DTFE)

### Three line demo ###
```Java
	public static void threeLiner() throws Exception {
		Triangulation t = new Triangulation();
		t.triangulate(Triangulations.randomVertices(1000, 400, 400));
		Demo.drawTriangulation(t, 400, 400, "triangulation.png");
	}
```
