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

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.UIObject;

/**
 * The string edit widget.<p>
 */
public class StringWidget extends UIObject implements I_EditWidget, EventListener {

    /** The handler manager. */
    private HandlerManager m_handlerManager;

    /** The previous value. */
    private String m_previousValue;

    /** The value changed handler initialized flag. */
    private boolean m_valueChangeHandlerInitialized;

    /**
     * Adds a native event handler to the widget and sinks the corresponding
     * native event. If you do not want to sink the native event, use the generic
     * addHandler method instead.<p>
     *
     * @param <H> the type of handler to add
     * @param type the event key
     * @param handler the handler
     * @return {@link HandlerRegistration} used to remove the handler
     */
    public final <H extends EventHandler> HandlerRegistration addDomHandler(final H handler, DomEvent.Type<H> type) {

        assert handler != null : "handler must not be null";
        assert type != null : "type must not be null";
        int typeInt = Event.getTypeInt(type.getName());
        if (typeInt == -1) {
            sinkBitlessEvent(type.getName());
        } else {
            sinkEvents(typeInt);
        }
        return ensureHandlers().addHandler(type, handler);
    }

    /**
     * Adds this handler to the widget.
     *
     * @param <H> the type of handler to add
     * @param type the event type
     * @param handler the handler
     * @return {@link HandlerRegistration} used to remove the handler
     */
    public final <H extends EventHandler> HandlerRegistration addHandler(final H handler, GwtEvent.Type<H> type) {

        return ensureHandlers().addHandler(type, handler);
    }

    /**
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {

        // Initialization code
        if (!m_valueChangeHandlerInitialized) {
            m_valueChangeHandlerInitialized = true;
            addDomHandler(new KeyPressHandler() {

                @Override
                public void onKeyPress(KeyPressEvent event) {

                    fireValueChange();

                }
            }, KeyPressEvent.getType());
            addDomHandler(new ChangeHandler() {

                @Override
                public void onChange(ChangeEvent event) {

                    fireValueChange();

                }
            }, ChangeEvent.getType());
            addDomHandler(new BlurHandler() {

                @Override
                public void onBlur(BlurEvent event) {

                    fireValueChange();
                }
            }, BlurEvent.getType());
        }
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * @see com.google.gwt.event.shared.HasHandlers#fireEvent(com.google.gwt.event.shared.GwtEvent)
     */
    public void fireEvent(GwtEvent<?> event) {

        if (m_handlerManager != null) {
            m_handlerManager.fireEvent(event);
        }

    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#getValue()
     */
    @Override
    public String getValue() {

        return getElement().getInnerText();
    }

    /**
     * @see com.alkacon.forms.client.I_EditWidget#initWidget(com.google.gwt.user.client.Element, com.alkacon.vie.client.I_Entity, java.lang.String, int)
     */
    public I_EditWidget initWidget(Element element, I_Entity entity, String attributeName, int valueIndex) {

        setElement(element);
        DOM.setEventListener(getElement(), this);
        m_previousValue = getValue();
        getElement().setAttribute("contenteditable", "true");
        getElement().getStyle().setColor("red");
        return this;
    }

    /**
     * @see com.google.gwt.user.client.EventListener#onBrowserEvent(com.google.gwt.user.client.Event)
     */
    public void onBrowserEvent(Event event) {

        switch (DOM.eventGetType(event)) {
            case Event.ONMOUSEOVER:
                // Only fire the mouse over event if it's coming from outside this
                // widget.
            case Event.ONMOUSEOUT:
                // Only fire the mouse out event if it's leaving this
                // widget.
                Element related = event.getRelatedEventTarget().cast();
                if ((related != null) && getElement().isOrHasChild(related)) {
                    return;
                }
                break;
        }
        DomEvent.fireNativeEvent(event, this, this.getElement());

    }

    /**
     * @see com.alkacon.forms.client.I_EditWidget#setConfiguration(java.lang.String)
     */
    public void setConfiguration(String confuguration) {

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

        getElement().setInnerText(value);
        if (fireEvents) {
            fireValueChange();
        }
    }

    /**
     * Creates the {@link HandlerManager} used by this Widget. You can override
     * this method to create a custom {@link HandlerManager}.
     *
     * @return the {@link HandlerManager} you want to use
     */
    protected HandlerManager createHandlerManager() {

        return new HandlerManager(this);
    }

    /**
     * Fires the value change event, if the value has changed.<p>
     */
    protected void fireValueChange() {

        String currentValue = getValue();
        if (!currentValue.equals(m_previousValue)) {
            m_previousValue = currentValue;
        }
        ValueChangeEvent.fire(this, currentValue);
    }

    /**
     * Ensures the existence of the handler manager.<p>
     *
     * @return the handler manager
     * */
    HandlerManager ensureHandlers() {

        return m_handlerManager == null ? m_handlerManager = createHandlerManager() : m_handlerManager;
    }

    /**
     * Returns the handler manager.<p>
     * 
     * @return the handler manager
     */
    HandlerManager getHandlerManager() {

        return m_handlerManager;
    }
}
