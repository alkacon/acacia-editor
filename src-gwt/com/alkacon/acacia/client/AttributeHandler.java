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

import com.alkacon.acacia.client.css.I_LayoutBundle;
import com.alkacon.acacia.client.ui.AttributeValueView;
import com.alkacon.acacia.client.widgets.I_FormEditWidget;
import com.alkacon.geranium.client.dnd.DNDHandler;
import com.alkacon.geranium.client.dnd.DNDHandler.Orientation;
import com.alkacon.geranium.client.ui.TabbedPanel;
import com.alkacon.vie.client.I_Vie;
import com.alkacon.vie.shared.I_Entity;
import com.alkacon.vie.shared.I_EntityAttribute;
import com.alkacon.vie.shared.I_Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The attribute handler. Handles value changes, addition of new values, remove and move operations on values.<p> 
 */
public class AttributeHandler {

    /** Map of all attribute handlers. */
    private static final Map<String, AttributeHandler> m_attributeHandlers = new HashMap<String, AttributeHandler>();

    /** The attribute name. */
    private String m_attributeName;

    /** The attribute type. */
    private I_Type m_attributeType;

    /** Registered attribute values. */
    private List<AttributeValueView> m_attributeValueViews;

    /** The attribute drag and drop handler. */
    private DNDHandler m_dndHandler;

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
        m_attributeHandlers.put(attributeName, this);
    }

    /**
     * Clears the error styles on the given tabbed panel.<p>
     * 
     * @param tabbedPanel the tabbed panel
     */
    public static void clearErrorStyles(TabbedPanel<?> tabbedPanel) {

        for (int i = 0; i < tabbedPanel.getTabCount(); i++) {
            Widget tab = tabbedPanel.getTabWidget(i);
            tab.setTitle(null);
            tab.getParent().removeStyleName(I_LayoutBundle.INSTANCE.form().hasError());
            tab.getParent().removeStyleName(I_LayoutBundle.INSTANCE.form().hasWarning());
        }
    }

    /**
     * Returns the attribute handler for the given attribute name.<p>
     * 
     * @param attributeName the attribute name
     * 
     * @return the attribute handler
     */
    public static AttributeHandler getAttributeHandler(String attributeName) {

        return m_attributeHandlers.get(attributeName);
    }

    /**
     * Adds a new attribute value below the reference view.<p>
     * 
     * @param reference the reference value view
     */
    public void addNewAttributeValue(AttributeValueView reference) {

        // make sure not to add more values than allowed
        int maxOccurrence = getEntityType().getAttributeMaxOccurrence(m_attributeName);
        I_EntityAttribute attribute = m_entity.getAttribute(m_attributeName);
        boolean mayHaveMore = ((attribute == null) || (attribute.getValueCount() < maxOccurrence));
        if (mayHaveMore) {
            if (getAttributeType().isSimpleType()) {
                String value = m_widgetService.getDefaultAttributeValue(m_attributeName);
                I_FormEditWidget widget = m_widgetService.getAttributeFormWidget(m_attributeName);
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
                valueWidget.setValueWidget(widget, value, true);
            } else {
                I_Entity value = m_vie.createEntity(null, m_attributeType.getId());
                insertValueAfterReference(value, reference);
            }
        }
        updateButtonVisisbility();
    }

    /**
     * Adds a new choice attribute value.<p>
     * 
     * @param reference the reference value view
     * @param attributeChoice the attribute choice
     */
    public void addNewChoiceAttributeValue(AttributeValueView reference, String attributeChoice) {

        I_Entity value = m_vie.createEntity(null, getAttributeType().getId());
        // create the attribute choice
        I_Type choiceType = getAttributeType().getAttributeType(attributeChoice);
        if (choiceType.isSimpleType()) {
            String choiceValue = m_widgetService.getDefaultAttributeValue(attributeChoice);
            value.addAttributeValue(attributeChoice, choiceValue);
        } else {
            I_Entity choiceValue = m_vie.createEntity(null, choiceType.getId());
            value.addAttributeValue(attributeChoice, choiceValue);
        }
        insertValueAfterReference(value, reference);
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
     * Destroys the attribute handler instance.<p>
     */
    public void destroy() {

        m_attributeHandlers.remove(m_attributeName);
        m_attributeName = null;
        m_attributeType = null;
        m_attributeValueViews.clear();
        m_attributeValueViews = null;
        m_dndHandler = null;
        m_entity = null;
        m_entityType = null;
        m_vie = null;
        m_widgetService = null;
    }

    /**
     * Returns the attribute name.<p>
     * 
     * @return the attribute name
     */
    public String getAttributeName() {

        return m_attributeName;
    }

    /**
     * Returns the drag and drop handler.<p>
     * 
     * @return the drag and drop handler
     */
    public DNDHandler getDNDHandler() {

        if (m_dndHandler == null) {
            m_dndHandler = new DNDHandler(new AttributeDNDController());
            m_dndHandler.setOrientation(Orientation.VERTICAL);
        }
        return m_dndHandler;
    }

    /**
     * Moves the give attribute value from one position to another.<p>
     * 
     * @param valueView the value to move
     * @param currentPosition the current position
     * @param targetPosition the target position
     */
    public void moveAttributeValue(AttributeValueView valueView, int currentPosition, int targetPosition) {

        if (currentPosition == targetPosition) {
            return;
        }
        FlowPanel parent = (FlowPanel)valueView.getParent();

        valueView.removeFromParent();
        m_attributeValueViews.remove(valueView);
        if (getAttributeType().isSimpleType()) {
            String value = m_entity.getAttribute(m_attributeName).getSimpleValues().get(currentPosition);
            m_entity.removeAttributeValue(m_attributeName, currentPosition);
            m_entity.insertAttributeValue(m_attributeName, value, targetPosition);
            AttributeValueView valueWidget = new AttributeValueView(
                this,
                m_widgetService.getAttributeLabel(m_attributeName),
                m_widgetService.getAttributeHelp(m_attributeName));
            parent.insert(valueWidget, targetPosition);
            valueWidget.setValueWidget(m_widgetService.getAttributeFormWidget(m_attributeName), value, true);
            HighlightingHandler.getInstance().setFocusHighlighted(valueWidget);
        } else {
            I_Entity value = m_entity.getAttribute(m_attributeName).getComplexValues().get(currentPosition);
            m_entity.removeAttributeValue(m_attributeName, currentPosition);
            m_entity.insertAttributeValue(m_attributeName, value, targetPosition);
            AttributeValueView valueWidget = new AttributeValueView(
                this,
                m_widgetService.getAttributeLabel(m_attributeName),
                m_widgetService.getAttributeHelp(m_attributeName));
            parent.insert(valueWidget, targetPosition);
            valueWidget.setValueEntity(
                m_widgetService.getRendererForAttribute(m_attributeName, getAttributeType()),
                value);
            HighlightingHandler.getInstance().setFocusHighlighted(valueWidget);
        }
        updateButtonVisisbility();
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
        moveAttributeValue(reference, index, index + 1);
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
        moveAttributeValue(reference, index, index - 1);
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

        I_EntityAttribute attribute = m_entity.getAttribute(m_attributeName);
        if (attribute.isSingleValue()) {
            m_entity.removeAttribute(m_attributeName);
            reference.removeValue();
            if (attribute.isSimpleValue()) {
                reference.setValueWidget(
                    m_widgetService.getAttributeFormWidget(m_attributeName),
                    m_widgetService.getDefaultAttributeValue(m_attributeName),
                    false);
            }
        } else {
            int index = reference.getValueIndex();
            m_entity.removeAttributeValue(m_attributeName, index);
            reference.removeFromParent();
            m_attributeValueViews.remove(reference);
        }
        updateButtonVisisbility();
    }

    /**
     * Sets the error message for the given value index.<p>
     * 
     * @param valueIndex the value index
     * @param message the error message
     * @param tabbedPanel the forms tabbed panel if available
     */
    public void setErrorMessage(int valueIndex, String message, TabbedPanel<?> tabbedPanel) {

        if (!m_attributeValueViews.isEmpty()) {
            FlowPanel parent = (FlowPanel)m_attributeValueViews.get(0).getParent();
            AttributeValueView valueView = (AttributeValueView)parent.getWidget(valueIndex);
            valueView.setErrorMessage(message);
            if (tabbedPanel != null) {
                int tabIndex = tabbedPanel.getTabIndex(valueView.getElement());
                if (tabIndex > -1) {
                    Widget tab = tabbedPanel.getTabWidget(tabIndex);
                    tab.setTitle("This tab has errors.");
                    tab.getParent().removeStyleName(I_LayoutBundle.INSTANCE.form().hasWarning());
                    tab.getParent().addStyleName(I_LayoutBundle.INSTANCE.form().hasError());
                }

            }
        }
    }

    /**
     * Sets the warning message for the given value index.<p>
     * 
     * @param valueIndex the value index
     * @param message the warning message
     * @param tabbedPanel the forms tabbed panel if available
     */
    public void setWarningMessage(int valueIndex, String message, TabbedPanel<?> tabbedPanel) {

        if (!m_attributeValueViews.isEmpty()) {
            FlowPanel parent = (FlowPanel)m_attributeValueViews.get(0).getParent();
            AttributeValueView valueView = (AttributeValueView)parent.getWidget(valueIndex);
            valueView.setWarningMessage(message);
            if (tabbedPanel != null) {
                int tabIndex = tabbedPanel.getTabIndex(valueView.getElement());
                if (tabIndex > -1) {
                    Widget tab = tabbedPanel.getTabWidget(tabIndex);
                    tab.setTitle("This tab has warnings.");
                    tab.getParent().addStyleName(I_LayoutBundle.INSTANCE.form().hasWarning());
                }

            }
        }
    }

    /**
     * Updates the add, remove and sort button visibility on all registered attribute value views.<p>
     */
    public void updateButtonVisisbility() {

        int minOccurrence = getEntityType().getAttributeMinOccurrence(m_attributeName);
        int maxOccurrence = getEntityType().getAttributeMaxOccurrence(m_attributeName);
        I_EntityAttribute attribute = m_entity.getAttribute(m_attributeName);
        boolean mayHaveMore = (maxOccurrence > minOccurrence)
            && (((attribute == null) || (attribute.getValueCount() < maxOccurrence)));
        boolean needsRemove = false;
        boolean needsSort = false;
        if (!getEntityType().isChoice() && m_entity.hasAttribute(m_attributeName)) {
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

    /**
     * Inserts an entity value after the given reference.<p>
     * 
     * @param value the entity value
     * @param reference the reference
     */
    private void insertValueAfterReference(I_Entity value, AttributeValueView reference) {

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
        I_EntityRenderer renderer = m_widgetService.getRendererForAttribute(m_attributeName, m_attributeType);
        valueWidget.setValueEntity(renderer, value);
    }
}
