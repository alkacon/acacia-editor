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

package com.alkacon.acacia.shared;

import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Contains all information defining a content entity type.<p>
 */
public class ContentDefinition implements IsSerializable {

    /** The attribute configurations. */
    private Map<String, AttributeConfiguration> m_configurations;

    /** The entity. */
    private Entity m_entity;

    /** The content locale. */
    private String m_locale;

    /** The types defining the entity. */
    private Map<String, Type> m_types;

    /**
     * Constructor.<p>
     * 
     * @param entity the entity
     * @param configurations the attribute configurations
     * @param types the types
     * @param locale the content locale
     */
    public ContentDefinition(
        Entity entity,
        Map<String, AttributeConfiguration> configurations,
        Map<String, Type> types,
        String locale) {

        m_entity = entity;
        m_configurations = configurations;
        m_types = types;
        m_locale = locale;
    }

    /**
     * Constructor. Used for serialization only.<p>
     */
    protected ContentDefinition() {

        // nothing to do
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
     * Returns the entity.<p>
     *
     * @return the entity
     */
    public Entity getEntity() {

        return m_entity;
    }

    /**
     * Returns the locale.<p>
     *
     * @return the locale
     */
    public String getLocale() {

        return m_locale;
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
