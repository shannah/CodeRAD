/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.attributes;

import com.codename1.rad.models.EntityTest;
import com.codename1.rad.models.Property.Test;

/**
 * An attribute to specify a test condition.  A test condition is a function that takes an {@link Entity} as input, and returns a boolean
 * value.  This is useful for providing tests to {@link ActionNodes} to determine selected and enabled states.
 * @author shannah
 * @see com.codename1.rad.ui.UI#condition(com.codename1.rad.models.EntityTest) 
 */
public class Condition extends Test {
    
    public Condition(EntityTest value) {
        super(value);
    }
    
}
