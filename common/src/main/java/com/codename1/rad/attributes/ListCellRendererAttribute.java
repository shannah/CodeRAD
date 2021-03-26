/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.attributes;

import com.codename1.rad.ui.EntityListCellRenderer;
import com.codename1.rad.models.Attribute;
import com.codename1.ui.list.ListCellRenderer;

/**
 * An attribute for specifying the list cell renderer for a {@link com.codename1.rad.ui.entityviews.EntityListView}.  
 * 
 * This attribute can be added to any node, as nodes that are interested in rendering a list cell, may 
 * use {@link com.codename1.rad.nodes.Node#findInheritedAttribute(java.lang.Class) to find a renderer from its parents.
 * @author shannah
 * @see com.codename1.rad.ui.UI#cellRenderer(com.codename1.rad.ui.EntityListCellRenderer) 
 */
public class ListCellRendererAttribute extends Attribute<EntityListCellRenderer> {
    
    public ListCellRendererAttribute(EntityListCellRenderer value) {
        super(value);
    }
    
}
