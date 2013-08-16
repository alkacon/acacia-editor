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

package com.alkacon.acacia.client.ui;

import com.alkacon.acacia.client.ButtonBarHandler;
import com.alkacon.acacia.client.ChoiceMenuEntryBean;
import com.alkacon.acacia.client.I_WidgetService;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Handles mouse over / out events on choice menu entries by displaying submenus if the corresponding choice menu entries are
 * not leaf entries.<p>
 */
public class ChoiceMenuHandler implements MouseOverHandler, MouseOutHandler {

    /**
     * The singleton instance of this handler.<p>
     */
    public static final ChoiceMenuHandler INSTANCE = new ChoiceMenuHandler();

    /** The tineout after which the menus are hidden after a mouse out event. */
    private static final int TIMEOUT = 900;

    /** The currently active attribute choice widget. */
    private AttributeChoiceWidget m_attributeChoiceWidget;

    /** The timer which is started after mouseout events. */
    private Timer m_mouseoutTimer = new Timer() {

        @Override
        public void run() {

            closeAll();
        }

    };

    /** The currently active submenus. */
    private List<ChoiceSubmenu> m_submenus = new ArrayList<ChoiceSubmenu>();

    /** Flag which indicates  whether we have already installed the preview handler. */
    private static boolean installedPreviewHandler = false;

    /**
     * Creates a new instance.<p>
     */
    public ChoiceMenuHandler() {

        if (!installedPreviewHandler) {

            Event.addNativePreviewHandler(new NativePreviewHandler() {

                public void onPreviewNativeEvent(NativePreviewEvent event) {

                    NativeEvent nativeEvent = event.getNativeEvent();
                    if (event.getTypeInt() != Event.ONMOUSEDOWN) {
                        return;
                    }
                    if (nativeEvent == null) {
                        return;
                    }
                    EventTarget target = nativeEvent.getEventTarget();

                    if (Element.is(target)) {
                        Element targetElement = Element.as(target);
                        AttributeValueView view = ButtonBarHandler.INSTANCE.getView();
                        if (view == null) {
                            return;
                        }
                        boolean clickedOnMenu = view.hasButtonElement(targetElement);
                        if (!clickedOnMenu) {
                            ChoiceMenuHandler.INSTANCE.closeAll();
                            ButtonBarHandler.INSTANCE.hideCurrent();
                        }
                    }
                }
            });
        }
        installedPreviewHandler = true;
    }

    /**
     * Creates a new menu entry widget.<p>
     * 
     * @param paramWidgetService the widget service 
     * @param menuEntry the menu entry bean  
     * @param selectHandler the select handler 
     * @param choiceWidget the root menu 
     * @param submenu the submenu (may be null for root menu entries)  
     * 
     * @return the new menu entry 
     */
    public ChoiceMenuEntryWidget createMenuEntryWidget(
        I_WidgetService paramWidgetService,
        final ChoiceMenuEntryBean menuEntry,
        final AsyncCallback<ChoiceMenuEntryBean> selectHandler,
        final AttributeChoiceWidget choiceWidget,
        ChoiceSubmenu submenu) {

        ChoiceMenuEntryWidget choice = new ChoiceMenuEntryWidget(
            paramWidgetService,
            menuEntry,
            selectHandler,
            choiceWidget,
            submenu);
        return choice;
    }

    /**
     * Checks if an attribute choice widget is currently active and has submenus.<p>
     *  
     * @param attributeChoiceWidget the attribute choice widget to check 
     * 
     * @return true if the widget is currently active and has submenus 
     */
    public boolean hasSubmenus(AttributeChoiceWidget attributeChoiceWidget) {

        return (m_attributeChoiceWidget == attributeChoiceWidget) && !m_submenus.isEmpty();
    }

    /**
     * @see com.google.gwt.event.dom.client.MouseOutHandler#onMouseOut(com.google.gwt.event.dom.client.MouseOutEvent)
     */
    public void onMouseOut(MouseOutEvent event) {

        onMouseout((ChoiceMenuEntryWidget)event.getSource());
    }

    /**
     * Mouseover handler.<p>
     * 
     * @param attributeChoiceWidget the choice widget over which the mouseover event occurred 
     */
    public void onMouseover(AttributeChoiceWidget attributeChoiceWidget) {

        m_mouseoutTimer.cancel();
        if (attributeChoiceWidget != m_attributeChoiceWidget) {
            closeAll();
        }
        m_attributeChoiceWidget = attributeChoiceWidget;
        attributeChoiceWidget.show();
    }

    /**
     * @see com.google.gwt.event.dom.client.MouseOverHandler#onMouseOver(com.google.gwt.event.dom.client.MouseOverEvent)
     */
    public void onMouseOver(MouseOverEvent event) {

        onMouseover((ChoiceMenuEntryWidget)event.getSource());
    }

    /**
     * Adds a new submenu.<p>
     * 
     * @param entryWidget the entry widget whose children should be added to the submenu 
     */
    protected void addSubmenu(ChoiceMenuEntryWidget entryWidget) {

        ChoiceMenuEntryBean menuEntry = entryWidget.getEntryBean();
        AsyncCallback<ChoiceMenuEntryBean> selectHandler = entryWidget.getSelectHandler();
        AttributeChoiceWidget choiceWidget = entryWidget.getAttributeChoiceWidget();
        ChoiceSubmenu submenu = new ChoiceSubmenu(menuEntry);
        submenu.positionDeferred(entryWidget);
        choiceWidget.getSubmenuPanel().add(submenu);
        m_submenus.add(submenu);
        for (ChoiceMenuEntryBean subEntry : menuEntry.getChildren()) {
            submenu.addChoice(createMenuEntryWidget(
                entryWidget.getWidgetService(),
                subEntry,
                selectHandler,
                choiceWidget,
                submenu));
        }
    }

    /**
     * Removes unnecessary submenus when the user hovers over a given menu entry.<p>
     * 
     * @param entryWidget the menu entry over which the user is hovering 
     */
    protected void cleanUpSubmenus(ChoiceMenuEntryWidget entryWidget) {

        ChoiceSubmenu submenu = entryWidget.getSubmenu();
        // First remove all submenus which are deeper than the submenu in which the current entry is located  
        while (!m_submenus.isEmpty() && (getLastSubmenu() != submenu)) {
            removeSubmenu(getLastSubmenu());
        }
        // if it is a root entry, switch the attribute choice widget 
        if (submenu == null) {
            AttributeChoiceWidget choiceWidget = entryWidget.getAttributeChoiceWidget();
            if (choiceWidget != m_attributeChoiceWidget) {
                closeAll();
                m_attributeChoiceWidget = choiceWidget;
            }
        }
    }

    /**
     * Gets the last entry in the current list of active submenus.<p>
     * 
     * @return the last submenu 
     */
    protected ChoiceSubmenu getLastSubmenu() {

        return m_submenus.get(m_submenus.size() - 1);
    }

    /**
     * Event handler.<p>
     * 
     * @param widget the widget over which the event occurred
     */
    protected void onMouseout(AttributeChoiceWidget widget) {

        m_mouseoutTimer.cancel();
        m_mouseoutTimer.schedule(TIMEOUT);
    }

    /**
     * This method is called when the mouse cursor moves over a menu entry.<p>
     * 
     * @param choiceWidget the menu entry widget 
     */
    protected void onMouseout(ChoiceMenuEntryWidget choiceWidget) {

        m_mouseoutTimer.cancel();
        m_mouseoutTimer.schedule(TIMEOUT);
    }

    /**
     * This method is called when the mouse cursor moves out of a menu entry.
     * 
     * @param entryWidget the menu entry
     */
    protected void onMouseover(ChoiceMenuEntryWidget entryWidget) {

        m_mouseoutTimer.cancel();

        cleanUpSubmenus(entryWidget);
        ChoiceMenuEntryBean entryBean = entryWidget.getEntryBean();
        if (!entryBean.isLeaf()) {
            addSubmenu(entryWidget);
        }
    }

    /**
     * Removes a submenu and hides it.<p>
     * 
     * @param submenu the submenu to remove 
     */
    protected void removeSubmenu(ChoiceSubmenu submenu) {

        submenu.removeFromParent();
        m_submenus.remove(submenu);
    }

    /**
     * Closes all currently active submenus and the root menu.<p>
     */
    void closeAll() {

        if (m_attributeChoiceWidget != null) {
            m_attributeChoiceWidget.hide();
        }
        for (ChoiceSubmenu submenu : new ArrayList<ChoiceSubmenu>(m_submenus)) {
            removeSubmenu(submenu);
        }
    }

}
