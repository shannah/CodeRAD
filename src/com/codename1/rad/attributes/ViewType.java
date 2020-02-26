/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.attributes;

import com.codename1.rad.models.Attribute;
import com.codename1.rad.models.Property;
import com.codename1.rad.models.Property.Name;
import com.codename1.rad.ui.DefaultEntityViewFactory;
import com.codename1.rad.ui.entityviews.MultiButtonEntityView;
import java.util.Objects;

/**
 * An attribute for specifying the view type for a view node so that the {@link EntityViewFactory} can generate the proper type of view.
 * @author shannah
 * @see com.codename1.rad.ui.UI#viewFactory(com.codename1.rad.ui.EntityViewFactory) 
 */
public class ViewType extends Attribute<Name> {
    
    /**
     * A multibutton view type.  Will result in a {@link MultiButtonEntityView} when {@link ViewNode} is converted to an {@link EntityView} by
     * {@link DefaultEntityViewFactory}.
     */
    public static final ViewType MULTIBUTTON = new ViewType(new Property.Name("multibutton"));

    
    
    public ViewType(Property.Name name) {
        super(name);
    }

}
