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

package com.alkacon.forms.shared;

import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Contains all information defining a content entity type.<p>
 */
public class ContentDefinition implements IsSerializable {

    /** The base type name. */
    private String m_baseType;

    /** The attribute configurations. */
    private Map<String, AttributeConfiguration> m_configurations;

    /** The types defining the entity. */
    private Map<String, Type> m_types;

    /**
     * Constructor.<p>
     * 
     * @param baseType the base type name
     * @param configurations the attribute configurations
     * @param types the types
     */
    public ContentDefinition(
        String baseType,
        Map<String, AttributeConfiguration> configurations,
        Map<String, Type> types) {

        m_baseType = baseType;
        m_configurations = configurations;
        m_types = types;
    }

    /**
     * Constructor. Used for serialization only.<p>
     */
    protected ContentDefinition() {

        // nothing to do
    }

    /**
     * Returns the base type.<p>
     *
     * @return the base type
     */
    public String getBaseType() {

        return m_baseType;
    }

    /**
     * Returns the attribute configurations.<p>
     *
     * @return the attribute configurations
     */
    public Map<String, AttributeConfiguration> getConfigurations() {

        return m_configurations;
    }

    /**
     * Returns the types.<p>
     *
     * @return the types
     */
    public Map<String, Type> getTypes() {

        return m_types;
    }
}
