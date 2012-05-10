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

import com.alkacon.acacia.client.css.I_LayoutBundle;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Widget subclass which uses TinyMCE for editing.<p>
 */
public class TinyMCEWidget extends A_EditWidget {

    /** The TinyMCE wrapper object. */
    protected TinyMCE m_editor;

    /** Indicating if the widget is active. */
    private boolean m_active;

    /**
     * @see com.alkacon.acacia.client.widgets.A_EditWidget#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {

        return m_editor.addValueChangeHandler(handler);
    }

    /**
     * @see com.alkacon.acacia.client.widgets.I_EditWidget#initWidget(com.google.gwt.dom.client.Element, boolean)
     */
    public I_EditWidget initWidget(Element element, boolean inline) {

        setElement(element);
        m_editor = new TinyMCE(element);
        m_editor.init();
        m_active = true;
        return this;
    }

    /**
     * @see com.alkacon.acacia.client.widgets.I_EditWidget#isActive()
     */
    public boolean isActive() {

        return m_active;
    }

    /**
     * @see com.alkacon.acacia.client.widgets.I_EditWidget#setActive(boolean)
     */
    public void setActive(boolean active) {

        if (m_active == active) {
            return;
        }
        m_active = active;
        if (m_active) {
            m_editor.getEditorParentElement().removeClassName(I_LayoutBundle.INSTANCE.form().inActive());
            // getElement().focus();
            fireValueChange(true);
        } else {
            m_editor.getEditorParentElement().addClassName(I_LayoutBundle.INSTANCE.form().inActive());
        }
    }

    /**
     * @see com.alkacon.acacia.client.widgets.I_EditWidget#setConfiguration(java.lang.String)
     */
    public void setConfiguration(String configuration) {

        // do nothing 
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
     */
    public void setValue(String value) {

        m_editor.setValue(value);
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean)
     */
    public void setValue(String value, boolean fireEvents) {

        m_editor.setValue(value, fireEvents);
    }

    /**
     * @see com.alkacon.acacia.client.widgets.A_EditWidget#fireValueChange(boolean)
     */
    @Override
    protected void fireValueChange(boolean force) {

        m_editor.fireChange(force);
    }
}
