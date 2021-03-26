/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

import static com.codename1.rad.models.ContentType.DateType;
import java.util.Date;

/**
 * A {@link Property} that stores {@link java.util.Date} values.
 * @author shannah
 * 
 * @see EntityType#date(com.codename1.rad.models.Attribute...) 
 */
public class DateProperty extends AbstractProperty<Date> {
    public DateProperty() {
        super(DateType);
    }
}
