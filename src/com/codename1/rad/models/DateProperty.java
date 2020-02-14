/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

import static com.codename1.rad.models.ContentType.DateType;
import java.util.Date;

/**
 *
 * @author shannah
 */
public class DateProperty extends AbstractProperty<Date> {
    public DateProperty() {
        super(DateType);
    }
}
