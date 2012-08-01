package org.delaunay.algorithm.samples;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Random;

import org.delaunay.algorithm.samples.LocateStrategies.LocateStrategy;
import org.delaunay.algorithm.samples.LocateStrategies.NaiveLocateStrategy;
import org.delaunay.algorithm.samples.SampleFunctions.SampleFunction;
import org.delaunay.model.Vector;

import com.google.common.collect.Lists;

public class SampleBuilder {
	private final List<Vector> samples = Lists.newArrayList();
	private int maxTries = 5;
	private LocateStrategy locateStrategy = new NaiveLocateStrategy();

	public int getMaxTries() {
		return maxTries;
	}

	public SampleBuilder setMaxTries(int maxTries) {
		this.maxTries = maxTries;
		return this;
	}

	public LocateStrategy getLocateStrategy() {
		return locateStrategy;
	}

	public SampleBuilder setLocateStrategy(LocateStrategy locateStrategy) {
		this.locateStrategy = locateStrategy;
		return this;
	}

	public List<Vector> getSamples() {
		return samples;
	}

	public SampleBuilder fill(SampleFunction function) {
		List<Vector> queue = Lists.newArrayList();
		Random random = new Random(System.currentTimeMillis());
		locateStrategy.initialize(samples, function.getBoundingShape().getBounds2D());

		// Generate and add first sample
		Vector firstSample = null;
		while (firstSample == null || !locateStrategy.addSample(firstSample)) {
			firstSample = function.createSampleIn(function.getBoundingShape());
		}
		queue.add(firstSample);
		samples.add(firstSample);

		while (!queue.isEmpty()) {
			// Get random element from the queue.
			int queueIndex = random.nextInt(queue.size());
			Vector sample = queue.get(queueIndex);
			
			// Attempt to a create new valid sample near the existing sample.
			Vector newValidSample = createNewValidSample(function, sample);

			// Add the new valid sample or remove the existing sample from the queue.
			if (newValidSample != null && locateStrategy.addSample(newValidSample)) {
				queue.add(newValidSample);
				samples.add(newValidSample);
			} else {
				queue.remove(queueIndex);
			}
		}
		return this;
	}

	private Vector createNewValidSample(SampleFunction function, Vector sample) {
		for (int i = 0; i < maxTries; i++) {
			Vector newSample = function.createSampleNear(sample);
			if (isValid(function, newSample)) {
				return newSample;
			}
		}
		return null;
	}

	private boolean isValid(SampleFunction function, final Vector v) {
		Vector nearest = locateStrategy.getNearest(v);
		if (nearest == null) {
			return false;
		}

		Rectangle2D bounds = function.getBoundingShape().getBounds2D();
		if (!bounds.contains(v.x, v.y)) {
			return false;
		}

		double minDist = function.getMimimumDistance(nearest);
		return (nearest.subtract(v).lengthSquared() > (minDist * minDist));
	}
}
