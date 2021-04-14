/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.models.Attribute;
import com.codename1.rad.models.Entity;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.Node;
import com.codename1.ui.Container;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.GridLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A light-weight list of actions.  Includes some utility methods that are handy for performing on 
 * groups of actions together.
 * @author shannah
 */
public class Actions implements Iterable<ActionNode> {
    private List<ActionNode> actions = new ArrayList<>();

    public Actions() {

    }

    public Actions(Iterable<ActionNode> actions) {
        add(actions);
    }

    public Actions(ActionNode... actions) {
        add(actions);
    }

    public void add(ActionNode... nodes) {
        for (ActionNode n : nodes) {
            actions.add(n);
        }
    }
    
    public void add(Iterable<ActionNode> actions) {
        if (actions == this) {
            return;
        }
        for (ActionNode n : actions) {
            this.actions.add(n);
        }
    }
    /**
     * Get only the enabled actions from this actions list.
     * @param entity
     * @return 
     */
    public Actions getEnabled(Entity entity) {
        Actions out = new Actions();
        for (ActionNode action : this) {
            if (action.isEnabled(entity)) {
                out.add(action);
            }
        }
        return out;
    }
    
    public int size() {
        return actions.size();
    }
    
    public boolean isEmpty() {
        return actions.isEmpty();
    }
    
    @Override
    public Iterator<ActionNode> iterator() {
        return actions.iterator();
    }
    
    public ActionNode[] toArray() {
        return actions.toArray(new ActionNode[actions.size()]);
    }
    
    public void addToContainer(Container cnt, Entity entity) {
        boolean requiresFlowLayoutWrapperForBadge = (cnt.getLayout() instanceof GridLayout || cnt.getLayout() instanceof BorderLayout || cnt.getLayout() instanceof BoxLayout);
        for (ActionNode n : this) {
            if (requiresFlowLayoutWrapperForBadge && n.getBadge() != null) {
                // If there is a badge, we'll wrap it in a flowlayout
                Container fl = FlowLayout.encloseCenter(n.createView(entity));
                fl.getStyle().stripMarginAndPadding();
                cnt.addComponent(fl);
            } else {
                cnt.addComponent(n.createView(entity));
            }
        }
    }
    
    public Actions proxy(Node parent) {
        Actions out = new Actions();
        for (ActionNode action : this) {
            out.add((ActionNode)action.proxy(parent));
        }
        return out;
    }
    
    public Actions proxy() {
        Actions out = new Actions();
        for (ActionNode action : this) {
            out.add((ActionNode)action.proxy(action.getParent()));
        }
        return out;
    }
    
    public Actions setAttributes(Attribute... atts) {
        for (ActionNode action : this) {
            action.setAttributes(atts);
        }
        return this;
    }
    
    public Actions setAttributesIfNotSet(Attribute... atts) {
        for (ActionNode action : this) {
            for (Attribute att : atts) {
                Attribute existing = action.findAttribute(att.getClass());
                if (existing == null) {
                    action.setAttributes(att);
                }
            }
        }
        return this;
    }
    
}
