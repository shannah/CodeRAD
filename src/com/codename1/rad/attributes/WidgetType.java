/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.attributes;

import com.codename1.rad.ui.WidgetTypes;
import com.codename1.rad.models.Attribute;
import com.codename1.rad.models.Property.Name;
import java.util.Objects;

/**
 *
 * @author shannah
 */
public class WidgetType extends Attribute<Name> implements WidgetTypes {
    
    
    public WidgetType(Name name) {
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
