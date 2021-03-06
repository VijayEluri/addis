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

package org.drugis.addis.gui.knowledge;

import java.awt.Dimension;

import javax.swing.JDialog;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.treatment.TreatmentCategorization;
import org.drugis.addis.gui.AddDosedDrugTreatmentWizard;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.builder.TreatmentCategorizationView;
import org.drugis.addis.presentation.TreatmentCategorizationPresentation;
import org.drugis.addis.presentation.wizard.TreatmentCategorizationWizardPresentation;
import org.drugis.common.gui.ViewBuilder;
import org.pietschy.wizard.WizardFrameCloser;

import com.jgoodies.binding.value.ValueModel;

public class TreatmentCategorizationKnowledge extends CategoryKnowledgeBase {

	public TreatmentCategorizationKnowledge() {
		super("treatment", FileNames.ICON_HEART, TreatmentCategorization.class);
	}

	@Override
	public JDialog getAddDialog(final AddisWindow mainWindow, final Domain domain, final ValueModel selectionModel) {
		final TreatmentCategorization treatment = TreatmentCategorization.createDefault();

		final TreatmentCategorizationWizardPresentation pm = new TreatmentCategorizationWizardPresentation(treatment, domain);
		return buildDosedDrugTreatmentWizardDialog(mainWindow, domain, "Add Treatment", pm);
	}

	public static JDialog buildDosedDrugTreatmentWizardDialog(final AddisWindow mainWindow, final Domain domain, final String title, final TreatmentCategorizationWizardPresentation pm) {
		final JDialog dialog = new JDialog(mainWindow, title, true);
		final AddDosedDrugTreatmentWizard wizard = new AddDosedDrugTreatmentWizard(pm, dialog);
		dialog.getContentPane().add(wizard);
		dialog.setMinimumSize(new Dimension(550, 400));
		dialog.setPreferredSize(AddisWindow.fitDimensionToScreen(640, 600));
		dialog.pack();
		WizardFrameCloser.bind(wizard, dialog);
		Main.bindPrintScreen(wizard);
		return dialog;
	}

	@Override
	protected String[] getShownProperties() {
		return new String[] { 
				TreatmentCategorization.PROPERTY_DRUG, 
				TreatmentCategorization.PROPERTY_NAME, 
				TreatmentCategorization.PROPERTY_CATEGORIES };
	}

	@Override
	public ViewBuilder getEntityViewBuilder(final AddisWindow main, final Domain domain, final Entity entity) {
		return new TreatmentCategorizationView((TreatmentCategorizationPresentation) main.getPresentationModelFactory().getModel(((TreatmentCategorization) entity)), main);
	}

}
