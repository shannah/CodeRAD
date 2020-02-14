/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.attributes.WidgetType;
import com.codename1.rad.models.Property;

/**
 *
 * @author shannah
 */
public interface WidgetTypes {
    public static final WidgetType TEXT = new WidgetType(new Property.Name("text"));
    public static final WidgetType TEXTAREA = new WidgetType(new Property.Name("textarea"));
    public static final WidgetType COMBOBOX = new WidgetType(new Property.Name("combobox"));
    public static final WidgetType TABLE = new WidgetType(new Property.Name("table"));
    
    
}
