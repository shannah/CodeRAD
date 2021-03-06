/*
 * Copyright (c) 2012, Eric Coolman, Codename One and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Codename One designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *  
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 * 
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Please contact Codename One through http://www.codenameone.com/ if you 
 * need additional information or have any questions.
 */
package com.codename1.rad.processing;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;

/**
 * Internal class, do not use.
 *
 * An accessor implementation for working with subset of data.
 *
 * @author Eric Coolman
 *
 */
class SubContent implements StructuredContent {

    private List<StructuredContent> root;
    private StructuredContent parent;

    /**
     * Construct from subset of content.
     *
     * @param content subset content
     */
    public SubContent(List<StructuredContent> content) {
        this.root = content;
    }

    /**
     * INTERNAL - link a node to it's parent so we can traverse backwards when
     * required.
     *
     * @param content a subset of data.
     * @param parent the parent element of content.
     */
    SubContent(List<StructuredContent> content, StructuredContent parent) {
        this.root = content;
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.codename1.processing.StructuredContent#getChildren(java.lang.String)
     */
    public List getChildren(String name) {
        List v = new ArrayList();
        if (root != null) {
            for (StructuredContent sc : root) {
                v.addAll(sc.getChildren(name));
            }
        }
        return v;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.codename1.processing.StructuredContent#getChild(int)
     */
    public StructuredContent getChild(int index) {
        if (root != null && root.size() > 0) {
            return root.get(0).getChild(index);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.codename1.processing.StructuredContent#getDescendants(java.lang.String
     * )
     */
    public List getDescendants(String name) {
        List v = new ArrayList();
        if (root != null) {
            for (StructuredContent sc : root) {
                v.addAll(sc.getDescendants(name));
            }
        }
        return v;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.codename1.processing.StructuredContent#getAttribute(java.lang.String)
     */
    public String getAttribute(String name) {
        if (root != null && root.size() > 0) {
            return root.get(0).getAttribute(name);
        }
        return null;
    }
    
    public Iterable<String> getAttributeIterator(String name) {
        int len = root == null ? 0 : root.size();
        List out = new ArrayList();
        for (int i=0; i<len; i++) {
            Object val = root.get(i).getAttribute(name);
            if (val != null) {
                out.add(val);
            }
        }
        return (Iterable<String>)out;
        
    }
    
    public int size() {
        return root == null ? 0 : root.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.codename1.processing.StructuredContent#getAttributes()
     */
    public Map getAttributes() {
        if (root != null && root.size() > 0) {
            return root.get(0).getAttributes();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.codename1.processing.StructuredContent#getParent()
     */
    public StructuredContent getParent() {
        return parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.codename1.processing.StructuredContent#getText()
     */
    public String getText() {
        if (root != null && root.size() > 0) {
            return root.get(0).getText();
        }
        return null;
    }
    
    public Iterable<String> getTextIterator() {
        int len = root == null ? 0 : root.size();
        List out = new ArrayList();
        for (int i=0; i<len; i++) {
            Object val = root.get(i).getText();
            if (val != null) {
                out.add(val);
            }
        }
        return (Iterable<String>)out;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.codename1.processing.StructuredContent#getNativeRoot()
     */
    public Object getNativeRoot() {
        if (parent != null) {
            return parent.getNativeRoot();
        }
        return null;
    }

    @Override
    public String toString() {
        return ""+root;
    }
    
    
}
