/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.attributes;

import com.codename1.rad.models.Attribute;

/**
 * Attribute used to provide a badge UIID for an action.  This can be added to any {@link com.codename1.rad.nodes.Node}, but there is no guarantee that it will be used.  Views
 * are free to use this attribute or not.
 * @author shannah
 * @see com.codename1.rad.ui.UI#badgeUiid(java.lang.String) 
 */
public class BadgeUIID extends Attribute<String> {
    
    public BadgeUIID(String value) {
        super(value);
    }
    
}
