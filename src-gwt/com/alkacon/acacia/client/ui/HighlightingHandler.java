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

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * The attribute value view highlighting handler.<p>
 */
public class HighlightingHandler implements MouseOverHandler, MouseOutHandler, ClickHandler {

    /** The handler instance. */
    private static HighlightingHandler INSTANCE;

    /** The currently selected value view. */
    private AttributeValueView m_currentClick;

    /** The currently highlighted value view. */
    private AttributeValueView m_currentHover;

    /** The handler registration. */
    private HandlerRegistration m_handlerRegistration;

    /** The highlighting queue. */
    private Set<AttributeValueView> m_hoverHighlightingQueue;

    /**
     * Constructor.<p>
     */
    private HighlightingHandler() {

        m_hoverHighlightingQueue = new HashSet<AttributeValueView>();
        m_handlerRegistration = RootPanel.get().addDomHandler(this, ClickEvent.getType());
    }

    /**
     * Returns the highlighting handler instance.<p>
     * 
     * @return the highlighting handler instance
     */
    public static HighlightingHandler getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new HighlightingHandler();
        }
        return INSTANCE;
    }

    /**
     * Destroys the current handler instance.<p>
     */
    public void destroy() {

        m_currentClick = null;
        m_currentHover = null;
        if (m_hoverHighlightingQueue != null) {
            m_hoverHighlightingQueue.clear();
        }
        m_hoverHighlightingQueue = null;
        if (m_handlerRegistration != null) {
            m_handlerRegistration.removeHandler();
        }
        m_handlerRegistration = null;
        INSTANCE = null;
    }

    /**
     * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
     */
    public void onClick(ClickEvent event) {

        event.stopPropagation();
        if (RootPanel.get().equals(event.getSource())) {
            if (m_currentClick != null) {
                m_currentClick.toggleClickHighlighting(false);
                m_currentClick = null;
            }
        } else {
            if (event.getSource().equals(m_currentClick)) {
                return;
            }
            if ((m_currentClick != null)) {
                m_currentClick.toggleClickHighlighting(false);
            }
            m_currentClick = (AttributeValueView)event.getSource();
            m_currentClick.toggleClickHighlighting(true);
        }
    }

    /**
     * @see com.google.gwt.event.dom.client.MouseOutHandler#onMouseOut(com.google.gwt.event.dom.client.MouseOutEvent)
     */
    public void onMouseOut(MouseOutEvent event) {

        AttributeValueView source = (AttributeValueView)event.getSource();
        m_hoverHighlightingQueue.remove(source);
        source.toggleHoverHighlighting(false);
        if (source.equals(m_currentHover)) {
            m_currentHover = null;
            if (!m_hoverHighlightingQueue.isEmpty()) {
                for (AttributeValueView queued : m_hoverHighlightingQueue) {
                    if ((m_currentHover == null) || m_currentHover.getElement().isOrHasChild(queued.getElement())) {
                        m_currentHover = queued;
                    }
                }
                m_currentHover.toggleHoverHighlighting(true);
            }
        }
    }

    /**
     * @see com.google.gwt.event.dom.client.MouseOverHandler#onMouseOver(com.google.gwt.event.dom.client.MouseOverEvent)
     */
    public void onMouseOver(MouseOverEvent event) {

        if (m_currentHover == null) {
            m_currentHover = (AttributeValueView)event.getSource();
            m_currentHover.toggleHoverHighlighting(true);
        } else {
            AttributeValueView source = (AttributeValueView)event.getSource();
            if (source.equals(m_currentHover)) {
                // already highlighted
                return;
            }
            if (m_currentHover.getElement().isOrHasChild(source.getElement())) {
                // cursor moved from parent to child, highlight the child and queue the parent
                m_hoverHighlightingQueue.add(m_currentHover);
                m_currentHover.toggleHoverHighlighting(false);
                m_currentHover = source;
                m_currentHover.toggleHoverHighlighting(true);
            } else if (source.getElement().isOrHasChild(m_currentHover.getElement())) {
                // cursor is within parent and child, keep highlighting the child and queue the parent
                m_hoverHighlightingQueue.add(source);
            } else {
                // current is neither child nor parent of event source, 
                // switch highlighting to the event source and remove current from the queue
                m_hoverHighlightingQueue.remove(m_currentHover);
                m_currentHover.toggleHoverHighlighting(false);
                m_currentHover = source;
                m_currentHover.toggleHoverHighlighting(true);
            }
        }
    }

}
