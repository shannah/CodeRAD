/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.attributes;

import com.codename1.rad.models.Attribute;

/**
 * An attribute for specifying the icon of an {@link com.codename1.rad.nodes.ActionNode} as a material icon.
 * 
 * @author shannah
 * @see ImageIcon
 * @see com.codename1.rad.ui.UI#icon(char) 
 */
public class MaterialIcon extends Attribute<Character> {
    
    public MaterialIcon(Character value) {
        super(value);
    }
    
}
