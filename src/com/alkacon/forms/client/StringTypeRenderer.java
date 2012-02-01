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

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * The simple type renderer.<p>
 */
public class StringTypeRenderer extends A_SimpleTypeRenderer {

    /**
     * Constructor.<p>
     */
    public StringTypeRenderer() {

    }

    /**
     * Constructor.<p>
     * 
     * @param label the attribute label
     * @param help the attribute help information
     */
    public StringTypeRenderer(String label, String help) {

        super(label, help);
    }

    /**
     * @see com.alkacon.forms.client.A_SimpleTypeRenderer#getWidget(java.lang.String, com.alkacon.vie.client.I_Entity, java.lang.String, int)
     */
    @Override
    protected Widget getWidget(String value, I_Entity entity, String attributeName, int valueIndex) {

        TextBox textBox = new TextBox();
        textBox.setValue(value, false);
        textBox.addValueChangeHandler(new SimpleValueChangeHandler(entity, attributeName, valueIndex));
        return textBox;
    }
}
