package org.drugis.addis.entities;


public enum PopulationCharacteristic implements Characteristic {

	MALE("Male subjects", Integer.class),
	FEMALE("Female subjects", Integer.class),
	AGE("Age", BasicContinuousMeasurement.class);

	private String d_description;
	private Class<?> d_type;
	
	PopulationCharacteristic(String description, Class<?> type) {
			d_description = description;
			d_type = type;
	}

	public String getDescription() {
		return d_description;
	}

	public Class<?> getValueType() {
		return d_type;
	}	
}
