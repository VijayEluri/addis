/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.drugis.mtc.summary.Summary;

@SuppressWarnings("serial")
public class AllSummariesDefinedModel extends UnmodifiableHolder<Boolean> implements PropertyChangeListener {

	private List<? extends Summary> d_summaries;
	boolean d_oldVal;

	public AllSummariesDefinedModel(List<? extends Summary> summaries) {
		super(evaluate(summaries));
		d_summaries = summaries;
		d_oldVal = evaluate(d_summaries);
		for(Summary s: d_summaries) {
			s.addPropertyChangeListener(this);
		}
	}

	private static boolean evaluate(List<? extends Summary> summaries) {
		for(Summary s : summaries) {
			if(!s.getDefined()) {
				return false;
			}
		}
		return true;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		firePropertyChange("value", d_oldVal, evaluate(d_summaries));
		d_oldVal = evaluate(d_summaries);
	}

	@Override
	public Boolean getValue() {
		return evaluate(d_summaries);
	}
	
}
