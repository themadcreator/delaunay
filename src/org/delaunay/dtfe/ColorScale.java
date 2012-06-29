package org.delaunay.dtfe;

import java.awt.Color;
import java.util.TreeSet;

import com.google.common.collect.Sets;

public class ColorScale{
	private final TreeSet<ColorScale.Stop> stops = Sets.newTreeSet();
	
	public static class Stop implements Comparable<ColorScale.Stop>{
		int a,r,g,b;
		int argb;
		Double offset;
		
		private Stop(int argb, Double offset) {
			this.argb = argb;
			this.a = (argb >> 24) & 0xFF;
			this.r = (argb >> 16) & 0xFF;
			this.g = (argb >> 8) & 0xFF;
			this.b = argb & 0xFF;
			this.offset = offset;
		}

		public int compareTo(ColorScale.Stop other) {
			return offset.compareTo(other.offset);
		}
	}

	public ColorScale stop(int argb, double value) {
		stops.add(new Stop(argb, value));
		return this;
	}

	public ColorScale stopAlpha(int rgb, int alpha, double value) {
		stops.add(new Stop((rgb & 0xFFFFFF) | (alpha << 24), value));
		return this;
	}
	
	public Color get(Double value){
		if(stops.first().offset.compareTo(value) >= 0){
			return new Color(stops.first().argb, true);
		} else if(stops.last().offset.compareTo(value) <= 0){
			return new Color(stops.last().argb, true);
		} else{
			ColorScale.Stop s0 = stops.headSet(new Stop(0, value)).last();
			ColorScale.Stop s1 = stops.tailSet(new Stop(0, value)).first();
			double t = (value - s0.offset) / (s1.offset - s0.offset);
			int a = 0xFF & (int)(s0.a * (1.0 - t) + s1.a * t);
			int r = 0xFF & (int)(s0.r * (1.0 - t) + s1.r * t);
			int g = 0xFF & (int)(s0.g * (1.0 - t) + s1.g * t);
			int b = 0xFF & (int)(s0.b * (1.0 - t) + s1.b * t);
			int argb = (a << 24) | (r << 16) | (g << 8) | b;
			return new Color(argb, true);
		}
	}

	public double getMinStop() {
		return stops.first().offset;
	}

	public double getMaxStop() {
		return stops.last().offset;
	}
}