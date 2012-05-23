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

package com.alkacon.acacia.client.widgets;

import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * The edit widget interface.<p>
 */
public interface I_EditWidget extends HasValue<String>, HasFocusHandlers, IsWidget {

    /**
     * This method is called when a widget is attached to the browser's document.<p>
     * It needs to call the {@link com.google.gwt.user.client.ui.Widget#onAttach()} method.<p>
     */
    @SuppressWarnings("javadoc")
    public void onAttachWidget();

    /**
     * Sets the widget active/inactive.<p>
     * 
     * @param active <code>true</code> to activate the widget
     */
    void setActive(boolean active);

    /**
     * Returns if the widget is active.<p>
     * 
     * @return <code>true</code> if the widget is active
     */
    boolean isActive();

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean)
     */
    void setValue(String value, boolean fireEvent);

    /**
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler);
}
