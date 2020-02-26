/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.attributes;

import com.codename1.rad.models.EntityTest;
import com.codename1.rad.models.Property.Test;

/**
 * Selected condition, for {@link ActionNode}.  This test will be run when updating an action's View to determine whether it is in selected state. 
 * 
 * If this attribute is added to an action, the {@link DefaultActionViewFactory} will render the action as a toggle button instead of a 
 * regular button.
 * @author shannah
 * @see com.codename1.rad.ui.UI#selectedCondition(com.codename1.rad.models.EntityTest) 
 */
public class SelectedCondition extends Test {
    
    public SelectedCondition(EntityTest value) {
        super(value);
    }
    
}
