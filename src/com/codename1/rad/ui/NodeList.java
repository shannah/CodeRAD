/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.nodes.Node;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author shannah
 */
public class NodeList implements Iterable<Node> {
    private Set<Node> nodes = new LinkedHashSet<>();

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
