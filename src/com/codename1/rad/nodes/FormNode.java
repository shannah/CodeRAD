/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.nodes;

import com.codename1.rad.ui.ActionCategories;
import com.codename1.rad.models.Attribute;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author shannah
 */
public class FormNode extends Node implements Iterable<SectionNode>, ActionCategories {
    private Sections sections;
    private SectionNode globalSection;
    private ListNode rootList;
    private ViewNode rootView;
    
    public FormNode(Attribute... atts) {
        super(null, atts);
        if (sections == null) {
            sections = new Sections();
        }

    }

    @Override
    public Attribute findAttribute(Class type) {
        Attribute out = super.findAttribute(type);
        
        
        return out;
    }

    
    
    public void setAttributes(Attribute... atts) {
        if (sections == null) {
            sections = new Sections();
        }

        //super.setAttributes(atts);
        for (Attribute att : atts) {
            if (att.getClass() == SectionNode.class) {
                sections.add((SectionNode)att);
                ((SectionNode)att).setParent(this);
            } else if (att.getClass() == FieldNode.class) {
                if (globalSection == null) {
                    globalSection = new SectionNode();
                    globalSection.setParent(this);
                }
                globalSection.setAttributes(att);
            } else if (att instanceof ListNode) {
                rootList = (ListNode)att;
                super.setAttributes(att);
            } else if (att instanceof ViewNode) {
                rootView = (ViewNode)att;
                super.setAttributes(att);
            } else {
                super.setAttributes(att);
            }
        }
    }

    @Override
    public Iterator<SectionNode> iterator() {
        if (globalSection != null) {
            ArrayList<SectionNode> out = new ArrayList<>();
            out.add(globalSection);
            out.addAll(sections.sections);
            return out.iterator();
        }
            
        
        return sections.iterator();
    }
    
    public Sections getSections() {
        if (globalSection != null) {
            Sections out = new Sections();
            out.add(globalSection);
            for (SectionNode sec : sections) {
                out.add(sec);
            }
            return out;
        }
        return sections;
    }
    
    
    public class Sections implements Iterable<SectionNode> {
        private List<SectionNode> sections = new ArrayList<>();

        @Override
        public Iterator<SectionNode> iterator() {
            return sections.iterator();
        }
        
        public void add(SectionNode node) {
            sections.add(node);
        }
        
        public boolean isEmpty() {
            return sections.isEmpty();
        }
    }
    
    public FieldNode[] getAllFields() {
        List<FieldNode> out = new ArrayList<>();
        for (SectionNode section : this) {
            for (FieldNode fn : section) {
                out.add(fn);
            }
        }
        return out.toArray(new FieldNode[out.size()]);
    }
    
    

    
    
    
    
    public ListNode getRootList() {
        return rootList;
    }
    
    public ViewNode getRootView() {
        return rootView;
    }
    
}
