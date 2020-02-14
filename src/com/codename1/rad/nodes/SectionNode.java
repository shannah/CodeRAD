/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.nodes;


import com.codename1.rad.attributes.Columns;
import com.codename1.rad.models.Attribute;
import com.codename1.rad.models.Property;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author shannah
 */
public class SectionNode extends Node implements Iterable<FieldNode> {
    private Fields fields;
    
    public SectionNode(Attribute... atts) {
        super(null, atts);
        if (fields == null) {
            fields = new Fields();
        }

        
    }

    @Override
    public Iterator<FieldNode> iterator() {
        return fields.iterator();
    }
    
    public void setAttributes(Attribute... atts) {
        if (fields == null) {
            fields = new Fields();
            
        }

        //super.setAttributes(atts);
        
        for (Attribute att : atts) {
            if (att.getClass() == FieldNode.class) {
                fields.add((FieldNode)att);
                ((FieldNode)att).setParent(this);
            } else {
                super.setAttributes(att);  
            }
        }
    }

    public Property.Label getLabel() {
        return (Property.Label)findAttribute(Property.Label.class);
    }
    
    public Property.Description getDescription() {
        return (Property.Description)findAttribute(Property.Description.class);
    }
    
    public class Fields implements Iterable<FieldNode> {
        private ArrayList<FieldNode> fields = new ArrayList<>();

        @Override
        public Iterator<FieldNode> iterator() {
            return fields.iterator();
        }
        
        public void add(FieldNode node) {
            fields.add(node);
        }
        
        
    }
    
    public Columns getColumns() {
        Columns out = (Columns)findInheritedAttribute(Columns.class);
        if (out == null) {
            out = new Columns(1);
        }
        return out;
    }
    
    
}
