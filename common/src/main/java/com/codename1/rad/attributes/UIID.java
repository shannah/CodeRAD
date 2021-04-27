/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.attributes;

import com.codename1.rad.models.Attribute;

import com.codename1.rad.models.StringProvider;
import com.codename1.rad.models.Entity;

/**
 * Attribute used to provide a UIID for a view.  This can be added to any {@link com.codename1.rad.nodes.Node}, but there is no guarantee that it will be used.  Views
 * are free to use this attribute or not.
 * @author shannah
 * @see com.codename1.rad.ui.UI#uiid(java.lang.String) 
 */
public class UIID extends Attribute<String> {
    private StringProvider provider;
    public UIID(String value) {
        this(value, e->{
            return value;
        });
    }
    
    public UIID(String value, StringProvider provider) {
        super(value);
        this.provider = provider;
    }
    
    public String getValue(Entity context) {
        if (provider != null) {
            return provider.getString(context);
        } else {
            return getValue();
        }
    }
    
}
