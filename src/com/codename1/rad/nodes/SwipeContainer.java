/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.nodes;

import com.codename1.rad.nodes.Node;
import com.codename1.rad.models.Attribute;

/**
 *
 * @author shannah
 */
public class SwipeContainer extends Node {
    private ViewNode left, right;
    public SwipeContainer(ViewNode left, ViewNode right, Attribute... atts) {
        super(atts);
        this.left = left;
        if (left != null) {
            left.setParent(this);
        }
        this.right = right;
        if (right != null) {
            right.setParent(this);
        }
    }
    
    public ViewNode getLeft() {
        return left;
    }
    
    public ViewNode getRight() {
        return right;
    }
    
}
