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
import com.alkacon.acacia.client.widgets.StringWidget;
import com.alkacon.acacia.client.widgets.TinyMCEWidget;
import com.alkacon.acacia.shared.ContentDefinition;
import com.alkacon.acacia.shared.rpc.I_ContentServiceAsync;
import com.alkacon.vie.client.Entity;
import com.alkacon.vie.client.I_Vie;
import com.alkacon.vie.client.Vie;
import com.alkacon.vie.shared.I_Entity;
import com.alkacon.vie.shared.I_Type;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The content editor base.<p>
 */
public class EditorBase {

    /** The content service instance. */
    private I_ContentServiceAsync m_service;

    /** The VIE instance. */
    private I_Vie m_vie;

    /** The widget service. */
    private WidgetService m_widgetService;

    /**
     * Constructor.<p>
     * 
     * @param service the content service 
     */
    public EditorBase(I_ContentServiceAsync service) {

        m_service = service;
        m_vie = Vie.getInstance();
        m_widgetService = new WidgetService();
        I_EntityRenderer renderer = new Renderer(m_vie, m_widgetService);
        m_widgetService.setDefaultRenderer(renderer);
        m_widgetService.addWidgetFactory("string", new I_WidgetFactory() {

            public I_EditWidget createWidget(String configuration) {

                return new StringWidget();
            }
        });
        m_widgetService.addWidgetFactory("html", new I_WidgetFactory() {

            public I_EditWidget createWidget(String configuration) {

                return new TinyMCEWidget();
            }
        });
    }

    /**
     * Returns the widget service.<p>
     * 
     * @return the widget service
     */
    protected I_WidgetService getWidgetService() {

        return m_widgetService;
    }

    /**
     * Adds the value change handler to the entity with the given id.<p>
     * 
     * @param entityId the entity id
     * @param handler the change handler
     */
    public void addEntityChangeHandler(String entityId, ValueChangeHandler<I_Entity> handler) {

        I_Entity entity = m_vie.getEntity(entityId);
        if (entity != null) {
            ((Entity)entity).addValueChangeHandler(handler);
        }
    }

    /**
     * Clears all entities from VIE.<p>
     */
    public void clearVie() {

        m_vie.clearEntities();
    }

    /**
     * Returns the content service instance.<p>
     * 
     * @return the content service
     */
    public I_ContentServiceAsync getService() {

        return m_service;
    }

    /**
     * Loads the content definition for the given entity and executes the callback on success.<p>
     * 
     * @param entityId the entity id
     * @param locale the content locale
     * @param callback the callback
     */
    public void loadContentDefinition(final String entityId, final String locale, final Command callback) {

        AsyncCallback<ContentDefinition> asyncCallback = new AsyncCallback<ContentDefinition>() {

            public void onFailure(Throwable caught) {

                onRpcError(caught);
            }

            public void onSuccess(ContentDefinition result) {

                registerContentDefinition(result);
                callback.execute();
            }
        };
        getService().loadContentDefinition(entityId, locale, asyncCallback);
    }

    /**
     * Registers the types and entities of the given content definition.<p>
     * 
     * @param definition the content definition
     */
    public void registerContentDefinition(ContentDefinition definition) {

        m_widgetService.addConfigurations(definition.getConfigurations());
        I_Type baseType = definition.getTypes().get(definition.getEntityTypeName());
        m_vie.registerTypes(baseType, definition.getTypes());
        m_vie.registerTypes(baseType, definition.getTypes());
        m_vie.registerEntity(definition.getEntity());
    }

    /**
     * Renders the entity form within the given context.<p>
     * 
     * @param entityId the entity id
     * @param context the context element
     * @param inline <code>true</code> to render the entity for in-line editing, <code>false</code> to render a form
     */
    public void renderEntity(String entityId, Element context, boolean inline) {

        I_Entity entity = m_vie.getEntity(entityId);
        if (entity != null) {
            I_Type type = m_vie.getType(entity.getTypeName());
            if (inline) {
                m_widgetService.getRendererForType(type).renderInline(entity, context);
            } else {
                Element formElement = DOM.createDiv();
                context.appendChild(formElement);
                m_widgetService.getRendererForType(type).renderForm(entity, formElement);
            }
        }
    }

    /**
     * Saves the given entity.<p>
     * 
     * @param entity the entity
     * @param locale the content locale
     * @param clearOnSuccess <code>true</code> to clear all entities from VIE on success
     * @param callback the callback executed on success
     */
    public void saveEntity(I_Entity entity, String locale, final boolean clearOnSuccess, final Command callback) {

        AsyncCallback<Void> asyncCallback = new AsyncCallback<Void>() {

            public void onFailure(Throwable caught) {

                onRpcError(caught);
            }

            public void onSuccess(Void result) {

                callback.execute();
                if (clearOnSuccess) {
                    clearVie();
                }
            }
        };
        getService().saveEntity(com.alkacon.acacia.shared.Entity.serializeEntity(entity), locale, asyncCallback);
    }

    /**
     * Saves the given entity.<p>
     * 
     * @param entityId the entity id
     * @param locale the content locale
     * @param clearOnSuccess <code>true</code> to clear all entities from VIE on success
     * @param callback the callback executed on success
     */
    public void saveEntity(String entityId, String locale, boolean clearOnSuccess, Command callback) {

        I_Entity entity = m_vie.getEntity(entityId);
        saveEntity(entity, locale, clearOnSuccess, callback);
    }

    /**
     * Saves the given entity.<p>
     * 
     * @param entityId the entity id
     * @param locale the content locale
     * @param callback the callback executed on success
     */
    public void saveEntity(String entityId, String locale, Command callback) {

        I_Entity entity = m_vie.getEntity(entityId);
        saveEntity(entity, locale, false, callback);
    }

    /**
     * Handles RPC errors.<p>
     * 
     * Override this for better error handling
     * 
     * @param caught the error caught from the RPC
     */
    protected void onRpcError(Throwable caught) {

        // doing nothing
    }
}