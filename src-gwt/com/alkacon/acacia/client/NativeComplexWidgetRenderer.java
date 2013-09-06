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

import com.alkacon.acacia.client.css.I_LayoutBundle;
import com.alkacon.acacia.shared.AcaciaConstants;
import com.alkacon.acacia.shared.TabInfo;
import com.alkacon.geranium.client.ui.FlowPanel;
import com.alkacon.geranium.client.ui.TabbedPanel;
import com.alkacon.vie.client.I_Vie;
import com.alkacon.vie.client.Vie;
import com.alkacon.vie.shared.I_Entity;
import com.alkacon.vie.shared.I_EntityAttribute;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.Panel;

/**
 * Renderer which delegates the rendering of an entity to native Javascript.
 * 
 * This renderer will interpret its configuration string as a JSON object (which we will call 'config').
 * To render an entity, it will take the name of a function from config.render and then call the function
 * with the entity to render, the parent element, a VIE wrapper, and the configuration object as parameters. 
 */
public class NativeComplexWidgetRenderer implements I_EntityRenderer {

    /** The entity CSS class. */
    public static final String ENTITY_CLASS = I_LayoutBundle.INSTANCE.form().entity();

    /** The attribute label CSS class. */
    public static final String LABEL_CLASS = I_LayoutBundle.INSTANCE.form().label();

    /** The widget holder CSS class. */
    public static final String WIDGET_HOLDER_CLASS = I_LayoutBundle.INSTANCE.form().widgetHolder();

    /** The configuration string. */
    private String m_configuration;

    /** The parsed JSON value from the configuration string. */
    private JSONObject m_jsonConfig;

    /** The native renderer instance. */
    private JavaScriptObject m_nativeInstance;

    /** 
     * Default constructor.<p>
     */
    public NativeComplexWidgetRenderer() {

    }

    /**
     * Creates a new configured instance.<p>
     * 
     * @param configuration the configuration string 
     */
    public NativeComplexWidgetRenderer(String configuration) {

        m_configuration = configuration;
        m_jsonConfig = JSONParser.parseLenient(configuration).isObject();
    }

    /**
     * @see com.alkacon.acacia.client.I_EntityRenderer#configure(java.lang.String)
     */
    public NativeComplexWidgetRenderer configure(String configuration) {

        return new NativeComplexWidgetRenderer(configuration);
    }

    /**
     * @see com.alkacon.acacia.client.I_EntityRenderer#getName()
     */
    public String getName() {

        return AcaciaConstants.NATIVE_RENDERER;
    }

    /**
     * @see com.alkacon.acacia.client.I_EntityRenderer#renderAttributeValue(com.alkacon.vie.shared.I_Entity, java.lang.String, int, com.google.gwt.user.client.ui.Panel)
     */
    public void renderAttributeValue(I_Entity parentEntity, String attributeName, int attributeIndex, Panel context) {

        notSupported();
    }

    /**
     * @see com.alkacon.acacia.client.I_EntityRenderer#renderForm(com.alkacon.vie.shared.I_Entity, java.util.List, com.google.gwt.user.client.ui.Panel, com.alkacon.acacia.client.I_AttributeHandler, int)
     */
    public TabbedPanel<FlowPanel> renderForm(
        I_Entity entity,
        List<TabInfo> tabInfos,
        Panel context,
        I_AttributeHandler parentHandler,
        int attributeIndex) {

        throw new UnsupportedOperationException("Custom renderer does not support tabs!");

    }

    /**
     * @see com.alkacon.acacia.client.I_EntityRenderer#renderForm(com.alkacon.vie.shared.I_Entity, com.google.gwt.user.client.ui.Panel, com.alkacon.acacia.client.I_AttributeHandler, int)
     */
    public void renderForm(
        final I_Entity entity,
        Panel context,
        final I_AttributeHandler parentHandler,
        final int attributeIndex) {

        context.addStyleName(ENTITY_CLASS);
        context.getElement().setAttribute("typeof", entity.getTypeName());
        context.getElement().setAttribute("about", entity.getId());
        String initFunction = AcaciaConstants.FUNCTION_RENDER_FORM;
        renderNative(
            getNativeInstance(),
            initFunction,
            context.getElement(),
            entity,
            Vie.getInstance(),
            m_jsonConfig.isObject().getJavaScriptObject());
    }

    /**
     * @see com.alkacon.acacia.client.I_EntityRenderer#renderInline(com.alkacon.vie.shared.I_Entity, com.google.gwt.dom.client.Element)
     */
    public void renderInline(I_Entity entity, Element context) {

        notSupported();
    }

    /**
     * @see com.alkacon.acacia.client.I_EntityRenderer#renderInline(com.alkacon.vie.shared.I_Entity, com.alkacon.acacia.client.I_InlineFormParent, com.alkacon.acacia.client.I_InlineHtmlUpdateHandler)
     */
    public void renderInline(I_Entity entity, I_InlineFormParent formParent, I_InlineHtmlUpdateHandler updateHandler) {

        notSupported();
    }

    /**
     * @see com.alkacon.acacia.client.I_EntityRenderer#renderInline(com.alkacon.vie.shared.I_Entity, java.lang.String, com.google.gwt.dom.client.Element, int, int)
     */
    public void renderInline(
        I_Entity parentEntity,
        String attributeName,
        Element context,
        int minOccurrence,
        int maxOccurrence) {

        notSupported();

    }

    /**
     * @see com.alkacon.acacia.client.I_EntityRenderer#renderInline(com.alkacon.vie.shared.I_Entity, java.lang.String, com.alkacon.acacia.client.I_InlineFormParent, com.alkacon.acacia.client.I_InlineHtmlUpdateHandler, int, int)
     */
    public void renderInline(
        I_Entity parentEntity,
        String attributeName,
        I_InlineFormParent formParent,
        I_InlineHtmlUpdateHandler updateHandler,
        int minOccurrence,
        int maxOccurrence) {

        I_EntityAttribute attribute = parentEntity.getAttribute(attributeName);
        String renderInline = AcaciaConstants.FUNCTION_RENDER_INLINE;
        if (attribute != null) {
            List<I_Entity> values = attribute.getComplexValues();
            List<Element> elements = Vie.getInstance().getAttributeElements(
                parentEntity,
                attributeName,
                formParent.getElement());
            for (int i = 0; i < elements.size(); i++) {
                Element element = elements.get(i);
                if (i < values.size()) {
                    I_Entity value = values.get(i);
                    renderNative(
                        getNativeInstance(),
                        renderInline,
                        element,
                        value,
                        Vie.getInstance(),
                        m_jsonConfig.getJavaScriptObject());
                }
            }
        }
    }

    /** 
     * Creates the native renderer instance.<p>
     * 
     * @param initCall the name of the native function which creates the native renderer instance  
     * 
     * @return the native renderer instance 
     */
    protected native JavaScriptObject createNativeInstance(String initCall) /*-{
                                                                            if ($wnd[initCall]) {
                                                                            return $wnd[initCall]();
                                                                            } else {
                                                                            throw ("No init function found: " + initCall);
                                                                            }
                                                                            }-*/;

    /** 
     * Gets the native renderer instance.<p>
     * 
     * @return the native renderer instance 
     */
    protected JavaScriptObject getNativeInstance() {

        if (m_nativeInstance == null) {
            m_nativeInstance = createNativeInstance(m_jsonConfig.get(AcaciaConstants.PARAM_INIT_CALL).isString().stringValue());
        }
        return m_nativeInstance;
    }

    /**
     * Calls the native render function.<p>
     * 
     * @param nativeRenderer the native renderer instance 
     * @param renderFunction the name of the render function 
     * @param element the element in which to render the entity 
     * @param entity the entity to render 
     * @param vie the VIE wrapper to use 
     * @param config the configuration 
     */
    protected native void renderNative(
        JavaScriptObject nativeRenderer,
        String renderFunction,
        com.google.gwt.dom.client.Element element,
        I_Entity entity,
        I_Vie vie,
        JavaScriptObject config) /*-{
                                 var entityWrapper = new $wnd.acacia.EntityWrapper();
                                 entityWrapper.setEntity(entity);
                                 var vieWrapper = new $wnd.acacia.VieWrapper();
                                 if (nativeRenderer && nativeRenderer[renderFunction]) {
                                 nativeRenderer[renderFunction](element, entityWrapper, vieWrapper,
                                 config);
                                 } else if ($wnd.console) {
                                 $wnd.console.log("Rendering function not found: " + renderFunction);
                                 }
                                 }-*/;

    /** 
     * Throws an error indicating that a method is not supported.<p>
     */
    private void notSupported() {

        throw new UnsupportedOperationException("method not supported by this renderer!");
    }
}
