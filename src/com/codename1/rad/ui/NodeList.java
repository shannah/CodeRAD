/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.models.Attribute;
import com.codename1.rad.nodes.Node;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A light-weight data structure for Nodes.  A thin wrapper around a {@link java.util.Set}
 * @author shannah
 */
public class NodeList implements Iterable<Node> {
    private Set<Node> nodes = new LinkedHashSet<>();
    
    public Attribute[] asAttributes() {
        return nodes.toArray(new Attribute[nodes.size()]);
    }
    
    public <T extends Node> Iterable<T> as(Class<T> nodeType) {
        return (Iterable<T>)this;
    }
    
    public <T extends Node> Iterable<T> filter(Class<T> nodeType) {
        ArrayList<T> out = new ArrayList<T>();
        for (Node item : nodes) {
            if (item.getClass() == nodeType) {
                out.add((T)item);
            }
        }
        return out;
    }

    public void add(Node... nodes) {
        for (Node n : nodes) {
            this.nodes.add(n);
        }
    }
    
    public void add(NodeList nodes) {
        for (Node n : nodes) {
            this.nodes.add(n);
        }
    }
    
    public void remove(Node... nodes) {
        for (Node n : nodes) {
            this.nodes.remove(n);
        }
    }
    
    @Override
    public Iterator<Node> iterator() {
        return nodes.iterator();
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }
}
