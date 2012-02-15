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

package com.alkacon.acacia.client.example;

import com.alkacon.acacia.client.I_EntityRenderer;
import com.alkacon.acacia.client.I_WidgetFactory;
import com.alkacon.acacia.client.InlineRenderer;
import com.alkacon.acacia.client.WidgetService;
import com.alkacon.acacia.client.css.I_LayoutBundle;
import com.alkacon.acacia.client.widgets.I_EditWidget;
import com.alkacon.acacia.client.widgets.StringWidget;
import com.alkacon.acacia.client.widgets.TinyMCEWidget;
import com.alkacon.acacia.shared.AttributeConfiguration;
import com.alkacon.acacia.shared.ContentDefinition;
import com.alkacon.acacia.shared.Type;
import com.alkacon.vie.client.Entity;
import com.alkacon.vie.client.I_Vie;
import com.alkacon.vie.client.Vie;
import com.alkacon.vie.shared.I_Entity;
import com.alkacon.vie.shared.I_Type;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Show case for the form renderer.<p>
 */
public class Example implements EntryPoint {

    /** Address attribute name. */
    private static String addressAttribute = "person:address";

    /** Address type name. */
    private static String addressTypeName = "type:address";

    /** City attribute name. */
    private static String cityAttribute = "address:city";

    /** Country attribute type name. */
    private static String countryAttribute = "address:country";

    /** First name attribute name. */
    private static String firstnameAttribute = "person:firstname";

    /** Last name attribute name. */
    private static String lastNameAttribute = "person:lastname";

    /** Person type name. */
    private static String personTypeName = "type:person";

    /** String type name. */
    private static String stringTypeName = "type:string";

    /**
     * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
     */
    public void onModuleLoad() {

        I_LayoutBundle.INSTANCE.inline().ensureInjected();

        I_Vie vie = Vie.getInstance();
        ContentDefinition definition = generateContentDefinition();
        I_Entity person = register(vie, definition);

        WidgetService service = new WidgetService();
        service.init(definition);
        service.addWidgetFactory("string", new I_WidgetFactory() {

            public I_EditWidget createWidget(String configuration) {

                return new StringWidget();
            }
        });
        service.addWidgetFactory("tinymce", new I_WidgetFactory() {

            public I_EditWidget createWidget(String configuration) {

                return new TinyMCEWidget(null);
            }
        });

        I_EntityRenderer inlineRenderer = new InlineRenderer(vie, service);
        service.setDefaultComplexRenderer(inlineRenderer);
        service.setDefaultSimpleRenderer(inlineRenderer);
        I_EntityRenderer renderer = service.getRendererForType(vie.getType(personTypeName));
        renderer.render(person, RootPanel.getBodyElement());

        ((Entity)person).addValueChangeHandler(new ValueChangeHandler<I_Entity>() {

            public void onValueChange(ValueChangeEvent<I_Entity> event) {

                RootPanel.get().getElement().getStyle().setBackgroundColor("#77f3f3");
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
        Map<String, I_Type> types = new HashMap<String, I_Type>();
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

        com.alkacon.acacia.shared.Entity addressEntity = new com.alkacon.acacia.shared.Entity(
            "myAdress",
            addressTypeName);

        addressEntity.setAttributeValue(cityAttribute, "Hamburg");
        addressEntity.setAttributeValue(countryAttribute, "fr");

        com.alkacon.acacia.shared.Entity personEntity = new com.alkacon.acacia.shared.Entity("myPerson", personTypeName);
        personEntity.setAttributeValue(firstnameAttribute, "Hans");
        personEntity.setAttributeValue(lastNameAttribute, "Albers");
        personEntity.setAttributeValue(addressAttribute, addressEntity);
        return new ContentDefinition(personEntity, attributes, types, "de");
    }

    /**
     * Registers the types within the content definition.<p>
     * 
     * @param vie the VIE instance
     * @param definition the content definition
     * 
     * @return the registered content entity 
     */
    private I_Entity register(I_Vie vie, ContentDefinition definition) {

        Map<String, I_Type> types = definition.getTypes();
        I_Type base = types.get(definition.getEntity().getTypeName());
        vie.registerTypes(base, types);
        return vie.registerEntity(definition.getEntity());
    }
}