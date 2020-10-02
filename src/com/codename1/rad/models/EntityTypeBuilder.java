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
package com.codename1.rad.models;


import java.util.ArrayList;

/**
 * A builder to help constructing EntityTypes  This provides, and possibly easier syntax
 * alternative to creating EntityTypes as static internal classes of their associated Entity class.
 * 
 * === Example
 * 
 * [source,java]
 * ----
 * public static EntityType personType = new EntityTypeBuilder()
            .string(Person.name)
            .string(Person.email)
            .Date(Person.birthDate)
            .list(People.class, Person.children)
            .build();
   ----
 * 
 * @author shannah
 */
public class EntityTypeBuilder {
    ArrayList<Property> properties = new ArrayList();
    private Class listType, rowType;
    
    private Class entityClass = Entity.class;
    
    public EntityTypeBuilder entityClass(Class<? extends Entity> cls) {
        this.entityClass = cls;
        return this;
    }
    
    public EntityTypeBuilder string(Attribute... atts) {
        StringProperty prop = new StringProperty();
        prop.setAttributes(atts);
        properties.add(prop);
        return this;
    }
    
    public EntityTypeBuilder Double(Attribute... atts) {
        DoubleProperty prop = new DoubleProperty();
        prop.setAttributes(atts);
        properties.add(prop);
        return this;
    }
    
    public EntityTypeBuilder Date(Attribute... atts) {
        DateProperty prop = new DateProperty();
        prop.setAttributes(atts);
        properties.add(prop);
        return this;
    }
    
    public EntityTypeBuilder Integer(Attribute... atts) {
        IntProperty prop = new IntProperty();
        prop.setAttributes(atts);
        properties.add(prop);
        return this;
    }
    
    public EntityTypeBuilder Boolean(Attribute... atts) {
        BooleanProperty prop = new BooleanProperty();
        prop.setAttributes(atts);
        properties.add(prop);
        return this;
    }
    
    public EntityTypeBuilder list(Class<?> listClass, Attribute... atts) {
        EntityListProperty prop = new EntityListProperty(listClass);
        prop.setAttributes(atts);
        properties.add(prop);
        return this;
    }
    
    public EntityTypeBuilder entity(Class<?> cls, Attribute... atts) {
        EntityProperty prop = new EntityProperty(cls);
        prop.setAttributes(atts);
        properties.add(prop);
        return this;
    }
    
    public EntityTypeBuilder object(Class cls, Attribute... atts) {
        SimpleProperty prop = new SimpleProperty(cls);
        prop.setAttributes(atts);
        properties.add(prop);
        return this;
        
    }
    
    public EntityTypeBuilder listType(Class<? extends EntityList> listType) {
        this.listType = listType;
        return this;
    }
    
    public EntityTypeBuilder rowType(Class<?extends Entity> rowType) {
        this.rowType = rowType;
        return this;
    }
    
    public EntityType build() {
        EntityType out = new EntityType(properties.toArray(new Property[properties.size()]));
        out.setEntityClass(entityClass);
        if (listType != null) {
            out.setListType(listType);
        }
        if (rowType != null) {
            out.setRowType(rowType);
        }
        return out;
    }
    
}
