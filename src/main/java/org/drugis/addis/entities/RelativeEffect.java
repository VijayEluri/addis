package org.drugis.addis.entities;

import org.drugis.common.Interval;

public interface RelativeEffect<T extends Measurement> {

	public enum AxisType {
		LINEAR,
		LOGARITHMIC;
	}
	
	public T getSubject();

	public T getBaseline();

	public Endpoint getEndpoint();

	public Integer getSampleSize();

	/**
	 * Get the 95% confidence interval.
	 * @return The confidence interval.
	 */
	public Interval<Double> getConfidenceInterval();

	public Double getRelativeEffect();

	public Double getError();
	
	public String getName();
	
	public AxisType getAxisType();
}