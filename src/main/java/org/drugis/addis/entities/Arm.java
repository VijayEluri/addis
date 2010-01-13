/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.entities;

import java.util.Set;


public class Arm extends AbstractEntity implements Population {
	private static final long serialVersionUID = -2092185548220089471L;
	private Integer d_size;
	private Drug d_drug;
	private AbstractDose d_dose;
	
	public static final String PROPERTY_SIZE = "size";
	public static final String PROPERTY_DRUG = "drug";
	public static final String PROPERTY_DOSE = "dose";	
	
	private VariableMap d_chars = new VariableMap();
	
	public Arm(Drug drug, AbstractDose dose, int size) {
		d_drug = drug;
		d_dose = dose;
		d_size = size;
		init();
	}
		
	public Drug getDrug() {
		return d_drug;
	}
	
	public void setDrug(Drug drug) {
		Drug oldVal = d_drug;
		d_drug = drug;
		firePropertyChange(PROPERTY_DRUG, oldVal, d_drug);
	}
	
	public AbstractDose getDose() {
		return d_dose;
	}
	
	public void setDose(AbstractDose dose) {
		AbstractDose oldVal = d_dose;
		d_dose = dose;
		firePropertyChange(PROPERTY_DOSE, oldVal, d_dose);
	}
	
	@Override
	public String toString() {
		return  d_drug + ", " + d_dose + ", size: " + d_size;
	}

	public Integer getSize() {
		return d_size;
	}

	public void setSize(Integer size) {
		Integer oldVal = d_size;
		d_size = size;
		firePropertyChange(PROPERTY_SIZE, oldVal, d_size);
	}
	
	public Set<Entity> getDependencies() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setPopulationCharacteristic(Variable v, Measurement val) {
		d_chars.put(v, val);
	}

	public Measurement getPopulationCharacteristic(Variable v) {
		return d_chars.get(v);
	}

	public VariableMap getPopulationCharacteristics() {
		return d_chars;
	}
}