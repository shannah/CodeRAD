/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

/**
 *
 * @author shannah
 */
public  class EntityProperty<T extends Entity> extends AbstractProperty<T> {
    
    public EntityProperty(Class<T> cls) {
        super(new ContentType(new Name(cls.getName()), cls));
    }
    
    
    
}
