/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.attributes.WidgetType;
import com.codename1.rad.models.Property;

/**
 * An interface to keep the standard widget types.  This list will grow.
 * @author shannah
 */
public interface WidgetTypes {
    public static final WidgetType TEXT = new WidgetType(new Property.Name("text"));
    public static final WidgetType TEXTAREA = new WidgetType(new Property.Name("textarea"));
    public static final WidgetType COMBOBOX = new WidgetType(new Property.Name("combobox"));
    public static final WidgetType TABLE = new WidgetType(new Property.Name("table"));
    public static final WidgetType CHECKBOX_LIST = new WidgetType(new Property.Name("checkbox-list"));
    public static final WidgetType RADIO_LIST = new WidgetType(new Property.Name("radio-list"));
    public static final WidgetType SWITCH_LIST = new WidgetType(new Property.Name("switch-list"));
    public static final WidgetType SWITCH = new WidgetType(new Property.Name("switch"));
    public static final WidgetType RADIO = new WidgetType(new Property.Name("radio"));
    public static final WidgetType CHECKBOX = new WidgetType(new Property.Name("checkbox"));
    public static final WidgetType HTML_COMPONENT = new WidgetType(new Property.Name("html-component"));
    
    
}
