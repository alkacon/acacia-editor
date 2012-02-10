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

import com.alkacon.acacia.client.widgets.I_EditWidget;
import com.alkacon.acacia.client.widgets.StringWidget;
import com.alkacon.acacia.shared.AttributeConfiguration;
import com.alkacon.acacia.shared.ContentDefinition;
import com.alkacon.acacia.shared.Type;
import com.alkacon.vie.client.I_Vie;
import com.alkacon.vie.shared.I_Entity;
import com.alkacon.vie.shared.I_EntityAttribute;
import com.alkacon.vie.shared.I_Type;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Service providing form widget renderer for entity attributes.<p>
 */
public class WidgetService implements I_WidgetService {

    /** The attribute configurations. */
    private Map<String, AttributeConfiguration> m_attributeConfigurations;

    /** The default complex type renderer. */
    private I_EntityRenderer m_defaultComplexTypeRenderer;

    /** The default simple type renderer. */
    private I_EntityRenderer m_defaultSimpleTypeRenderer;

    /** Map of renderer by type name. */
    private Map<String, I_EntityRenderer> m_rendererByType;

    /** The registered widget factories. */
    private Map<String, I_WidgetFactory> m_widgetFactories;

    /**
     * Constructor.<p>
     */
    public WidgetService() {

        m_rendererByType = new HashMap<String, I_EntityRenderer>();
        m_widgetFactories = new HashMap<String, I_WidgetFactory>();
    }

    /**
     * Registers the types within the content definition.<p>
     * 
     * @param vie the VIE instance
     * @param definition the content definition
     * 
     * @return the registered content entity 
     */
    public static I_Entity register(I_Vie vie, ContentDefinition definition) {

        Set<String> registeredTypes = new HashSet<String>();
        Map<String, Type> types = definition.getTypes();
        Type base = types.get(definition.getEntity().getTypeName());
        registerType(vie, base, types, registeredTypes);
        return registerEntity(vie, definition.getEntity());
    }

    /**
     * Registers the given entity within the VIE model.<p>
     * 
     * @param vie the VIE instance
     * @param entity the entity to register
     * 
     * @return the new registered entity object
     */
    public static I_Entity registerEntity(I_Vie vie, com.alkacon.acacia.shared.Entity entity) {

        I_Entity result = vie.createEntity(entity.getId(), entity.getTypeName());
        for (I_EntityAttribute attribute : entity.getAttributes()) {
            if (attribute.isSimpleValue()) {
                for (String value : attribute.getSimpleValues()) {
                    result.addAttributeValue(attribute.getAttributeName(), value);
                }
            } else {
                for (I_Entity value : attribute.getComplexValues()) {
                    result.addAttributeValue(
                        attribute.getAttributeName(),
                        registerEntity(vie, (com.alkacon.acacia.shared.Entity)value));
                }
            }
        }
        return result;
    }

    /**
     * Registers the type and it's sub-types.<p>
     * 
     * @param vie the VIE instance
     * @param type the type to register
     * @param types the available types
     * @param registered the already registered types
     */
    public static void registerType(I_Vie vie, Type type, Map<String, Type> types, Set<String> registered) {

        if (registered.contains(type.getId())) {
            return;
        }
        I_Type regType = vie.createType(type.getId());
        registered.add(type.getId());
        if (type.isSimpleType()) {
            return;
        }
        for (String attributeName : type.getAttributeNames()) {
            String attributeType = type.getAttributeTypeName(attributeName);
            registerType(vie, types.get(attributeType), types, registered);
            regType.addAttribute(
                attributeName,
                attributeType,
                type.getAttributeMinOccurrence(attributeName),
                type.getAttributeMaxOccurrence(attributeName));
        }
    }

    /**
     * @see com.alkacon.acacia.client.I_WidgetService#getAttributeHelp(java.lang.String)
     */
    public String getAttributeHelp(String attributeName) {

        if (m_attributeConfigurations != null) {
            AttributeConfiguration config = m_attributeConfigurations.get(attributeName);
            if (config != null) {
                return config.getHelp();
            }
        }
        return null;
    }

    /**
     * @see com.alkacon.acacia.client.I_WidgetService#getAttributeLabel(java.lang.String)
     */
    public String getAttributeLabel(String attributeName) {

        if (m_attributeConfigurations != null) {
            AttributeConfiguration config = m_attributeConfigurations.get(attributeName);
            if (config != null) {
                return config.getLabel();
            }
        }
        return attributeName;
    }

    /**
     * @see com.alkacon.acacia.client.I_WidgetService#getAttributeWidget(java.lang.String)
     */
    public I_EditWidget getAttributeWidget(String attributeName) {

        if (m_attributeConfigurations != null) {
            AttributeConfiguration config = m_attributeConfigurations.get(attributeName);
            if (config != null) {
                I_WidgetFactory factory = m_widgetFactories.get(config.getWidgetName());
                if (factory != null) {
                    return factory.createWidget(config.getWidgetConfig());
                }
            }
        }
        // no configuration or widget factory found, return default string widget 
        return new StringWidget();
    }

    /**
     * @see com.alkacon.acacia.client.I_WidgetService#getRendererForAttribute(java.lang.String, com.alkacon.vie.shared.I_Type)
     */
    public I_EntityRenderer getRendererForAttribute(String attributeName, I_Type attributeType) {

        if (m_attributeConfigurations != null) {
            AttributeConfiguration config = m_attributeConfigurations.get(attributeName);
            if (config != null) {

                // TODO: change render mechanism

                //            if (config.getWidgetName().equals("string")) {
                //                I_EntityRenderer renderer = new StringTypeRenderer(config.getLabel(), config.getHelp());
                //                return renderer;
                //            }
                //            if (config.getWidgetName().equals("select")) {
                //                I_EntityRenderer renderer = new SelectTypeRenderer(config.getLabel(), config.getHelp());
                //                return renderer;
                //            }
            }
        }
        if (attributeType.isSimpleType()) {
            return m_defaultSimpleTypeRenderer;
        }
        return m_defaultComplexTypeRenderer;
    }

    /**
     * @see com.alkacon.acacia.client.I_WidgetService#getRendererForType(com.alkacon.vie.shared.I_Type)
     */
    public I_EntityRenderer getRendererForType(I_Type entityType) {

        if (m_rendererByType.containsKey(entityType.getId())) {
            return m_rendererByType.get(entityType.getId());
        }
        if (entityType.isSimpleType()) {
            return m_defaultSimpleTypeRenderer;
        }
        return m_defaultComplexTypeRenderer;
    }

    /**
     * Initializes the widget service with the given content definition.<p>
     * 
     * @param definition the content definition
     */
    public void init(ContentDefinition definition) {

        m_attributeConfigurations = definition.getConfigurations();
    }

    /**
     * Registers the given widget factory with the service.<p>
     * 
     * @param widgetName the widget name
     * @param widgetFactory the widget factory
     */
    public void registerWidgetFactory(String widgetName, I_WidgetFactory widgetFactory) {

        m_widgetFactories.put(widgetName, widgetFactory);
    }

    /**
     * Adds the default complex type renderer.<p>
     * 
     * @param renderer the renderer
     */
    public void setDefaultComplexRenderer(I_EntityRenderer renderer) {

        m_defaultComplexTypeRenderer = renderer;
    }

    /**
     * Sets the default simple type renderer.<p>
     * 
     * @param renderer the renderer
     */
    public void setDefaultSimpleRenderer(I_EntityRenderer renderer) {

        m_defaultSimpleTypeRenderer = renderer;
    }

}
