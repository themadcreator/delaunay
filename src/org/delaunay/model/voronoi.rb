module Delaunay
  class ConvexPolygon
    attr_accessor :vertices
    
    def initialize(verts)
      @vertices = verts.dup
    end

    def intersect(o)
      # http://en.wikipedia.org/wiki/Sutherland%E2%80%93Hodgman_algorithm

      output = o.vertices.dup
      @vertices.each_index do |i|
        # Clipping edge a-b
        a = @vertices[i - 1]
        b = @vertices[i]

        input = output.dup
        output = []

        input.each_index do |j|
          # Test edge c-d
          c = input[j - 1]
          d = input[j]

          if(c.orientation(a,b) >= 0)
            output << c
            if(d.orientation(a,b) < 0)
              output << intersection(a,b,c,d)
            end
          elsif (d.orientation(a,b) >= 0)
            output << intersection(a,b,c,d)
          end
        end
      end

      return ConvexPolygon.new(output)
    end

    # Returns the vector that is the intersetion of the lines defined by vectors s-e and a-b
    # This does not check for parallel lines since we only use it when we know they are not parallel
    # http://bloggingmath.wordpress.com/2009/05/29/line-segment-intersection/
    def intersection(a,b, c,d)
      p, r = a, b - a
      q, s = c, d - c
      t = (q - p).cross(s) / r.cross(s)
      return p + (r * t)
    end

    def area
      # lazy init
      return @area unless @area.nil?

      @area = 0.0
      c = @vertices.first
      @vertices.each_cons(2) do |a, b|
        next if a == c
        # compute area of triangle defined by vectors a, b, c
        # http://stackoverflow.com/questions/3477775/converting-ruby-array-to-array-of-consecutive-pairs
        @area += (a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y)).abs / 2.0
      end
      return @area
    end
  end

  class Voronoi < ConvexPolygon
    attr_accessor :neighbor_vertices
    
    def initialize(vert, verts, tris)
      @neighbor_vertices = verts.sort { |a,b| b.orientation(a, vert) }
      @vertices = []
      @neighbor_vertices.each_index do |i|
        a = @neighbor_vertices[i - 1]
        b = @neighbor_vertices[i]
        t = tris.select{ |t| t.vertices.include?(a) && t.vertices.include?(b) }.first
        @vertices << t.circum_center
      end
    end
  end

  class Vertex
    def voronoi
      # lazy init
      return @voronoi unless @voronoi.nil?
      
      @voronoi = Voronoi.new(self, @neighbor_vertices.to_a, @neighbor_triangles)
      return @voronoi
    end
  end
end