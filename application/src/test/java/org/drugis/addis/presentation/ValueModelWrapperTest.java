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

import static org.junit.Assert.assertEquals;
import static org.easymock.EasyMock.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.common.JUnitUtil;
import org.junit.Test;

import com.jgoodies.binding.value.ValueModel;

public class ValueModelWrapperTest {

	@Test
	public void testGetAndSetValue() {
		ValueModel nested = new ModifiableHolder<Object>(false);
		ValueModelWrapper<Boolean> wrapper = new ValueModelWrapper<Boolean>(nested);
		assertEquals(false, wrapper.getValue());
		nested.setValue(true);
		assertEquals(true, wrapper.getValue());
		wrapper.setValue(false);
		assertEquals(false, nested.getValue());
	}
	
	@Test
	public void testEventPropagation() {
		ValueModel nested = new ModifiableHolder<Object>(false);
		ValueModelWrapper<Boolean> wrapper = new ValueModelWrapper<Boolean>(nested);

		PropertyChangeListener listener = createStrictMock(PropertyChangeListener.class);
		listener.propertyChange(JUnitUtil.eqPropertyChangeEvent(new PropertyChangeEvent(wrapper, "value", false, true)));
		listener.propertyChange(JUnitUtil.eqPropertyChangeEvent(new PropertyChangeEvent(wrapper, "value", true, false)));
		replay(listener);
		
		wrapper.addPropertyChangeListener(listener);
		nested.setValue(true);
		wrapper.setValue(false);
		verify(listener);
	}
}
