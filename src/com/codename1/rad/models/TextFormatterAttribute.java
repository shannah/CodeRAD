/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

import com.codename1.rad.text.TextFormatter;

/**
 * An attribute for setting a {@1ink TextFormatter} on a node.  This is usually added to the {@link FieldNode} so that PropertyViews can use
 * it to format the string for rendering.
 * @author shannah
 * 
 * @see com.codename1.rad.ui.UI#dateFormat(com.codename1.rad.text.DateFormatter) 
 */
public class TextFormatterAttribute extends Attribute<TextFormatter> {
    
    public TextFormatterAttribute(TextFormatter value) {
        super(value);
    }
    
}
