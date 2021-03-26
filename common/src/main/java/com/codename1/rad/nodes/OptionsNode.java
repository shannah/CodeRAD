/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.nodes;

import com.codename1.rad.models.Attribute;
import com.codename1.ui.list.ListModel;

/**
 * A {@link Node} representing the options of a {@link FieldNode} when the field has "options" (e.g. for a combobox).
 * @author shannah
 
 */
public class OptionsNode extends Node<ListModel> {
    
    public OptionsNode(ListModel value, Attribute... atts) {
        super(value, atts);
    }
    
}
