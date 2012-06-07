package org.delaunay.dtfe;

public class BasicDensityModel implements DensityModel {
	protected double density = 0.0;
	protected double weight = 1.0;

	public BasicDensityModel() {
		this(1.0);
	}

	public BasicDensityModel(double weight) {
		this.weight = weight;
	}

	public double getDensity() {
		return density;
	}

	public void setDensity(double d) {
		this.density = d;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getWeight() {
		return weight;
	}
}
