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

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Indication;
import org.drugis.common.gui.OkCancelDialog;

import com.jgoodies.binding.PresentationModel;

@SuppressWarnings("serial")
public class AddIndicationDialog extends OkCancelDialog {
	private Domain d_domain;
	private Indication d_indication;
	private Main d_main;
	
	public AddIndicationDialog(Main frame, Domain domain) {
		super(frame, "Add Indication");
		setModal(true);
		d_main = frame;
		d_domain = domain;
		d_indication = new Indication(0L, "");
		AddIndicationView view = new AddIndicationView(new PresentationModel<Indication>(d_indication), d_okButton);
		getUserPanel().add(view.buildPanel());
		pack();
		d_okButton.setEnabled(false);
		getRootPane().setDefaultButton(d_okButton);
	}

	@Override
	protected void cancel() {
		setVisible(false);
	}

	@Override
	protected void commit() {
		d_domain.addIndication(d_indication);
		setVisible(false);
		d_main.leftTreeFocusOnIndication(d_indication);
	}
}