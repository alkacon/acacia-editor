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
import com.alkacon.acacia.client.I_InlineFormParent;
import com.alkacon.acacia.client.I_WidgetService;
import com.alkacon.acacia.client.css.I_LayoutBundle;
import com.alkacon.geranium.client.I_DescendantResizeHandler;
import com.alkacon.geranium.client.ui.I_Button.ButtonStyle;
import com.alkacon.geranium.client.ui.PushButton;
import com.alkacon.geranium.client.util.DomUtil;
import com.alkacon.geranium.client.util.PositionBean;
import com.alkacon.vie.client.Entity;
import com.alkacon.vie.client.Vie;
import com.alkacon.vie.shared.I_Entity;
import com.alkacon.vie.shared.I_Type;

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

    /** Indicates if the content has been changed while the edit pop-up was shown. */
    private boolean m_hasChanges;

    /** The parent of the entity to edit. */
    private I_Entity m_parentEntity;

    /** The reference DOM element, will be highlighted during editing. */
    private Element m_referenceElement;

    /** The widget service. */
    private I_WidgetService m_widgetService;

    /**
     * Constructor.<p>
     * 
     * @param referenceElement the reference DOM element, will be highlighted during editing
     * @param parentEntity the parent of the entity to edit
     * @param attributeName the attribute name
     * @param attributeIndex the attribute value index
     * @param widgetService the widget service
     */
    private InlineEntityWidget(
        Element referenceElement,
        I_Entity parentEntity,
        String attributeName,
        int attributeIndex,
        I_WidgetService widgetService) {

        m_parentEntity = parentEntity;
        m_attributeName = attributeName;
        m_attributeIndex = attributeIndex;
        m_referenceElement = referenceElement;
        m_widgetService = widgetService;
        m_button = new PushButton();
        m_button.setText("Click Me!");
        m_button.setButtonStyle(ButtonStyle.TRANSPARENT, null);

        m_button.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {

                showEditPopup();
            }
        });
        initWidget(m_button);
    }

    /**
     * Creates the inline edit widget and injects it next to the context element.<p>
     * 
     * @param element the context element
     * @param formParent the parent widget
     * @param parentEntity the parent entity
     * @param attributeName the attribute name
     * @param attributeIndex the attribute value index
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
        I_WidgetService widgetService) {

        InlineEntityWidget widget = new InlineEntityWidget(
            element,
            parentEntity,
            attributeName,
            attributeIndex,
            widgetService);
        element.getParentElement().insertAfter(widget.getElement(), element);
        formParent.adoptWidget(widget);
        widget.positionWidget(element);
        return widget;
    }

    /**
     * Cleanup after the edit pop-up was opened.<p>
     */
    void onPopupClose() {

        InlineEditOverlay.removeLastOverlay();
        if (m_entityChangeHandlerRegistration != null) {
            m_entityChangeHandlerRegistration.removeHandler();
        }
        if (m_hasChanges) {
            Window.alert("Requires updated HTML");
        }
        m_popup = null;
    }

    /**
     * Sets the changed flag.<p>
     */
    void setChanged() {

        m_hasChanges = true;
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

                setChanged();
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

    }

    /**
     * Positions the given pop-up relative to the reference element.<p>
     */
    protected void positionPopup() {

        int windowHeight = Window.getClientHeight();
        int scrollTop = Window.getScrollTop();
        int referenceHeight = m_referenceElement.getOffsetHeight();
        int contentHeight = m_popup.getOffsetHeight();
        int top = m_referenceElement.getAbsoluteTop();
        if (((windowHeight + scrollTop) < (top + referenceHeight + contentHeight + 20)) && ((contentHeight + 40) < top)) {
            top = top - contentHeight - 5;
        } else {
            top = top + referenceHeight + 5;
        }
        m_popup.center();
        m_popup.setPopupPosition(m_popup.getPopupLeft(), top);
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
        getElement().getStyle().setTop(position.getTop() - topOffset, Unit.PX);
        getElement().getStyle().setLeft(position.getLeft() - leftOffset, Unit.PX);
    }
}
