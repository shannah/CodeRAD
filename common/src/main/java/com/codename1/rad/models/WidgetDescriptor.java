/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

import com.codename1.rad.models.Property.Description;
import com.codename1.rad.models.Property.Label;



/**
 * A special class that provides configuration for a "widget" which is used to edit a {@link Property} in the {@link EntityEditor}.
 * @author shannah
 * @see EntityType#widget(com.codename1.rad.models.Attribute...) 
 * @see com.codename1.rad.ui.UI#widget(com.codename1.rad.models.Attribute...) 
 * 
 */
public class WidgetDescriptor {
    private Property property;
    private AttributeSet attributes = new AttributeSet();
    
    public WidgetDescriptor(){
    
    }

    void setProperty(Property prop) {
        this.property = prop;
    }
    
    public WidgetDescriptor(Property prop) {
        this.property = prop;
    }
    
    public void setAttributes(Attribute... atts) {
        attributes.setAttributes(atts);
    }
    
    public <T extends Attribute> T getAttribute(Class<T> type) {
        return attributes.getAttribute(type);
    }

    public Type getWidgetType() {
        return attributes.getAttribute(Type.class);
    }
    
    public Label getLabel() {
        return attributes.getAttribute(Label.class);
    }
    
    public Description getDescription() {
        return attributes.getAttribute(Description.class);
    }
    
    public static class Type extends Attribute<Class> {
        
        public Type(Class value) {
            super(value);
        }
        
    }
    
    public AttributeSet getAllAttributes() {
        
        AttributeSet out = new AttributeSet();
        if (property != null) {
            out.addAll(property.getAttributes());
        }
        
        out.addAll(attributes);
        return out;
    }
    
    
}
