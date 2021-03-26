/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

import com.codename1.ui.Component;

/**
 * A base class for a transformer that can be registered with {@link ContentType} to add support for converting between content types. 
 * 
 * It should rarely be necessary to implement a custom DataTransformer as the built-in content types will handle most cases, and if a custom ContentType is introduced,
 * it will generally know how to convert to and from the basic types.  This would only be necessary for converting between two content types that 
 * are not base types and do not know about each other.
 * @author shannah
 */
public class DataTransformer extends Attribute {
    
    public DataTransformer() {
        super(null);
    }
    
    
    /**
     * Checks to see if this transformer supports the given source and target content types.
     * @param source The source content type.
     * @param target The target content type.
     * @return 
     */
    public boolean supports(ContentType source, ContentType target) {
        return false;
    }
    /**
     * Transforms data from one content type to another.
     * @param <T> The source type representation class.
     * @param <V> The target type representation class.
     * @param source The source type
     * @param target The target type
     * @param data The data to be converted.
     * @return 
     */
    public <T,V> V transform(ContentType<T> source, ContentType<V> target, T data) {
        throw new IllegalArgumentException("Cannot transform data "+data+" from "+source+" to "+target);
    }
    
    
}
