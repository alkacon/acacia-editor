/*
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) Alkacon Software GmbH (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.alkacon.forms.client;

import com.alkacon.vie.client.Entity;
import com.alkacon.vie.client.I_Entity;
import com.alkacon.vie.client.I_Vie;
import com.alkacon.vie.client.Vie;
import com.alkacon.vie.shared.I_Type;

import java.util.List;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Tests the forms.<p>
 */
public class TestForms extends GWTTestCase {

    /** Change counter. */
    private int m_changeCount;

    /**
     * @see com.google.gwt.junit.client.GWTTestCase#getModuleName()
     */
    @Override
    public String getModuleName() {

        return "com.alkacon.forms.Forms";
    }

    /**
     * Tests the form renderer.<p>
     */
    public void testFormRenderer() {

        String simpleTypeId = "<cms:simple>";
        String complexTypeId = "<cms:complex>";
        String attributeName = "<http:opencms/simpleAttribute>";
        I_Vie vie = getVieInstance();
        vie.createType(simpleTypeId);
        I_Type complex = vie.createType(complexTypeId);
        complex.addAttribute(attributeName, simpleTypeId, 1, 1);
        I_Entity entity = vie.createEntity("<myEntity>", complexTypeId);
        entity.setAttributeValue(attributeName, "my attribute value");
        WidgetService service = new WidgetService();
        I_EntityRenderer defaultRenderer = new ComplexTypeRenderer(service, vie);
        service.setDefaultComplexRenderer(defaultRenderer);
        service.setDefaultSimpleRenderer(defaultRenderer);
        I_EntityRenderer renderer = service.getRendererForType(complex);
        Element context = DOM.createDiv();
        RootPanel.getBodyElement().appendChild(context);
        renderer.render(entity, (com.google.gwt.user.client.Element)context);
        assertEquals(
            "The forms inner HTML should match the exspected.",
            "<div typeof=\"cms:complex\" about=\"myEntity\" class=\"entity\"><div title=\"\" class=\"label\">&lt;http:opencms/simpleAttribute&gt;</div><div class=\"widgetHolder\"><div style=\"color: red;\" contenteditable=\"true\" property=\"http:opencms/simpleAttribute\">my attribute value</div></div></div>",
            context.getInnerHTML());
        // TODO: fix event triggering
        resetChangeCount();
        ((Entity)entity).addValueChangeHandler(new ValueChangeHandler<I_Entity>() {

            public void onValueChange(ValueChangeEvent<I_Entity> event) {

                assertNotNull(event.getValue());
                incrementChangeCount();
            }
        });
        List<com.google.gwt.user.client.Element> inputs = ((Vie)vie).select(
            "[property='http:opencms/simpleAttribute']",
            null);
        Element input = inputs.get(0);
        input.setInnerText("my new value");
        triggerChangeEvent(input);
        assertEquals(1, getChangeCount());
    }

    /**
     * Triggers a change event on the given element.<p>
     * 
     * @param element the element
     */
    private void triggerChangeEvent(Element element) {

        NativeEvent nativeEvent = Document.get().createBlurEvent();
        element.dispatchEvent(nativeEvent);
    }

    /**
     * Increments the change counter.<p>
     */
    protected void incrementChangeCount() {

        m_changeCount++;
    }

    /**
     * Returns the change count.<p>
     * 
     * @return the change count
     */
    protected int getChangeCount() {

        return m_changeCount;
    }

    /**
     * Returns the {@link Vie} instance.<p>
     * 
     * @return the {@link Vie} instance
     */
    private I_Vie getVieInstance() {

        return Vie.getInstance();
    }

    /**
     * Resets the change counter.<p>
     */
    private void resetChangeCount() {

        m_changeCount = 0;
    }

}
