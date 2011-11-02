/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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

package org.drugis.addis.gui.wizard;

import javax.swing.JOptionPane;

import org.drugis.addis.entities.EntityIdExistsException;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.presentation.StudyGraphModel;
import org.drugis.addis.presentation.wizard.AbstractMetaAnalysisWizardPM;
import org.pietschy.wizard.InvalidStateException;
import org.pietschy.wizard.PanelWizardStep;

@SuppressWarnings("serial")
public abstract class AbstractOverviewWizardStep<G extends StudyGraphModel> extends PanelWizardStep {
	protected final AbstractMetaAnalysisWizardPM<G> d_pm;
	protected final AddisWindow d_mainWindow;

	public AbstractOverviewWizardStep(AbstractMetaAnalysisWizardPM<G> pm, AddisWindow main) {
		super("Overview","Overview of selected analysis.");
		d_pm = pm;
		d_mainWindow = main;
	}


	@Override
	public void applyState()
	throws InvalidStateException {
		saveAsAnalysis();
	}

	private void saveAsAnalysis() throws InvalidStateException {
		String res = JOptionPane.showInputDialog(this.getTopLevelAncestor(),
				"Input name for new analysis", 
				"Save analysis", JOptionPane.QUESTION_MESSAGE);
		if (res != null) {
			try {
				d_mainWindow.leftTreeFocus(d_pm.saveMetaAnalysis(res));
			} catch (EntityIdExistsException e) {
				JOptionPane.showMessageDialog(this.getTopLevelAncestor(), 
						"There already exists an analysis with the given name, input another name",
						"Unable to save analysis", JOptionPane.ERROR_MESSAGE);
				saveAsAnalysis();
			}
		} else {
			throw new InvalidStateException();
		}
	}
}