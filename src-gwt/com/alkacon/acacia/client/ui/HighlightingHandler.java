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

package com.alkacon.acacia.client.ui;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;

/**
 * The attribute value view highlighting handler.<p>
 */
public class HighlightingHandler implements MouseOverHandler, MouseOutHandler {

    /** The handler instance. */
    private static HighlightingHandler INSTANCE;

    /** The currently highlighted value view. */
    private AttributeValueView m_current;

    /** The highlighting queue. */
    private Set<AttributeValueView> m_highlightingQueue;

    /**
     * Constructor.<p>
     */
    private HighlightingHandler() {

        m_highlightingQueue = new HashSet<AttributeValueView>();
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
     * @see com.google.gwt.event.dom.client.MouseOutHandler#onMouseOut(com.google.gwt.event.dom.client.MouseOutEvent)
     */
    public void onMouseOut(MouseOutEvent event) {

        AttributeValueView source = (AttributeValueView)event.getSource();
        m_highlightingQueue.remove(source);
        source.toggleHighlighting(false);
        if (source.equals(m_current) && !m_highlightingQueue.isEmpty()) {
            m_current = null;
            for (AttributeValueView queued : m_highlightingQueue) {
                if ((m_current == null) || m_current.getElement().isOrHasChild(queued.getElement())) {
                    m_current = queued;
                }
            }
            m_current.toggleHighlighting(true);
        }
    }

    /**
     * @see com.google.gwt.event.dom.client.MouseOverHandler#onMouseOver(com.google.gwt.event.dom.client.MouseOverEvent)
     */
    public void onMouseOver(MouseOverEvent event) {

        if (m_current == null) {
            m_current = (AttributeValueView)event.getSource();
            m_current.toggleHighlighting(true);
        } else {
            AttributeValueView source = (AttributeValueView)event.getSource();
            if (m_current.getElement().isOrHasChild(source.getElement())) {
                // cursor moved from parent to child, highlight the child and queue the parent
                m_highlightingQueue.add(m_current);
                m_current.toggleHighlighting(false);
                m_current = source;
                m_current.toggleHighlighting(true);
            } else if (source.getElement().isOrHasChild(m_current.getElement())) {
                // cursor is within parent and child, keep highlighting the child and queue the parent
                m_highlightingQueue.add(source);
            } else {
                // current is neither child nor parent of event source, 
                // switch highlighting to the event source and remove current from the queue
                m_highlightingQueue.remove(m_current);
                m_current.toggleHighlighting(false);
                m_current = source;
                m_current.toggleHighlighting(true);
            }
        }
    }

}
