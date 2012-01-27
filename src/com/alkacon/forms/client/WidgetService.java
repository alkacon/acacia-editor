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

import com.alkacon.vie.shared.I_Type;

import java.util.HashMap;
import java.util.Map;

/**
 * Service providing form widget renderer for entity attributes.<p>
 */
public class WidgetService implements I_WidgetService {

    /** Map of renderer by attribute name. */
    private Map<String, I_EntityRenderer> m_rendererByAttribute;

    /** Map of renderer by type name. */
    private Map<String, I_EntityRenderer> m_rendererByType;

    /** The default complex type renderer. */
    private I_EntityRenderer m_defaultComplexTypeRenderer;

    /** The default simple type renderer. */
    private I_EntityRenderer m_defaultSimpleTypeRenderer;

    /**
     * Constructor.<p>
     */
    public WidgetService() {

        m_rendererByAttribute = new HashMap<String, I_EntityRenderer>();
        m_rendererByType = new HashMap<String, I_EntityRenderer>();
    }

    /**
     * Adds a renderer for the given attribute.<p>
     * 
     * @param attributeName the attribute name
     * @param renderer the renderer
     */
    public void addRendererForAttribute(String attributeName, I_EntityRenderer renderer) {

        m_rendererByAttribute.put(attributeName, renderer);
    }

    /**
     * Adds a renderer for the given type.<p>
     * 
     * @param typeName the type name
     * @param renderer the renderer
     */
    public void addRendererForType(String typeName, I_EntityRenderer renderer) {

        m_rendererByType.put(typeName, renderer);
    }

    /**
     * @see com.alkacon.forms.client.I_WidgetService#getRendererForAttribute(java.lang.String, com.alkacon.vie.shared.I_Type)
     */
    public I_EntityRenderer getRendererForAttribute(String attributeName, I_Type attributeType) {

        if (m_rendererByAttribute.containsKey(attributeName)) {
            return m_rendererByAttribute.get(attributeName);
        }
        return getRendererForType(attributeType);
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

}
