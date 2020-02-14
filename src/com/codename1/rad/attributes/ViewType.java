/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.attributes;

import com.codename1.rad.models.Attribute;
import com.codename1.rad.models.Property;
import com.codename1.rad.models.Property.Name;
import java.util.Objects;

/**
 *
 * @author shannah
 */
public class ViewType extends Attribute<Name> {
    public static final ViewType MULTIBUTTON = new ViewType(new Property.Name("multibutton"));

    
    
    public ViewType(Property.Name name) {
        super(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() == WidgetType.class) {
            WidgetType t = (WidgetType)obj;
            return Objects.equals(t.getValue(), getValue());
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getValue());
    }
}
