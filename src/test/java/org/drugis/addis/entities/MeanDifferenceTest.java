package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.entities.Endpoint.Type;
import org.drugis.common.StudentTTable;
import org.junit.Before;
import org.junit.Test;

public class MeanDifferenceTest {
	private static final double s_mean1 = 0.2342;
	private static final double s_mean2 = 4.7811;
	private static final double s_stdDev1 = 0.2;
	private static final double s_stdDev2 = 2.5;
	private static final int s_subjSize = 35;
	private static final int s_baslSize = 41;
	private MeanDifference d_md;
	private BasicContinuousMeasurement d_subject;
	private BasicContinuousMeasurement d_baseline;
	
	@Before
	public void setUp() {
		Endpoint e = new Endpoint("E", Type.CONTINUOUS);
		PatientGroup subjs = new BasicPatientGroup(null, null, null, s_subjSize);
		PatientGroup basels = new BasicPatientGroup(null, null, null, s_baslSize);
		d_subject = new BasicContinuousMeasurement(e, s_mean1, s_stdDev1, subjs);
		d_baseline = new BasicContinuousMeasurement(e, s_mean2, s_stdDev2, basels);
		d_md = new MeanDifference(d_baseline, d_subject);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructorThrowsException() {
		Endpoint e2 = new Endpoint("E2", Type.CONTINUOUS);
		PatientGroup subjs = new BasicPatientGroup(null, null, null, s_subjSize);
		ContinuousMeasurement subject = new BasicContinuousMeasurement(e2, s_mean1, s_stdDev1, subjs);
		new MeanDifference(d_baseline, subject);
	}
	
	@Test
	public void testGetMean() {
		assertEquals(s_mean1 - s_mean2, d_md.getRelativeEffect(),0.0001);
	}
	
	@Test
	public void testGetError() {
		double expected = Math.sqrt(square(s_stdDev1) / (double) s_subjSize + square(s_stdDev2) / (double) s_baslSize);
		assertEquals(expected, d_md.getError(),0.0001);
	}

	@Test
	public void testGetCI() {
		double t = StudentTTable.getT(s_subjSize + s_baslSize - 2);
		double upper = d_md.getRelativeEffect() + t*d_md.getError();
		double lower = d_md.getRelativeEffect() - t*d_md.getError();
		assertEquals(upper, d_md.getConfidenceInterval().getUpperBound(), 0.0001);
		assertEquals(lower, d_md.getConfidenceInterval().getLowerBound(), 0.0001);
	}
	
	@Test
	public void testGetEndpoint() {
		assertEquals(d_subject.getEndpoint(),d_md.getEndpoint());
		assertEquals(d_baseline.getEndpoint(),d_md.getEndpoint());
	}
	
	@Test
	public void testGetSampleSize() {
		int expected = s_subjSize + s_baslSize;
		assertEquals(expected, (int) d_md.getSampleSize());
	}
	
	private double square(double x) {
		return x*x;
	}
}