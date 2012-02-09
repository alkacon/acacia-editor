/*
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) Alkacon Software (http://www.alkacon.com)
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

package com.alkacon.acacia.client.widgets;

import com.alkacon.vie.shared.I_Entity;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;

/**
 * Widget subclass which uses TinyMCE for editing.<p>
 */
public class TinyMCEWidget extends A_EditWidget {

    /** The TinyMCE wrapper object. */
    protected TinyMCE m_editor;

    /**
     * Creates a new instance.<p>
     * 
     * @param editor the TinyMCE wrapper object 
     */
    public TinyMCEWidget(TinyMCE editor) {

        m_editor = editor;
    }

    /**
     * @see com.alkacon.acacia.client.widgets.A_EditWidget#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {

        return m_editor.addValueChangeHandler(handler);
    }

    /**
     * @see com.alkacon.acacia.client.widgets.I_EditWidget#initWidget(com.google.gwt.user.client.Element, com.alkacon.vie.shared.I_Entity, java.lang.String, int)
     */
    public I_EditWidget initWidget(
        Element element,
        final I_Entity entity,
        final String attributeName,
        final int valueIndex) {

        TinyMCE editor = new TinyMCE(element);
        editor.addValueChangeHandler(new ValueChangeHandler<String>() {

            public void onValueChange(ValueChangeEvent<String> event) {

                entity.setAttributeValue(attributeName, event.getValue(), valueIndex);
            }
        });
        editor.init();
        return new TinyMCEWidget(editor);
    }

    /**
     * @see com.alkacon.acacia.client.widgets.I_EditWidget#setConfiguration(java.lang.String)
     */
    public void setConfiguration(String confuguration) {

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

}
