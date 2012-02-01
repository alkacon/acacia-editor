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

import com.alkacon.vie.client.I_Entity;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Select box renderer.<p>
 */
public class SelectTypeRenderer extends A_SimpleTypeRenderer {

    /**
     * The change handler.<p>
     */
    protected class SelectChangeHandler implements ChangeHandler {

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
        protected SelectChangeHandler(I_Entity parentEntity, String attributeName, int index) {

            m_entity = parentEntity;
            m_attributeName = attributeName;
            m_index = index;
        }

        /**
         * @see com.google.gwt.event.dom.client.ChangeHandler#onChange(com.google.gwt.event.dom.client.ChangeEvent)
         */
        public void onChange(ChangeEvent event) {

            ListBox box = (ListBox)event.getSource();
            String newValue = box.getValue(box.getSelectedIndex());
            m_entity.setAttributeValue(m_attributeName, newValue, m_index);
        }
    }

    /**
     * Select option bean.<p>
     */
    protected class SelectOption {

        /** The value. */
        private String m_value;

        /** The label. */
        private String m_label;

        /**
         * Constructor.<p>
         * 
         * @param value the value
         * @param label the label
         */
        protected SelectOption(String value, String label) {

            m_value = value;
            m_label = label;
        }

        /**
         * Returns the value.<p>
         * 
         * @return the value
         */
        protected String getValue() {

            return m_value;
        }

        /**
         * Returns the label.<p>
         * 
         * @return the label
         */
        protected String getLabel() {

            return m_label;
        }
    }

    /** The select options. */
    private List<SelectOption> m_options;

    /**
     * Constructor.<p>
     */
    public SelectTypeRenderer() {

    }

    /**
     * Constructor.<p>
     * 
     * @param label the attribute label
     * @param help the attribute help information
     */
    public SelectTypeRenderer(String label, String help) {

        super(label, help);
    }

    /**
     * @see com.alkacon.forms.client.StringTypeRenderer#initConfiguration(java.lang.String)
     */
    @Override
    public void initConfiguration(String configuration) {

        m_options = new ArrayList<SelectOption>();
        String[] options = configuration.split("\\|");
        for (int i = 0; i < options.length; i++) {
            String opt = options[i];
            int index = opt.indexOf("=");
            if (index >= 0) {
                m_options.add(new SelectOption(opt.substring(0, index), opt.substring(index + 1)));
            } else {
                m_options.add(new SelectOption(opt, opt));
            }
        }
    }

    /**
     * @see com.alkacon.forms.client.A_SimpleTypeRenderer#getWidget(java.lang.String, com.alkacon.vie.client.I_Entity, java.lang.String, int)
     */
    @Override
    protected Widget getWidget(String value, I_Entity entity, String attributeName, int valueIndex) {

        ListBox select = new ListBox();
        int index = -1;
        for (int j = 0; j < m_options.size(); j++) {
            SelectOption option = m_options.get(j);
            select.addItem(option.getLabel(), option.getValue());
            if ((index == -1) && option.getValue().equals(value)) {
                index = j;
            }
        }
        if (index == -1) {
            index = 0;
        }

        select.setSelectedIndex(index);
        select.addChangeHandler(new SelectChangeHandler(entity, attributeName, valueIndex));
        return select;
    }

}
