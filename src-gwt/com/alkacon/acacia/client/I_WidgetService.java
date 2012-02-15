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
import com.alkacon.acacia.shared.AttributeConfiguration;
import com.alkacon.vie.shared.I_Type;

import java.util.Map;

/**
 * Provides widget renderer for entity attributes.<p>
 */
public interface I_WidgetService {

    /**
     * Adds all configurations.<p>
     * 
     * @param configurations the configurations to add
     */
    public void addConfigurations(Map<String, AttributeConfiguration> configurations);

    /**
     * Registers the given widget factory with the service.<p>
     * 
     * @param widgetName the widget name
     * @param widgetFactory the widget factory
     */
    void addWidgetFactory(String widgetName, I_WidgetFactory widgetFactory);

    /**
     * Returns the attribute help information.<p>
     * 
     * @param attributeName the attribute name
     * 
     * @return the attribute help information
     */
    String getAttributeHelp(String attributeName);

    /**
     * Returns the label for the given attribute.<p>
     * 
     * @param attributeName the attribute name
     * 
     * @return the attribute label
     */
    String getAttributeLabel(String attributeName);

    /**
     * Returns the attribute widget.<p>
     * 
     * @param attributeName the attribute name
     * 
     * @return the attribute widget
     */
    I_EditWidget getAttributeWidget(String attributeName);

    /**
     * Returns the renderer for the given attribute.<p>
     * 
     * @param attributeName the name of the attribute
     * @param attributeType the type of the attribute
     * 
     * @return the renderer
     */
    I_EntityRenderer getRendererForAttribute(String attributeName, I_Type attributeType);

    /**
     * Returns the renderer for the given entity type.<p>
     * 
     * @param entityType the type
     * 
     * @return the renderer
     */
    I_EntityRenderer getRendererForType(I_Type entityType);

    /**
     * Sets the widget factories.<p>
     * 
     * @param widgetFactories the widget factories
     */
    void setWidgetFactories(Map<String, I_WidgetFactory> widgetFactories);
}
