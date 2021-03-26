/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.nodes;

import com.codename1.rad.ui.ActionViewFactory;
import com.codename1.rad.models.Attribute;

/**
 * A node wrapper for an {@link ActionViewFactory}.
 * @author shannah
 * 
 * @see com.codename1.rad.ui.UI#actionViewFactory(com.codename1.rad.ui.ActionViewFactory, com.codename1.rad.models.Attribute...) 
 */
public class ActionViewFactoryNode extends Node<ActionViewFactory> {
    
    
    public ActionViewFactoryNode(ActionViewFactory value, Attribute... atts) {
        super(value, atts);
    }
}
