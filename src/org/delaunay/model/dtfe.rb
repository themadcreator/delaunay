require './voronoi'

module Delaunay
  class Vertex
    attr_accessor :density, :weight
    def compute_density
      @weight = 1.0 if @weight.nil?
      area = 0
      @neighbor_triangles.each do |t|
        area = (t.c - t.a).cross(t.b - t.a) / 2.0 + area
      end
      @density = @weight / area unless area == 0
    end
  end

  class Triangulation
    def point_density(v)
      # doing a locate first is faster than exception handling
      t = locate(v)
      return nil if t.nil?
      begin
        return natural_neighbor_interpolation(v)
      rescue InvalidVertexException
        return nil
      end
      #      return t.first_order_interpolation(v)
      #      return t.max_neighbor_interpolation(v)
    end

    def natural_neighbor_interpolation(vertex)
      vor = second_order_voronoi(vertex)
      area = 0.0
      vor.neighbor_vertices.each do |v|
        area += vor.intersect(v.voronoi).area * v.density
      end
      return area / vor.area
    end
    
    def second_order_voronoi(vertex)
      cavity_tris = get_circumcircle_triangles(vertex)
      tris = create_triangles(get_edge_set(cavity_tris), vertex)
      verts = tris.collect{ |t| t.vertices }.flatten.to_set
      verts.delete(vertex)
      return Voronoi.new(vertex, verts, tris)
    end
  end

  class Triangle
    def max_neighbor_interpolation()
      return t.vertices.collect {|v| v.density }.max
    end

    def first_order_interpolation(v)
      @an = b.normal_to(c).normalize if @an.nil?
      @bn = c.normal_to(a).normalize if @bn.nil?
      @cn = a.normal_to(b).normalize if @cn.nil?
      norms = [@an, @bn, @cn]

      @a_max = @an.dot(b - a) if @a_max.nil?
      @b_max = @bn.dot(c - b) if @b_max.nil?
      @c_max = @cn.dot(a - c) if @c_max.nil?
      maxes = [@a_max, @b_max, @c_max]

      ds = norms.zip(vertices).collect { |n,vv| n.dot(vv - v).abs }
      coeffs = ds.zip(maxes).collect { |d,m| 1.0 - d / m}
      dens = coeffs.zip(vertices).collect { |c, vv| c * vv.density }
      density = dens.inject { |sum, x| sum + x }
      return density
    end
    
  end
end