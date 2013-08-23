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
import com.alkacon.acacia.client.ui.AttributeChoiceWidget;
import com.alkacon.acacia.client.ui.ChoiceMenuEntryWidget;
import com.alkacon.acacia.client.ui.ChoiceSubmenu;

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
import com.google.gwt.user.client.ui.Widget;

/**
 * Helper class for controlling visibility of button hover bars of attribute value views.<p>
 */
public class ButtonBarHandler implements MouseOverHandler, MouseOutHandler {

    /** Global instance of the button bar handler. */
    public static final ButtonBarHandler INSTANCE = new ButtonBarHandler();

    /** The timeout for hiding the buttons. */
    public static final int TIMEOUT = 900;

    /** The visible button bar.*/
    Widget m_buttonBar;

    /** The timer for hiding the button bar. */
    private Timer m_buttonBarTimer;

    /** The visible choice menu. */
    private AttributeChoiceWidget m_choice;

    /** The timer for hiding the choice menu. */
    private Timer m_choiceTimer;

    /** The currently active submenus. */
    private List<ChoiceSubmenu> m_submenus = new ArrayList<ChoiceSubmenu>();

    /**
     * Constructor.<p>
     */
    private ButtonBarHandler() {

        Event.addNativePreviewHandler(new NativePreviewHandler() {

            public void onPreviewNativeEvent(NativePreviewEvent event) {

                NativeEvent nativeEvent = event.getNativeEvent();
                if (event.getTypeInt() != Event.ONMOUSEDOWN) {
                    return;
                }
                if (nativeEvent == null) {
                    return;
                }
                if (m_buttonBar == null) {
                    return;
                }
                EventTarget target = nativeEvent.getEventTarget();

                if (Element.is(target)) {
                    Element targetElement = Element.as(target);
                    boolean clickedOnMenu = m_buttonBar.getElement().isOrHasChild(targetElement);
                    if (!clickedOnMenu) {
                        closeAll();

                    }
                }
            }
        });
        m_choiceTimer = new Timer() {

            @Override
            public void run() {

                closeAllChoices();
            }
        };
        m_buttonBarTimer = new Timer() {

            @Override
            public void run() {

                closeAll();
            }
        };
    }

    /**
     * Closes all visible button bars and menus.<p>
     */
    public void closeAll() {

        if (m_buttonBar != null) {
            setButtonBarVisibility(m_buttonBar, false);
            m_buttonBar = null;
        }
        closeAllChoices();
    }

    /**
     * @see com.google.gwt.event.dom.client.MouseOutHandler#onMouseOut(com.google.gwt.event.dom.client.MouseOutEvent)
     */
    public void onMouseOut(MouseOutEvent event) {

        Object source = event.getSource();
        if ((source instanceof AttributeChoiceWidget) || (source instanceof ChoiceMenuEntryWidget)) {
            rescheduleChoiceTimer();
        } else {
            rescheduleButtonBarTimer();
        }
    }

    /**
     * @see com.google.gwt.event.dom.client.MouseOverHandler#onMouseOver(com.google.gwt.event.dom.client.MouseOverEvent)
     */
    public void onMouseOver(MouseOverEvent event) {

        cancelButtonBarTimer();
        Object source = event.getSource();
        if (source instanceof AttributeChoiceWidget) {
            overAttributeChoice((AttributeChoiceWidget)source);
        } else if (source instanceof ChoiceMenuEntryWidget) {
            overChoiceEntry((ChoiceMenuEntryWidget)source);
        } else {
            overButtonBar((Widget)source);
        }
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
            submenu.addChoice(new ChoiceMenuEntryWidget(
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
            if (choiceWidget != m_choice) {
                closeAllChoices();
                m_choice = choiceWidget;
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
    void closeAllChoices() {

        if (m_choice != null) {
            m_choice.hide();
        }
        m_choice = null;
        for (ChoiceSubmenu submenu : new ArrayList<ChoiceSubmenu>(m_submenus)) {
            removeSubmenu(submenu);
        }
    }

    /**
     * Cancels the timer.<p>
     */
    private void cancelButtonBarTimer() {

        m_buttonBarTimer.cancel();
    }

    /**
     * Cancels the timer.<p>
     */
    private void cancelChoiceTimer() {

        m_choiceTimer.cancel();
    }

    /**
     * Handles the mouse over event for a choice widget.<p>
     * 
     * @param choice the event source
     */
    private void overAttributeChoice(AttributeChoiceWidget choice) {

        cancelChoiceTimer();
        if (choice.getParent() != m_buttonBar) {
            closeAll();
            m_buttonBar = choice.getParent();
            setButtonBarVisibility(m_buttonBar, true);
        }
        if (m_choice != choice) {
            closeAllChoices();
            m_choice = choice;
            m_choice.show();
        }
    }

    /**
     * Handles the mouse over event for a button bar.<p>
     * 
     * @param buttonBar the event source
     */
    private void overButtonBar(Widget buttonBar) {

        if (buttonBar != m_buttonBar) {
            closeAll();
            m_buttonBar = buttonBar;
            setButtonBarVisibility(m_buttonBar, true);
        }
    }

    /**
     * Handles the mouse over event for a choice menu entry.<p>
     * 
     * @param entryWidget the event source
     */
    private void overChoiceEntry(ChoiceMenuEntryWidget entryWidget) {

        cancelChoiceTimer();
        cleanUpSubmenus(entryWidget);
        ChoiceMenuEntryBean entryBean = entryWidget.getEntryBean();
        if (!entryBean.isLeaf()) {
            addSubmenu(entryWidget);
        }
    }

    /**
     * Reschedules the timer that hides the currently visible button bar.<p>
     */
    private void rescheduleButtonBarTimer() {

        m_buttonBarTimer.cancel();
        m_buttonBarTimer.schedule(TIMEOUT);
    }

    /**
     * Reschedules the timer that hides the currently visible choice menu.<p>
     */
    private void rescheduleChoiceTimer() {

        m_choiceTimer.cancel();
        m_choiceTimer.schedule(TIMEOUT);
    }

    /**
     * Sets the button bar visibility.<p>
     * 
     * @param buttonBar the button bar
     * @param visible <code>true</code> to show the button bar
     */
    private void setButtonBarVisibility(Widget buttonBar, boolean visible) {

        String hoverStyle = I_LayoutBundle.INSTANCE.form().hoverButton();
        if (visible) {
            buttonBar.addStyleName(hoverStyle);
        } else {
            buttonBar.removeStyleName(hoverStyle);
        }
    }
}
