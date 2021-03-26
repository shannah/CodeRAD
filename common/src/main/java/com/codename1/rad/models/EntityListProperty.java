/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

/**
 * A property containing an {@link EntityList} value.
 * @author shannah
 * 
 * @see EntityType#list(java.lang.Class, com.codename1.rad.models.Attribute...) 
 */
public class EntityListProperty<T extends EntityList> extends EntityProperty<T> {
    
    public EntityListProperty(Class<T> cls) {
        super(cls);
    }
    
}
