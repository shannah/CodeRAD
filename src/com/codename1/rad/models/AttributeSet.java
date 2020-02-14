/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author shannah
 */
public class AttributeSet implements Iterable<Attribute> {
    private Map<Class<? extends Attribute>, Attribute> attributes = new HashMap<>();
    
    public void setAttributes(Attribute... atts) {
        for (Attribute att : atts) {
            if (att != null) {
                attributes.put(att.getClass(), att);
            }
        }
    }
    
    public <T extends Attribute> T getAttribute(Class<T> attType) {
        return (T)attributes.get(attType);
    }
    
    public <T extends Attribute> T getAttribute(Class<T> type, T defaultVal) {
        if (attributes.containsKey(type)) {
            return (T)attributes.get(type);
        } else {
            return defaultVal;
        }
    }
    
    public <V> V getValue(Class<? extends Attribute<V>> type, V defaultValue) {
        Attribute<V> att = getAttribute(type, null);
        if (att == null) {
            return defaultValue;
        }
        return att.getValue();
        
    }
    
    public void addAll(AttributeSet... sources) {
        for (AttributeSet as : sources) {
            for (Attribute a : as) {
                setAttributes(a);
            }
        }
        
    }
    
    public static AttributeSet merge(AttributeSet... attrs) {
        AttributeSet out = new AttributeSet();
        out.addAll(attrs);
        return out;
    }

    @Override
    public Iterator<Attribute> iterator() {
        return attributes.values().iterator();
    }
}
