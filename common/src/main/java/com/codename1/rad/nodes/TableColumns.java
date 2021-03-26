/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.nodes;

import com.codename1.rad.models.Attribute;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A node that specifies the columns that should be included in a {@link Table}.  This node should be added to the {@link FieldNode} with type {@link com.codename1.rad.attributes.WidgetType#TABLE}.
 * 
 * == Example
 * 
 * [source,java]
 * ----
 * ...
 * table(actions(OVERFLOW_MENU, deleteAction, printAction),
        label("Quick Links"),
        description("Useful links related to this person"),
        editable(true),
        //property(quicklinks),
        tags(com.codename1.rad.schemas.Person.url),
        columns(new QuickLinkEditor().getAllFields()) <1>
 )
  ...
  * ---
  * <1> This example imports all of the fields from another UI descriptor, and adds them as columns to this table. 
  * 
 * 
 * @author shannah
 * 
 * @see com.codename1.rad.ui.UI#columns(com.codename1.rad.nodes.FieldNode...) 
 * @see com.codename1.rad.ui.UI#table(com.codename1.rad.models.Attribute...) 
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
