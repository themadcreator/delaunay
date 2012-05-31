package org.delaunay.algorithm;

import java.awt.Point;


public class HilbertTableIndex {
	/**
	 * @see http://blog.notdot.net/2009/11/Damn-Cool-Algorithms-Spatial-indexing-with-Quadtrees-and-Hilbert-Curves
	 * 
	 * <pre>
	 * Coding system applied to the four orientations of the unit shape:
	 * 
	 * 
	 *  square:    0       1       2       3
	 *  
	 *           1--2    1--0    3--2    3  0
	 *  index:   |  |    |          |    |  |
	 *           0  3    2--3    0--1    2--1
	 *           
	 *           0--0    1--3    3--2    2  1
	 *  sub-sq:  |  |    |          |    |  |
	 *           2  1    1--0    0--2    3--3
	 *           
	 *  char:      A       C       D       U
	 *           
	 *           A--A    C--U    U--D    D  C
	 *  sub-sq:  |  |    |          |    |  |
	 *           D  C    C--A    A--D    U--U
	 * 
	 * </pre>
	 */

	private static final int[][] squares = new int[4][];
	private static final int[][] indexes = new int[4][];
	private static final int[][] reverse = new int[4][];
	private static final int[][] revsqrs = new int[4][];
	static{
		// using i = xb<<1 | yb
		squares[0] = new int[]{2,0,1,0};
		squares[1] = new int[]{1,1,0,3};
		squares[2] = new int[]{0,3,2,2};
		squares[3] = new int[]{3,2,3,1};
		
		indexes[0] = new int[]{0,1,3,2};
		indexes[1] = new int[]{2,1,3,0};
		indexes[2] = new int[]{0,3,1,2};
		indexes[3] = new int[]{2,3,1,0};
		
		reverse[0] = new int[]{0,1,3,2};
		reverse[1] = new int[]{3,1,0,2};
		reverse[2] = new int[]{0,2,3,1};
		reverse[3] = new int[]{3,2,0,1};
		
		revsqrs[0] = new int[]{2,0,0,1};
		revsqrs[1] = new int[]{3,1,1,0};
		revsqrs[2] = new int[]{0,2,2,3};
		revsqrs[3] = new int[]{1,3,3,2};
	}

	private final int order;
	private final int startsquare;

	public HilbertTableIndex(final int order) {
		this(order,0);
	}
	
	public HilbertTableIndex(final int order, final int startsquare) {
		if(order <= 0) throw new IllegalArgumentException("order must be > 0");
		this.order = order;
		this.startsquare = startsquare;
	}

	public int getIndex(Point p) {
		final int x = p.x;
		final int y = p.y;
		if (x < 0 || x >= 1 << order || y < 0 || y >= 1 << order) return -1;
		
		int index = 0;
		int sq = startsquare;
		int o = order - 1;
		while (o >= 0) {
			int i = ((((x >> o) & 1) << 1) | ((y >> o) & 1));
			index = (index << 2) | indexes[sq][i];
			sq = squares[sq][i];
			o--;
		}
		
		return index;
	}

	public Point getPoint(int index) {
		if (index < 0 || index > (1 << (2*order))) return null;
		
		int x = 0;
		int y = 0;
		int sq = startsquare;
		int o = order - 1;
		while (o >= 0) {
			int i = (index >> (o * 2) & 3);
			x = x << 1 | (reverse[sq][i] >> 1 & 1);
			y = y << 1 | (reverse[sq][i] & 1);
			sq = revsqrs[sq][i];
			o--;
		}
		return new Point(x, y);
	}

}
