/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.models.Attribute;
import com.codename1.rad.models.AttributeSet;
import com.codename1.rad.models.ContentType;

/**
 *
 * @author shannah
 */
public class ViewProperty<T> {
    private ContentType<T> contentType;
    private AttributeSet attributes = new AttributeSet();
    
    public ViewProperty(ContentType<T> contentType, Attribute... attributes) {
        this.contentType = contentType;
        this.attributes.setAttributes(attributes);
    }
    
    public ContentType<T> getContentType() {
        return contentType;
    }
    
    public static ViewProperty<String> stringProperty(Attribute... atts) {
        return new ViewProperty<String>(ContentType.Text, atts);
    }
    
    public static ViewProperty<Integer> intProperty(Attribute... atts) {
        return new ViewProperty<Integer>(ContentType.IntegerType, atts);
    }
    
    public static ViewProperty<Boolean> booleanProperty(Attribute... atts) {
        return new ViewProperty<Boolean>(ContentType.BooleanType, atts);
    }
    
    public static ViewProperty<Double> doubleProperty(Attribute... atts) {
        return new ViewProperty<Double>(ContentType.DoubleType, atts);
    }
    
    public static ViewProperty<Float> floatProperty(Attribute... atts) {
        return new ViewProperty<Float>(ContentType.FloatType, atts);
    }
}
