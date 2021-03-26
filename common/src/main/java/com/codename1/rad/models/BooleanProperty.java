/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

import static com.codename1.rad.models.ContentType.BooleanType;

/**
 * A {@link Property} type for {@link Boolean} values.  Use {@link EntityType#Boolean(com.codename1.rad.models.Attribute...) } as a short cut.
 * 
 * @author shannah
 * 
 * @see EntityType#Boolean(com.codename1.rad.models.Attribute...) 
 */
public class BooleanProperty extends AbstractProperty<Boolean> {
    public BooleanProperty() {
        super(BooleanType);
    }
}
