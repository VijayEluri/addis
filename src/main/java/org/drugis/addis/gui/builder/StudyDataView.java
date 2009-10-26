package org.drugis.addis.gui.builder;

import java.text.NumberFormat;

import javax.swing.JPanel;

import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.PatientGroup;
import org.drugis.addis.entities.Study;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.presentation.PresentationModelManager;
import org.drugis.common.ImageLoader;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;

public class StudyDataView implements ViewBuilder {
	
	private PresentationModel<? extends Study> model;
	private ImageLoader loader;
	private PresentationModelManager pm;

	public StudyDataView(PresentationModel<? extends Study> model, ImageLoader loader, PresentationModelManager pm) {
		this.model = model;
		this.loader = loader;
		this.pm = pm;
	}

	public JPanel buildPanel() {
		CellConstraints cc = new CellConstraints();
		FormLayout layout = new FormLayout( 
				"left:pref, 5dlu, center:pref, 5dlu, center:pref",
				"p"
				);
		
		int fullWidth = 5;
		for (int i = 1; i < model.getBean().getEndpoints().size(); ++i) {			
			layout.appendColumn(ColumnSpec.decode("3dlu"));
			layout.appendColumn(ColumnSpec.decode("center:pref"));			
			fullWidth += 2;
		}
		PanelBuilder builder = new PanelBuilder(layout);
		
		int row = 1;
		builder.addLabel("Size", cc.xy(3, row, "center, center"));		
		int col = 5;
		for (Endpoint e : model.getBean().getEndpoints()) {
			builder.add(
					GUIFactory.createEndpointLabelWithIcon(loader, model.getBean(), e),
							cc.xy(col, row));
			col += 2;
		}
		row += 2;
	
		for (PatientGroup g : model.getBean().getPatientGroups()) {
			row = buildPatientGroup(layout, builder, cc, row, g);
		}
		return builder.getPanel();
	}

	private int buildPatientGroup(FormLayout layout, PanelBuilder builder, CellConstraints cc, int row, PatientGroup g) {
		int col;
		LayoutUtil.addRow(layout);
		builder.add(
				BasicComponentFactory.createLabel(pm.getLabeledModel(g).getLabelModel()),
				cc.xy(1, row));
		
		builder.add(
				BasicComponentFactory.createLabel(
						new PresentationModel<PatientGroup>(g).getModel(BasicPatientGroup.PROPERTY_SIZE),
						NumberFormat.getInstance()),
						cc.xy(3, row, "center, center"));
		
		col = 5;
		for (Endpoint e : model.getBean().getEndpoints()) {
			Measurement m = model.getBean().getMeasurement(e, g);
			if (m != null) {
				builder.add(
						BasicComponentFactory.createLabel(pm.getLabeledModel(m).getLabelModel()),
						cc.xy(col, row));
			}
			col += 2;
		}
		
		row += 2;
		return row;
	}

}