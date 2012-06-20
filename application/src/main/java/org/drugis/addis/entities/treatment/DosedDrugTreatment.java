package org.drugis.addis.entities.treatment;

import java.util.Collections;
import java.util.Set;

import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.AbstractNamedEntity;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.ScaleModifier;
import org.drugis.addis.util.EntityUtil;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;


public class DosedDrugTreatment extends AbstractNamedEntity<DosedDrugTreatment> {
	public static final String PROPERTY_DOSE_UNIT = "doseUnit";
	
	private final ObservableList<CategoryNode> d_categories = new ArrayListModel<CategoryNode>();
	private Drug d_drug;
	private DecisionTreeNode d_rootNode;

	private DoseUnit d_doseUnit;
	
	public DosedDrugTreatment() { 
		super("");
	}
	
	public DosedDrugTreatment(String name, Drug drug, DoseUnit unit, DecisionTreeNode rootNode) {
		super(name);
		d_drug = drug;
		d_doseUnit = unit;
		d_rootNode = rootNode;
	}

	public DosedDrugTreatment(String name, Drug drug) {
		this(name, drug, new DoseUnit(Domain.GRAM, ScaleModifier.MILLI, EntityUtil.createDuration("P1D")), new ExcludeNode());
	}

	public Drug getDrug() {
		return d_drug;
	}
	
	public DecisionTreeNode getRootNode() {
		return d_rootNode;
	}

	public void setRootNode(DecisionTreeNode rootNode) {
		d_rootNode = rootNode;
	}
	
	public DecisionTreeNode getCategoryNode(AbstractDose dose) { 
		DecisionTreeNode node = getRootNode();
		while (!node.isLeaf()) {
			node = node.decide(dose);
		}
		return node;
	}
	
	public String getCategoryName(AbstractDose dose) {
		DecisionTreeNode node = getCategoryNode(dose);
		if (node instanceof CategoryNode) {
			return ((CategoryNode)node).getName();
		} else if (node instanceof ExcludeNode) {
			return ExcludeNode.NAME;
		}
		return null;
	}
	
	public void addCategory(CategoryNode categoryNode) {
		d_categories.add(categoryNode);
	}
	
	public ObservableList<CategoryNode> getCategories() {
		return d_categories;
	}
	
	@Override
	public String getLabel() {
		return getName() + " " + getDrug().getLabel();
	}
	
	@Override
	public String toString() { 
		return getLabel();
	}
	
	@Override
	public Set<? extends Entity> getDependencies() {
		return Collections.singleton(d_drug);
	}

	public void setDoseUnit(DoseUnit unit) {
		DoseUnit oldVal = d_doseUnit;
		d_doseUnit = unit;
		firePropertyChange(PROPERTY_DOSE_UNIT, oldVal, unit);
	}
	
	public DoseUnit getDoseUnit() {
		return d_doseUnit;
	}



}