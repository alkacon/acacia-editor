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

import com.alkacon.vie.client.I_Vie;
import com.alkacon.vie.shared.I_Entity;
import com.alkacon.vie.shared.I_EntityAttribute;
import com.alkacon.vie.shared.I_Type;

import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Element;

/**
 * Renders the widgets for an in-line form.<p>
 */
public class InlineFormRenderer implements I_EntityRenderer {

    /**
     * The value change handler.<p>
     */
    protected class SimpleValueChangeHandler implements ValueChangeHandler<String> {

        /** The attribute name. */
        private String m_attributeName;

        /** The entity. */
        private I_Entity m_entity;

        /** The value index. */
        private int m_index;

        /**
         * Constructor.<p>
         * 
         * @param parentEntity the entity to change
         * @param attributeName the attribute name
         * @param index the value index
         */
        protected SimpleValueChangeHandler(I_Entity parentEntity, String attributeName, int index) {

            m_entity = parentEntity;
            m_attributeName = attributeName;
            m_index = index;
        }

        /**
         * @see com.google.gwt.event.logical.shared.ValueChangeHandler#onValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent)
         */
        public void onValueChange(ValueChangeEvent<String> event) {

            String newValue = event.getValue();
            m_entity.setAttributeValue(m_attributeName, newValue, m_index);
        }
    }

    /** The VIE instance. */
    private I_Vie m_vie;

    /** The widget service. */
    private I_WidgetService m_widgetService;

    /**
     * Constructor.<p>
     * 
     * @param vie the VIE instance
     * @param widgetService the widget service
     */
    public InlineFormRenderer(I_Vie vie, I_WidgetService widgetService) {

        m_vie = vie;
        m_widgetService = widgetService;
    }

    /**
     * @see com.alkacon.acacia.client.I_EntityRenderer#render(com.alkacon.vie.shared.I_Entity, com.google.gwt.user.client.Element)
     */
    public void render(I_Entity entity, Element context) {

        I_Type entityType = m_vie.getType(entity.getTypeName());
        List<String> attributeNames = entityType.getAttributeNames();
        for (String attributeName : attributeNames) {
            I_Type attributeType = entityType.getAttributeType(attributeName);
            I_EntityRenderer renderer = m_widgetService.getRendererForAttribute(attributeName, attributeType);
            renderer.render(
                entity,
                attributeName,
                context,
                entityType.getAttributeMinOccurrence(attributeName),
                entityType.getAttributeMaxOccurrence(attributeName));
        }
    }

    /**
     * @see com.alkacon.acacia.client.I_EntityRenderer#render(com.alkacon.vie.shared.I_Entity, java.lang.String, com.google.gwt.user.client.Element, int, int)
     */
    public void render(
        I_Entity parentEntity,
        String attributeName,
        Element context,
        int minOccurrence,
        int MaxOccurrence) {

        I_EntityAttribute attribute = parentEntity.getAttribute(attributeName);
        if (attribute.isSimpleValue()) {
            List<Element> elements = m_vie.getAttributeElements(parentEntity, attributeName, context);
            for (int i = 0; i < elements.size(); i++) {
                Element element = elements.get(i);
                m_widgetService.getAttributeWidget(attributeName).initWidget(element, parentEntity, attributeName, i);
            }
        } else {
            for (I_Entity entity : attribute.getComplexValues()) {
                render(entity, context);
            }
        }
    }
}
