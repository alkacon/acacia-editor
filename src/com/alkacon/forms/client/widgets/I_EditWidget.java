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

package com.alkacon.forms.client.widgets;

import com.alkacon.vie.client.I_Entity;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HasValue;

/**
 * The edit widget interface.<p>
 */
public interface I_EditWidget extends HasValue<String> {

    /**
     * Initializes the widget using the given element.<p>
     * The element needs to be attached to the DOM containing the current value.<p>
     * 
     * @param element the element
     * @param entity the parent widget
     * @param attributeName the attribute name
     * @param valueIndex the value index
     * 
     * @return the initialized widget
     */
    I_EditWidget initWidget(Element element, I_Entity entity, String attributeName, int valueIndex);

    /**
     * Sets the configuration for the given widget.<p>
     * 
     *  @param confuguration the configuration string
     */
    void setConfiguration(String confuguration);
}
