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

package com.alkacon.acacia.client;

import com.alkacon.acacia.client.css.I_LayoutBundle;
import com.alkacon.acacia.client.widgets.I_EditWidget;
import com.alkacon.acacia.client.widgets.SimpleButton;
import com.alkacon.vie.client.I_Vie;
import com.alkacon.vie.shared.I_Entity;
import com.alkacon.vie.shared.I_EntityAttribute;
import com.alkacon.vie.shared.I_Type;

import java.util.List;

import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * Renders the widgets for an in-line form.<p>
 */
public class Renderer implements I_EntityRenderer {

    /**
     * The widget value change handler.<p>
     */
    protected class WidgetChangeHandler implements ValueChangeHandler<String> {

        /** The entity. */
        private I_Entity m_entity;

        /** The attribute name. */
        private String m_attributeName;

        /** Flag indicating if the widget is rendered in-line or form. */
        private boolean m_isInline;

        /** The value index. */
        private int m_valueIndex;

        /**
         * Constructor.<p>
         * 
         * @param entity the entity
         * @param attributeName the attribute name
         * @param isInline flag indicating if the widget is rendered in-line or form
         * @param valueIndex the value index, only relevant for in-line rendering
         */
        protected WidgetChangeHandler(I_Entity entity, String attributeName, boolean isInline, int valueIndex) {

            m_entity = entity;
            m_attributeName = attributeName;
            m_isInline = isInline;
            m_valueIndex = valueIndex;
        }

        /**
         * @see com.google.gwt.event.logical.shared.ValueChangeHandler#onValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent)
         */
        public void onValueChange(ValueChangeEvent<String> event) {

            if (!m_isInline) {
                m_valueIndex = getWidgetElementIndex((I_EditWidget)event.getSource());
            }
            m_entity.setAttributeValue(m_attributeName, event.getValue(), m_valueIndex);

        }

        /**
         * Returns the index of the widget element.<p>
         * 
         * @param widget the widget 
         * 
         * @return the index of the widget element
         */
        private int getWidgetElementIndex(I_EditWidget widget) {

            int result = 0;
            Node previousSibling = widget.getElement().getPreviousSibling();
            while (previousSibling != null) {
                result++;
                previousSibling = previousSibling.getPreviousSibling();
            }
            return result;
        }
    }

    /** The entity CSS class. */
    public static final String ENTITY_CLASS = I_LayoutBundle.INSTANCE.form().entity();

    /** The attribute label CSS class. */
    public static final String LABEL_CLASS = I_LayoutBundle.INSTANCE.form().label();

    /** The widget holder CSS class. */
    public static final String WIDGET_HOLDER_CLASS = I_LayoutBundle.INSTANCE.form().widgetHolder();

    /** The VIE instance. */
    I_Vie m_vie;

    /** The widget service. */
    private I_WidgetService m_widgetService;

    /**
     * Constructor.<p>
     * 
     * @param vie the VIE instance
     * @param widgetService the widget service
     */
    public Renderer(I_Vie vie, I_WidgetService widgetService) {

        m_vie = vie;
        m_widgetService = widgetService;
    }

    /**
     * @see com.alkacon.acacia.client.I_EntityRenderer#renderInline(com.alkacon.vie.shared.I_Entity, com.google.gwt.user.client.Element)
     */
    public void renderInline(I_Entity entity, Element context) {

        I_Type entityType = m_vie.getType(entity.getTypeName());
        List<String> attributeNames = entityType.getAttributeNames();
        for (String attributeName : attributeNames) {
            I_Type attributeType = entityType.getAttributeType(attributeName);
            I_EntityRenderer renderer = m_widgetService.getRendererForAttribute(attributeName, attributeType);
            renderer.renderInline(
                entity,
                attributeName,
                context,
                entityType.getAttributeMinOccurrence(attributeName),
                entityType.getAttributeMaxOccurrence(attributeName));
        }
    }

    /**
     * @see com.alkacon.acacia.client.I_EntityRenderer#renderInline(com.alkacon.vie.shared.I_Entity, java.lang.String, com.google.gwt.user.client.Element, int, int)
     */
    public void renderInline(
        I_Entity parentEntity,
        String attributeName,
        Element context,
        int minOccurrence,
        int MaxOccurrence) {

        I_EntityAttribute attribute = parentEntity.getAttribute(attributeName);
        if (attribute != null) {
            if (attribute.isSimpleValue()) {
                List<Element> elements = m_vie.getAttributeElements(parentEntity, attributeName, context);
                for (int i = 0; i < elements.size(); i++) {
                    Element element = elements.get(i);
                    I_EditWidget widget = m_widgetService.getAttributeWidget(attributeName).initWidget(element);
                    widget.addValueChangeHandler(new WidgetChangeHandler(parentEntity, attributeName, true, i));
                }
            } else {
                for (I_Entity entity : attribute.getComplexValues()) {
                    renderInline(entity, context);
                }
            }
        }
    }

    /**
     * @see com.alkacon.acacia.client.I_EntityRenderer#renderForm(com.alkacon.vie.shared.I_Entity, com.google.gwt.user.client.Element)
     */
    public void renderForm(final I_Entity entity, Element context) {

        Element result = DOM.createDiv();
        context.appendChild(result);
        result.addClassName(ENTITY_CLASS);
        result.setAttribute("typeof", entity.getTypeName());
        result.setAttribute("about", entity.getId());
        I_Type entityType = m_vie.getType(entity.getTypeName());
        List<String> attributeNames = entityType.getAttributeNames();
        for (final String attributeName : attributeNames) {
            I_Type attributeType = entityType.getAttributeType(attributeName);
            I_EntityRenderer renderer = m_widgetService.getRendererForAttribute(attributeName, attributeType);
            Element label = DOM.createDiv();
            label.setInnerText(m_widgetService.getAttributeLabel(attributeName));
            label.addClassName(LABEL_CLASS);
            label.setTitle(m_widgetService.getAttributeHelp(attributeName));
            result.appendChild(label);
            final Element holderDiv = DOM.createDiv();
            holderDiv.addClassName(WIDGET_HOLDER_CLASS);
            result.appendChild(holderDiv);
            int minOccurrence = entityType.getAttributeMinOccurrence(attributeName);
            int maxOccurrence = entityType.getAttributeMaxOccurrence(attributeName);
            if (maxOccurrence > minOccurrence) {
                Element buttonEl = DOM.createButton();
                label.appendChild(buttonEl);
                buttonEl.setInnerText("+");
                SimpleButton button = new SimpleButton(buttonEl);
                button.addClickHandler(new ClickHandler() {

                    public void onClick(ClickEvent event) {

                        I_Type valueType = m_vie.getType(entity.getTypeName()).getAttributeType(attributeName);
                        if (valueType.isSimpleType()) {
                            addValueWidget(entity, attributeName, holderDiv, "");
                        } else {
                            I_Entity value = m_vie.createEntity("", valueType.getId());
                            entity.addAttributeValue(attributeName, value);
                            renderForm(value, holderDiv);
                        }
                    }
                });
            }
            renderer.renderForm(entity, attributeName, holderDiv, minOccurrence, maxOccurrence);
        }
    }

    /**
     * @see com.alkacon.acacia.client.I_EntityRenderer#renderForm(com.alkacon.vie.shared.I_Entity, java.lang.String, com.google.gwt.user.client.Element, int, int)
     */
    public void renderForm(
        I_Entity parentEntity,
        String attributeName,
        Element context,
        int minOccurrence,
        int MaxOccurrence) {

        I_EntityAttribute attribute = parentEntity.getAttribute(attributeName);
        if ((attribute == null) && (minOccurrence > 0)) {
            attribute = createEmptyAttribute(parentEntity, attributeName, minOccurrence);
        }
        if (attribute != null) {

            if (attribute.isSimpleValue()) {
                for (int i = 0; i < attribute.getSimpleValues().size(); i++) {
                    String value = attribute.getSimpleValues().get(i);
                    addValueWidget(parentEntity, attributeName, context, value);
                }
            } else {
                context.setAttribute("rel", attributeName);
                for (I_Entity entity : attribute.getComplexValues()) {
                    renderForm(entity, context);
                }
            }
        }
    }

    /**
     * Adds a value widget for the given value and attribute to the DOM context element.<p>
     * 
     * @param parentEntity the parent entity
     * @param attributeName the attribute name
     * @param context the DOM context element
     * @param value the attribute value
     */
    protected void addValueWidget(I_Entity parentEntity, String attributeName, Element context, String value) {

        Element valueDiv = DOM.createDiv();
        valueDiv.setAttribute("property", attributeName);
        valueDiv.setInnerHTML(value);
        context.appendChild(valueDiv);
        I_EditWidget widget = m_widgetService.getAttributeWidget(attributeName).initWidget(valueDiv);
        widget.addValueChangeHandler(new WidgetChangeHandler(parentEntity, attributeName, false, 0));
    }

    /**
     * Creates an empty attribute.<p>
     * 
     * @param parentEntity the parent entity
     * @param attributeName the attribute name
     * @param minOccurrence the minimum occurrence of the attribute
     * @return
     */
    private I_EntityAttribute createEmptyAttribute(I_Entity parentEntity, String attributeName, int minOccurrence) {

        I_EntityAttribute result = null;
        I_Type attributeType = m_vie.getType(parentEntity.getTypeName()).getAttributeType(attributeName);
        if (attributeType.isSimpleType()) {
            for (int i = 0; i < minOccurrence; i++) {
                parentEntity.addAttributeValue(attributeName, "");
            }
            result = parentEntity.getAttribute(attributeName);
        } else {
            for (int i = 0; i < minOccurrence; i++) {
                parentEntity.addAttributeValue(attributeName, m_vie.createEntity("", attributeType.getId()));
            }
            result = parentEntity.getAttribute(attributeName);
        }
        return result;
    }
}
