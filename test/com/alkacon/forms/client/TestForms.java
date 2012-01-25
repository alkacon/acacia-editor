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

import com.alkacon.vie.client.I_Entity;
import com.alkacon.vie.client.I_Type;
import com.alkacon.vie.client.I_Vie;
import com.alkacon.vie.client.Vie;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.Widget;

/**
 * Tests the forms.<p>
 */
public class TestForms extends GWTTestCase {

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
        service.setDefaultComplexRenderer(new ComplexTypeRenderer(service, vie));
        service.setDefaultSimpleRenderer(new SimpleTypeRenderer());
        I_EntityRenderer renderer = service.getRendererForType(complex);
        Widget form = renderer.render(entity);
        assertNotNull("The form should not be null", form);
        assertEquals(
            "The forms inner HTML should match the exspected.",
            "<div class=\"gwt-Label\">&lt;http:opencms/simpleAttribute&gt;</div><div class=\"gwt-Label\">my attribute value</div>",
            form.getElement().getInnerHTML());
    }

    /**
     * Returns the {@link Vie} instance.<p>
     * 
     * @return the {@link Vie} instance
     */
    private I_Vie getVieInstance() {

        return Vie.getInstance();
    }

}
