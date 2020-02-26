/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

import static com.codename1.rad.models.ContentType.IntegerType;

/**
 * A {@link Property} containing {@link Integer} values.
 * @author shannah
 * 
 * @see EntityType#Integer(com.codename1.rad.models.Attribute...) 
 */
public class IntProperty extends AbstractProperty<Integer> {
    public IntProperty() {
        super(IntegerType);
    }
}
