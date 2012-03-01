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

package com.alkacon.acacia.client.css;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.Shared;

/**
 * Bundle for CSS resources.<p>
 */
public interface I_LayoutBundle extends ClientBundle {

    /** The style CSS resource. */
    @Shared
    public interface I_Style extends CssResource {

        /** 
         * Returns the attribute CSS class name.<p>
         * 
         * @return the attribute CSS class
         */
        String attribute();

        /**
         * Returns the attribute value CSS class name.<p>
         * 
         * @return the attribute value CSS class
         */
        String attributeValue();

        /**
         * Returns the button CSS class name.<p>
         * 
         * @return the button CSS class
         */
        String button();

        /**
         * Returns the button bar CSS class name.<p>
         * 
         * @return the button bar CSS class
         */
        String buttonBar();

        /**
         * Returns the closed bubble CSS class name.<p>
         * 
         * @return the closed bubble CSS class
         */
        String closedBubble();

        /**
         * Returns the entity CSS class name.<p>
         * 
         * @return the entity CSS class
         */
        String entity();

        /**
         * Returns the focused CSS class name.<p>
         * 
         * @return the focused CSS class
         */
        String focused();

        /**
         * Returns the help bubble CSS class name.<p>
         * 
         * @return the help bubble CSS class
         */
        String helpBubble();

        /**
         * Returns the help bubble close CSS class name.<p>
         * 
         * @return the help bubble close CSS class
         */
        String helpBubbleClose();

        /**
         * Returns the help bubble arrow CSS class name.<p>
         * 
         * @return the help bubble arrow CSS class
         */
        String helpBubbleArrow();

        /**
         * Returns the highlighting CSS class name.<p>
         * 
         * @return the highlighting CSS class
         */
        String highlighting();

        /**
         * Returns the input field CSS class name.<p>
         * 
         * @return the input field CSS class name
         */
        String input();

        /**
         * Returns the label CSS class name.<p>
         * 
         * @return the label CSS class
         */
        String label();

        /**
         * Returns the widget CSS class name.<p>
         * 
         * @return the widget CSS class
         */
        String widget();

        /**
         * Returns the widget holder CSS class name.<p>
         * 
         * @return the widget holder CSS class
         */
        String widgetHolder();
    }

    /** The bundle instance. */
    I_LayoutBundle INSTANCE = GWT.create(I_LayoutBundle.class);

    /**
     * Returns the style CSS.<p>
     * 
     * @return the style CSS
     */
    @Source("form.css")
    I_Style form();
}
