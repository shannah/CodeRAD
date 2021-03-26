/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.attributes;

import com.codename1.rad.models.Attribute;

/**
 * An attribute to specify the number of columns in a form or a form section.  Can be passed as an attribute to {@link FormNode} or {@link SectionNode}.
 * @author shannah
 * @see com.codename1.rad.ui.UI#columns(int) 
 */
public class Columns extends Attribute<Integer> {
    
    public Columns(Integer value) {
        super(value);
    }
    
}
