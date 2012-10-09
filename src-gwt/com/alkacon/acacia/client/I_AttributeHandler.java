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

/**
 * Attribute handler interface.<p>
 */
public interface I_AttributeHandler {

    /**
     * Returns the handler for the given attribute at the given index.<p>
     * 
     * @param attributeName the attribute name
     * @param index the value index
     * 
     * @return the handler
     */
    AttributeHandler getChildHandler(String attributeName, int index);

    /**
     * Inserts a handler map at the given index.<p>
     * 
     * @param index the value index
     */
    void insertHandlers(int index);

    /**
     * Removes the handlers at the given index.<p>
     * 
     * @param index the value index
     */
    void removeHandlers(int index);

    /**
     * Sets a child attribute handler.<p>
     * 
     * @param index the value index
     * @param attributeName the attribute name
     * @param handler the handler
     */
    void setHandler(int index, String attributeName, AttributeHandler handler);
}
