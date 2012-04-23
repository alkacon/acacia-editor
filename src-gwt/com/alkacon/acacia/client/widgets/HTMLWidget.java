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

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * A wrapper widget, delegating either to the hallo widget for in line editing or the TinyMCE widget for form based editing.<p>
 */
public class HTMLWidget implements I_EditWidget {

    /** The widget configuration. */
    private String m_configuration;

    /** The used widget. */
    private I_EditWidget m_widget;

    /**
     * @see com.google.gwt.event.dom.client.HasFocusHandlers#addFocusHandler(com.google.gwt.event.dom.client.FocusHandler)
     */
    public HandlerRegistration addFocusHandler(FocusHandler handler) {

        return m_widget.addFocusHandler(handler);
    }

    /**
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {

        return m_widget.addValueChangeHandler(handler);
    }

    /**
     * @see com.google.gwt.event.shared.HasHandlers#fireEvent(com.google.gwt.event.shared.GwtEvent)
     */
    public void fireEvent(GwtEvent<?> event) {

        m_widget.fireEvent(event);
    }

    /**
     * @see com.alkacon.acacia.client.widgets.I_EditWidget#getElement()
     */
    public Element getElement() {

        return m_widget.getElement();
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#getValue()
     */
    public String getValue() {

        return m_widget.getValue();
    }

    /**
     * @see com.alkacon.acacia.client.widgets.I_EditWidget#initWidget(com.google.gwt.dom.client.Element, boolean)
     */
    public I_EditWidget initWidget(Element element, boolean inline) {

        if (inline) {
            m_widget = new HalloWidget();
        } else {
            m_widget = new TinyMCEWidget();
        }
        m_widget.setConfiguration(m_configuration);
        return m_widget.initWidget(element, inline);
    }

    /**
     * @see com.alkacon.acacia.client.widgets.I_EditWidget#setConfiguration(java.lang.String)
     */
    public void setConfiguration(String configuration) {

        m_configuration = configuration;
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
     */
    public void setValue(String value) {

        m_widget.setValue(value);
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean)
     */
    public void setValue(String value, boolean fireEvents) {

        m_widget.setValue(value, fireEvents);
    }

}
