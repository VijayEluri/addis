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

import java.io.Serializable;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

public enum SIUnit implements Serializable {
	MILLIGRAMS_A_DAY("Milligrams a Day", "mg/day");
	
	private static final long serialVersionUID = 1L;
	private String d_name;
	private String d_symbol;
	private SIUnit(String name, String symbol) {
		d_name = name;
		d_symbol = symbol;
	}
	
	public String getName() {
		return d_name;
	}
	
	public String getSymbol() {
		return d_symbol;
	}
	
	public String toString() {
		return d_symbol;
	}
	
	protected static final XMLFormat<SIUnit> XML = new XMLFormat<SIUnit>(SIUnit.class) {
		public SIUnit newInstance(Class<SIUnit> cls, InputElement ie) throws XMLStreamException {
			// In newInstance, only use getAttribute, not get. Thats why no indication can be instantiated at this point
			return SIUnit.MILLIGRAMS_A_DAY;
		}
		public boolean isReferenceable() {
			return false;
		}
		public void read(InputElement ie, SIUnit d) throws XMLStreamException {
		}
		
		public void write(SIUnit d, OutputElement oe) throws XMLStreamException {
			oe.setAttribute("unit", d.toString());
		}
	};
}
