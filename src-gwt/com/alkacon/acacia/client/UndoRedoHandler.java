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

import com.alkacon.acacia.client.UndoRedoHandler.UndoRedoState;
import com.alkacon.acacia.shared.ContentDefinition;
import com.alkacon.acacia.shared.Entity;
import com.alkacon.vie.shared.I_Entity;

import java.util.Stack;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;

/**
 * Handler for the undo redo function.<p>
 */
public class UndoRedoHandler implements HasValueChangeHandlers<UndoRedoState> {

    /** The change types. */
    public enum ChangeType {
        /** New value added change. */
        add,

        /** A choice change. */
        choice,

        /** Value removed change. */
        remove,

        /** Value sort change. */
        sort,

        /** A simple value change. */
        value
    }

    /** Representing the undo/redo state. */
    public class UndoRedoState {

        /** Indicating if there are changes to be re done. */
        private boolean m_hasRedo;

        /** Indicating if there are changes to be undone. */
        private boolean m_hasUndo;

        /**
         * Constructor.<p>
         * 
         * @param hasUndo if there are changes to be undone
         * @param hasRedo if there are changes to be re done
         */
        UndoRedoState(boolean hasUndo, boolean hasRedo) {

            m_hasUndo = hasUndo;
            m_hasRedo = hasRedo;
        }

        /**
         * Returns if there are changes to be re done.
         * 
         * @return <code>true</code> if there are changes to be re done.
         */
        public boolean hasRedo() {

            return m_hasRedo;
        }

        /**
         * Returns if there are changes to be undone.
         * 
         * @return <code>true</code> if there are changes to be undone.
         */
        public boolean hasUndo() {

            return m_hasUndo;
        }
    }

    /**
     * Representing a change stack entry.<p>
     */
    private class Change {

        /** The entity data. */
        private Entity m_entityData;

        /** The path elements. */
        private String[] m_pathElements;

        /** The change type. */
        private ChangeType m_type;

        /**
         * Constructor.<p>
         * 
         * @param entityData the chane entity data
         * @param pathElements the path elements
         * @param type the change type
         */
        Change(Entity entityData, String[] pathElements, ChangeType type) {

            m_pathElements = pathElements;
            m_type = type;
            m_entityData = entityData;
        }

        /**
         * Returns the change entity data.<p>
         * 
         * @return the change entity data
         */
        public Entity getEntityData() {

            return m_entityData;
        }

        /**
         * Returns the change value path elements.<p>
         * 
         * @return the path elements
         */
        public String[] getPathElements() {

            return m_pathElements;
        }

        /**
         * The change type.<p>
         * 
         * @return the change type
         */
        public ChangeType getType() {

            return m_type;
        }
    }

    /** The static instance. */
    private static UndoRedoHandler INSTANCE;

    /** The current data state. */
    private Change m_current;

    /** The editor instance. */
    private EditorBase m_editor;

    /** The edited entity. */
    private I_Entity m_entity;

    /** The event bus. */
    private SimpleEventBus m_eventBus;

    /** The redo stack. */
    private Stack<Change> m_redo;

    /** The root attribute handler. */
    private RootHandler m_rootHandler;

    /** The undo stack. */
    private Stack<Change> m_undo;

    /**
     * Constructor.<p>
     */
    private UndoRedoHandler() {

        m_undo = new Stack<Change>();
        m_redo = new Stack<Change>();
    }

    /**
     * Returns the undo redo handler instance.<p>
     * 
     * @return the handler instance
     */
    public static UndoRedoHandler getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new UndoRedoHandler();
        }
        return INSTANCE;
    }

    /**
     * Adds a change to the undo stack.<p>
     * 
     * @param valuePath the entity value path
     * @param changeType the change type
     */
    public void addChange(String valuePath, ChangeType changeType) {

        Entity currentData = Entity.serializeEntity(m_entity);
        if (!currentData.equals(m_current.getEntityData())) {
            m_undo.push(m_current);
            String[] pathElements = valuePath.substring(m_entity.getId().length() + 1).split("/");
            m_current = new Change(currentData, pathElements, changeType);
            m_redo.clear();
            fireStateChange();
        }
    }

    /**
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<UndoRedoState> handler) {

        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * Clears the undo/redo stacks and all references.<p>
     */
    public void clear() {

        m_undo.clear();
        m_redo.clear();
        m_entity = null;
        m_editor = null;
        m_rootHandler = null;
    }

    /**
     * @see com.google.gwt.event.shared.HasHandlers#fireEvent(com.google.gwt.event.shared.GwtEvent)
     */
    public void fireEvent(GwtEvent<?> event) {

        ensureHandlers().fireEventFromSource(event, this);
    }

    /**
     * Indicates if there are changes to be undone.<p>
     * 
     * @return <code>true</code> if there are changes to be undone
     */
    public boolean hasRedo() {

        return !m_redo.isEmpty();
    }

    /**
     * Indicates if there are changes to be undone.<p>
     * 
     * @return <code>true</code> if there are changes to be undone
     */
    public boolean hasUndo() {

        return !m_undo.isEmpty();
    }

    /**
     * Initializes the handler to be used for the given entity.<p>
     * 
     * @param entity the edited entity
     * @param editor the editor instance
     * @param rootHandler the root attribute handler
     */
    public void initialize(I_Entity entity, EditorBase editor, RootHandler rootHandler) {

        m_undo.clear();
        m_redo.clear();
        m_entity = entity;
        m_editor = editor;
        m_rootHandler = rootHandler;
        m_current = new Change(Entity.serializeEntity(m_entity), null, null);
        fireStateChange();
    }

    /**
     * Indicates if the handler has been initialized.<p>
     * 
     * @return <code>true</code> if the handler has been initialized
     */
    public boolean isIntitalized() {

        return m_entity != null;
    }

    /**
     * Re-applies the latest state in the redo stack.<p>
     */
    public void redo() {

        if (!m_redo.isEmpty()) {
            m_undo.push(m_current);
            m_current = m_redo.pop();
            changeEntityContentValues(m_current.getEntityData(), m_current.getPathElements(), m_current.getType());
            fireStateChange();
        }
    }

    /**
     * Reverts to the latest state in the undo stack.<p>
     */
    public void undo() {

        if (hasUndo()) {
            ChangeType type = m_current.getType();
            String[] pathElements = m_current.getPathElements();
            m_redo.push(m_current);
            m_current = m_undo.pop();
            changeEntityContentValues(m_current.getEntityData(), pathElements, type);
            fireStateChange();
        }
    }

    /**
     * Adds this handler to the widget.
     * 
     * @param <H> the type of handler to add
     * @param type the event type
     * @param handler the handler
     * @return {@link HandlerRegistration} used to remove the handler
     */
    protected final <H extends EventHandler> HandlerRegistration addHandler(final H handler, GwtEvent.Type<H> type) {

        return ensureHandlers().addHandlerToSource(type, this, handler);
    }

    /**
     * Sets the editor to the given state.<p>
     * 
     * @param newContent the state content
     * @param pathElements the value path elements
     * @param type the change type
     */
    private void changeEntityContentValues(Entity newContent, String[] pathElements, ChangeType type) {

        switch (type) {
            case value:
                AttributeHandler handler = m_rootHandler.getHandlersBySimplePath(pathElements);
                String value = Entity.getValueForPath(newContent, pathElements);
                int valueIndex = ContentDefinition.extractIndex(pathElements[pathElements.length - 1]);
                if (valueIndex > 0) {
                    valueIndex--;
                }
                if ((handler != null) && handler.hasValueView(valueIndex) && (value != null)) {
                    handler.changeValue(value, valueIndex);
                    break;
                }
                //$FALL-THROUGH$
            default:
                m_editor.rerenderForm(newContent);
        }
    }

    /**
     * Lazy initializing the handler manager.<p>
     * 
     * @return the handler manager
     */
    private SimpleEventBus ensureHandlers() {

        if (m_eventBus == null) {
            m_eventBus = new SimpleEventBus();
        }
        return m_eventBus;
    }

    /**
     * Fires a value change event to indicate the undo/redo state has changed.<p>
     */
    private void fireStateChange() {

        ValueChangeEvent.fire(this, new UndoRedoState(hasUndo(), hasRedo()));
    }
}
