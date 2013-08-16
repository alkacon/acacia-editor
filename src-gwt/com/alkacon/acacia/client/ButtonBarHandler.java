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

import com.alkacon.acacia.client.ui.AttributeValueView;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Timer;

/**
 * Helper class for controlling visibility of button hover bars of attribute value views.<p>
 */
public class ButtonBarHandler {

    /**
     * Event handler class which reacts to mouseover/mouseout events by calling the corresponding methods on the instance of the surrounding class.<p>
     */
    public class EventHandler implements MouseOverHandler, MouseOutHandler {

        /** The value view instance. */
        private AttributeValueView m_handlerView;

        /** 
         * Creates a new instance.<p>
         * 
         * @param view the value view 
         */
        public EventHandler(AttributeValueView view) {

            m_handlerView = view;
        }

        /**
         * @see com.google.gwt.event.dom.client.MouseOutHandler#onMouseOut(com.google.gwt.event.dom.client.MouseOutEvent)
         */
        public void onMouseOut(MouseOutEvent event) {

            ButtonBarHandler.this.onMouseOut(m_handlerView);
        }

        /**
         * @see com.google.gwt.event.dom.client.MouseOverHandler#onMouseOver(com.google.gwt.event.dom.client.MouseOverEvent)
         */
        public void onMouseOver(MouseOverEvent event) {

            ButtonBarHandler.this.onMouseOver(m_handlerView);
        }
    }

    /** Global instance of the button bar handler. */
    public static final ButtonBarHandler INSTANCE = new ButtonBarHandler();

    /** The timer for hiding the buttons. */
    private Timer m_timer;

    /** The current value view. */
    private AttributeValueView m_view;

    /** The timeout for hiding the buttons. */
    public static final int TIMEOUT = 900;

    /**
     * Creates an event handler for mouseover/out events which delegates its methods to this class.<p>
     * 
     * @param view the view for which to create an event handler 
     * 
     * @return the event handler 
     */
    public EventHandler createEventHandler(AttributeValueView view) {

        return new EventHandler(view);
    }

    /**
     * Gets the current attribute value view.<p>
     * 
     * @return the current view 
     */
    public AttributeValueView getView() {

        return m_view;
    }

    /**
     * Hides the current button bar.<p>
     */
    public void hideCurrent() {

        changeValueView(getView(), null);
    }

    /**
     * Handles mouseout events.<p>
     * 
     * @param view the view on which the event occurred 
     */
    public void onMouseOut(final AttributeValueView view) {

        if (m_view != null) {
            m_timer = new Timer() {

                @Override
                public void run() {

                    hideCurrent();
                }
            };
            m_timer.schedule(TIMEOUT);
        }
    }

    /**
     * Handles mouseover events.<p>
     * 
     * @param view the view on which the mouseover occurred 
     */
    public void onMouseOver(AttributeValueView view) {

        changeValueView(m_view, view);
    }

    /**
     * Changes the current view.<p>
     * 
     * @param oldView the old view 
     * @param newView the new view 
     */
    protected void changeValueView(AttributeValueView oldView, AttributeValueView newView) {

        cancelTimer();
        if ((oldView != null) && (oldView != newView)) {
            oldView.setButtonsVisible(false);
        }
        if (newView != null) {
            newView.setButtonsVisible(true);
        }
        m_view = newView;
    }

    /**
     * Cancels the timer.<p>
     */
    private void cancelTimer() {

        if (m_timer != null) {
            m_timer.cancel();
            m_timer = null;
        }
    }

}
