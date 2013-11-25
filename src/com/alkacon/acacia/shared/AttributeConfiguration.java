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

package com.alkacon.acacia.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * The attribute configuration. Stating the attribute label, help, widget name and widget configuration.<p>
 */
public class AttributeConfiguration implements IsSerializable {

    /** The attribute default value. */
    private String m_defaultValue;

    /** The attribute help information. */
    private String m_help;

    /** The attribute label. */
    private String m_label;

    /** The widget configuration. */
    private String m_widgetConfig;

    /** The widget name. */
    private String m_widgetName;

    /** The visibility flag. */
    private boolean m_visible;

    /**
     * Constructor.<p>
     * 
     * @param label the attribute label
     * @param help the attribute help information
     * @param widgetName the widget name
     * @param widgetConfig the widget configuration
     * @param defaultValue the attribute default value
     * @param visible if the attribute should be visible in the editor
     */
    public AttributeConfiguration(String label, String help, String widgetName, String widgetConfig, String defaultValue, boolean visible) {

        m_label = label;
        m_help = help;
        m_widgetName = widgetName;
        m_widgetConfig = widgetConfig;
        m_defaultValue = defaultValue;
        m_visible = visible;
    }

    /**
     * Constructor. Used for serialization only.<p>
     */
    protected AttributeConfiguration() {

        // nothing to do
    }

    /**
     * Returns the default value.<p>
     *
     * @return the default value
     */
    public String getDefaultValue() {

        return m_defaultValue;
    }

    /**
     * Returns the attribute help information.<p>
     *
     * @return the attribute help information
     */
    public String getHelp() {

        return m_help;
    }

    /**
     * Returns the attribute label.<p>
     *
     * @return the attribute label
     */
    public String getLabel() {

        return m_label;
    }

    /**
     * Returns the widget configuration.<p>
     *
     * @return the widget configuration
     */
    public String getWidgetConfig() {

        return m_widgetConfig;
    }

    /**
     * Returns the widget name.<p>
     *
     * @return the widget name
     */
    public String getWidgetName() {

        return m_widgetName;
    }

    /**
     * Returns if the given attribute should be visible in the editor.<p>
     * 
     * @return <code>true</code> if the given attribute should be visible in the editor
     */
    public boolean isVisible() {

        return m_visible;
    }
}
