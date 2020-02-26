/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.attributes;

import com.codename1.rad.ui.ActionStyle;
import com.codename1.rad.models.Attribute;

/**
 * Attribute for specifying the style of an action.  Can be passed as an attribute to {@link ActionNode} or {@link ActionsNode}.
 * @author shannah
 * 
 * @see com.codename1.rad.ui.UI#actionStyle(com.codename1.rad.ui.ActionStyle) 
 */
public class ActionStyleAttribute extends Attribute<ActionStyle> {
    
    public ActionStyleAttribute(ActionStyle value) {
        super(value);
    }
    
}
