/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.attributes;

import com.codename1.rad.ui.image.EntityImageRenderer;
import com.codename1.rad.models.Attribute;

/**
 * Attribute to specify a renderer for rendering an icon.  This attribute can be added to any {@link Node}, as views that are interested
 * in rendering icons can use {@link com.codename1.rad.nodes.Node#findInheritedAttribute(java.lang.Class) } to get the renderer from a parent.
 * @author shannah
 * @see com.codename1.rad.ui.UI#iconRenderer(com.codename1.rad.ui.image.EntityImageRenderer) 
 */
public class IconRendererAttribute extends Attribute<EntityImageRenderer> {
    
    public IconRendererAttribute(EntityImageRenderer value) {
        super(value);
    }
    
}
