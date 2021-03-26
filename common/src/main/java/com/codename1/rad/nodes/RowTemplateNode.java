/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.nodes;

import com.codename1.rad.models.Attribute;

/**
 * A node that provides settings for the rows of a list.  This node can be added to a {@link ListNode} to provide a sort of template for each row.
 * @author shannah
 * 
 * @see com.codename1.rad.ui.UI#rowTemplate(com.codename1.rad.models.Attribute...) 
 * @see ListNode
 */
public class RowTemplateNode extends Node {
    
    public RowTemplateNode(Attribute... atts) {
        super(null, atts);
    }
    
    public ViewNode getViewNode() {
        return (ViewNode)findAttribute(ViewNode.class);
    }
    
}
