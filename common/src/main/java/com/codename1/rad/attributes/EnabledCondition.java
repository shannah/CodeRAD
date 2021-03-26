/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.attributes;

import com.codename1.rad.models.EntityTest;
import com.codename1.rad.models.Property.Test;

/**
 * Enabled condition, for Action Nodes.  Supported by {@link ActionNode}.
 * @author shannah
 * @see com.codename1.rad.ui.UI#enabledCondition(com.codename1.rad.models.EntityTest) 
 */
public class EnabledCondition extends Test {
    
    public EnabledCondition(EntityTest value) {
        super(value);
    }
    
}
