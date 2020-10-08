/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;



/**
 * An abstract base class for {@link Property} that includes most of the essential plumbing 
 * to handle attributes and getting and setting values.
 * @author shannah
 * @param <T> The type of value held in thi sproperty.
 */
public class AbstractProperty<T> implements Property<T>  {
    private final AttributeSet attributes = new AttributeSet();
    private final ContentType<T> contentType;
    private final Getter<T> defaultGetter = (entity, unused)->{
        return (T) PropertyUtil.getRawProperty(entity, this);
    };
    private final Setter<T> defaultSetter = (entity, value, unused) ->{
        PropertyUtil.setRawProperty(entity, this, value);
    };
    
    private Getter<T> getter;
    private Setter<T> setter;
    
    public AbstractProperty<T> getter(Getter<T> getter) {
        this.getter = getter;
        return this;
    }
    
    public AbstractProperty<T> setter(Setter<T> setter) {
        this.setter = setter;
        return this;
    }
    
    public AbstractProperty(ContentType<T> contentType) {
        this.contentType = contentType;
    }
    
    public void setAttributes(Attribute... atts) {
        for (Attribute att : atts) {
            if (att instanceof Tag) {
                getTags().addTags((Tag)att);
            } else if (att instanceof GetterAttribute) {
                getter(((GetterAttribute)att).getValue());
            } else if (att instanceof SetterAttribute) {
                setter(((SetterAttribute)att).getValue());
            } else {
                attributes.setAttributes(att);
            }
        }
        
    }
    
    

    @Override
    public T getValue(Entity entity) {
        if (getter != null) {
            return getter.getValue(entity, defaultGetter);
        } else {
            return defaultGetter.getValue(entity, null);
        }
    }

    @Override
    public void setValue(Entity entity, T value) {
        if (setter != null) {
            setter.setValue(entity, value, defaultSetter);
        } else {
            defaultSetter.setValue(entity, value, null);
        }
    }
    
    public Label getLabel() {
        return attributes.getAttribute(Label.class);
    }
    
    public Description getDescription() {
        return attributes.getAttribute(Description.class);
    }
    
    public Widget getWidget() {
        return attributes.getAttribute(Widget.class);
        
    }
    
    @Override
    public <V extends Attribute> V getAttribute(Class<V> type) {
        return attributes.getAttribute(type);
    }

    @Override
    public AttributeSet getAttributes() {
        return attributes;
    }

    @Override
    public void freeze() {
        for (Attribute att : attributes) {
            if (att.getClass() == Widget.class) {
                ((Widget)att).getValue().setProperty(this);
            }
            att.freeze();
        }
    }

    @Override
    public ContentType<T> getContentType() {
        return contentType;
    }
    
    
    public Tags getTags() {
        Tags out = getAttribute(Tags.class);
        if (out == null) {
            out = new Tags();
            attributes.setAttributes(out);
        }
        return out;
    }
    
}
