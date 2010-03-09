package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.metaanalysis.NetworkMetaAnalysis;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.SelectableStudyGraphModel;
import org.drugis.addis.presentation.StudyGraphModel;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.addis.presentation.StudyGraphModel.Edge;
import org.drugis.addis.presentation.StudyGraphModel.Vertex;
import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.jags.JagsModelFactory;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public class NetworkMetaAnalysisWizardPM extends AbstractMetaAnalysisWizardPM<SelectableStudyGraphModel>{
	private DrugSelectionCompleteListener d_connectedDrugsSelectedModel;
	private StudyGraphModel d_selectedStudyGraph;
	private ValueHolder<Boolean> d_studySelectionCompleteModel;

	public NetworkMetaAnalysisWizardPM(Domain d, PresentationModelFactory pmm) {
		super(d, pmm);
		d_selectedStudyGraph = new StudyGraphModel(getSelectedStudiesModel(), 
				getSelectedDrugsModel());
		d_studyGraphPresentationModel.getSelectedDrugsModel().addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				updateArmHolders();
			}
		});
		d_studySelectionCompleteModel = new StudySelectionCompleteListener();
	}

	@Override
	protected void buildDrugHolders() {
		d_connectedDrugsSelectedModel = new DrugSelectionCompleteListener();
		d_studyGraphPresentationModel.getSelectedDrugsModel().addValueChangeListener(d_connectedDrugsSelectedModel);
	}

	public ListHolder<Drug> getSelectedDrugsModel() {
		return d_studyGraphPresentationModel.getSelectedDrugsModel();
	}
	
	public StudyGraphModel getSelectedStudyGraphModel(){
		return d_selectedStudyGraph;
	}

	@Override
	protected SelectableStudyGraphModel buildStudyGraphPresentation() {
		return new SelectableStudyGraphModel(d_indicationHolder, d_endpointHolder, d_drugListHolder, d_domain);
	}
	
	public ValueModel getConnectedDrugsSelectedModel() {
		return d_connectedDrugsSelectedModel;
	}
	
	public ValueHolder<Boolean> getStudySelectionCompleteModel() {
		return d_studySelectionCompleteModel;
	}
	
	@SuppressWarnings("serial")
	public class StudySelectionCompleteListener extends AbstractValueModel 
	implements ValueHolder<Boolean> {
		private boolean d_value;
		
		public StudySelectionCompleteListener() {
			update();
			getSelectedStudyGraphModel().addGraphListener(new GraphListener<Vertex, Edge>() {
				
				public void vertexRemoved(GraphVertexChangeEvent<Vertex> e) {
					update();
				}
				
				public void vertexAdded(GraphVertexChangeEvent<Vertex> e) {
					update();
				}
				
				public void edgeRemoved(GraphEdgeChangeEvent<Vertex, Edge> e) {
					update();
				}
				
				public void edgeAdded(GraphEdgeChangeEvent<Vertex, Edge> e) {
					update();
				}
			});
		}
		
		private void update() {
			Boolean oldValue = d_value;
			Boolean newValue = selectedStudiesConnected();
			if (oldValue != newValue) {
				d_value = newValue;
				fireValueChange(oldValue, newValue);
			}
		}

		public Boolean getValue() {
			return d_value;
		}

		public void setValue(Object newValue) {
			throw new RuntimeException();
		}
	}
	
	private boolean selectedStudiesConnected() {
		ConnectivityInspector<Vertex, Edge> inspectorGadget = 
			new ConnectivityInspector<Vertex, Edge>(getSelectedStudyGraphModel());
		return inspectorGadget.isGraphConnected();
	}
	
	@SuppressWarnings("serial")
	private class DrugSelectionCompleteListener extends ModifiableHolder<Boolean> implements PropertyChangeListener {
		public DrugSelectionCompleteListener() {
			setValue(false);
		}
		
		@SuppressWarnings("unchecked")
		public void propertyChange(PropertyChangeEvent evt) {
			List<Drug> selectedDrugs = (List<Drug>) evt.getNewValue();	
			setValue(selectedDrugs.size() > 1 && d_studyGraphPresentationModel.isSelectionConnected());			
		}
	}

	public NetworkMetaAnalysis createMetaAnalysis(String name) {
		Indication indication = getIndicationModel().getValue();
		OutcomeMeasure om = getOutcomeMeasureModel().getValue();
		List<? extends Study> studies = getSelectedStudiesModel().getValue();
		List<Drug> drugs = getSelectedDrugsModel().getValue();
		Map<Study, Map<Drug, Arm>> armMap = getArmMap();
		InconsistencyModel model = createInconsistencyModel();
		
		return new NetworkMetaAnalysis(name, indication, om, studies, drugs, armMap, model);
	}

	private InconsistencyModel createInconsistencyModel() {
		NetworkBuilder builder = new NetworkBuilder();
        for(Study s : getSelectedStudiesModel().getValue()){
			for (Drug d : getSelectedDrugsModel().getValue()) {
				for (Variable v : s.getVariables(Endpoint.class)) {
					if(! s.getDrugs().contains(d))
						break;
					Arm a = getSelectedArmModel(s, d).getValue();
//					TODO: this check must be changed after meta analysis can be done over other measurements as well 
					if(! (s.getMeasurement(v, a) instanceof BasicRateMeasurement)) 
						break;
					BasicRateMeasurement m = (BasicRateMeasurement)s.getMeasurement(v, a);	
					builder.add(s.getId(), a.getDrug().getName(), m.getRate(), m.getSampleSize());
				}
        	}
        }
        InconsistencyModel model = (new JagsModelFactory()).getInconsistencyModel(builder.buildNetwork());
		return model;
	}

	private Map<Study, Map<Drug, Arm>> getArmMap() {
		Map<Study, Map<Drug, Arm>> map = new HashMap<Study, Map<Drug,Arm>>();
		for (Study s : d_selectedArms.keySet()) {
			map.put(s, new HashMap<Drug, Arm>());
			for (Drug d : d_selectedArms.get(s).keySet()) {
				map.get(s).put(d, d_selectedArms.get(s).get(d).getValue());
			}
		}
		return map;
	}

	public ListHolder<Study> getSelectedStudiesModel() {
		return getStudyListModel().getSelectedStudiesModel();
	}
}
