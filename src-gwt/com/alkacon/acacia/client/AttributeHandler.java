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
import com.alkacon.acacia.shared.Type;
import com.alkacon.geranium.client.dnd.DNDHandler;
import com.alkacon.geranium.client.dnd.DNDHandler.Orientation;
import com.alkacon.geranium.client.ui.TabbedPanel;
import com.alkacon.vie.client.I_Vie;
import com.alkacon.vie.shared.I_Entity;
import com.alkacon.vie.shared.I_EntityAttribute;
import com.alkacon.vie.shared.I_Type;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The attribute handler. Handles value changes, addition of new values, remove and move operations on values.<p> 
 */
public class AttributeHandler extends RootHandler {

    /** The global widget resize handler. */
    private static ResizeHandler m_resizeHandler;

    /** The scroll element. */
    private static Element m_scrollElement;

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
        if (!getAttributeType().isSimpleType()) {
            int count = 0;
            I_EntityAttribute attribute = entity.getAttribute(attributeName);
            if (attribute != null) {
                count = attribute.getValueCount();
            }
            initHandlers(count);
        }
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
     * Returns the global widget resize handler.<p>
     * 
     * @return the global widget resize handler
     */
    public static ResizeHandler getResizeHandler() {

        return m_resizeHandler;
    }

    /**
     * Returns <code>true</code> if a global widget resize handler is present.<p>
     * 
     * @return <code>true</code> if a global widget resize handler is present
     */
    public static boolean hasResizeHandler() {

        return m_resizeHandler != null;
    }

    /**
     * Sets the global widget resize handler.<p>
     * 
     * @param handler the resize handler
     */
    public static void setResizeHandler(ResizeHandler handler) {

        m_resizeHandler = handler;
    }

    /**
     * Sets the scroll element. To be used for automatic scrolling during drag and drop.<p>
     * 
     * @param scrollElement the scroll element
     */
    public static void setScrollElement(Element scrollElement) {

        m_scrollElement = scrollElement;
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
     * @param choicePath the path of the selected (possibly nested) choice attribute, consisting of attribute names 
     */
    public void addNewChoiceAttributeValue(AttributeValueView reference, List<String> choicePath) {

        HighlightingHandler.getInstance().clearFocusHighlighting();
        if (isChoiceHandler()) {
            addChoiceOption(reference, choicePath);
        } else {
            addComplexChoiceValue(reference, choicePath);
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

        if (getEntityType().isChoice()) {
            I_Entity choice = m_entity.getAttribute(Type.CHOICE_ATTRIBUTE_NAME).getComplexValues().get(
                reference.getValueIndex());
            String attributeName = getChoiceName(reference.getValueIndex());
            if (attributeName != null) {
                choice.setAttributeValue(attributeName, value, 0);
            }
        } else {
            m_entity.setAttributeValue(m_attributeName, value, reference.getValueIndex());
        }

    }

    /**
     * Creates a sequence of nested entities according to a given path of choice attribute names.<p>
     * 
     * @param value the entity into which the new entities for the given path should be inserted 
     * @param choicePath the path of choice attributes 
     */
    public void createNestedEntitiesForChoicePath(I_Entity value, List<String> choicePath) {

        I_Entity parentValue = value;
        for (String attributeChoice : choicePath) {
            I_Type choiceType = m_vie.getType(parentValue.getTypeName()).getAttributeType(Type.CHOICE_ATTRIBUTE_NAME);
            I_Entity choice = m_vie.createEntity(null, choiceType.getId());
            parentValue.addAttributeValue(Type.CHOICE_ATTRIBUTE_NAME, choice);
            I_Type choiceOptionType = choiceType.getAttributeType(attributeChoice);
            if (choiceOptionType.isSimpleType()) {
                String choiceValue = m_widgetService.getDefaultAttributeValue(attributeChoice);
                choice.addAttributeValue(attributeChoice, choiceValue);
                break;
            } else {
                I_Entity choiceValue = m_vie.createEntity(null, choiceOptionType.getId());
                choice.addAttributeValue(attributeChoice, choiceValue);
                parentValue = choiceValue;
            }
        }
    }

    /**
     * Destroys the attribute handler instance.<p>
     */
    public void destroy() {

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
     * Returns the attribute type.<p>
     * 
     * @return the attribute type
     */
    public I_Type getAttributeType() {

        if (m_attributeType == null) {
            m_attributeType = getEntityType().getAttributeType(m_attributeName);
        }
        return m_attributeType;
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
            m_dndHandler.setScrollEnabled(true);
            m_dndHandler.setScrollElement(m_scrollElement);
        }
        return m_dndHandler;
    }

    /**
     * Returns if this is a choice handler.<p>
     * 
     * @return <code>true</code> if this is a choice handler
     */
    public boolean isChoiceHandler() {

        return Type.CHOICE_ATTRIBUTE_NAME.equals(m_attributeName);
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
        AttributeValueView valueWidget = null;
        if (isChoiceHandler()) {
            removeHandlers(currentPosition);
            I_Entity value = m_entity.getAttribute(m_attributeName).getComplexValues().get(currentPosition);
            m_entity.removeAttributeValue(m_attributeName, currentPosition);
            m_entity.insertAttributeValue(m_attributeName, value, targetPosition);
            String attributeChoice = getChoiceName(targetPosition);
            I_Type optionType = getAttributeType().getAttributeType(attributeChoice);
            valueWidget = new AttributeValueView(
                this,
                m_widgetService.getAttributeLabel(attributeChoice),
                m_widgetService.getAttributeHelp(attributeChoice));
            parent.insert(valueWidget, targetPosition);
            insertHandlers(targetPosition);
            if (optionType.isSimpleType()) {
                valueWidget.setValueWidget(
                    m_widgetService.getAttributeFormWidget(attributeChoice),
                    value.getAttribute(attributeChoice).getSimpleValue(),
                    true);
            } else {
                valueWidget.setValueEntity(
                    m_widgetService.getRendererForAttribute(attributeChoice, getAttributeType()),
                    value.getAttribute(attributeChoice).getComplexValue());
            }

            List<ChoiceMenuEntryBean> menuEntries = Renderer.getChoiceEntries(getAttributeType(), true);
            for (ChoiceMenuEntryBean menuEntry : menuEntries) {
                valueWidget.addChoice(m_widgetService, menuEntry);
            }
        } else if (getAttributeType().isSimpleType()) {
            String value = m_entity.getAttribute(m_attributeName).getSimpleValues().get(currentPosition);
            m_entity.removeAttributeValue(m_attributeName, currentPosition);
            m_entity.insertAttributeValue(m_attributeName, value, targetPosition);
            valueWidget = new AttributeValueView(
                this,
                m_widgetService.getAttributeLabel(m_attributeName),
                m_widgetService.getAttributeHelp(m_attributeName));
            parent.insert(valueWidget, targetPosition);
            valueWidget.setValueWidget(m_widgetService.getAttributeFormWidget(m_attributeName), value, true);
        } else {
            removeHandlers(currentPosition);
            I_Entity value = m_entity.getAttribute(m_attributeName).getComplexValues().get(currentPosition);
            m_entity.removeAttributeValue(m_attributeName, currentPosition);
            m_entity.insertAttributeValue(m_attributeName, value, targetPosition);
            valueWidget = new AttributeValueView(
                this,
                m_widgetService.getAttributeLabel(m_attributeName),
                m_widgetService.getAttributeHelp(m_attributeName));
            parent.insert(valueWidget, targetPosition);
            insertHandlers(targetPosition);
            valueWidget.setValueEntity(
                m_widgetService.getRendererForAttribute(m_attributeName, getAttributeType()),
                value);

        }
        HighlightingHandler.getInstance().setFocusHighlighted(valueWidget);
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
            } else {
                removeHandlers(0);
            }
        } else {
            int index = reference.getValueIndex();
            if (attribute.isComplexValue()) {
                removeHandlers(index);
            }
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

        int minOccurrence = 0;
        int maxOccurrence = 0;
        if (isChoiceHandler()) {
            minOccurrence = 1;
            maxOccurrence = getEntityType().getChoiceMaxOccurrence();
        } else {
            minOccurrence = getEntityType().getAttributeMinOccurrence(m_attributeName);
            maxOccurrence = getEntityType().getAttributeMaxOccurrence(m_attributeName);
        }
        I_EntityAttribute attribute = m_entity.getAttribute(m_attributeName);
        boolean mayHaveMore = (maxOccurrence > minOccurrence)
            && (((attribute == null) || (attribute.getValueCount() < maxOccurrence)));
        boolean needsRemove = false;
        boolean needsSort = false;
        if ((isChoiceHandler() || !getEntityType().isChoice()) && m_entity.hasAttribute(m_attributeName)) {
            int valueCount = m_entity.getAttribute(m_attributeName).getValueCount();
            needsRemove = (maxOccurrence > minOccurrence) && (valueCount > minOccurrence);
            needsSort = valueCount > 1;
        }
        for (AttributeValueView value : m_attributeValueViews) {
            value.updateButtonVisibility(mayHaveMore, needsRemove, needsSort);
        }
    }

    /**
     * Adds a new choice option.<p>
     * 
     * @param reference the reference view
     * @param choicePath the choice attribute path
     */
    private void addChoiceOption(AttributeValueView reference, List<String> choicePath) {

        String attributeChoice = choicePath.get(0);
        I_Type optionType = getAttributeType().getAttributeType(attributeChoice);

        I_Entity choiceEntity = m_vie.createEntity(null, getAttributeType().getId());
        AttributeValueView valueWidget = reference;
        if (reference.hasValue()) {
            valueWidget = new AttributeValueView(
                this,
                m_widgetService.getAttributeLabel(attributeChoice),
                m_widgetService.getAttributeHelp(attributeChoice));
        }

        List<ChoiceMenuEntryBean> menuEntries = Renderer.getChoiceEntries(getAttributeType(), true);
        for (ChoiceMenuEntryBean menuEntry : menuEntries) {
            valueWidget.addChoice(m_widgetService, menuEntry);
        }
        int valueIndex = reference.getValueIndex() + 1;
        m_entity.insertAttributeValue(m_attributeName, choiceEntity, valueIndex);
        ((FlowPanel)reference.getParent()).insert(valueWidget, valueIndex);
        insertHandlers(valueWidget.getValueIndex());

        if (optionType.isSimpleType()) {
            String value = m_widgetService.getDefaultAttributeValue(attributeChoice);
            I_FormEditWidget widget = m_widgetService.getAttributeFormWidget(attributeChoice);
            choiceEntity.addAttributeValue(attributeChoice, value);
            valueWidget.setValueWidget(widget, value, true);
        } else {
            I_Entity value = m_vie.createEntity(null, optionType.getId());
            choiceEntity.addAttributeValue(attributeChoice, value);
            List<String> remainingAttributeNames = tail(choicePath);
            createNestedEntitiesForChoicePath(value, remainingAttributeNames);
            I_EntityRenderer renderer = m_widgetService.getRendererForAttribute(attributeChoice, optionType);
            valueWidget.setValueEntity(renderer, value);
        }
        updateButtonVisisbility();

    }

    /**
     * Adds a new complex value which corresponds to a choice element.<p>
     * 
     * @param reference the reference view  
     * @param choicePath the path of choice attribute names 
     */
    private void addComplexChoiceValue(AttributeValueView reference, List<String> choicePath) {

        I_Entity value = m_vie.createEntity(null, getAttributeType().getId());
        I_Entity parentValue = value;
        for (String attributeChoice : choicePath) {
            I_Type choiceType = m_vie.getType(parentValue.getTypeName()).getAttributeType(Type.CHOICE_ATTRIBUTE_NAME);
            I_Entity choice = m_vie.createEntity(null, choiceType.getId());
            parentValue.addAttributeValue(Type.CHOICE_ATTRIBUTE_NAME, choice);
            I_Type choiceOptionType = choiceType.getAttributeType(attributeChoice);
            if (choiceOptionType.isSimpleType()) {
                String choiceValue = m_widgetService.getDefaultAttributeValue(attributeChoice);
                choice.addAttributeValue(attributeChoice, choiceValue);
                break;
            } else {
                I_Entity choiceValue = m_vie.createEntity(null, choiceOptionType.getId());
                choice.addAttributeValue(attributeChoice, choiceValue);
                parentValue = choiceValue;
            }
        }
        insertValueAfterReference(value, reference);
    }

    /**
     * Returns the attribute choice name for the given index.<p>
     * 
     * @param valueIndex the value index
     * 
     * @return the attribute choice name
     */
    private String getChoiceName(int valueIndex) {

        if (isChoiceHandler()) {
            I_Entity choice = m_entity.getAttribute(Type.CHOICE_ATTRIBUTE_NAME).getComplexValues().get(valueIndex);
            if (choice != null) {
                for (String option : getAttributeType().getAttributeNames()) {
                    if (choice.hasAttribute(option)) {
                        return option;

                    }
                }
            }
        }
        return null;
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
            Renderer.setAttributeChoice(m_widgetService, valueWidget, getAttributeType());
            if (valueIndex == -1) {
                ((FlowPanel)reference.getParent()).add(valueWidget);
            } else {
                ((FlowPanel)reference.getParent()).insert(valueWidget, valueIndex);
            }
        }
        valueIndex = valueWidget.getValueIndex();
        insertHandlers(valueIndex);
        I_EntityRenderer renderer = m_widgetService.getRendererForAttribute(m_attributeName, getAttributeType());
        valueWidget.setValueEntity(renderer, value);
    }

    /**
     * Creates a list consisting of all but the first element of another list.<p>
     * 
     * @param values the list 
     * 
     * @return the tail of the list 
     */
    private List<String> tail(List<String> values) {

        List<String> result = new ArrayList<String>();
        boolean first = true;
        for (String value : values) {
            if (!first) {
                result.add(value);
            }
            first = false;
        }
        return result;

    }
}
