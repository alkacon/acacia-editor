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

package com.alkacon.forms.client.example;

import com.alkacon.forms.client.ComplexTypeRenderer;
import com.alkacon.forms.client.I_EntityRenderer;
import com.alkacon.forms.client.SimpleTypeRenderer;
import com.alkacon.forms.client.WidgetService;
import com.alkacon.forms.shared.AttributeConfiguration;
import com.alkacon.forms.shared.ContentDefinition;
import com.alkacon.forms.shared.Type;
import com.alkacon.vie.client.Entity;
import com.alkacon.vie.client.I_Entity;
import com.alkacon.vie.client.I_Vie;
import com.alkacon.vie.client.Vie;
import com.alkacon.vie.shared.I_Type;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class Example implements EntryPoint {

    public void onModuleLoad() {

        I_Vie vie = Vie.getInstance();
        ContentDefinition definition = generateContentDefinition();
        registerTypes(vie, definition);
        I_Entity entity = vie.createEntity("<myEntity>", "<type:address>");
        entity.setAttributeValue("<http://address/firstname>", "Hans");
        entity.setAttributeValue("<http://address/lastname>", "Albers");
        entity.setAttributeValue("<http://address/city>", "Hamburg");
        entity.setAttributeValue("<http://address/country>", "Deutschland");
        WidgetService service = new WidgetService();
        service.setDefaultComplexRenderer(new ComplexTypeRenderer(service, vie));
        service.setDefaultSimpleRenderer(new SimpleTypeRenderer());
        I_EntityRenderer renderer = service.getRendererForType(vie.getType("<type:address>"));
        Widget form = renderer.render(entity);
        RootPanel.get().add(form);
        ((Entity)entity).addValueChangeHandler(new ValueChangeHandler<I_Entity>() {

            @Override
            public void onValueChange(ValueChangeEvent<I_Entity> event) {

                RootPanel.get().getElement().getStyle().setBackgroundColor("red");

            }
        });
    }

    private ContentDefinition generateContentDefinition() {

        String stringTypeName = "<type:string>";
        String addressTypeName = "<type:address>";
        Type string = new Type(stringTypeName);
        Type address = new Type("<type:address>");
        address.addAttribute("<http://address/firstname>", stringTypeName, 1, 1);
        address.addAttribute("<http://address/lastname>", stringTypeName, 1, 1);
        address.addAttribute("<http://address/city>", stringTypeName, 1, 1);
        address.addAttribute("<http://address/country>", stringTypeName, 1, 1);
        Map<String, Type> types = new HashMap<String, Type>();
        types.put(stringTypeName, string);
        types.put(addressTypeName, address);
        Map<String, AttributeConfiguration> attributes = new HashMap<String, AttributeConfiguration>();
        attributes.put("<http://address/firstname>", new AttributeConfiguration(
            "Firstname",
            "The firtname",
            "string",
            null));
        attributes.put("<http://address/lastname>", new AttributeConfiguration(
            "Lastname",
            "The lastname",
            "string",
            null));
        attributes.put("<http://address/city>", new AttributeConfiguration("City", "The city", "string", null));
        attributes.put("<http://address/country>", new AttributeConfiguration("Country", "The country", "string", null));
        return new ContentDefinition(addressTypeName, attributes, types);
    }

    private void registerTypes(I_Vie vie, ContentDefinition definition) {

        Set<String> registered = new HashSet<String>();
        Map<String, Type> types = definition.getTypes();
        Type base = types.get(definition.getBaseType());
        registerType(vie, base, types, registered);
    }

    private void registerType(I_Vie vie, Type type, Map<String, Type> types, Set<String> registered) {

        if (registered.contains(type.getId())) {
            return;
        }
        I_Type regType = vie.createType(type.getId());
        registered.add(type.getId());
        if (type.isSimpleType()) {
            return;
        }
        for (String attributeName : type.getAttributeNames()) {
            String attributeType = type.getAttributeTypeName(attributeName);
            registerType(vie, types.get(attributeType), types, registered);
            regType.addAttribute(
                attributeName,
                attributeType,
                type.getAttributeMinOccurrence(attributeName),
                type.getAttributeMaxOccurrence(attributeName));
        }
    }

}