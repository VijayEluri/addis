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

package org.drugis.addis.gui;

import java.util.Map;

import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.BasicStudy;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Dose;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.gui.builder.StudyAddPatientGroupView;
import org.drugis.common.gui.OkCancelDialog;

@SuppressWarnings("serial")
public class StudyAddPatientGroupDialog extends OkCancelDialog {

	private Domain d_domain;
	private BasicStudy d_study;
	private StudyAddPatientGroupView d_view;
	private Main d_main;

	public StudyAddPatientGroupDialog(Main main, Domain domain, BasicStudy study) {
		super(main, "Add Patient Group to Study");
		d_main = main;
		this.setModal(true);
		d_domain = domain;
		d_study = study;
		d_view = new StudyAddPatientGroupView(d_domain, d_study, d_okButton);
		getUserPanel().removeAll();
		getUserPanel().add(d_view.buildPanel());		
		pack();
	}

	@Override
	protected void cancel() {
		setVisible(false);
	}

	@Override
	protected void commit() {
		BasicPatientGroup pg = d_view.getPatientGroup();
		
		validateFlexibleDose();
				
		d_study.addPatientGroup(pg);
		for (Map.Entry<Endpoint, Measurement> entry : d_view.getMeasurements().entrySet()) {
			d_study.setMeasurement(entry.getKey(), pg, entry.getValue());
		}
		setVisible(false);
		d_main.leftTreeFocusOnStudy(d_study);
	}

	private void validateFlexibleDose() {
		Dose oldDose = d_view.getPatientGroup().getDose();
		
		if (oldDose.getMinDose() >= oldDose.getMaxDose())
			d_view.getPatientGroup().setDose(new Dose(oldDose.getMinDose(), oldDose.getUnit()));
	}	
}
