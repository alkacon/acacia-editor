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

package com.alkacon.acacia.client;

import com.alkacon.acacia.shared.TabInfo;
import com.alkacon.geranium.client.ui.FlowPanel;
import com.alkacon.geranium.client.ui.TabbedPanel;
import com.alkacon.vie.shared.I_Entity;

import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Panel;

/**
 * Renders an entity into a widget.<p>
 */
public interface I_EntityRenderer {

    /**
     * Renders the given entity into a form with tabs.<p>
     * 
     * @param entity the entity to render
     * @param tabInfos the tab infos
     * @param context the context widget panel
     * 
     * @return the tabbed panel 
     */
    public TabbedPanel<FlowPanel> renderForm(I_Entity entity, List<TabInfo> tabInfos, Panel context);

    /**
     * Renders the given entity into a form.<p>
     * 
     * @param entity the entity to render
     * @param context the context widget panel
     */
    void renderForm(I_Entity entity, Panel context);

    /**
     * Injects editing widgets into the given DOM context to enable editing of the given entity.<p>
     * 
     * @param entity the entity to render
     * @param context the context DOM element
     */
    void renderInline(I_Entity entity, Element context);

    /**
     * Injects editing widgets into the given DOM context to enable editing of the given entity.<p>
     * 
     * @param entity the entity to render
     * @param formParent formParent the form parent widget
     */
    void renderInline(I_Entity entity, I_InlineFormParent formParent);

    /**
     * Injects editing widgets into the given DOM context to enable editing of the given entity attribute.<p>
     * 
     * @param parentEntity the parent entity 
     * @param attributeName the attribute name
     * @param context the context DOM element
     * @param minOccurrence the minimum occurrence of this attribute
     * @param maxOccurrence the maximum occurrence of this attribute
     */
    void renderInline(I_Entity parentEntity, String attributeName, Element context, int minOccurrence, int maxOccurrence);

    /**
     * Injects editing widgets into the given DOM context to enable editing of the given entity attribute.<p>
     * 
     * @param parentEntity the parent entity 
     * @param attributeName the attribute name
     * @param formParent the form parent widget
     * @param minOccurrence the minimum occurrence of this attribute
     * @param maxOccurrence the maximum occurrence of this attribute
     */
    void renderInline(
        I_Entity parentEntity,
        String attributeName,
        I_InlineFormParent formParent,
        int minOccurrence,
        int maxOccurrence);
}
