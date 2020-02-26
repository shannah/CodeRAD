/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

/**
 * A {@link Property} containing an {@link Entity} value.
 * @author shannah
 * 
 * @see EntityType#entity(java.lang.Class) 
 */
public  class EntityProperty<T extends Entity> extends AbstractProperty<T> {
    
    public EntityProperty(Class<T> cls) {
        super(new ContentType(new Name(cls.getName()), cls));
    }
    
    
    
}
