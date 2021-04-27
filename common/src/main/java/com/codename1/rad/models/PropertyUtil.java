/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

import java.util.Objects;

/**
 * An internal utility class for Properties to get their "raw" properties from their entity.
 * @author shannah
 */
public class PropertyUtil {
    
    /**
     * Get the raw value of a property from an entity.
     * @param entity
     * @param prop
     * @return 
     */
    public static Object getRawProperty(Entity entity, Property prop) {
        
        if (entity.getEntity().properties != null) {
            return entity.getEntity().properties.get(prop);
        }
        return null;
    }
    
    /**
     * Set the raw value of a property in an entity.
     * @param entity
     * @param prop
     * @param value 
     */
    public static void setRawProperty(Entity entity, Property prop, Object value) {
        if (value != null && !prop.getContentType().getRepresentationClass().isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("Property "+prop+" of type "+prop.getContentType().getRepresentationClass()+" is not assignable by value "+value+" of type "+value.getClass());
        }
        if (entity.getEntity().properties == null) {
            if (entity.getEntityType() != null) {
                if (!entity.getEntityType().contains(prop)) {
                    throw new IllegalArgumentException("Entity type "+entity.getEntityType()+" does not contain property "+prop);
                }
                entity.getEntity().initProperties();
            }
        }
        if (entity.getEntityType() == null) {
            entity.setEntityType(new DynamicEntityType());
        }
        if (entity.getEntityType().getClass() == DynamicEntityType.class) {
            entity.getEntityType().addProperty(prop);
        }
        if (!entity.getEntityType().contains(prop)) {
            throw new IllegalArgumentException("Entity type "+entity.getEntityType()+" does not contain property "+prop);
        }
        Object existing = entity.getEntity().properties.get(prop);
        if (!Objects.equals(existing, value)) {
            if (entity.hasVetoablePropertyChangeListeners(prop)) {
                VetoablePropertyChangeEvent vevt = new VetoablePropertyChangeEvent(entity, prop, existing, value);
                entity.getEntity().fireVetoablePropertyChangeEvent(vevt);
                if (vevt.isVetoed()) {
                    throw new PropertyVetoException(vevt.getReason(), vevt);
                }
            }
            entity.getEntity().properties.put(prop, value);
            entity.getEntity().setChangedInternal();
            if (entity.hasPropertyChangeListeners(prop)) {
                entity.getEntity().firePropertyChangeEvent(new PropertyChangeEvent(entity, prop, existing, value));
            }
            
        }
        
        
    }
}
