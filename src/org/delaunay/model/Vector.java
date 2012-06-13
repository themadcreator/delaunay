package org.delaunay.model;

import java.awt.geom.Point2D;

public strictfp class Vector {
	public final double x, y;

	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector multiply(double s) {
		return new Vector(x * s, y * s);
	}

	public Vector divide(double s) {
		return new Vector(x / s, y / s);
	}

	public Vector subtract(Vector v) {
		return new Vector(x - v.x, y - v.y);
	}

	public Vector add(Vector v) {
		return new Vector(x + v.x, y + v.y);
	}
	
	public double dot(Vector v){
		return (x * v.x) + (y * v.y);
	}
	
	public double cross(Vector v){
	      return (x * v.y) - (y * v.x);
	}
	
	public double lengthSquared(){
		return dot(this);
	}
	
	public double length(){
		return Math.sqrt(lengthSquared());
	}
	
	public Vector normalize(){
		double l = length();
		return new Vector(x/l, y/l);
	}
	
	public Vector normalTo(Vector v){
		return new Vector(y - v.y, v.x - x);
	}
	
	/*
	 * Does this vector lie on the left or right of ab?
	 *   -1 = left
	 *    0 = on
	 *    1 = right
	 */
	public int orientation(Vector a, Vector b) {
		double det = (a.x - x) * (b.y - y) - (b.x - x) * (a.y - y);
		return (new Double(det).compareTo(0.0));
	}
	
	public Point2D.Double toPoint() {
		return new Point2D.Double(x, y);
	}

	public String toString(){
		return String.format("(%.4f, %.4f)", x, y);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector other = (Vector) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}
}
