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

import com.alkacon.acacia.client.ui.AttributeValue;
import com.alkacon.acacia.client.widgets.I_EditWidget;
import com.alkacon.vie.client.I_Vie;
import com.alkacon.vie.shared.I_Entity;
import com.alkacon.vie.shared.I_Type;

import com.google.gwt.dom.client.Node;

/**
 * The attribute handler. Handles value changes, addition of new values, remove and move operations on values.<p> 
 */
public class AttributeHandler {

    /** The attribute name. */
    private String m_attributeName;

    /** The attribute type. */
    private I_Type m_attributeType;

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
    }

    /**
     * Adds a new attribute value below the reference.<p>
     * 
     * @param reference the reference value
     */
    public void addNewAttributeValue(AttributeValue reference) {

        if (getAttributeType().isSimpleType()) {
            String value = m_widgetService.getDefaultAttributeValue(m_attributeName);
            I_EditWidget widget = m_widgetService.getAttributeWidget(m_attributeName);
            if (reference.getElement().getNextSiblingElement() == null) {
                m_entity.addAttributeValue(m_attributeName, value);
            } else {
                int index = reference.getValueIndex();
                m_entity.insertAttributeValue(m_attributeName, value, index + 1);
            }
            AttributeValue valueWidget = reference;
            if (reference.hasValue()) {
                valueWidget = new AttributeValue(
                    this,
                    m_widgetService.getAttributeLabel(m_attributeName),
                    m_widgetService.getAttributeHelp(m_attributeName));
                reference.getElement().getParentElement().insertAfter(valueWidget.getElement(), reference.getElement());
            }
            valueWidget.setValueWidget(widget, value);
        } else {
            I_Entity value = m_vie.createEntity(null, m_attributeType.getId());
            I_EntityRenderer renderer = m_widgetService.getRendererForAttribute(m_attributeName, m_attributeType);
            if (reference.getElement().getNextSiblingElement() == null) {
                m_entity.addAttributeValue(m_attributeName, value);
            } else {
                int index = reference.getValueIndex();
                m_entity.insertAttributeValue(m_attributeName, value, index + 1);
            }
            AttributeValue valueWidget = reference;
            if (reference.hasValue()) {
                valueWidget = new AttributeValue(
                    this,
                    m_widgetService.getAttributeLabel(m_attributeName),
                    m_widgetService.getAttributeHelp(m_attributeName));
                reference.getElement().getParentElement().insertAfter(valueWidget.getElement(), reference.getElement());
            }
            valueWidget.setValueEntity(renderer, value);
        }
    }

    /**
     * Changes the attribute value.<p>
     * 
     * @param reference the attribute value reference
     * @param value the value
     */
    public void changeValue(AttributeValue reference, String value) {

        m_entity.setAttributeValue(m_attributeName, value, reference.getValueIndex());
    }

    /**
     * Moves the reference value down in the value list.<p>
     * 
     * @param reference the reference value
     */
    public void moveAttributeValueDown(AttributeValue reference) {

        int index = reference.getValueIndex();
        if (index >= (m_entity.getAttribute(m_attributeName).getValueCount() - 1)) {
            return;
        }
        Node sibling = reference.getElement().getNextSibling();
        Node parent = reference.getElement().getParentElement();
        reference.getElement().removeFromParent();
        if (getAttributeType().isSimpleType()) {
            String value = m_entity.getAttribute(m_attributeName).getSimpleValues().get(index);
            m_entity.removeAttributeValue(m_attributeName, index);
            m_entity.insertAttributeValue(m_attributeName, value, index + 1);
            AttributeValue valueWidget = new AttributeValue(
                this,
                m_widgetService.getAttributeLabel(m_attributeName),
                m_widgetService.getAttributeHelp(m_attributeName));
            parent.insertAfter(valueWidget.getElement(), sibling);
            valueWidget.setValueWidget(m_widgetService.getAttributeWidget(m_attributeName), value);
        } else {
            I_Entity value = m_entity.getAttribute(m_attributeName).getComplexValues().get(index);
            m_entity.removeAttributeValue(m_attributeName, index);
            m_entity.insertAttributeValue(m_attributeName, value, index + 1);
            AttributeValue valueWidget = new AttributeValue(
                this,
                m_widgetService.getAttributeLabel(m_attributeName),
                m_widgetService.getAttributeHelp(m_attributeName));
            parent.insertAfter(valueWidget.getElement(), sibling);
            valueWidget.setValueEntity(
                m_widgetService.getRendererForAttribute(m_attributeName, getAttributeType()),
                value);
        }
    }

    /**
     * Moves the reference value up in the value list.<p>
     * 
     * @param reference the reference value
     */
    public void moveAttributeValueUp(AttributeValue reference) {

        int index = reference.getValueIndex();
        if (index == 0) {
            return;
        }
        Node sibling = reference.getElement().getPreviousSibling();
        Node parent = reference.getElement().getParentElement();
        reference.getElement().removeFromParent();
        if (getAttributeType().isSimpleType()) {
            String value = m_entity.getAttribute(m_attributeName).getSimpleValues().get(index);
            m_entity.removeAttributeValue(m_attributeName, index);
            m_entity.insertAttributeValue(m_attributeName, value, index - 1);
            AttributeValue valueWidget = new AttributeValue(
                this,
                m_widgetService.getAttributeLabel(m_attributeName),
                m_widgetService.getAttributeHelp(m_attributeName));
            parent.insertBefore(valueWidget.getElement(), sibling);
            valueWidget.setValueWidget(m_widgetService.getAttributeWidget(m_attributeName), value);
        } else {
            I_Entity value = m_entity.getAttribute(m_attributeName).getComplexValues().get(index);
            m_entity.removeAttributeValue(m_attributeName, index);
            m_entity.insertAttributeValue(m_attributeName, value, index - 1);
            AttributeValue valueWidget = new AttributeValue(
                this,
                m_widgetService.getAttributeLabel(m_attributeName),
                m_widgetService.getAttributeHelp(m_attributeName));
            parent.insertBefore(valueWidget.getElement(), sibling);
            valueWidget.setValueEntity(
                m_widgetService.getRendererForAttribute(m_attributeName, getAttributeType()),
                value);
        }
    }

    /**
     * Removes the reference attribute value.<p>
     * 
     * @param reference the reference
     */
    public void removeAttributeValue(AttributeValue reference) {

        if (m_entity.getAttribute(m_attributeName).isSingleValue()) {
            m_entity.removeAttribute(m_attributeName);
            reference.removeValue();
        } else {
            int index = reference.getValueIndex();
            m_entity.removeAttributeValue(m_attributeName, index);
            reference.getElement().removeFromParent();
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
