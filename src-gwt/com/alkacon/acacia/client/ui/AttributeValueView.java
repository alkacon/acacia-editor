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

package com.alkacon.acacia.client.ui;

import com.alkacon.acacia.client.AttributeHandler;
import com.alkacon.acacia.client.I_EntityRenderer;
import com.alkacon.acacia.client.css.I_LayoutBundle;
import com.alkacon.acacia.client.widgets.I_EditWidget;
import com.alkacon.vie.shared.I_Entity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.UIObject;

/**
 * UI object holding an attribute value.<p>
 */
public class AttributeValueView extends UIObject {

    /**
     * The widget value change handler.<p>
     */
    protected class ChangeHandler implements ValueChangeHandler<String> {

        /**
         * @see com.google.gwt.event.logical.shared.ValueChangeHandler#onValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent)
         */
        public void onValueChange(ValueChangeEvent<String> event) {

            getHandler().changeValue(AttributeValueView.this, event.getValue());
        }
    }

    /**
     * The UI binder interface.<p>
     */
    interface AttributeValueUiBinder extends UiBinder<Element, AttributeValueView> {
        // nothing to do
    }

    /** The UI binder instance. */
    private static AttributeValueUiBinder uiBinder = GWT.create(AttributeValueUiBinder.class);

    /** The add button element. */
    @UiField
    protected DivElement m_addButtonElement;

    /** The down button elemenet. */
    @UiField
    protected DivElement m_downButtonElement;

    /** The label element. */
    @UiField
    protected SpanElement m_label;

    /** The remove button element. */
    @UiField
    protected DivElement m_removeButtonElement;

    /** The up button element. */
    @UiField
    protected DivElement m_upButtonElement;

    /** The widget holder elemenet. */
    @UiField
    protected DivElement m_widgetHolder;

    /** The add button. */
    private SimpleButton m_addButton;

    /** The down button. */
    private SimpleButton m_downButton;

    /** Flag indicating if there is a value set for this UI object. */
    private boolean m_hasValue;

    /** The remove button. */
    private SimpleButton m_removeButton;

    /** The up button. */
    private SimpleButton m_upButton;

    /** The attribute handler. */
    private AttributeHandler m_handler;

    /**
     * Constructor.<p>
     * 
     * @param handler the attribute handler
     * @param label the attribute label
     * @param help the attribute help information
     */
    public AttributeValueView(AttributeHandler handler, String label, String help) {

        setElement(uiBinder.createAndBindUi(this));
        m_handler = handler;
        m_handler.registerAttributeValue(this);
        m_label.setInnerHTML(label);
        m_label.setTitle(help);
    }

    /**
     * Returns the attribute handler.<p>
     * 
     * @return the attribute handler
     */
    protected AttributeHandler getHandler() {

        return m_handler;
    }

    /**
     * Returns the attribute value index.<p>
     * 
     * @return the attribute value index
     */
    public int getValueIndex() {

        int result = 0;
        Node previousSibling = getElement().getPreviousSibling();
        while (previousSibling != null) {
            result++;
            previousSibling = previousSibling.getPreviousSibling();
        }
        return result;
    }

    /**
     * Returns if there is a value set for this attribute.<p>
     * 
     * @return <code>true</code> if there is a value set for this attribute
     */
    public boolean hasValue() {

        return m_hasValue;
    }

    /**
     * Removes the value.<p>
     */
    public void removeValue() {

        m_hasValue = false;
        m_widgetHolder.setInnerHTML("");
    }

    /**
     * Sets the value entity.<p>
     * 
     * @param renderer the entity renderer
     * @param value the value entity
     */
    public void setValueEntity(I_EntityRenderer renderer, I_Entity value) {

        if (m_hasValue) {
            throw new RuntimeException("Value has already been set");
        }
        m_hasValue = true;
        Element entityDiv = DOM.createDiv();
        m_widgetHolder.appendChild(entityDiv);
        renderer.renderForm(value, entityDiv);
    }

    /**
     * Sets the value widget.<p>
     * 
     * @param widget the widget
     * @param value the value
     */
    public void setValueWidget(I_EditWidget widget, String value) {

        if (m_hasValue) {
            throw new RuntimeException("Value has already been set");
        }
        m_hasValue = true;
        Element valueDiv = DOM.createDiv();
        m_widgetHolder.appendChild(valueDiv);
        valueDiv.setInnerHTML(value);
        valueDiv.addClassName(I_LayoutBundle.INSTANCE.form().widget());
        widget.initWidget(valueDiv);
        widget.addValueChangeHandler(new ChangeHandler());
    }

    /**
     * Updates the visibility of the add, remove, up and down buttons.<p>
     * 
     * @param hasAddButton <code>true</code> if the add button should be visible
     * @param hasRemoveButton <code>true</code> if the remove button should be visible
     * @param hasSortButtons <code>true</code> if the sort buttons should be visible
     */
    public void updateButtonVisibility(boolean hasAddButton, boolean hasRemoveButton, boolean hasSortButtons) {

        if (m_addButton == null) {
            initButtons();
        }
        if (hasAddButton) {
            m_addButton.getElement().getStyle().clearDisplay();
        } else {
            m_addButton.getElement().getStyle().setDisplay(Display.NONE);
        }
        if (hasRemoveButton) {
            m_removeButton.getElement().getStyle().clearDisplay();
        } else {
            m_removeButton.getElement().getStyle().setDisplay(Display.NONE);
        }
        if (hasSortButtons && (getValueIndex() != 0)) {
            m_upButton.getElement().getStyle().clearDisplay();
        } else {
            m_upButton.getElement().getStyle().setDisplay(Display.NONE);
        }
        if (hasSortButtons && (getElement().getNextSibling() != null)) {
            m_downButton.getElement().getStyle().clearDisplay();
        } else {
            m_downButton.getElement().getStyle().setDisplay(Display.NONE);
        }
    }

    /**
     * Initializes the buttons.<p>
     */
    private void initButtons() {

        m_addButtonElement.setInnerText("+");
        m_addButton = new SimpleButton(m_addButtonElement);
        m_addButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {

                getHandler().addNewAttributeValue(AttributeValueView.this);

            }
        });
        m_removeButtonElement.setInnerText("-");
        m_removeButton = new SimpleButton(m_removeButtonElement);
        m_removeButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {

                getHandler().removeAttributeValue(AttributeValueView.this);

            }
        });
        m_upButtonElement.setInnerHTML("&uarr;");
        m_upButton = new SimpleButton(m_upButtonElement);
        m_upButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {

                getHandler().moveAttributeValueUp(AttributeValueView.this);

            }
        });
        m_downButtonElement.setInnerHTML("&darr;");
        m_downButton = new SimpleButton(m_downButtonElement);
        m_downButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {

                getHandler().moveAttributeValueDown(AttributeValueView.this);

            }
        });
    }
}
