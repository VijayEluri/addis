/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

import static org.junit.Assert.*;
import static org.drugis.addis.entities.AssertEntityEquals.*;

import java.util.Collections;

import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.util.XMLHelper;
import org.junit.Before;
import org.junit.Test;

public class CategoricalPopulationCharacteristicTest {
	CategoricalPopulationCharacteristic d_gender;
	
	@Before
	public void setUp() {
		d_gender = new CategoricalPopulationCharacteristic("Gender", new String[]{"Male", "Female"});
	}
	
	@Test
	public void testCategories() {
		String[] cats = {"Male", "Female"};
		assertEquals(cats[0], d_gender.getCategories()[0]);
		assertEquals(cats[1], d_gender.getCategories()[1]);
		assertEquals(cats.length, d_gender.getCategories().length);
	}
	
	@Test
	public void testGetName() {
		assertEquals("Gender", d_gender.getName());
	}
	
	@Test
	public void testBuildMeasurement() {
		Measurement m = d_gender.buildMeasurement();
		assertTrue(m instanceof FrequencyMeasurement);
		assertEquals(d_gender, ((FrequencyMeasurement)m).getCategoricalVariable());
		assertEquals(new Integer(0), m.getSampleSize());
	}
	
	@Test
	public void testGetDependencies() {
		assertEquals(Collections.emptySet(), d_gender.getDependencies());
	}
	
	@Test
	public void testToString() {
		assertEquals(d_gender.getName(), d_gender.toString());
	}
	
	@Test
	public void testEquals() {
		CategoricalPopulationCharacteristic gender2 = new CategoricalPopulationCharacteristic("Gender", new String[]{"Male", "Female"});
		assertTrue(gender2.equals(d_gender));
		
		gender2 = new CategoricalPopulationCharacteristic("Gender2", new String[]{"Male", "Female"});
		assertFalse(gender2.equals(d_gender));

		gender2 = new CategoricalPopulationCharacteristic(null, new String[]{"Male", "Female"});
		assertFalse(gender2.equals(d_gender));

		assertFalse(gender2.equals(new Integer(2)));
	}
	
	@Test
	public void testXML() throws XMLStreamException {
		CategoricalPopulationCharacteristic gender = new CategoricalPopulationCharacteristic("Gender", new String[]{"Male", "Female"});
		String xml = XMLHelper.toXml(gender, CategoricalPopulationCharacteristic.class);
		System.out.println(xml);
		CategoricalPopulationCharacteristic objFromXml = XMLHelper.fromXml(xml);
		assertEntityEquals(gender, objFromXml);
	}
	
}
