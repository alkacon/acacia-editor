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

package com.alkacon.forms.shared.rpc;

import com.alkacon.forms.shared.ContentDefinition;
import com.alkacon.forms.shared.Entity;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The content service used to load and persist entity and type information.<p>
 * 
 * Use this asynchronous interface on the client side.<p>
 */
public interface I_ContentServiceAsync {

    /**
     * Loads the entity from the server.<p>
     * 
     * @param entityId the entity id/URI
     * @param locale the entity content locale
     * @param callback the asynchronous callback
     */
    void loadEntity(String entityId, String locale, AsyncCallback<Entity> callback);

    /**
     * Loads the content definition for a given type.<p>
     * 
     * @param typeName the type name
     * @param callback the asynchronous callback
     */
    void loadContentDefinition(String typeName, AsyncCallback<ContentDefinition> callback);

    /**
     * Saves the given entity.<p>
     * 
     * @param entity the entity to save
     * @param locale the entity content locale
     * @param callback the asynchronous callback
     */
    void saveEntity(Entity entity, String locale, AsyncCallback<Void> callback);
}
