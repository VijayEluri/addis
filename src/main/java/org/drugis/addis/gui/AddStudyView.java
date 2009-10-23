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

import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.DefaultFormatter;

import org.drugis.addis.entities.AbstractStudy;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.BasicStudy;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Dose;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.StudyCharacteristic;
import org.drugis.common.ImageLoader;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class AddStudyView implements ViewBuilder {
	private PresentationModel<BasicStudy> d_model;
	private PresentationModel<EndpointHolder> d_endpointPresentation;
	private Domain d_domain;
	private NotEmptyValidator d_validator;
	private JButton d_okButton;
	private ImageLoader d_loader;
	private Main d_mainWindow;

	public AddStudyView(PresentationModel<BasicStudy> presentationModel,
			PresentationModel<EndpointHolder> presentationModel2, Domain domain,
			JButton okButton, Main mainWindow) {
		d_okButton = okButton;
		d_model = presentationModel;
		d_endpointPresentation = presentationModel2;
		d_domain = domain;
		d_loader = mainWindow.getImageLoader();
		d_mainWindow = mainWindow;
	}
	
	public void initComponents() {
		
		createIdComponent();
				
		createEndpointComponent();
	}

	private JComponent createEndpointComponent() {
		AbstractValueModel valueModel = d_endpointPresentation.getModel(EndpointHolder.PROPERTY_ENDPOINT);
		JComboBox endpoint = AuxComponentFactory.createBoundComboBox(d_domain.getEndpoints().toArray(), valueModel);
		d_validator.add(endpoint);
		
		ComboBoxPopupOnFocusListener.add(endpoint);
		return endpoint;
	}

	private JComponent createIdComponent() {
		JTextField id = BasicComponentFactory.createTextField(d_model.getModel(AbstractStudy.PROPERTY_ID));
		id.setColumns(30);
		AutoSelectFocusListener.add(id);
		d_validator.add(id);
		return id;
	}
	
	public JComponent buildPanel() {
		d_validator = new NotEmptyValidator(d_okButton); // reset validator
		
		FormLayout layout = new FormLayout(
				"fill:pref, 3dlu, center:pref:grow, 3dlu, pref, 3dlu, pref, 3dlu, pref",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
				);	
		int fullWidth = 9;
		if (getEndpoint() != null) {
			for (int i = 0; i < MeasurementInputHelper.numComponents(getEndpoint()); ++i) {
				LayoutUtil.addColumn(layout);
				fullWidth += 2;
			}
		}
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("Study", cc.xyw(1, 1, fullWidth));
		builder.addLabel("Identifier:", cc.xy(1, 3, "right, c"));
		int componentWidth = fullWidth - 4;
		builder.add(createIdComponent(), cc.xyw(3, 3, componentWidth));

		int row = 5;
		row = buildCharacteristicsPart(fullWidth, builder, cc, row, layout);
		
		builder.addLabel("Endpoint:", cc.xy(1, row, "right, c"));
		builder.add(createEndpointComponent(), cc.xyw(3, row, componentWidth));
		builder.add(createNewEndpointButton(), cc.xy(fullWidth, row));
		
		row += 2;
		builder.addSeparator("Patient Groups", cc.xyw(1, row, fullWidth));
		row += 2;
		builder.addLabel("Drug", cc.xyw(1, row, 3));
		builder.addLabel("Dose", cc.xy(5, row));
		builder.addLabel("Group Size", cc.xy(7, row));
		if (getEndpoint() != null) {
			int col = 9;
			for (String header : MeasurementInputHelper.getHeaders(getEndpoint())) {
				builder.addLabel(header, cc.xy(col, row));
				col += 2;
			}
		}
		if (patientGroupsPresent()) {
			buildPatientGroups(layout, fullWidth, builder, cc, row + 2);
		} else {
			LayoutUtil.addRow(layout);
			builder.addLabel("No patient groups present", cc.xyw(1, row + 2, fullWidth));
		}
		
		return builder.getPanel();	
	}

	private int buildCharacteristicsPart(int fullWidth, PanelBuilder builder,
			CellConstraints cc, int row, FormLayout layout) {
		
		for (StudyCharacteristic c : StudyCharacteristic.values()) {
			LayoutUtil.addRow(layout);
			
			builder.addLabel(c.getDescription() + ":", cc.xy(1, row, "right, c"));
			builder.add(createCharacteristicComponent(c), cc.xyw(3, row, fullWidth-4));
			if (c.equals(StudyCharacteristic.INDICATION)) {
				builder.add(createNewIndicationButton(), cc.xy(fullWidth, row));
			}
				
			
			row += 2;
		}

		return row;
	}

	@SuppressWarnings("serial")
	private JButton createNewIndicationButton() {
		JButton btn = GUIFactory.createPlusButton(d_loader, "New Indication");
		btn.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				d_mainWindow.showAddIndicationDialog();
			}
		});
		return btn;
	}
	
	@SuppressWarnings("serial")
	private JButton createNewEndpointButton() {
		JButton btn = GUIFactory.createPlusButton(d_loader, "New Endpoint");
		btn.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				d_mainWindow.showAddEndpointDialog();
			}
		});
		return btn;
	}
	
	@SuppressWarnings("serial")
	private JButton createNewDrugButton() {
		JButton btn = GUIFactory.createPlusButton(d_loader, "New Drug");
		btn.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				d_mainWindow.showAddDrugDialog();
			}
		});
		return btn;
	}

	private JComponent createCharacteristicComponent(StudyCharacteristic c) {
		JComponent component = null;
		if (c.getValueType().equals(StudyCharacteristic.ValueType.INDICATION)) {
			ArrayList<Indication> options = new ArrayList<Indication>(d_domain.getIndications());			
			component = createOptionsComboBox(c, options.toArray());
		} else if (c.getValueType().equals(StudyCharacteristic.ValueType.TEXT)) {
			ValueModel model = new MutableCharacteristicHolder(d_model.getBean(), c);
			component = BasicComponentFactory.createTextField(model);
			d_validator.add(component);
		} else if (c.getValueType().equals(StudyCharacteristic.ValueType.POSITIVE_INTEGER)) {
			ValueModel model = new MutableCharacteristicHolder(d_model.getBean(), c);
			if (model.getValue() == null) {
				model.setValue(1);
			}
			@SuppressWarnings("serial")
			JFormattedTextField f = new JFormattedTextField(new DefaultFormatter() {
				@Override
				public Object stringToValue(String string) throws ParseException {
					int val = 0;
					try {
						val = Integer.parseInt(string);
					} catch (NumberFormatException e) {
						
					}
					if (val < 1) {
						throw new ParseException("Non-positive values not allowed", 0);
					}
					return val;
				}
			});
			PropertyConnector.connectAndUpdate(model, f, "value");
			component = f;
		} else if (c.getValueType().equals(StudyCharacteristic.ValueType.DATE)) {
			ValueModel model = new MutableCharacteristicHolder(d_model.getBean(), c);
			if (model.getValue() == null) {
				model.setValue(new Date());
			}
			JFormattedTextField f = new JFormattedTextField(new DefaultFormatter());
			PropertyConnector.connectAndUpdate(model, f, "value");
			component = f;
		} else if (c.getValueType().valueClass.isEnum()) {
			try {
				component = createOptionsComboBox(c, c.getValueType().valueClass);
			} catch (Exception e) {
				component = new JLabel("ILLEGAL CHARACTERISTIC ENUM TYPE");
			}
		} else {
			component = new JLabel("NOT IMPLEMENTED");
		}
		d_validator.add(component);		
		return component;
	}
	
	@SuppressWarnings("unchecked")
	private JComponent createOptionsComboBox(StudyCharacteristic c, Class type) {
		Object[] options = null;
		if (type.equals(StudyCharacteristic.Allocation.class)) {
			options = StudyCharacteristic.Allocation.values();
		} else if (type.equals(StudyCharacteristic.Blinding.class)) {
			options = StudyCharacteristic.Blinding.values();
		} else if (type.equals(StudyCharacteristic.Status.class)) {
			options = StudyCharacteristic.Status.values();
		} else {
			throw new RuntimeException("Illegal study characteristic enum type");
		}
		
		return createOptionsComboBox(c, options);
	}

	private <E> JComponent createOptionsComboBox(StudyCharacteristic c, E[] options) {
		MutableCharacteristicHolder selectionHolder =
			new MutableCharacteristicHolder(d_model.getBean(), c);
		JComboBox component = AuxComponentFactory.createBoundComboBox(options, selectionHolder);
		ComboBoxPopupOnFocusListener.add(component);
		return component;
	}

	private Endpoint getEndpoint() {
		return d_endpointPresentation.getBean().getEndpoint();
	}

	private void buildPatientGroups(FormLayout layout, int fullWidth,
			PanelBuilder builder, CellConstraints cc, int row) {
		List<BasicPatientGroup> groups = d_model.getBean().getPatientGroups();
		for (BasicPatientGroup g : groups) {
			LayoutUtil.addRow(layout);
			
			PresentationModel<BasicPatientGroup> model = new PresentationModel<BasicPatientGroup>(g);
			
			int col = 1;
			
			JComboBox selector = GUIFactory.createDrugSelector(model, d_domain);
			d_validator.add(selector);
			ComboBoxPopupOnFocusListener.add(selector);
			builder.add(selector, cc.xy(col, row));
			col += 2;
			
			builder.add(createNewDrugButton(), cc.xy(col, row));
			col += 2;
			
			DoseView view = new DoseView(new PresentationModel<Dose>(g.getDose()),
					d_validator);
			builder.add(view.buildPanel(), cc.xy(col, row));
			col += 2;
			
			JTextField field = MeasurementInputHelper.buildFormatted(model.getModel(BasicPatientGroup.PROPERTY_SIZE));
			d_validator.add(field);
			AutoSelectFocusListener.add(field);
			builder.add(field, cc.xy(col, row));
			col += 2;

			Measurement meas = d_model.getBean().getMeasurement(
					d_endpointPresentation.getBean().getEndpoint(),g);
			for (JTextField component : MeasurementInputHelper.getComponents((BasicMeasurement)meas)) {
				d_validator.add(component);
				builder.add(component, cc.xy(col, row));
				col += 2;
			}

			row += 2;
		}
	}

	private boolean patientGroupsPresent() {
		return !d_model.getBean().getPatientGroups().isEmpty();
	}
}