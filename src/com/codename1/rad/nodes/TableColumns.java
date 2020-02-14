/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.nodes;

import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.models.Attribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author shannah
 */
public class TableColumns extends Node<FieldNode[]> implements Iterable<FieldNode> {
    private List<FieldNode> fields;
    public TableColumns(FieldNode... atts) {
        super(null, atts);
        if (fields == null) {
            fields = new ArrayList<>();
        }
    }

    @Override
    public void setAttributes(Attribute... atts) {
        if (fields == null) {
            fields = new ArrayList<>();
        }
       for (Attribute att : atts) {
            if (att instanceof FieldNode) {
                FieldNode fn = (FieldNode)att;
                fn = fn.createProxy(this);
                
                fields.add(fn);
            } else {
                super.setAttributes(att);
            }
        }
    }
    
    

    @Override
    public Iterator<FieldNode> iterator() {
        return fields.iterator();
    }
    
    public FieldNode getColumn(int column) {
        return fields.get(column);
    }
    
    public FieldNode[] toFieldArray() {
        return fields.toArray(new FieldNode[fields.size()]);
    }
    
    public int getColumnCount() {
        return fields.size();
    }

   
    
    
    
}
