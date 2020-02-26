/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

import java.util.Objects;

/**
 * An interface to be implemented by all properties of entities.  Generally new Property types should extend {@link AbstractProperty} since it includes default implementations of the boiler plate stuff.
 * @author shannah
 */
public interface Property<T> {
    
    public AttributeSet getAttributes();
    //public Class<T> getClazz();
    public ContentType<T> getContentType();
    
    //public String getName();
    public T getValue(Entity entity);
    public void setValue(Entity entity, T value);

    public void freeze();
    
    public static class Widget extends Attribute<WidgetDescriptor> {
        
        public Widget(WidgetDescriptor value) {
            super(value);
        }
        
    }
    
    public static class Label extends Attribute<String> {
        public Label(String value) {
            super(value);
        }
    }
    
    public static class Name extends Attribute<String> {
        
        public Name(String value) {
            super(value);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj.getClass() == Name.class) {
                Name t = (Name)obj;
                return Objects.equals(t.getValue(), getValue());
            }
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getValue());
        }

    
    }
    
    public static class Editable extends Attribute<Boolean> {
        
        public Editable(Boolean value) {
            super(value);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj.getClass() == Name.class) {
                Name t = (Name)obj;
                return Objects.equals(t.getValue(), getValue());
            }
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getValue());
        }
        
        
        
    }
    
    public static class Test extends Attribute<EntityTest> {
        
        public Test(EntityTest value) {
            super(value);
        }
        
    }
    
    public static class Description extends Attribute<String> {
        public Description(String value) {
            super(value);
        }
    }
    
    public <V extends Attribute> V getAttribute(Class<V> type);
    public Tags getTags();
    
    
}
