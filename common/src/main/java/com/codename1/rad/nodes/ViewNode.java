/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.nodes;

import com.codename1.rad.ui.EntityView;
import com.codename1.rad.ui.EntityViewFactory;
import com.codename1.rad.ui.UI;
import com.codename1.rad.attributes.IconRendererAttribute;
import com.codename1.rad.attributes.PropertySelectorAttribute;
import com.codename1.rad.models.Attribute;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.PropertySelector;

/**
 * A node representing an EntityView.
 * @author shannah
 */
public class ViewNode extends Node {
    
    public ViewNode(Attribute... atts) {
        super(null, atts);
    }
    
    public IconRendererAttribute getIconRenderer() {
        return (IconRendererAttribute)findInheritedAttribute(IconRendererAttribute.class);
    }
    
    private EntityViewFactoryNode getViewFactoryNode() {
        return (EntityViewFactoryNode)findInheritedAttribute(EntityViewFactoryNode.class);
    }
    
    public EntityViewFactory getViewFactory(EntityViewFactory defaultVal) {
        EntityViewFactoryNode node = getViewFactoryNode();
        if (node == null) {
            return defaultVal;
        } else {
            return node.getValue();
        }
    }
    
    public EntityViewFactory getViewFactory() {
        return getViewFactory(UI.getDefaultEntityViewFactory());
    }
    
    public EntityView createView(Entity entity) {
        return getViewFactory().createView(entity, this);
    }
    
    public EntityView createView(Entity entity, EntityViewFactory defaultFactory) {
        return getViewFactory(defaultFactory).createView(entity, this);
    }
    
    
    
}
