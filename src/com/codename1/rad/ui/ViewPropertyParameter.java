/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.models.ContentType;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.Property;
import com.codename1.rad.models.Tag;
import com.codename1.rad.models.Tags;

/**
 * A parameter that can be added to the node hierarchy to set the value or binding of a ViewProperty.
 * @author shannah
 */
public class ViewPropertyParameter<T> {
    private ViewProperty<T> property;
    private T value;
    private Tags tags;
    
    private ViewPropertyParameter() {
        
    }
    
    
    public  static <V> ViewPropertyParameter<V> createValueParam(ViewProperty<V> property, V value) {
        ViewPropertyParameter<V> out = new ViewPropertyParameter<V>();
        out.value = value;
        out.property = property;
        return out;
        
    }
    
    public  static <V> ViewPropertyParameter<V> createBindingParam(ViewProperty<V> property, Tag... tags) {
        ViewPropertyParameter<V> out = new ViewPropertyParameter<V>();
        out.tags = new Tags(tags);
        out.property = property;
        return out;
        
    }
    
    public ViewProperty<T> getProperty() {
        return property;
        
    }
    
    public T getValue() {
        return value;
    }
    
    public Tags getBindings() {
        return tags;
    }
    
    
    public T getValue(Entity context) {
        if (tags == null) {
            return value;
        }
        return (T)context.getEntityType().getPropertyValue(context, property.getContentType(), tags.toArray());
    }
    
    public Property findProperty(Entity context) {
        if (tags == null) {
            return null;
        }
        
        return (Property)context.getEntityType().findProperty(tags.toArray());
    }
    
    
}
