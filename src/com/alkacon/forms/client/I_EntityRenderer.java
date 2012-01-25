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

import com.alkacon.vie.client.I_Entity;
import com.alkacon.vie.client.I_EntityAttribute;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

/**
 * Renders an entity into a widget.<p>
 */
public interface I_EntityRenderer {

    /**
     * Renders the given entity into a widget.<p>
     * 
     * @param entity the entity to render
     * 
     * @return the widget
     */
    Widget render(I_Entity entity);

    /**
     * Renders the attribute values.<p>
     * 
     * @param parentEntity the parent entity 
     * @param attribute the attribute
     * @param parentPanel the parent widget
     */
    void render(I_Entity parentEntity, I_EntityAttribute attribute, HasWidgets parentPanel);
}
