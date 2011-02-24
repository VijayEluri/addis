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

package org.drugis.addis.gui;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainEvent;
import org.drugis.addis.entities.DomainListener;
import org.drugis.addis.presentation.ValueHolder;

import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class DomainChangedModel extends AbstractValueModel implements ValueHolder<Boolean> {
	private boolean d_changed;

	public DomainChangedModel(Domain domain, boolean changed) {
		d_changed = changed;
		domain.addListener(new DomainListener() {
			public void domainChanged(DomainEvent evt) {
				setValue(true);
			}
		});
	}

	public Boolean getValue() {
		return d_changed;
	}
	
	public void setValue(Object newValue) {
		boolean oldValue = d_changed;
		d_changed = ((Boolean)newValue);
		fireValueChange(oldValue, d_changed);
	}
}