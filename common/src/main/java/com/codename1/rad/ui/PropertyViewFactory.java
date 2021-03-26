/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.models.Entity;

/**
 * Factory for creating a property view.  I.e. Renders a {@link FieldNode} as a {@link PropertyView}.
 * @author shannah
 */
public interface PropertyViewFactory {
    
    
    public PropertyView createPropertyView(Entity entity, FieldNode field);
}
