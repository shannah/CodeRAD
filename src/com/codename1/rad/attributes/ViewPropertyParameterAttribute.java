/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.attributes;

import com.codename1.rad.ui.ViewPropertyParameter;
import com.codename1.rad.models.Attribute;

/**
 * Attribute to add a {@link ViewPropertyParameter} to a node.  This can be added to any node, as interested views can use {@link com.codename1.rad.nodes.Node#findInheritedAttribute(java.lang.Class) } to 
 * obtain the parameter it wants.  
 * @author shannah
 * @see com.codename1.rad.ui.UI#param(com.codename1.rad.ui.ViewProperty, java.lang.Object) 
 * @see com.codename1.rad.ui.UI#param(com.codename1.rad.ui.ViewProperty, com.codename1.rad.models.Tag...) 
 */
public class ViewPropertyParameterAttribute<T> extends Attribute<ViewPropertyParameter<T>> {
    
    public ViewPropertyParameterAttribute(ViewPropertyParameter<T> value) {
        super(value);
    }
    
}
