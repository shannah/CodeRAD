/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.nodes;

import com.codename1.rad.models.Attribute;
import com.codename1.rad.models.Property;

/**
 * A {@link Node} for binding a {@link FieldNode} to a {@link Property}.
 * 
 * WARNING: Avoid binding {@link FieldNode}s directly to {@link Property}s.  This results in a tight coupling between the view and the model.  Prefer to bind to a {@link Tag} so that binding can occur dynamically, 
 * by binding to a {@link Property} that has that {@link Tag} at render time.
 * 
 * @author shannah
 * @see FieldNode
 * @see com.codename1.rad.ui.UI#property(com.codename1.rad.models.Property, com.codename1.rad.models.Attribute...) 
 */
public class PropertyNode extends Node<Property> {
    
    public PropertyNode(Property value, Attribute... atts) {
        super(value, atts);
    }
    
}
