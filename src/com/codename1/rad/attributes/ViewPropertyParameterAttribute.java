/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.attributes;

import com.codename1.rad.ui.ViewPropertyParameter;
import com.codename1.rad.models.Attribute;

/**
 *
 * @author shannah
 */
public class ViewPropertyParameterAttribute<T> extends Attribute<ViewPropertyParameter<T>> {
    
    public ViewPropertyParameterAttribute(ViewPropertyParameter<T> value) {
        super(value);
    }
    
}
