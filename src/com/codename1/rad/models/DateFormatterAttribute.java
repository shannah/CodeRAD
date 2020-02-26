/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

import com.codename1.rad.text.DateFormatter;

/**
 * An attribute for setting a {@1ink DateFormatter} on a node.  This can be added to any {@link com.codename1.rad.nodes.Node}
 * as any given view can call {@link com.codename1.rad.nodes.Node#findInheritedAttribute(java.lang.Class) } to find the formatter from a parent node.
 * @author shannah
 * 
 * @see com.codename1.rad.ui.UI#dateFormat(com.codename1.rad.text.DateFormatter) 
 */
public class DateFormatterAttribute extends Attribute<DateFormatter> {
    
    public DateFormatterAttribute(DateFormatter value) {
        super(value);
    }
    
}
