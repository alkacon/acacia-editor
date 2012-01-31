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

package com.alkacon.forms.client;

import com.alkacon.forms.client.css.I_LayoutBundle;
import com.alkacon.vie.client.I_Entity;
import com.alkacon.vie.client.I_EntityAttribute;

import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * The simple type renderer.<p>
 */
public class StringTypeRenderer implements I_EntityRenderer {

    /**
     * The value change handler.<p>
     */
    protected class SimpleValueChangeHandler implements ValueChangeHandler<String> {

        /** The attribute name. */
        private String m_attributeName;

        /** The entity. */
        private I_Entity m_entity;

        /** The value index. */
        private int m_index;

        /**
         * Constructor.<p>
         * 
         * @param parentEntity the entity to change
         * @param attributeName the attribute name
         * @param index the value index
         */
        protected SimpleValueChangeHandler(I_Entity parentEntity, String attributeName, int index) {

            m_entity = parentEntity;
            m_attributeName = attributeName;
            m_index = index;
        }

        /**
         * @see com.google.gwt.event.logical.shared.ValueChangeHandler#onValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent)
         */
        public void onValueChange(ValueChangeEvent<String> event) {

            String newValue = event.getValue();
            I_EntityAttribute attribute = m_entity.getAttribute(m_attributeName);
            if ((m_index == 0) && attribute.isSingleValue()) {
                m_entity.setAttributeValue(m_attributeName, newValue);
            } else {
                List<String> values = attribute.getSimpleValues();
                if (m_index >= values.size()) {
                    // TODO: throw exception
                }
                m_entity.removeAttributeSilent(m_attributeName);
                for (int i = 0; i < values.size(); i++) {
                    if (i == m_index) {
                        m_entity.addAttributeValue(m_attributeName, newValue);
                    } else {
                        m_entity.addAttributeValue(m_attributeName, values.get(i));
                    }
                }
            }

        }
    }

    /** The widget holder CSS class. */
    public static final String WIDGET_HOLDER_CLASS = I_LayoutBundle.INSTANCE.style().widgetHolder();

    /** The help information. */
    private String m_help;

    /** The label. */
    private String m_label;

    /**
     * Constructor.<p>
     */
    public StringTypeRenderer() {

    }

    /**
     * Constructor.<p>
     * 
     * @param label the attribute label
     * @param help the attribute help information
     */
    public StringTypeRenderer(String label, String help) {

        m_label = label;
        m_help = help;
    }

    /**
     * @see com.alkacon.forms.client.I_EntityRenderer#getHelp(java.lang.String)
     */
    public String getHelp(String attributeName) {

        return m_help != null ? m_help : attributeName;
    }

    /**
     * @see com.alkacon.forms.client.I_EntityRenderer#getLabel(java.lang.String)
     */
    public String getLabel(String attributeName) {

        return m_label != null ? m_label : attributeName;
    }

    /**
     * @see com.alkacon.forms.client.I_EntityRenderer#initConfiguration(java.lang.String)
     */
    public void initConfiguration(String configuration) {

        // nothing to do
    }

    /**
     * @see com.alkacon.forms.client.I_EntityRenderer#render(com.alkacon.vie.client.I_Entity)
     */
    public Widget render(I_Entity entity) {

        // TODO: throw exception
        return null;
    }

    /**
     * @see com.alkacon.forms.client.I_EntityRenderer#render(com.alkacon.vie.client.I_Entity, com.alkacon.vie.client.I_EntityAttribute, com.google.gwt.user.client.ui.HasWidgets)
     */
    public void render(I_Entity parentEntity, I_EntityAttribute attribute, HasWidgets parentPanel) {

        if (attribute.isComplexValue()) {
            // TODO: throw exception
        } else {
            List<String> values = attribute.getSimpleValues();
            for (int i = 0; i < values.size(); i++) {
                SimplePanel panel = new SimplePanel();
                panel.setStyleName(WIDGET_HOLDER_CLASS);
                TextBox textBox = new TextBox();
                textBox.setValue(values.get(i), false);
                textBox.addValueChangeHandler(new SimpleValueChangeHandler(
                    parentEntity,
                    attribute.getAttributeName(),
                    i));
                panel.setWidget(textBox);
                parentPanel.add(panel);
            }
        }
    }
}
