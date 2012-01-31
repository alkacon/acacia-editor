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
import com.alkacon.forms.client.StringTypeRenderer;
import com.alkacon.forms.client.WidgetService;
import com.alkacon.forms.client.css.I_LayoutBundle;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Show case for the form renderer.<p>
 */
public class Example implements EntryPoint {

    /** Address attribute name. */
    private static String addressAttribute = "<http://person/address>";

    /** Address type name. */
    private static String addressTypeName = "<type:address>";

    /** City attribute name. */
    private static String cityAttribute = "<http://address/city>";

    /** Country attribute type name. */
    private static String countryAttribute = "<http://address/country>";

    /** First name attribute name. */
    private static String firstnameAttribute = "<http://person/firstname>";

    /** Last name attribute name. */
    private static String lastNameAttribute = "<http://person/lastname>";

    /** Person type name. */
    private static String personTypeName = "<type:person>";

    /** String type name. */
    private static String stringTypeName = "<type:string>";

    /**
     * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
     */
    public void onModuleLoad() {

        I_LayoutBundle.INSTANCE.style().ensureInjected();

        I_Vie vie = Vie.getInstance();
        ContentDefinition definition = generateContentDefinition();
        registerTypes(vie, definition);
        I_Entity address = vie.createEntity("<myAdress>", addressTypeName);

        address.setAttributeValue(cityAttribute, "Hamburg");
        address.setAttributeValue(countryAttribute, "fr");

        I_Entity person = vie.createEntity("<myPerson>", personTypeName);
        person.setAttributeValue(firstnameAttribute, "Hans");
        person.setAttributeValue(lastNameAttribute, "Albers");
        person.setAttributeValue(addressAttribute, address);
        WidgetService service = new WidgetService();
        service.init(definition);
        service.setDefaultComplexRenderer(new ComplexTypeRenderer(service, vie));
        service.setDefaultSimpleRenderer(new StringTypeRenderer());
        I_EntityRenderer renderer = service.getRendererForType(vie.getType(personTypeName));
        Widget form = renderer.render(person);
        RootPanel.get().add(form);
        ((Entity)person).addValueChangeHandler(new ValueChangeHandler<I_Entity>() {

            @Override
            public void onValueChange(ValueChangeEvent<I_Entity> event) {

                RootPanel.get().getElement().getStyle().setBackgroundColor("red");
                RootPanel.get().add(new Label(event.getValue().toJSON()));
            }
        });
    }

    /**
     * Generates a content definition.<p>
     * 
     * @return the content definition
     */
    private ContentDefinition generateContentDefinition() {

        Type string = new Type(stringTypeName);
        Type address = new Type(addressTypeName);
        address.addAttribute(cityAttribute, stringTypeName, 1, 1);
        address.addAttribute(countryAttribute, stringTypeName, 1, 1);
        Type person = new Type(personTypeName);
        person.addAttribute(firstnameAttribute, stringTypeName, 1, 1);
        person.addAttribute(lastNameAttribute, stringTypeName, 1, 1);
        person.addAttribute(addressAttribute, addressTypeName, 1, 1);
        Map<String, Type> types = new HashMap<String, Type>();
        types.put(stringTypeName, string);
        types.put(addressTypeName, address);
        types.put(personTypeName, person);
        Map<String, AttributeConfiguration> attributes = new HashMap<String, AttributeConfiguration>();
        attributes.put(firstnameAttribute, new AttributeConfiguration("Firstname", "The firtname", "string", null));
        attributes.put(lastNameAttribute, new AttributeConfiguration("Lastname", "The lastname", "string", null));

        attributes.put(cityAttribute, new AttributeConfiguration("City", "The city", "string", null));
        attributes.put(countryAttribute, new AttributeConfiguration(
            "Country",
            "The country",
            "select",
            "de=Deustchland|fr=Frankreich|it=Italien"));
        return new ContentDefinition(personTypeName, attributes, types);
    }

    /**
     * Registers the type and it's sub-types.<p>
     * 
     * @param vie the VIE instance
     * @param type the type to register
     * @param types the available types
     * @param registered the already registered types
     */
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

    /**
     * Registers the types within the content definition.<p>
     * 
     * @param vie the VIE instance
     * @param definition the content definition
     */
    private void registerTypes(I_Vie vie, ContentDefinition definition) {

        Set<String> registered = new HashSet<String>();
        Map<String, Type> types = definition.getTypes();
        Type base = types.get(definition.getBaseType());
        registerType(vie, base, types, registered);
    }

}