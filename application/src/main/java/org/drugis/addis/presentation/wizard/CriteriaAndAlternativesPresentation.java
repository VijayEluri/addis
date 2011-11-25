package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.EntityIdExistsException;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.validation.BooleanAndModel;
import org.drugis.common.validation.ListMinimumSizeModel;
import org.pietschy.wizard.InvalidStateException;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractConverter;
import com.jgoodies.binding.value.ValueModel;

public abstract class CriteriaAndAlternativesPresentation<Alternative extends Comparable<Alternative>> {
	final SelectableOptionsModel<OutcomeMeasure> d_selectedCriteria;
	protected final HashMap<Alternative, ModifiableHolder<Boolean>> d_alternativeEnabledMap;
	final SelectableOptionsModel<Alternative> d_selectedAlternatives;
	protected final ModifiableHolder<AnalysisType> d_analysisTypeHolder;
	protected final HashMap<OutcomeMeasure, ModifiableHolder<Boolean>> d_criteriaEnabledMap;
	protected final ModifiableHolder<Alternative> d_baselineModel;
	protected final ValueHolder<Indication> d_indicationModel;
	protected final ValueModel d_completeModel;
	protected final ObservableList<Alternative> d_availableAlternatives;
	protected final ListDataListener d_resetAlternativeEnabledModelsListener;

	public CriteriaAndAlternativesPresentation(final ValueHolder<Indication> indication, final ModifiableHolder<AnalysisType> analysisType) {
		d_indicationModel = indication;
		d_analysisTypeHolder = analysisType;
		d_selectedCriteria = new SelectableOptionsModel<OutcomeMeasure>();
		d_alternativeEnabledMap = new HashMap<Alternative, ModifiableHolder<Boolean>>();
		d_selectedAlternatives = new SelectableOptionsModel<Alternative>();
		d_criteriaEnabledMap = new HashMap<OutcomeMeasure, ModifiableHolder<Boolean>>();
		d_baselineModel = new ModifiableHolder<Alternative>();
		d_availableAlternatives = new ArrayListModel<Alternative>();
		
		PropertyChangeListener resetValuesListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				reset();
			}
		};

		d_indicationModel.addValueChangeListener(resetValuesListener);
		d_analysisTypeHolder.addValueChangeListener(resetValuesListener);
		
		d_resetAlternativeEnabledModelsListener = new ListDataListener() {
			public void contentsChanged(ListDataEvent e) {
				updateAlternativesEnabled();
			}
			public void intervalAdded(ListDataEvent e) {
				updateAlternativesEnabled();
			}
			public void intervalRemoved(ListDataEvent e) {
				updateAlternativesEnabled();
			}
		};
		getSelectedCriteria().addListDataListener(d_resetAlternativeEnabledModelsListener);
		getSelectedAlternatives().addListDataListener(d_resetAlternativeEnabledModelsListener);
		
		ListDataListener resetCriteriaEnabledModels = new ListDataListener() {
			public void contentsChanged(ListDataEvent e) {
				updateCriteriaEnabled();
			}
			public void intervalAdded(ListDataEvent e) {
				updateCriteriaEnabled();			
			}
			public void intervalRemoved(ListDataEvent e) {
				updateCriteriaEnabled();
			}
		};
		getSelectedCriteria().addListDataListener(resetCriteriaEnabledModels);
		
		
		AbstractConverter baselineValidModel = new AbstractConverter(d_baselineModel) {
			private static final long serialVersionUID = -8879640617811142054L;

			@Override
			public void setValue(Object newValue) {
			}
			
			@Override
			public Boolean convertFromSubject(Object subjectValue) {
				return getSelectedAlternatives().contains(subjectValue);
			}
		};
		
		d_completeModel = new BooleanAndModel(Arrays.<ValueModel>asList(
				new ListMinimumSizeModel(getSelectedAlternatives(), 2),
				new ListMinimumSizeModel(getSelectedCriteria(), 2),
				baselineValidModel));
		
	}
	
	protected abstract void reset();

	public abstract ObservableList<OutcomeMeasure> getCriteriaListModel();
	
	public ValueHolder<Boolean> getCriterionSelectedModel(OutcomeMeasure om) {
		return d_selectedCriteria.getSelectedModel(om);
	}
	
	public ValueHolder<Boolean> getAlternativeSelectedModel(Alternative alternative) {
		return d_selectedAlternatives.getSelectedModel(alternative);
	}
	
	public ValueHolder<Boolean> getAlternativeEnabledModel(Alternative e) {
		return d_alternativeEnabledMap.get(e);
	}
	
	public ValueHolder<Boolean> getCriterionEnabledModel(OutcomeMeasure out) {
		return d_criteriaEnabledMap.get(out);
	}

	public abstract ValueModel getCompleteModel();
	
	public BenefitRiskAnalysis<Alternative> saveAnalysis(Domain domain, String id) throws InvalidStateException, EntityIdExistsException {
		BenefitRiskAnalysis<Alternative> brAnalysis = createAnalysis(id);

		if(domain.getBenefitRiskAnalyses().contains(brAnalysis))
			throw new EntityIdExistsException("Benefit Risk Analysis with this ID already exists in domain");

		domain.getBenefitRiskAnalyses().add(brAnalysis);
		return brAnalysis;
	}
	
	public abstract BenefitRiskAnalysis<Alternative> createAnalysis(String id) throws InvalidStateException;

	public ObservableList<Alternative> getSelectedAlternatives() {
		return d_selectedAlternatives.getSelectedOptions();
	}
	
	public ObservableList<OutcomeMeasure> getSelectedCriteria() {
		return d_selectedCriteria.getSelectedOptions();
	}

	public ValueModel getBaselineModel() {
		return d_baselineModel;
	}
	
	protected void initCriteria() {
		for (OutcomeMeasure om : getCriteriaListModel()) {
			d_selectedCriteria.addOption(om, false);
		}
		// create outcome enabled models
		for (OutcomeMeasure om : getCriteriaListModel()) {
			d_criteriaEnabledMap.put(om, new ModifiableHolder<Boolean>(getCriterionShouldBeEnabled(om)));
		}
	}

	protected void initAlternatives(Collection<Alternative> alternatives) {
		d_availableAlternatives.addAll(alternatives);
		
		// create alternative selected models
		for (Alternative alt : alternatives) {
			d_selectedAlternatives.addOption(alt, false);
		}
		// create alternative enabled models (they use the selected models -- don't merge the loops!)
		for (Alternative alt : alternatives) {
			d_alternativeEnabledMap.put(alt, new ModifiableHolder<Boolean>(getAlternativeShouldBeEnabled(alt)));
		}
	}
	
	protected boolean getAlternativeShouldBeEnabled(Alternative alt) {
		if (getAlternativeSelectedModel(alt).getValue() == true) {
			return true;
		}
		if (d_analysisTypeHolder.getValue() == AnalysisType.LyndOBrien) { 
			return getSelectedAlternatives().size() < 2;
		}
		return true;
	}

	private boolean getCriterionShouldBeEnabled(OutcomeMeasure crit) {
		if (getCriterionSelectedModel(crit).getValue() == true) {
			return true;
		}
		if (d_analysisTypeHolder.getValue() == AnalysisType.LyndOBrien) {
			return getSelectedCriteria().size() < 2;
		}
		return true;
	}
	
	private void updateCriteriaEnabled() {
		for (Entry<OutcomeMeasure, ModifiableHolder<Boolean>> e : d_criteriaEnabledMap.entrySet()) {
			boolean enabled = getCriterionShouldBeEnabled(e.getKey());
			e.getValue().setValue(enabled);
			if (!enabled && getCriterionSelectedModel(e.getKey()).getValue()) {
				getCriterionSelectedModel(e.getKey()).setValue(false);
			}
		}
	}

	private void updateAlternativesEnabled() {
		for (Entry<Alternative, ModifiableHolder<Boolean>> e : d_alternativeEnabledMap.entrySet()) {
			boolean enabled = getAlternativeShouldBeEnabled(e.getKey());
			e.getValue().setValue(enabled);
			if (!enabled && getAlternativeSelectedModel(e.getKey()).getValue()) {
				getAlternativeSelectedModel(e.getKey()).setValue(false);
			}
		}
	}

	public ObservableList<Alternative> getAlternativesListModel() {
		return d_availableAlternatives;
	}
}