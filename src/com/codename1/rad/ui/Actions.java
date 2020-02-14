/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.nodes.ActionNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author shannah
 */
public class Actions implements Iterable<ActionNode> {
    private List<ActionNode> actions = new ArrayList<>();

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
    
}
