/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.attributes;

import com.codename1.rad.ui.WidgetTypes;
import com.codename1.rad.models.Attribute;
import com.codename1.rad.models.Property.Name;

/**
 * Attribute for specifying the {@link WidgetType} for a {@link FieldNode}.  The {@link WidgetType} is used by the {@link PropertyViewFactory}
 * for rendering the view for a {@link FieldNode}.
 * @author shannah
 * @see com.codename1.rad.ui.UI#field(com.codename1.rad.models.Attribute...) 
 * 
 */
public class WidgetType extends Attribute<Name> implements WidgetTypes {
    
    
    public WidgetType(Name name) {
        super(name);
    }

    
    
    
}
