/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.nodes;

import com.codename1.rad.attributes.ListCellRendererAttribute;
import com.codename1.rad.ui.entityviews.MultiButtonEntityView;
import com.codename1.rad.ui.EntityListCellRenderer;
import com.codename1.rad.models.Attribute;

/**
 *
 * @author shannah
 */
public class ListNode extends Node {
    
    public ListNode(Attribute... atts) {
        super(null, atts);
        
    }
    
    public EntityListCellRenderer getListCellRenderer() {
        ListCellRendererAttribute att =  (ListCellRendererAttribute)findAttribute(ListCellRendererAttribute.class);
        if (att != null) {
            return att.getValue();
        }
        return null;
    }
    
    
    public ViewNode getRowTemplate() {
        RowTemplateNode tpl = (RowTemplateNode)findAttribute(RowTemplateNode.class);
        if (tpl == null) {
            tpl = new RowTemplateNode();
            tpl.setAttributes(new ViewNode());
            setAttributes(tpl);
        }
        ViewNode n = tpl.getViewNode();
        if (n == null) {
            tpl.setAttributes(new ViewNode());
        }
        ViewNode out = tpl.getViewNode();
        
        return out;
    }
}
