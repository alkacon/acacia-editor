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
import com.alkacon.acacia.client.ui.AttributeValue;
import com.alkacon.acacia.client.widgets.I_EditWidget;
import com.alkacon.vie.client.I_Vie;
import com.alkacon.vie.shared.I_Entity;
import com.alkacon.vie.shared.I_EntityAttribute;
import com.alkacon.vie.shared.I_Type;

import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;

/**
 * Renders the widgets for an in-line form.<p>
 */
public class Renderer implements I_EntityRenderer {

    /**
     * The widget value change handler.<p>
     */
    protected class WidgetChangeHandler implements ValueChangeHandler<String> {

        /** The attribute name. */
        private String m_attributeName;

        /** The entity. */
        private I_Entity m_entity;

        /** The value index. */
        private int m_valueIndex;

        /**
         * Constructor.<p>
         * 
         * @param entity the entity
         * @param attributeName the attribute name
         * @param valueIndex the value index, only relevant for in-line rendering
         */
        protected WidgetChangeHandler(I_Entity entity, String attributeName, int valueIndex) {

            m_entity = entity;
            m_attributeName = attributeName;
            m_valueIndex = valueIndex;
        }

        /**
         * @see com.google.gwt.event.logical.shared.ValueChangeHandler#onValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent)
         */
        public void onValueChange(ValueChangeEvent<String> event) {

            m_entity.setAttributeValue(m_attributeName, event.getValue(), m_valueIndex);
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
    I_WidgetService m_widgetService;

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
     * @see com.alkacon.acacia.client.I_EntityRenderer#renderForm(com.alkacon.vie.shared.I_Entity, com.google.gwt.dom.client.Element)
     */
    public void renderForm(final I_Entity entity, final Element context) {

        context.addClassName(ENTITY_CLASS);
        context.setAttribute("typeof", entity.getTypeName());
        context.setAttribute("about", entity.getId());
        I_Type entityType = m_vie.getType(entity.getTypeName());
        List<String> attributeNames = entityType.getAttributeNames();
        for (final String attributeName : attributeNames) {
            AttributeHandler handler = new AttributeHandler(m_vie, entity, attributeName, m_widgetService);
            I_Type attributeType = entityType.getAttributeType(attributeName);
            I_EntityRenderer renderer = m_widgetService.getRendererForAttribute(attributeName, attributeType);
            int minOccurrence = entityType.getAttributeMinOccurrence(attributeName);
            int maxOccurrence = entityType.getAttributeMaxOccurrence(attributeName);
            boolean mayHaveMore = (maxOccurrence > minOccurrence)
                && ((!entity.hasAttribute(attributeName) || (entity.getAttribute(attributeName).getValueCount() < maxOccurrence)));
            String label = m_widgetService.getAttributeLabel(attributeName);
            String help = m_widgetService.getAttributeHelp(attributeName);
            Element attributeElement = DOM.createDiv();
            attributeElement.addClassName(I_LayoutBundle.INSTANCE.form().attribute());
            context.appendChild(attributeElement);
            I_EntityAttribute attribute = entity.getAttribute(attributeName);
            if ((attribute == null) && (minOccurrence > 0)) {
                attribute = createEmptyAttribute(entity, attributeName, minOccurrence);
            }
            if (attribute != null) {
                int valueCount = attribute.getValueCount();
                boolean needsRemove = (maxOccurrence > minOccurrence) && (valueCount > minOccurrence);
                boolean needsSort = valueCount > 1;
                for (int i = 0; i < attribute.getValueCount(); i++) {
                    AttributeValue valueWidget = new AttributeValue(handler, label, help);
                    attributeElement.appendChild(valueWidget.getElement());
                    if (attribute.isSimpleValue()) {
                        valueWidget.setValueWidget(
                            m_widgetService.getAttributeWidget(attributeName),
                            attribute.getSimpleValues().get(i));
                    } else {
                        valueWidget.setValueEntity(renderer, attribute.getComplexValues().get(i));
                    }
                    valueWidget.updateButtonVisibility(mayHaveMore, needsRemove, needsSort);
                }
            } else {
                AttributeValue valueWidget = new AttributeValue(handler, label, help);
                attributeElement.appendChild(valueWidget.getElement());
                valueWidget.updateButtonVisibility(mayHaveMore, false, false);
            }
        }
    }

    /**
     * @see com.alkacon.acacia.client.I_EntityRenderer#renderInline(com.alkacon.vie.shared.I_Entity, com.google.gwt.dom.client.Element)
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
     * @see com.alkacon.acacia.client.I_EntityRenderer#renderInline(com.alkacon.vie.shared.I_Entity, java.lang.String, com.google.gwt.dom.client.Element, int, int)
     */
    public void renderInline(
        I_Entity parentEntity,
        String attributeName,
        Element context,
        int minOccurrence,
        int maxOccurrence) {

        I_EntityAttribute attribute = parentEntity.getAttribute(attributeName);
        if (attribute != null) {
            if (attribute.isSimpleValue()) {
                List<Element> elements = m_vie.getAttributeElements(parentEntity, attributeName, context);
                for (int i = 0; i < elements.size(); i++) {
                    Element element = elements.get(i);
                    I_EditWidget widget = m_widgetService.getAttributeWidget(attributeName).initWidget(element);
                    widget.addValueChangeHandler(new WidgetChangeHandler(parentEntity, attributeName, i));
                }
            } else {
                for (I_Entity entity : attribute.getComplexValues()) {
                    renderInline(entity, context);
                }
            }
        }
    }

    /**
     * Creates an empty attribute.<p>
     * 
     * @param parentEntity the parent entity
     * @param attributeName the attribute name
     * @param minOccurrence the minimum occurrence of the attribute
     * 
     * @return the entity attribute
     */
    protected I_EntityAttribute createEmptyAttribute(I_Entity parentEntity, String attributeName, int minOccurrence) {

        I_EntityAttribute result = null;
        I_Type attributeType = m_vie.getType(parentEntity.getTypeName()).getAttributeType(attributeName);
        if (attributeType.isSimpleType()) {
            for (int i = 0; i < minOccurrence; i++) {
                parentEntity.addAttributeValue(attributeName, m_widgetService.getDefaultAttributeValue(attributeName));
            }
            result = parentEntity.getAttribute(attributeName);
        } else {
            for (int i = 0; i < minOccurrence; i++) {
                parentEntity.addAttributeValue(attributeName, m_vie.createEntity(null, attributeType.getId()));
            }
            result = parentEntity.getAttribute(attributeName);
        }
        return result;
    }

    /**
     * Re-renders the given entity.<p>
     * 
     * @param entity the entity
     * @param context the context DOM element
     */
    protected void rerenderForm(final I_Entity entity, final Element context) {

        context.setInnerHTML("");
        renderForm(entity, context);
    }
}
