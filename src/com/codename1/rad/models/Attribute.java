/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

/**
 *
 * @author shannah
 */
public class Attribute<T> {
    private T value;
    
    public Attribute(T value) {
        this.value = value;
    }
    
    
    
    public T getValue() {
        return value;
    }
    
    public void freeze() {
        
    }

    
    
}
