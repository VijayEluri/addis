package org.drugis.addis.entities.analysis;

import java.util.List;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.relativeeffect.Distribution;

public interface BenefitRiskAnalysis<Alternative extends Entity> extends Comparable<BenefitRiskAnalysis<?>> {
	public static String PROPERTY_NAME = "name";
	public static String PROPERTY_INDICATION = "indication";
	public static String PROPERTY_OUTCOMEMEASURES = "outcomeMeasures";
	public static String PROPERTY_ALTERNATIVES = "alternatives";
	
	public abstract Indication getIndication();

	public abstract List<OutcomeMeasure> getOutcomeMeasures();

	public abstract List<Alternative> getAlternatives();

	public abstract String getName();
	
	public Distribution getMeasurement(Alternative alternative, OutcomeMeasure criterion);

}