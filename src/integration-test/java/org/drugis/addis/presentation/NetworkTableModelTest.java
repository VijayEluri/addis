package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.LogContinuousMeasurementEstimate;
import org.drugis.addis.entities.metaanalysis.NetworkMetaAnalysis;
import org.drugis.mtc.Estimate;
import org.drugis.mtc.Treatment;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.PresentationModel;

public class NetworkTableModelTest {

	
	private PresentationModelFactory d_pmf;
	private NetworkTableModel d_tableModel;
	private NetworkMetaAnalysis d_analysis;

	@Before
	public void setUp() {
		DomainImpl domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		d_analysis = ExampleData.buildNetworkMetaAnalysis();
		d_pmf = new PresentationModelFactory(domain);
		d_tableModel = new NetworkTableModel((NetworkMetaAnalysisPresentation)d_pmf.getModel(d_analysis), d_pmf, d_analysis.getInconsistencyModel());
	}
	
	@Test
	public void testGetColumnCount() {
		assertEquals(d_analysis.getIncludedDrugs().size(), d_tableModel.getColumnCount());
	}

	@Test
	public void testGetRowCount() {
		assertEquals(d_analysis.getIncludedDrugs().size(), d_tableModel.getRowCount());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testValueAt() {
		for(int x = 0; x < d_tableModel.getColumnCount(); ++x) {
			for(int y = 0; y < d_tableModel.getRowCount(); ++y) {
				if(x == y){
					assertEquals(d_analysis.getIncludedDrugs().get(x), ((PresentationModel<Drug>) d_tableModel.getValueAt(x, y)).getBean());
					assertEquals(null, d_tableModel.getDescriptionAt(x, y));
				} else {
					assertEquals("n/a", ((PresentationModel<LogContinuousMeasurementEstimate>) d_tableModel.getValueAt(x, y)).getBean().toString());
					String expected = "\""+d_analysis.getIncludedDrugs().get(y)+"\" relative to \""+d_analysis.getIncludedDrugs().get(x)+"\"";
					assertEquals(expected, d_tableModel.getDescriptionAt(x, y));
				}
			}
		}	
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueAtAfterModelRun() {
		d_analysis.getInconsistencyModel().run();

		for(int x = 0; x < d_analysis.getIncludedDrugs().size(); ++x){
			for(int y = 0; y < d_analysis.getIncludedDrugs().size(); ++y){
				if(x == y){
					assertEquals(d_analysis.getIncludedDrugs().get(x), ((PresentationModel<Drug>) d_tableModel.getValueAt(x, y)).getBean());
				} else {
					Treatment t1 = d_analysis.getBuilder().getTreatment(d_analysis.getIncludedDrugs().get(x).getName());
					Treatment t2 = d_analysis.getBuilder().getTreatment(d_analysis.getIncludedDrugs().get(y).getName());
					Estimate relEffect = d_analysis.getInconsistencyModel().getRelativeEffect(t1, t2);
					assertEquals(new LogContinuousMeasurementEstimate(relEffect.getMean(), relEffect.getStandardDeviation()).toString(), ((PresentationModel<LogContinuousMeasurementEstimate>) d_tableModel.getValueAt(x, y)).getBean().toString());
				}
			}
		}	
	}
}