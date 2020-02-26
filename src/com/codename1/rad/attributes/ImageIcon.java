/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.attributes;

import com.codename1.rad.models.Attribute;
import com.codename1.ui.Image;

/**
 * An attribute for specifying the icon for an action as an {@link com.codename1.ui.Image}.  This is supported by {@link com.codename1.rad.nodes.ActionNode}.
 * @author shannah
 * 
 * @see MaterialIcon
 * @see com.codename1.rad.ui.UI#icon(com.codename1.ui.Image) 
 */
public class ImageIcon extends Attribute<Image> {
    
    public ImageIcon(Image value) {
        super(value);
    }
    
}
