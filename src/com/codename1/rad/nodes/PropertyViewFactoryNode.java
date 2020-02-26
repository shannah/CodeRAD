/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.nodes;

import com.codename1.rad.models.Attribute;
import com.codename1.rad.ui.PropertyViewFactory;

/**
 * A {@link Node} for specifying the {@link PropertyViewFactory} to use for rendering properties.  This can be added to any {@link Node}, since views will find its {@link PropertyViewFactory} node from its node's
 * parents or their parents if necessary.  If no {@link PropertyViewFactoryNode} is anywhere in the node hierarchy, then it falls back to the {@link DefaultPropertyViewFactory}.
 * 
 * 
 * @author shannah
 * 
 * @see FieldNode#getViewFactory() 
 * @see com.codename1.rad.ui.UI#propertyViewFactory(com.codename1.rad.ui.PropertyViewFactory, com.codename1.rad.models.Attribute...) 
 * @see com.codename1.rad.ui.UI#propertyView(com.codename1.rad.ui.PropertyViewFactory, com.codename1.rad.models.Attribute...) 
 * @see com.codename1.rad.ui.UI#viewFactory(com.codename1.rad.ui.PropertyViewFactory, com.codename1.rad.models.Attribute...) 
 */
public class PropertyViewFactoryNode extends Node<PropertyViewFactory> {
    
    public PropertyViewFactoryNode(PropertyViewFactory viewFactory, Attribute... atts) {
        super(viewFactory, atts);
    }
    
}
