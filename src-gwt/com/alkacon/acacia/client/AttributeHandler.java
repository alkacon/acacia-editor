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

import com.alkacon.acacia.client.ui.AttributeValueView;
import com.alkacon.acacia.client.widgets.I_EditWidget;
import com.alkacon.vie.client.I_Vie;
import com.alkacon.vie.shared.I_Entity;
import com.alkacon.vie.shared.I_Type;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * The attribute handler. Handles value changes, addition of new values, remove and move operations on values.<p> 
 */
public class AttributeHandler {

    /** The attribute name. */
    private String m_attributeName;

    /** The attribute type. */
    private I_Type m_attributeType;

    /** Registered attribute values. */
    private List<AttributeValueView> m_attributeValueViews;

    /** The entity. */
    private I_Entity m_entity;

    /** The entity type. */
    private I_Type m_entityType;

    /** The VIE instance. */
    private I_Vie m_vie;

    /** The widget service. */
    private I_WidgetService m_widgetService;

    /**
     * Constructor.<p>
     * 
     * @param vie the VIE instance
     * @param entity the entity
     * @param attributeName the attribute name
     * @param widgetService the widget service
     */
    public AttributeHandler(I_Vie vie, I_Entity entity, String attributeName, I_WidgetService widgetService) {

        m_vie = vie;
        m_entity = entity;
        m_attributeName = attributeName;
        m_widgetService = widgetService;
        m_attributeValueViews = new ArrayList<AttributeValueView>();
    }

    /**
     * Adds a new attribute value below the reference view.<p>
     * 
     * @param reference the reference value view
     */
    public void addNewAttributeValue(AttributeValueView reference) {

        if (getAttributeType().isSimpleType()) {
            String value = m_widgetService.getDefaultAttributeValue(m_attributeName);
            I_EditWidget widget = m_widgetService.getAttributeWidget(m_attributeName);
            int valueIndex = -1;
            if (reference.getElement().getNextSiblingElement() == null) {
                m_entity.addAttributeValue(m_attributeName, value);
            } else {
                valueIndex = reference.getValueIndex() + 1;
                m_entity.insertAttributeValue(m_attributeName, value, valueIndex);

            }
            AttributeValueView valueWidget = reference;
            if (reference.hasValue()) {
                valueWidget = new AttributeValueView(
                    this,
                    m_widgetService.getAttributeLabel(m_attributeName),
                    m_widgetService.getAttributeHelp(m_attributeName));
                if (valueIndex == -1) {
                    ((FlowPanel)reference.getParent()).add(valueWidget);
                } else {
                    ((FlowPanel)reference.getParent()).insert(valueWidget, valueIndex);
                }

            }
            valueWidget.setValueWidget(widget, value);
        } else {
            I_Entity value = m_vie.createEntity(null, m_attributeType.getId());
            I_EntityRenderer renderer = m_widgetService.getRendererForAttribute(m_attributeName, m_attributeType);
            int valueIndex = -1;
            if (reference.getElement().getNextSiblingElement() == null) {
                m_entity.addAttributeValue(m_attributeName, value);
            } else {
                valueIndex = reference.getValueIndex() + 1;
                m_entity.insertAttributeValue(m_attributeName, value, valueIndex);
            }
            AttributeValueView valueWidget = reference;
            if (reference.hasValue()) {
                valueWidget = new AttributeValueView(
                    this,
                    m_widgetService.getAttributeLabel(m_attributeName),
                    m_widgetService.getAttributeHelp(m_attributeName));
                if (valueIndex == -1) {
                    ((FlowPanel)reference.getParent()).add(valueWidget);
                } else {
                    ((FlowPanel)reference.getParent()).insert(valueWidget, valueIndex);
                }
            }
            valueWidget.setValueEntity(renderer, value);
        }
        updateButtonVisisbility();
    }

    /**
     * Changes the attribute value.<p>
     * 
     * @param reference the attribute value reference
     * @param value the value
     */
    public void changeValue(AttributeValueView reference, String value) {

        m_entity.setAttributeValue(m_attributeName, value, reference.getValueIndex());
    }

    /**
     * Moves the reference value down in the value list.<p>
     * 
     * @param reference the reference value
     */
    public void moveAttributeValueDown(AttributeValueView reference) {

        int index = reference.getValueIndex();
        if (index >= (m_entity.getAttribute(m_attributeName).getValueCount() - 1)) {
            return;
        }
        FlowPanel parent = (FlowPanel)reference.getParent();

        reference.removeFromParent();
        m_attributeValueViews.remove(reference);
        if (getAttributeType().isSimpleType()) {
            String value = m_entity.getAttribute(m_attributeName).getSimpleValues().get(index);
            m_entity.removeAttributeValue(m_attributeName, index);
            m_entity.insertAttributeValue(m_attributeName, value, index + 1);
            AttributeValueView valueWidget = new AttributeValueView(
                this,
                m_widgetService.getAttributeLabel(m_attributeName),
                m_widgetService.getAttributeHelp(m_attributeName));
            parent.insert(valueWidget, index + 1);
            valueWidget.setValueWidget(m_widgetService.getAttributeWidget(m_attributeName), value);
            valueWidget.toggleClickHighlighting(true);
        } else {
            I_Entity value = m_entity.getAttribute(m_attributeName).getComplexValues().get(index);
            m_entity.removeAttributeValue(m_attributeName, index);
            m_entity.insertAttributeValue(m_attributeName, value, index + 1);
            AttributeValueView valueWidget = new AttributeValueView(
                this,
                m_widgetService.getAttributeLabel(m_attributeName),
                m_widgetService.getAttributeHelp(m_attributeName));
            parent.insert(valueWidget, index + 1);
            valueWidget.setValueEntity(
                m_widgetService.getRendererForAttribute(m_attributeName, getAttributeType()),
                value);
            valueWidget.toggleClickHighlighting(true);
        }
        updateButtonVisisbility();
    }

    /**
     * Moves the reference value up in the value list.<p>
     * 
     * @param reference the reference value
     */
    public void moveAttributeValueUp(AttributeValueView reference) {

        int index = reference.getValueIndex();
        if (index == 0) {
            return;
        }
        FlowPanel parent = (FlowPanel)reference.getParent();
        reference.removeFromParent();
        m_attributeValueViews.remove(reference);
        if (getAttributeType().isSimpleType()) {
            String value = m_entity.getAttribute(m_attributeName).getSimpleValues().get(index);
            m_entity.removeAttributeValue(m_attributeName, index);
            m_entity.insertAttributeValue(m_attributeName, value, index - 1);
            AttributeValueView valueWidget = new AttributeValueView(
                this,
                m_widgetService.getAttributeLabel(m_attributeName),
                m_widgetService.getAttributeHelp(m_attributeName));
            parent.insert(valueWidget, index - 1);
            valueWidget.setValueWidget(m_widgetService.getAttributeWidget(m_attributeName), value);
            valueWidget.toggleClickHighlighting(true);
        } else {
            I_Entity value = m_entity.getAttribute(m_attributeName).getComplexValues().get(index);
            m_entity.removeAttributeValue(m_attributeName, index);
            m_entity.insertAttributeValue(m_attributeName, value, index - 1);
            AttributeValueView valueWidget = new AttributeValueView(
                this,
                m_widgetService.getAttributeLabel(m_attributeName),
                m_widgetService.getAttributeHelp(m_attributeName));
            parent.insert(valueWidget, index - 1);
            valueWidget.setValueEntity(
                m_widgetService.getRendererForAttribute(m_attributeName, getAttributeType()),
                value);
            valueWidget.toggleClickHighlighting(true);
        }
        updateButtonVisisbility();
    }

    /**
     * Registers an attribute value view.<p>
     * 
     * @param attributeValue the attribute value view
     */
    public void registerAttributeValue(AttributeValueView attributeValue) {

        m_attributeValueViews.add(attributeValue);
    }

    /**
     * Removes the reference attribute value view.<p>
     * 
     * @param reference the reference view
     */
    public void removeAttributeValue(AttributeValueView reference) {

        if (m_entity.getAttribute(m_attributeName).isSingleValue()) {
            m_entity.removeAttribute(m_attributeName);
            reference.removeValue();
        } else {
            int index = reference.getValueIndex();
            m_entity.removeAttributeValue(m_attributeName, index);
            reference.removeFromParent();
            m_attributeValueViews.remove(reference);
        }
        updateButtonVisisbility();
    }

    /**
     * Updates the add, remove and sort button visibility on all registered attribute value views.<p>
     */
    public void updateButtonVisisbility() {

        int minOccurrence = getEntityType().getAttributeMinOccurrence(m_attributeName);
        int maxOccurrence = getEntityType().getAttributeMaxOccurrence(m_attributeName);
        boolean mayHaveMore = (maxOccurrence > minOccurrence)
            && ((!m_entity.hasAttribute(m_attributeName) || (m_entity.getAttribute(m_attributeName).getValueCount() < maxOccurrence)));
        boolean needsRemove = false;
        boolean needsSort = false;
        if (m_entity.hasAttribute(m_attributeName)) {
            int valueCount = m_entity.getAttribute(m_attributeName).getValueCount();
            needsRemove = (maxOccurrence > minOccurrence) && (valueCount > minOccurrence);
            needsSort = valueCount > 1;
        }
        for (AttributeValueView value : m_attributeValueViews) {
            value.updateButtonVisibility(mayHaveMore, needsRemove, needsSort);
        }
    }

    /**
     * Returns the attribute type.<p>
     * 
     * @return the attribute type
     */
    private I_Type getAttributeType() {

        if (m_attributeType == null) {
            m_attributeType = getEntityType().getAttributeType(m_attributeName);
        }
        return m_attributeType;
    }

    /**
     * Returns the entity type.<p>
     * 
     * @return the entity type
     */
    private I_Type getEntityType() {

        if (m_entityType == null) {
            m_entityType = m_vie.getType(m_entity.getTypeName());
        }
        return m_entityType;
    }

}
