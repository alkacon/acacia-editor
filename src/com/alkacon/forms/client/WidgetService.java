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

package com.alkacon.forms.client;

import com.alkacon.forms.shared.AttributeConfiguration;
import com.alkacon.forms.shared.ContentDefinition;
import com.alkacon.vie.shared.I_Type;

import java.util.HashMap;
import java.util.Map;

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

    /**
     * Constructor.<p>
     */
    public WidgetService() {

        m_rendererByType = new HashMap<String, I_EntityRenderer>();
    }

    /**
     * @see com.alkacon.forms.client.I_WidgetService#getAttributeHelp(java.lang.String)
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
     * @see com.alkacon.forms.client.I_WidgetService#getAttributeLabel(java.lang.String)
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
     * @see com.alkacon.forms.client.I_WidgetService#getAttributeWidget(java.lang.String)
     */
    public I_EditWidget getAttributeWidget(String attributeName) {

        if (m_attributeConfigurations != null) {
            AttributeConfiguration config = m_attributeConfigurations.get(attributeName);
            if (config != null) {
                if (config.getWidgetName().equals("string")) {

                    return new StringWidget();
                }
            }
        }
        return new StringWidget();
    }

    /**
     * @see com.alkacon.forms.client.I_WidgetService#getRendererForAttribute(java.lang.String, com.alkacon.vie.shared.I_Type)
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
     * @see com.alkacon.forms.client.I_WidgetService#getRendererForType(com.alkacon.vie.shared.I_Type)
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
