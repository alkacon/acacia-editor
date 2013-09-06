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

import com.alkacon.acacia.client.AttributeHandler;
import com.alkacon.acacia.client.EditorBase;
import com.alkacon.acacia.client.I_InlineFormParent;
import com.alkacon.acacia.client.I_InlineHtmlUpdateHandler;
import com.alkacon.acacia.client.I_WidgetService;
import com.alkacon.acacia.client.css.I_LayoutBundle;
import com.alkacon.geranium.client.I_DescendantResizeHandler;
import com.alkacon.geranium.client.ui.I_Button.ButtonStyle;
import com.alkacon.geranium.client.ui.PushButton;
import com.alkacon.geranium.client.ui.css.I_ImageBundle;
import com.alkacon.geranium.client.util.DomUtil;
import com.alkacon.geranium.client.util.PositionBean;
import com.alkacon.vie.client.Entity;
import com.alkacon.vie.client.Vie;
import com.alkacon.vie.shared.I_Entity;
import com.alkacon.vie.shared.I_Type;

import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Widget allowing form based editing for parts of a content to enhance the in-line editing.<p>
 */
public class InlineEntityWidget extends Composite {

    /**
     * Flow panel with handling descendant resizes to reposition pop-up.<p>
     */
    protected class FormPanel extends FlowPanel implements I_DescendantResizeHandler {

        /**
         * Constructor.<p>
         */
        protected FormPanel() {

        }

        /**
         * @see com.alkacon.geranium.client.I_DescendantResizeHandler#onResizeDescendant()
         */
        public void onResizeDescendant() {

            positionPopup();
        }
    }

    /**
     * Timer to update the HTML.<p>
     */
    protected class UpdateTimer extends Timer {

        /** Indicates if the timer is scheduled. */
        private boolean m_scheduled;

        /**
         * @see com.google.gwt.user.client.Timer#cancel()
         */
        @Override
        public void cancel() {

            m_scheduled = false;
            super.cancel();
        }

        /**
         * Returns if the timer is already scheduled.<p>
         * 
         * @return <code>true</code> if the timer is scheduled
         */
        public boolean isScheduled() {

            return m_scheduled;
        }

        /**
         * @see com.google.gwt.user.client.Timer#run()
         */
        @Override
        public void run() {

            m_scheduled = false;
            runHtmlUpdate();
        }

        /**
         * @see com.google.gwt.user.client.Timer#schedule(int)
         */
        @Override
        public void schedule(int delayMillis) {

            m_scheduled = true;
            super.schedule(delayMillis);
        }
    }

    /** The pop-up panel. */
    PopupPanel m_popup;

    /** The attribute value index. */
    private int m_attributeIndex;

    /** The name of the attribute to edit. */
    private String m_attributeName;

    /** The injected button. */
    private PushButton m_button;

    /** The change handler registration. */
    private HandlerRegistration m_entityChangeHandlerRegistration;

    /** The parent widget. */
    private I_InlineFormParent m_formParent;

    /** Indicates if the content has been changed while the edit pop-up was shown. */
    private boolean m_hasChanges;

    /** Handles HTML updates if required. */
    private I_InlineHtmlUpdateHandler m_htmlUpdateHandler;

    /** The parent of the entity to edit. */
    private I_Entity m_parentEntity;

    /** Flag indicating the popup has been closed. */
    private boolean m_popupClosed;

    /** The reference DOM element, will be highlighted during editing. */
    private Element m_referenceElement;

    /** Flag indicating an HTML update is running. */
    private boolean m_runningUpdate;

    /** Schedules the HTML update. */
    private UpdateTimer m_updateTimer;

    /** The widget service. */
    private I_WidgetService m_widgetService;

    /**
     * Constructor.<p>
     * 
     * @param referenceElement the reference DOM element, will be highlighted during editing
     * @param formParent the parent widget
     * @param parentEntity the parent of the entity to edit
     * @param attributeName the attribute name
     * @param attributeIndex the attribute value index
     * @param htmlUpdateHandler handles HTML updates if required
     * @param widgetService the widget service
     */
    private InlineEntityWidget(
        Element referenceElement,
        I_InlineFormParent formParent,
        I_Entity parentEntity,
        String attributeName,
        int attributeIndex,
        I_InlineHtmlUpdateHandler htmlUpdateHandler,
        I_WidgetService widgetService) {

        m_parentEntity = parentEntity;
        m_attributeName = attributeName;
        m_attributeIndex = attributeIndex;
        m_referenceElement = referenceElement;
        m_formParent = formParent;
        m_htmlUpdateHandler = htmlUpdateHandler;
        m_widgetService = widgetService;
        m_button = new PushButton();
        if (EditorBase.getDictionary() != null) {
            m_button.setTitle(EditorBase.getDictionary().get(EditorBase.GUI_VIEW_EDIT_0)
                + " "
                + m_widgetService.getAttributeLabel(attributeName));
        }
        m_button.setImageClass(I_ImageBundle.INSTANCE.style().editIcon());
        m_button.setButtonStyle(ButtonStyle.TRANSPARENT, null);
        m_button.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {

                showEditPopup();
            }
        });
        initWidget(m_button);
        m_updateTimer = new UpdateTimer();
    }

    /**
     * Creates the inline edit widget and injects it next to the context element.<p>
     * 
     * @param element the context element
     * @param formParent the parent widget
     * @param parentEntity the parent entity
     * @param attributeName the attribute name
     * @param attributeIndex the attribute value index
     * @param htmlUpdateHandler handles HTML updates if required
     * @param widgetService the widget service
     * 
     * @return the widget instance
     */
    public static InlineEntityWidget createWidgetForEntity(
        Element element,
        I_InlineFormParent formParent,
        I_Entity parentEntity,
        String attributeName,
        int attributeIndex,
        I_InlineHtmlUpdateHandler htmlUpdateHandler,
        I_WidgetService widgetService) {

        InlineEntityWidget widget = new InlineEntityWidget(
            element,
            formParent,
            parentEntity,
            attributeName,
            attributeIndex,
            htmlUpdateHandler,
            widgetService);
        element.getParentElement().insertAfter(widget.getElement(), element);
        formParent.adoptWidget(widget);
        widget.positionWidget(element);
        return widget;
    }

    /**
     * Positions the given pop-up relative to the reference element.<p>
     */
    void positionPopup() {

        if (m_referenceElement != null) {
            int windowHeight = Window.getClientHeight();
            int scrollTop = Window.getScrollTop();
            int referenceHeight = m_referenceElement.getOffsetHeight();
            int contentHeight = m_popup.getOffsetHeight();
            int top = m_referenceElement.getAbsoluteTop();
            if (((windowHeight + scrollTop) < (top + referenceHeight + contentHeight + 20))
                && ((contentHeight + 40) < top)) {
                top = top - contentHeight - 5;
            } else {
                top = top + referenceHeight + 5;
            }
            m_popup.center();
            m_popup.setPopupPosition(m_popup.getPopupLeft(), top);
            if (((contentHeight + top) - scrollTop) > windowHeight) {
                Window.scrollTo(Window.getScrollLeft(), ((contentHeight + top) - windowHeight) + 20);
            }
        } else {
            m_popup.center();
        }
    }

    /**
     * Repositions the edit overlay after the HTML has been updated.<p>
     */
    void afterHtmlUpdate() {

        m_runningUpdate = false;
        List<Element> elements = Vie.getInstance().getAttributeElements(
            m_parentEntity,
            m_attributeName,
            m_formParent.getElement());
        if (m_popupClosed) {
            // the form popup has already been closed, reinitialize the editing widgets for updated HTML
            InlineEditOverlay.updateCurrentOverlayPosition();
            m_htmlUpdateHandler.reinitWidgets(m_formParent);
        } else {
            if (m_referenceElement != null) {
                InlineEditOverlay.removeLastOverlay();
            }
            if (elements.size() > m_attributeIndex) {
                m_referenceElement = elements.get(m_attributeIndex);
                InlineEditOverlay.addOverlayForElement(m_referenceElement);
                positionPopup();
            } else {
                m_referenceElement = null;
                InlineEditOverlay.updateCurrentOverlayPosition();
            }
        }
    }

    /**
     * Sets the changed flag.<p>
     */
    void onEntityChange() {

        if (m_updateTimer.isScheduled()) {
            m_updateTimer.cancel();
        }
        m_updateTimer.schedule(150);
        m_hasChanges = true;
    }

    /**
     * Cleanup after the edit pop-up was opened.<p>
     */
    void onPopupClose() {

        if (m_referenceElement != null) {
            InlineEditOverlay.removeLastOverlay();
        }
        InlineEditOverlay.updateCurrentOverlayPosition();
        if (m_entityChangeHandlerRegistration != null) {
            m_entityChangeHandlerRegistration.removeHandler();
        }
        if (!m_runningUpdate) {
            if (m_hasChanges) {
                m_htmlUpdateHandler.reinitWidgets(m_formParent);
            } else {
                m_button.setVisible(true);
            }
        }
        m_popup = null;
    }

    /**
     * Updates the HTML according to the entity data.<p>
     */
    void runHtmlUpdate() {

        if (m_runningUpdate) {
            m_updateTimer.schedule(50);
        } else {
            m_runningUpdate = true;
            m_htmlUpdateHandler.updateHtml(m_formParent, new Command() {

                public void execute() {

                    afterHtmlUpdate();
                }
            });
        }
    }

    /**
     * Opens the form popup.<p>
     */
    void showEditPopup() {

        m_popup = new PopupPanel(true, true);
        m_popup.addCloseHandler(new CloseHandler<PopupPanel>() {

            public void onClose(CloseEvent<PopupPanel> event) {

                onPopupClose();
            }
        });
        m_hasChanges = false;
        m_entityChangeHandlerRegistration = ((Entity)m_parentEntity).addValueChangeHandler(new ValueChangeHandler<Entity>() {

            public void onValueChange(ValueChangeEvent<Entity> event) {

                onEntityChange();
            }
        });
        I_Type type = Vie.getInstance().getType(m_parentEntity.getTypeName());
        FlowPanel formPanel = new FormPanel();
        formPanel.setStyleName(I_LayoutBundle.INSTANCE.form().formParent());
        m_popup.setWidget(formPanel);
        PushButton closeButton = new PushButton();
        closeButton.setTitle("Close");
        I_LayoutBundle.INSTANCE.dialogCss().ensureInjected();
        closeButton.setImageClass(I_LayoutBundle.INSTANCE.dialogCss().closePopupImage());
        closeButton.setButtonStyle(ButtonStyle.TRANSPARENT, null);
        closeButton.addClickHandler(new ClickHandler() {

            /**
             * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
             */
            public void onClick(ClickEvent event) {

                m_popup.hide();
            }
        });
        Style closeStyle = closeButton.getElement().getStyle();
        closeStyle.setPosition(Position.ABSOLUTE);
        closeStyle.setRight(-15, Unit.PX);
        closeStyle.setTop(-15, Unit.PX);
        formPanel.add(closeButton);
        m_popup.show();
        AttributeHandler.setScrollElement(formPanel.getElement());
        AttributeHandler.setResizeHandler(new ResizeHandler() {

            public void onResize(ResizeEvent event) {

                positionPopup();
            }
        });
        m_widgetService.getRendererForType(type).renderAttributeValue(
            m_parentEntity,
            m_attributeName,
            m_attributeIndex,
            formPanel);
        InlineEditOverlay.addOverlayForElement(m_referenceElement);
        positionPopup();
        m_popup.getElement().getStyle().setZIndex(I_LayoutBundle.INSTANCE.constants().css().zIndexPopup());
        m_popupClosed = false;
        m_button.setVisible(false);
    }

    /**
     * Positions the widget button above the reference element.<p>
     * 
     * @param reference the reference element
     */
    private void positionWidget(Element reference) {

        PositionBean position = PositionBean.generatePositionInfo(reference);
        int topOffset = 0;
        int leftOffset = 0;
        Element positioningParent = DomUtil.getPositioningParent(reference);
        if (positioningParent != null) {
            topOffset = positioningParent.getAbsoluteTop();
            leftOffset = positioningParent.getAbsoluteLeft();
        }
        getElement().getStyle().setPosition(Position.ABSOLUTE);
        getElement().getStyle().setTop((position.getTop() - topOffset) + 5, Unit.PX);
        getElement().getStyle().setLeft(((position.getLeft() - leftOffset) + position.getWidth()) - 25, Unit.PX);
    }
}
