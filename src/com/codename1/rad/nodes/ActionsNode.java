/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.nodes;

import com.codename1.rad.nodes.ActionNode.Category;
import com.codename1.rad.models.Attribute;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author shannah
 */
public class ActionsNode extends Node implements Iterable<ActionNode> {
    private List<ActionNode> actions;
    
    public ActionsNode(Attribute... atts) {
        super(null, atts);
    }

    @Override
    public void setAttributes(Attribute... atts) {
        //super.setAttributes(atts);
        for (Attribute att : atts) {
            if (att.getClass() == ActionNode.class) {
                ActionNode n = (ActionNode)att;
                if (actions == null) {
                    actions = new ArrayList<ActionNode>();
                }
                if (n.getParent() == null) {
                    n.setParent(this);
                } else {
                    n = (ActionNode)n.createProxy(this);
                }
                actions.add(n);
            } else {
                super.setAttributes(att);
            }
        }
    }
    
    
    public Category getCategory() {
        return (Category)findAttribute(Category.class);
    }
    

    @Override
    public Iterator<ActionNode> iterator() {
        if (actions == null) {
            actions = new ArrayList<>();
        }
        return actions.iterator();
    }
    
}
