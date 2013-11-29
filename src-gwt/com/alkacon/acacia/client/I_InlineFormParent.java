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

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * An inline form parent widget.<p>
 */
public interface I_InlineFormParent {

    /**
     * Adopts the given widget as a child widget.<p>
     * This assumes the widget element is already attached to the DOM and is a child or descendant of this widget element.<p>
     * 
     * @param widget the widget to adopt
     */
    void adoptWidget(IsWidget widget);

    /**
     * The widget element.<p>
     * 
     * @return the widget element
     */
    Element getElement();

    /** 
     * Replaces the inner HTML of widget to reflect content data changes.<p>
     * 
     * @param html the element HTML
     */
    void replaceHtml(String html);

}
