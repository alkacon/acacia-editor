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
import com.alkacon.vie.shared.I_Type;

import java.util.HashMap;
import java.util.Map;

/**
 * Service providing form widget renderer for entity attributes.<p>
 */
public class WidgetService implements I_WidgetService {

    /** The attribute configurations. */
    private Map<String, AttributeConfiguration> m_attributeConfigurations;

    /** The in-line renderer. */
    private I_EntityRenderer m_defaultRenderer;

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
        m_attributeConfigurations = new HashMap<String, AttributeConfiguration>();
    }

    /**
     * @see com.alkacon.acacia.client.I_WidgetService#addConfigurations(java.util.Map)
     */
    public void addConfigurations(Map<String, AttributeConfiguration> configurations) {

        m_attributeConfigurations.putAll(configurations);
    }

    /**
     * Adds a renderer for the given type.<p>
     * 
     * @param typeName the type name 
     * @param renderer the renderer
     */
    public void addRenderer(String typeName, I_EntityRenderer renderer) {

        m_rendererByType.put(typeName, renderer);
    }

    /**
     * @see com.alkacon.acacia.client.I_WidgetService#addWidgetFactory(java.lang.String, com.alkacon.acacia.client.I_WidgetFactory)
     */
    public void addWidgetFactory(String widgetName, I_WidgetFactory widgetFactory) {

        m_widgetFactories.put(widgetName, widgetFactory);
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
     * @see com.alkacon.acacia.client.I_WidgetService#getDefaultAttributeValue(java.lang.String)
     */
    public String getDefaultAttributeValue(String attributeName) {

        AttributeConfiguration config = m_attributeConfigurations.get(attributeName);
        return (config != null) && (config.getDefaultValue() != null) ? config.getDefaultValue() : "";

    }

    /**
     * @see com.alkacon.acacia.client.I_WidgetService#getRendererForAttribute(java.lang.String, com.alkacon.vie.shared.I_Type)
     */
    public I_EntityRenderer getRendererForAttribute(String attributeName, I_Type attributeType) {

        return getRendererForType(attributeType);
    }

    /**
     * @see com.alkacon.acacia.client.I_WidgetService#getRendererForType(com.alkacon.vie.shared.I_Type)
     */
    public I_EntityRenderer getRendererForType(I_Type entityType) {

        if (m_rendererByType.containsKey(entityType.getId())) {
            return m_rendererByType.get(entityType.getId());
        }
        return m_defaultRenderer;
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
     * Adds the default complex type renderer.<p>
     * 
     * @param renderer the renderer
     */
    public void setDefaultRenderer(I_EntityRenderer renderer) {

        m_defaultRenderer = renderer;
    }

    /**
     * @see com.alkacon.acacia.client.I_WidgetService#setWidgetFactories(java.util.Map)
     */
    public void setWidgetFactories(Map<String, I_WidgetFactory> widgetFactories) {

        m_widgetFactories = widgetFactories;
    }

}
