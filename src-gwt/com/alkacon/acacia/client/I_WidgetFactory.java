/*
 * This library is part of the Acacia Editor -
 * an open source inline and form based content editor for GWT.
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.alkacon.acacia.client;

import com.alkacon.acacia.client.widgets.I_EditWidget;

import com.google.gwt.user.client.Element;

/**
 * Generates an editing widget with the given configuration.<p>
 */
public interface I_WidgetFactory {

    /**
     * Creates the widget.<p>
     * 
     * @param configuration the widget configuration
     * 
     * @return the widget
     */
    I_EditWidget createWidget(String configuration);

    /**
     * Wraps an existing DOM element.<p>
     * 
     * @param configuration the widget configuration
     * @param element the element to wrap
     * 
     * @return the widget instance
     */
    I_EditWidget wrapElement(String configuration, Element element);
}
