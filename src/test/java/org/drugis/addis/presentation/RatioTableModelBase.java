package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.PatientGroup;
import org.drugis.addis.entities.AbstractRatio;
import org.drugis.addis.entities.Study;
import org.junit.Test;

import com.jgoodies.binding.PresentationModel;

public abstract class RatioTableModelBase {
	protected Study d_standardStudy;
	protected Study d_threeArmStudy;
	protected AbstractRatioTableModel d_stdModel;
	protected RelativeEffectTableModel d_threeArmModel;
	protected Endpoint d_endpoint;
	protected PresentationModelFactory d_pmf;
	protected Class<? extends AbstractRatio> d_ratioClass;

	@Test
	public void testGetColumnCount() {
		assertEquals(d_standardStudy.getPatientGroups().size(), d_stdModel.getColumnCount());
		assertEquals(d_threeArmStudy.getPatientGroups().size(), d_threeArmModel.getColumnCount());
	}

	@Test
	public void testGetRowCount() {
		assertEquals(d_standardStudy.getPatientGroups().size(), d_stdModel.getRowCount());
		assertEquals(d_threeArmStudy.getPatientGroups().size(), d_threeArmModel.getRowCount());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueAtUpperRightPart() {
		assertEquals(3, d_threeArmStudy.getPatientGroups().size());
		PatientGroup pg0 = d_threeArmStudy.getPatientGroups().get(0);
		PatientGroup pg1 = d_threeArmStudy.getPatientGroups().get(1);
		PatientGroup pg2 = d_threeArmStudy.getPatientGroups().get(2);
		
		PresentationModel<AbstractRatio> val01 = (PresentationModel<AbstractRatio>)d_threeArmModel.getValueAt(0, 1);
		assertTrue(d_ratioClass.isInstance(val01.getBean()));
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg0), val01.getBean().getDenominator());
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg1), val01.getBean().getNumerator());
		
		PresentationModel<AbstractRatio> val12 = (PresentationModel<AbstractRatio>)d_threeArmModel.getValueAt(1, 2);
		assertTrue(d_ratioClass.isInstance(val12.getBean()));
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg1), val12.getBean().getDenominator());
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg2), val12.getBean().getNumerator());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueAtLowerLeftPart() {
		assertEquals(3, d_threeArmStudy.getPatientGroups().size());
		PatientGroup pg0 = d_threeArmStudy.getPatientGroups().get(0);
		PatientGroup pg1 = d_threeArmStudy.getPatientGroups().get(1);
		PatientGroup pg2 = d_threeArmStudy.getPatientGroups().get(2);
		
		PresentationModel<AbstractRatio> val20 = (PresentationModel<AbstractRatio>)d_threeArmModel.getValueAt(2, 0);
		assertTrue(d_ratioClass.isInstance(val20.getBean()));
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg2), val20.getBean().getDenominator());
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg0), val20.getBean().getNumerator());
		
		PresentationModel<AbstractRatio> val21 = (PresentationModel<AbstractRatio>)d_threeArmModel.getValueAt(2, 1);
		assertTrue(d_ratioClass.isInstance(val21.getBean()));
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg2), val21.getBean().getDenominator());
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg1), val21.getBean().getNumerator());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueAtDiagonal() {
		for (int i = 0; i < d_standardStudy.getPatientGroups().size(); ++i) {
			Object val = d_stdModel.getValueAt(i, i);
			assertTrue("Instance of PresentationModel", val instanceof PresentationModel);
			assertEquals(((PresentationModel) val).getBean(), d_standardStudy.getPatientGroups().get(i));
		}
		for (int i = 0; i < d_threeArmStudy.getPatientGroups().size(); ++i) {
			Object val = d_threeArmModel.getValueAt(i, i);
			assertTrue("Instance of PresentationModel", val instanceof PresentationModel);
			assertEquals(((PresentationModel) val).getBean(), d_threeArmStudy.getPatientGroups().get(i));
		}
	}

	@Test
	public void testGetDescriptionAtDiagonal() {
		assertNull(d_threeArmModel.getDescriptionAt(1, 1));
	}

	@Test
	public void testGetDescriptionAt() {
		LabeledPresentationModel pg0 = d_pmf.getLabeledModel(d_threeArmStudy.getPatientGroups().get(0));
		LabeledPresentationModel pg1 = d_pmf.getLabeledModel(d_threeArmStudy.getPatientGroups().get(1));
		String expected = "\"" + pg1.getLabelModel().getValue() + "\" relative to \"" +
				pg0.getLabelModel().getValue() + "\"";
		assertEquals(expected, d_threeArmModel.getDescriptionAt(0, 1));
	}

	@Test
	public void testGetDescription() {
		String description = d_threeArmModel.getTitle() + " for \"" + d_threeArmStudy.getId()
				+ "\" on Endpoint \"" + d_endpoint.getName() + "\"";
		assertEquals(description, d_threeArmModel.getDescription());
	}

	protected void baseSetUp() {
		d_standardStudy = ExampleData.buildDefaultStudy2();
		d_threeArmStudy = ExampleData.buildAdditionalStudyThreeArm();
		d_endpoint = ExampleData.buildEndpointHamd();
		DomainImpl domain = new DomainImpl();
		d_pmf = new PresentationModelFactory(domain);
	}

}
