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

package org.drugis.addis.entities.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;


import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.MetaAnalysis;
import org.drugis.addis.entities.MetaStudy;
import org.drugis.addis.entities.PooledPatientGroup;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.UnknownDose;
import org.junit.Before;
import org.junit.Test;

public class PooledPatientGroupTest {
	
	private PooledPatientGroup d_pg;
	private MetaAnalysis d_analysis;
	private MetaStudy d_study;
	
	@Before
	public void setUp() {
		Domain d_domain = new DomainImpl();
		TestData.initDefaultData(d_domain);
		d_analysis = new MetaAnalysis(TestData.buildEndpointHamd(), 
				new ArrayList<Study>(d_domain.getStudies()));		
		d_study = new MetaStudy("s", d_analysis);
		d_pg = new PooledPatientGroup(d_study, TestData.buildDrugFluoxetine());
	}
	
	@Test
	public void testGetDose() {
		assertEquals(new UnknownDose(), d_pg.getDose());
	}
	
	@Test
	public void testGetDrug() {
		assertEquals(TestData.buildDrugFluoxetine(), d_pg.getDrug());
	}
	
	@Test
	public void testGetSize() {
		assertEquals(d_analysis.getPooledMeasurement(d_pg.getDrug()).getSampleSize(),
				d_pg.getSize());
	}

	@Test
	public void testGetStudy() {
		assertEquals(d_study, d_pg.getStudy());
	}
	
	@Test
	public void testGetLabel() {
		assertEquals("META " + d_pg.getDrug().toString(), d_pg.getLabel());
	}
}