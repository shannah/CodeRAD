/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.nodes.Node;

/**
 * A decorator that can be added to a node via {@link NodeDecoratorAttribute} which 
 * will be run just after the node is added to its parent.
 * @author shannah
 */
public interface NodeDecorator {
    public void decorate(Node node);
}
