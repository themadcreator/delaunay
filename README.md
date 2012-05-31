delaunay
========

Delaunay Triangulation for Java with Hilbert Curve linearization and a Delaunay Tesselation Field Estimator (DTFE)

### Three line demo ###
```Java
	public static void threeLiner() throws Exception {
		Triangulation t = new Triangulation();
		t.triangulate(Triangulations.randomVertices(1000, 400, 400));
		Demo.drawTriangulation(t, 400, 400, "triangulation.png");
	}
```

### Delaunay Triangulation ###
The `Triangulation` class creates a Delaunay Triangulation of the Vertexs. (http://en.wikipedia.org/wiki/Delaunay_triangulation)

This implementation uses a simple iterative approach, but with some pre-processing
 to make the real-world performance fast.

1. For each vertex, we walk to the enclosing triangle.
2. We create a cavity from that triangle and all neighboring triangles for which the vertex is
in its circumcircle.
3. We create new triangles between the edges of the cavity and the
vertex.
 
The basic incremental triangulation method inspired by Paul Bourke's notes
 and psuedocode. See: (http://paulbourke.net/papers/triangulate/).

### Performance Characteristics ###
Prior to triangulation, the vertices are sorted using a Hilbert Space-Filling curve (http://en.wikipedia.org/wiki/Hilbert_curve). Since
our locate method walks the triangulation, linearizing the points with a
space-filling curve gives us some pretty good locality when adding each
vertex, thus greatly reducing the number of hops required to locate the
vertex. The sort is O(n log n), but is fast since hilbert indices are
computed in O(h) (where h is a small constant), and results in a
triangulation asymptotic running time of O(n) for non-diabolical cases.

### Delaunay Tessellation Field Estimator ###
The `DtfeTriangulationMap` class performs the **Delaunay Tessellation Field Estimator** (DTFE) (http://en.wikipedia.org/wiki/Delaunay_tessellation_field_estimator) in two dimensions, which enables the reconstruction of the continuous density field from a set of points.

The DTFE is simple to understand:

1. Construct a triangulation of the points.
2. For each vertex, compute its density with the formula: density = point_mass / sum_of_area_of_neighboring_triangles
3. To reconstruct the continuous field, interpolate the density using the vertex densities.

There are several methods for interpolation, which are included in the `InterpolationStrategies` class.