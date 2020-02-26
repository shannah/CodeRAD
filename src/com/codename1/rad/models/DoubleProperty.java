/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

import static com.codename1.rad.models.ContentType.DoubleType;

/**
 * A {@link Property} that stores {@link Double} values.
 * @author shannah
 * 
 * @see EntityType#Double(com.codename1.rad.models.Attribute...) 
 */
public class DoubleProperty extends AbstractProperty<Double> {
    public DoubleProperty() {
        super(DoubleType);
    }
}
