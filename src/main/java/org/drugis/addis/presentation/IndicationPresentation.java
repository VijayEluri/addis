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

package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class IndicationPresentation extends PresentationModel<Indication> implements LabeledPresentationModel, StudyListPresentationModel {
	public class LabelModel extends AbstractValueModel implements PropertyChangeListener {
		protected LabelModel() {
			getBean().addPropertyChangeListener(this);
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(Indication.PROPERTY_CODE)) {
				firePropertyChange("value", (evt.getOldValue() + " " + getBean().getName()), getValue());
			} else if (evt.getPropertyName().equals(Indication.PROPERTY_NAME)) {
				firePropertyChange("value", (getBean().getCode() + " " + evt.getOldValue()), getValue());
			}
		}

		public String getValue() {
			return getBean().toString();
		}

		public void setValue(Object newValue) {
			throw new RuntimeException("Label is Read-Only");
		}
	}

	private CharacteristicVisibleMap d_charMap = new CharacteristicVisibleMap();
	private ListHolder<Study> d_studies;

	public IndicationPresentation(Indication bean, ListHolder<Study> studies) {
		super(bean);
		d_studies = studies;
	}

	public AbstractValueModel getLabelModel() {
		return new LabelModel();
	}

	public AbstractValueModel getCharacteristicVisibleModel(StudyCharacteristic c) {
		return d_charMap.get(c);
	}

	public ListHolder<Study> getIncludedStudies() {
		return d_studies;
	}	
}
