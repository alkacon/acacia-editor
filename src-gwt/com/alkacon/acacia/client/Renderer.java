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
import com.alkacon.vie.client.I_Vie;
import com.alkacon.vie.shared.I_Entity;
import com.alkacon.vie.shared.I_EntityAttribute;
import com.alkacon.vie.shared.I_Type;

import java.util.List;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * Renders the widgets for an in-line form.<p>
 */
public class Renderer implements I_EntityRenderer {

    /** The entity CSS class. */
    public static final String ENTITY_CLASS = I_LayoutBundle.INSTANCE.form().entity();

    /** The attribute label CSS class. */
    public static final String LABEL_CLASS = I_LayoutBundle.INSTANCE.form().label();

    /** The widget holder CSS class. */
    public static final String WIDGET_HOLDER_CLASS = I_LayoutBundle.INSTANCE.form().widgetHolder();

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
                    m_widgetService.getAttributeWidget(attributeName).initWidget(
                        element,
                        parentEntity,
                        attributeName,
                        i);
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
    public void renderForm(I_Entity entity, Element context) {

        Element result = DOM.createDiv();
        context.appendChild(result);
        result.addClassName(ENTITY_CLASS);
        result.setAttribute("typeof", entity.getTypeName());
        result.setAttribute("about", entity.getId());
        I_Type entityType = m_vie.getType(entity.getTypeName());
        List<String> attributeNames = entityType.getAttributeNames();
        for (String attributeName : attributeNames) {
            I_Type attributeType = entityType.getAttributeType(attributeName);
            I_EntityRenderer renderer = m_widgetService.getRendererForAttribute(attributeName, attributeType);
            Element label = DOM.createDiv();
            label.setInnerText(m_widgetService.getAttributeLabel(attributeName));
            label.addClassName(LABEL_CLASS);
            label.setTitle(m_widgetService.getAttributeHelp(attributeName));
            result.appendChild(label);
            renderer.renderForm(
                entity,
                attributeName,
                result,
                entityType.getAttributeMinOccurrence(attributeName),
                entityType.getAttributeMaxOccurrence(attributeName));
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
        if (attribute != null) {
            Element holderDiv = DOM.createDiv();
            holderDiv.addClassName(WIDGET_HOLDER_CLASS);
            context.appendChild(holderDiv);
            if (attribute.isSimpleValue()) {
                for (int i = 0; i < attribute.getSimpleValues().size(); i++) {
                    String value = attribute.getSimpleValues().get(i);
                    Element valueDiv = DOM.createDiv();
                    valueDiv.setAttribute("property", attributeName);
                    valueDiv.setInnerHTML(value);
                    holderDiv.appendChild(valueDiv);
                    m_widgetService.getAttributeWidget(attributeName).initWidget(
                        valueDiv,
                        parentEntity,
                        attributeName,
                        i);

                }
            } else {
                for (I_Entity entity : attribute.getComplexValues()) {
                    holderDiv.setAttribute("rel", attributeName);
                    renderForm(entity, holderDiv);
                }
            }
        } else {
            //TODO: handle empty attributes
        }
    }
}
