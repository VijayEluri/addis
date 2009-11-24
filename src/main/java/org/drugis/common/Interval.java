package org.drugis.common;

import java.io.Serializable;

public class Interval<N extends Number> implements Serializable {
	private static final long serialVersionUID = 1816274850902350090L;
	
	private N d_lowerBound;
	private N d_upperBound;
	
	public Interval(N lowerBound, N upperBound) {
		d_lowerBound = lowerBound;
		d_upperBound = upperBound;
	}
	
	public N getLowerBound() {
		return d_lowerBound;
	}
	
	public N getUpperBound() {
		return d_upperBound;
	}
	
	public double getLength() {
		return d_upperBound.doubleValue() - d_lowerBound.doubleValue();
	}
}
