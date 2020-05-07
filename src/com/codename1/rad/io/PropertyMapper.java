/*
 * Copyright 2020 shannah.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codename1.rad.io;

import com.codename1.rad.models.ContentType;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.EntityList;
import com.codename1.rad.models.Property;
import com.codename1.rad.models.Tag;
import java.util.Date;

/**
 *
 * @author shannah
 */
public class PropertyMapper<T> {
    private String name;
    private ContentType contentType;
    private Tag[] tags;
    private Property property;
    
    public boolean set(Entity target, Object value) {
        value = prepare(value);
        
        if (value == null) {
            return target.setText(null, tags);
            
        }
        Class cls = value.getClass();
        if (String.class.equals(cls)) {
            return target.setText((String)value, tags);
            
        }
        
        if (Double.class.equals(cls)) {

            return target.setDouble((Double)value, tags);
            
        }
        
        if (Float.class.equals(cls)) {
            return target.setFloat((Float)value, tags);
        }
        
        if (Date.class.equals(cls)) {
            return target.setDate((Date)value, tags);
        }
        
        if (Integer.class.equals(cls)) {
            return target.setInt((Integer)value, tags);
        }
        
        if (Boolean.class.equals(cls)) {
            return target.setBoolean((Boolean)value, tags);
        }
        
        if (Entity.class.equals(cls)) {
            return target.setEntity((Entity)value, tags);
        }
        
        if (EntityList.class.isAssignableFrom(cls)) {
            EntityList existing = target.getEntityList(tags);
            if (existing == null) {
                existing = createEntityList();
            } else {
                existing.clear();
            }
            EntityList<Entity> el = (EntityList<Entity>)value;
            for (Entity e : el) {
                existing.add(e);
            }
        }
        
        throw new RuntimeException("Not implemented yet");
    }
    
    public EntityList createEntityList() {
        return new EntityList();
    }
    
    public Entity createEntity() {
        return new Entity();
    }
    
    public Object prepare(Object value) {
        return value;
    }
    
    
}
