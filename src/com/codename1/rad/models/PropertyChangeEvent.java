/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

import com.codename1.ui.events.ActionEvent;

/**
 *
 * @author shannah
 */
public class PropertyChangeEvent extends ActionEvent {

    /**
     * @return the property
     */
    public Property getProperty() {
        return property;
    }

    /**
     * @return the oldValue
     */
    public Object getOldValue() {
        return oldValue;
    }

    /**
     * @return the newValue
     */
    public Object getNewValue() {
        return newValue;
    }
    private Property property;
    private Object oldValue, newValue;
    public PropertyChangeEvent(Entity source, Property prop, Object oldVal, Object newVal) {
        super(source);
        this.property = prop;
        this.oldValue = oldVal;
        this.newValue = newVal;
    }
    
    
    
}
