/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.nodes;

import com.codename1.rad.models.Attribute;

/**
 *
 * @author shannah
 */
public class RowTemplateNode extends Node {
    
    public RowTemplateNode(Attribute... atts) {
        super(null, atts);
    }
    
    public ViewNode getViewNode() {
        return (ViewNode)findAttribute(ViewNode.class);
    }
    
}
