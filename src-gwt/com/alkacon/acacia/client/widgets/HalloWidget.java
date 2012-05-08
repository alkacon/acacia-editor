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
import com.alkacon.vie.client.Vie;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Rich text editor widget based on the hallo editor.<p>
 */
public class HalloWidget extends A_EditWidget {

    /**
     * @see com.alkacon.acacia.client.widgets.A_EditWidget#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {

        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * @see com.alkacon.acacia.client.widgets.A_EditWidget#getValue()
     */
    @Override
    public String getValue() {

        return getElement().getInnerHTML();
    }

    /**
     * @see com.alkacon.acacia.client.widgets.I_EditWidget#initWidget(com.google.gwt.dom.client.Element, boolean)
     */
    public I_EditWidget initWidget(Element element, boolean inline) {

        setElement(element);
        setPreviousValue(getValue());
        element.addClassName(I_LayoutBundle.INSTANCE.form().input());
        init(element, Vie.getInstance());
        return this;
    }

    /**
     * @see com.alkacon.acacia.client.widgets.I_EditWidget#setConfiguration(java.lang.String)
     */
    public void setConfiguration(String configuration) {

        // TODO: Auto-generated method stub

    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
     */
    public void setValue(String value) {

        setValue(value, true);
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean)
     */
    public void setValue(String value, boolean fireEvents) {

        getElement().setInnerHTML(value);
        fireValueChange();
    }

    /**
     * Initializes the native java-script components of the widget.<p>
     * 
     * @param element the element
     * @param vie the VIE instance
     */
    private native void init(Element element, Vie vie) /*-{
        var _self = this;
        var editable = vie.jQuery(element);
        editable.hallo({
            plugins : {
                'halloformat' : {},
                'halloblock' : {},
                'hallojustify' : {},
                'hallolists' : {},
                'hallolink' : {},
                'halloreundo' : {}
            },
            editable : true
        });
        editable
                .bind(
                        'hallomodified',
                        function(event, data) {
                            _self.@com.alkacon.acacia.client.widgets.HalloWidget::fireValueChange()();
                        });
    }-*/;

}
