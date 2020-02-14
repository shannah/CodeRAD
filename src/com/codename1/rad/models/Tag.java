/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

import com.codename1.rad.models.Property.Name;

/**
 *
 * @author shannah
 */
public class Tag extends Attribute<Name> {
    
    public Tag(Name value) {
        super(value);
    }
    
    public Tag(String str) {
        this(new Name(str));
    }
    
    public Tag() {
        this(new Name(""));
    }
    
    
}
