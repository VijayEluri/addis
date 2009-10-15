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

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Study extends Comparable<Study>, Entity {
	public final static String PROPERTY_ID = "id";
	public final static String PROPERTY_ENDPOINTS = "endpoints";
	public final static String PROPERTY_PATIENTGROUPS = "patientGroups";

	public String getId();
	public Set<Endpoint> getEndpoints();
	public List<? extends PatientGroup> getPatientGroups();
	public Measurement getMeasurement(Endpoint e, PatientGroup g);
	public Set<Drug> getDrugs();	
	
	public Map<StudyCharacteristic, Object> getCharacteristics();
	public Object getCharacteristic(StudyCharacteristic c);
}