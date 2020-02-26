/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.nodes;

import com.codename1.rad.ui.EntityViewFactory;
import com.codename1.rad.models.Attribute;

/**
 * A node containing an {@link EntityViewFactory}.  This node can be added to any part of the node hierarchy.  Views that
 * are interested in retrieving the factory will call {@link ViewNode#getViewFactory() }, which will find the first 
 * view factory registered when walking up the node hierarchy. 
 * @author shannah
 * @see com.codename1.rad.ui.UI#viewFactory(com.codename1.rad.ui.EntityViewFactory, com.codename1.rad.models.Attribute...) 
 * @see EntityViewFactory
 * @see DefaultEntityViewFactory
 * @see ViewNode#getViewFactory()
 * @see com.codename1.rad.ui.UI#getDefaultEntityViewFactory() 
 * @see com.codename1.rad.ui.UI#setDefaultEntityViewFactory(com.codename1.rad.ui.EntityViewFactory) 
 */
public class EntityViewFactoryNode extends Node<EntityViewFactory> {
    
    public EntityViewFactoryNode(EntityViewFactory value, Attribute... atts) {
        super(value, atts);
    }
    
}
