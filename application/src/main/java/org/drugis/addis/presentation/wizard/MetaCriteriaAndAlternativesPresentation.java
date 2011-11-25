package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.beans.AbstractObservable;
import org.drugis.common.beans.ContentAwareListModel;
import org.drugis.common.beans.FilteredObservableList;
import org.drugis.common.beans.SortedSetModel;
import org.drugis.common.beans.TransformedObservableList;
import org.drugis.common.beans.FilteredObservableList.Filter;
import org.drugis.common.beans.TransformedObservableList.Transform;
import org.drugis.common.validation.BooleanAndModel;
import org.pietschy.wizard.InvalidStateException;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public class MetaCriteriaAndAlternativesPresentation extends CriteriaAndAlternativesPresentation<DrugSet> {
	private final class AutoSelectMetaAnalysisListener implements
			ListDataListener {
		public void intervalRemoved(ListDataEvent e) {
		}

		public void intervalAdded(ListDataEvent e) {
			autoSelectInterval(e.getIndex0(), e.getIndex1());
		}

		public void contentsChanged(ListDataEvent e) {
			autoSelectInterval(e.getIndex0(), e.getIndex1());
		}

		private void autoSelectInterval(int index0, int index1) {
			for (int i = index0; i <= index1; ++i) {
				OutcomeMeasure crit = getSelectedCriteria().get(i);
				CriterionAnalysisPair pair = findCriterionAnalysisPair(crit);
				if (pair.getAnalysis() == null) {
					pair.setAnalysis(getDefaultMetaAnalysis(crit));
				}
			}
		}
	}

	private static class CriterionAnalysisPair extends AbstractObservable {
		public static final String PROPERTY_ANALYSIS = "analysis";
		
		private final OutcomeMeasure d_criterion;
		private ModifiableHolder<MetaAnalysis> d_analysis;
		
		public CriterionAnalysisPair(OutcomeMeasure criterion, MetaAnalysis analysis) {
			d_criterion = criterion;
			d_analysis = new ModifiableHolder<MetaAnalysis>(analysis);
			d_analysis.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					firePropertyChange(PROPERTY_ANALYSIS, evt.getOldValue(), evt.getNewValue());
				}
			});
		}
		
		public OutcomeMeasure getCriterion() {
			return d_criterion;
		}
		
		public MetaAnalysis getAnalysis() {
			return d_analysis.getValue();
		}

		public ValueHolder<MetaAnalysis> getAnalysisModel() {
			return d_analysis;
		}

		public void setAnalysis(MetaAnalysis ma) {
			d_analysis.setValue(ma);
		}
	}
	
	private class CriteriaHaveAnalysisModel extends AbstractValueModel implements ValueModel {
		private static final long serialVersionUID = 5940166304699871730L;
		private boolean d_value;
		
		public CriteriaHaveAnalysisModel() {
			ListDataListener myListener = new ListDataListener() {
				public void intervalRemoved(ListDataEvent e) {
					update();
				}
				public void intervalAdded(ListDataEvent e) {
					update();
				}
				public void contentsChanged(ListDataEvent e) {
					update();
				}
			};
			getSelectedCriteria().addListDataListener(myListener);
			d_selectedMetaAnalysesPairs.addListDataListener(myListener);
		}

		private void update() {
			boolean newValue = calculate();
			if (d_value != newValue) {
				boolean oldValue = d_value;
				d_value = newValue;
				fireValueChange(oldValue, newValue);
			}
		}

		private boolean calculate() {
			for (OutcomeMeasure crit : getSelectedCriteria()) {
				if (getMetaAnalysesSelectedModel(crit).getValue() == null) {
					return false;
				}
			}
			return true;
		}

		public Object getValue() {
			return d_value;
		}

		public void setValue(Object newValue) {
		}
	}
	
	private class OutcomeMeasureFilter implements Filter<MetaAnalysis> {
		private final OutcomeMeasure d_om;
		public OutcomeMeasureFilter(OutcomeMeasure om) {
			d_om = om;
		}
		public boolean accept(MetaAnalysis ma) {
			return ma.getOutcomeMeasure().equals(d_om);
		}
	}

	public static class IndicationFilter implements Filter<MetaAnalysis> {
		private final Indication d_indication;
		public IndicationFilter(Indication i) {
			d_indication = i;
		}
		public boolean accept(MetaAnalysis ma) {
			return ma.getIndication().equals(d_indication);
		}
	}
	
	private final ObservableList<OutcomeMeasure> d_availableCriteria;
	private final ObservableList<OutcomeMeasure> d_metaAnalysesCriteria;
	private final FilteredObservableList<MetaAnalysis> d_metaAnalyses;
	
	private final ObservableList<CriterionAnalysisPair> d_selectedMetaAnalysesPairs;
	
	private final ValueModel d_metaCompleteModel;
	private ObservableList<MetaAnalysis> d_selectedMetaAnalyses;
	
	public MetaCriteriaAndAlternativesPresentation(ValueHolder<Indication> indication, ModifiableHolder<AnalysisType> analysisType, SortedSetModel<MetaAnalysis> metaAnalyses) {
		super(indication, analysisType);
		d_metaAnalyses = new FilteredObservableList<MetaAnalysis>(metaAnalyses, new IndicationFilter(d_indicationModel.getValue()));

		d_metaAnalysesCriteria = new TransformedObservableList<MetaAnalysis, OutcomeMeasure>(d_metaAnalyses, 
			new Transform<MetaAnalysis, OutcomeMeasure>() {
				public OutcomeMeasure transform(MetaAnalysis a) {
					return a.getOutcomeMeasure();
				}
			});
		d_availableCriteria = new SortedSetModel<OutcomeMeasure>();
		d_metaAnalysesCriteria.addListDataListener(new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {
				updateCriteria();
			}
			public void intervalAdded(ListDataEvent e) {
				updateCriteria();
			}
			public void contentsChanged(ListDataEvent e) {
				updateCriteria();
			}
		});

		d_selectedMetaAnalysesPairs = new ContentAwareListModel<CriterionAnalysisPair>(new ArrayListModel<CriterionAnalysisPair>(), 
				new String[] {CriterionAnalysisPair.PROPERTY_ANALYSIS});
		FilteredObservableList<CriterionAnalysisPair> pairsWithIncludedCriterion = 
			new FilteredObservableList<CriterionAnalysisPair>(d_selectedMetaAnalysesPairs, new Filter<CriterionAnalysisPair>() {
			public boolean accept(CriterionAnalysisPair obj) {
				return getSelectedCriteria().contains(obj.getCriterion());
			}
		});
		d_selectedMetaAnalyses = new TransformedObservableList<CriterionAnalysisPair, MetaAnalysis>(pairsWithIncludedCriterion, 
				new Transform<CriterionAnalysisPair, MetaAnalysis>() {
					public MetaAnalysis transform(CriterionAnalysisPair pair) {
						return pair.getAnalysis();
					}
		});
		
		d_selectedMetaAnalysesPairs.addListDataListener(d_resetAlternativeEnabledModelsListener);

		ValueModel criteriaHaveAnalysisModel = new CriteriaHaveAnalysisModel();
		d_metaCompleteModel = new BooleanAndModel(d_completeModel, criteriaHaveAnalysisModel);
		
		getSelectedCriteria().addListDataListener(new AutoSelectMetaAnalysisListener());
	}

	public ObservableList<MetaAnalysis> getSelectedMetaAnalyses() {
		return d_selectedMetaAnalyses;
	}

	protected void updateCriteria() {
		d_availableCriteria.clear();
		d_availableCriteria.addAll(d_metaAnalysesCriteria);
	}

	@Override
	public BenefitRiskAnalysis<DrugSet> createAnalysis(String id) throws InvalidStateException {
		DrugSet baseline = d_baselineModel.getValue();
		List<DrugSet> alternatives = new ArrayList<DrugSet>(getSelectedAlternatives());
		alternatives.remove(baseline);
		System.out.println(baseline);
		return new MetaBenefitRiskAnalysis(
				id,
				d_indicationModel.getValue(), 
				getSelectedMetaAnalyses(),
				baseline, 
				alternatives,
				d_analysisTypeHolder.getValue());
	}
	@Override
	public ValueModel getCompleteModel() {
		return d_metaCompleteModel;
	}

	@Override
	public ObservableList<OutcomeMeasure> getCriteriaListModel() {
		return d_availableCriteria;
	}

	@Override
	protected void reset() {
		// enabled maps should be cleared first
		d_alternativeEnabledMap.clear();
		d_criteriaEnabledMap.clear();
		
		// selected criteria have to be cleared before availableCriteria and selectedMetaAnalyses
		d_selectedCriteria.clear();
		d_selectedAlternatives.clear();

		d_selectedMetaAnalysesPairs.clear();

		d_baselineModel.setValue(null);

		d_availableCriteria.clear();
		d_availableAlternatives.clear();

		if (d_indicationModel.getValue() != null) {
			initializeValues();
		}
	}

	private void initializeValues() {
		d_metaAnalyses.setFilter(new IndicationFilter(d_indicationModel.getValue()));
		initCriteria();
		initAlternatives(getAlternatives());
		for (OutcomeMeasure om : getCriteriaListModel()) {
			d_selectedMetaAnalysesPairs.add(new CriterionAnalysisPair(om, null));
		}
	}
	
	private MetaAnalysis getDefaultMetaAnalysis(OutcomeMeasure om) {
		ObservableList<MetaAnalysis> metaAnalyses = getMetaAnalyses(om);
		return metaAnalyses.size() == 1 ? metaAnalyses.get(0) : null;
	}

	private Set<DrugSet> getAlternatives() {
		Set<DrugSet> alternatives = new TreeSet<DrugSet>();
		for(MetaAnalysis ma : d_metaAnalyses) {
			if(ma.getIndication() == d_indicationModel.getValue())
				alternatives.addAll(ma.getIncludedDrugs());
		}
		return alternatives;
	}
	
	
	public ObservableList<MetaAnalysis> getMetaAnalyses(OutcomeMeasure om) {
		return new FilteredObservableList<MetaAnalysis>(d_metaAnalyses, new OutcomeMeasureFilter(om));
	}

	public ValueHolder<MetaAnalysis> getMetaAnalysesSelectedModel(OutcomeMeasure om) {
		return findCriterionAnalysisPair(om).getAnalysisModel();
	}

	private CriterionAnalysisPair findCriterionAnalysisPair(OutcomeMeasure om) {
		for (CriterionAnalysisPair pair : d_selectedMetaAnalysesPairs) {
			if (pair.getCriterion().equals(om)) {
				return pair;
			}
		}
		return null;
	}

	@Override
	protected boolean getAlternativeShouldBeEnabled(DrugSet alternative) {
		if (!super.getAlternativeShouldBeEnabled(alternative)) {
			return false;
		}
		return getAlternativeIncludedInAllSelectedAnalyses((DrugSet) alternative);
	}

	private boolean getAlternativeIncludedInAllSelectedAnalyses(DrugSet alternative) {
		boolean noAnalysesSelected = true;
		for (CriterionAnalysisPair pair : d_selectedMetaAnalysesPairs) {
			if (pair.getAnalysis() != null) {
				noAnalysesSelected = false;
				if (!pair.getAnalysis().getIncludedDrugs().contains(alternative)) {
					return false;
				}
			}
		}
		return !noAnalysesSelected;
	}	
}