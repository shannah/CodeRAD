/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;


import com.codename1.rad.models.Property.Description;
import com.codename1.rad.models.Property.Label;
import com.codename1.rad.models.Property.Widget;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author shannah
 */
public class EntityType implements Iterable<Property> {
    private EntityType superType;
    //private Map<String,Property> properties = new HashMap<>();
    private final Set<Property> propertiesSet = new LinkedHashSet<>();
    private static Map<Class<? extends EntityType>, EntityType> types = new HashMap<>();
    
    public static EntityType getEntityType(Class<? extends EntityType> type) {
        EntityType t = types.get(type);
        if (t == null) {
            try {
                t = (EntityType)type.newInstance();
                types.put(type, t);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return t;
    }
    
    public void addProperty(Property property) {
        propertiesSet.add(property);
    }
    
    public void addAllProperties(Property... properties) {
        for (Property p : properties) {
            addProperty(p);
        }
    }
    
    public boolean removeProperty(Property property) {
        if (propertiesSet.contains(property)) {
            propertiesSet.remove(property);
            return true;
        }
        return false;
    }

    @Override
    public Iterator<Property> iterator() {
        return propertiesSet.iterator();
    }

    public boolean isDynamic() {
        return false;
    }
    
    
    public boolean contains(Property property) {
        return propertiesSet.contains(property);
    }
    
    
    public StringProperty string(Attribute... atts) {
        StringProperty out = new StringProperty();
        out.setAttributes(atts);
        propertiesSet.add(out);
        return out;
    }
    
    public DateProperty date(Attribute... atts) {
        DateProperty out = new DateProperty();
        out.setAttributes(atts);
        propertiesSet.add(out);
        return out;
    }
    
    public IntProperty Integer(Attribute... atts) {
        IntProperty p = new IntProperty();
        p.setAttributes(atts);
        propertiesSet.add(p);
        
        return p;
     
    }
    
    public DoubleProperty Double(Attribute... atts) {
        DoubleProperty d = new DoubleProperty();
        d.setAttributes(atts);
        propertiesSet.add(d);
        return d;
    }
    
    public BooleanProperty Boolean(Attribute... atts) {
        BooleanProperty b = new BooleanProperty();
        b.setAttributes(atts);
        propertiesSet.add(b);
        return b;
    }
    
    public <T extends EntityList> ListProperty<T> compose(Class<T> type, Attribute... atts) {
        ListProperty p = new ListProperty(type);
        p.setAttributes(atts);
        propertiesSet.add(p);
        return p;
    }
    
    public <T extends EntityList> ListProperty<T> list(Class<T> type, Attribute... atts) {
        return compose(type, atts);
    }
    
    
    public static Label label(String label) {
        return new Label(label);
    }
    
    public static Description description(String description) {
        return new Description(description);
    }
    
    public static Widget widget(Attribute... atts) {
        WidgetDescriptor desc = new WidgetDescriptor();
        desc.setAttributes(atts);
        return new Widget(desc);
    }
    
    public static Tags tags(Tag... atts) {
        return new Tags(atts);
    }
    
    public Property findProperty(Tag... tags) {
        for (Tag tag : tags) {
            for (Property prop : propertiesSet) {
                if (prop.getTags().contains(tag)) {
                    return prop;
                }
            }
        }
        return null;
    }
    
    public Object getPropertyValue(Property prop, Entity entity, ContentType outputType) {
        return ContentType.convert(prop.getContentType(), prop.getValue(entity), outputType);
    }
    
    public void setPropertyValue(Property prop, Entity entity, ContentType inputType, Object data) {
        prop.setValue(entity, ContentType.convert(inputType, data, prop.getContentType()));
    }
    
    
    
    public String getText(Property prop, Entity entity) {
        return (String)getPropertyValue(prop, entity, ContentType.Text);
    }
    
    public void setText(Property prop, Entity entity, String text) {
        setPropertyValue(prop, entity, ContentType.Text, text);
    }
    
    public Integer getInt(Property prop, Entity entity) {
        return (Integer)getPropertyValue(prop, entity, ContentType.IntegerType);
    }
    
    public void setInt(Property prop, Entity entity, int value) {
        setPropertyValue(prop, entity, ContentType.IntegerType, value);
    }
    
    public Double getDouble(Property prop, Entity entity) {
        return (Double)getPropertyValue(prop, entity, ContentType.DoubleType);
    }
    
    public void setDouble(Property prop, Entity entity, double val) {
        setPropertyValue(prop, entity, ContentType.DoubleType, val);
    }
    
    public Float getFloat(Property prop, Entity entity) {
        return (Float)getPropertyValue(prop, entity, ContentType.FloatType);
    }
    
    public void setFloat(Property prop, Entity entity, float val) {
        setPropertyValue(prop, entity, ContentType.FloatType, val);
    }
    
    public Boolean getBoolean(Property prop, Entity entity) {
        return (Boolean)getPropertyValue(prop, entity, ContentType.BooleanType);
    }
    
    public void setBoolean(Property prop, Entity entity, boolean val) {
        setPropertyValue(prop, entity, ContentType.BooleanType, val);
    }
    
    public Object getPropertyValue(Tag tag, Entity entity, ContentType outputType) {
        Property prop = findProperty(tag);
        if (prop == null) {
            return null;
        }
        return getPropertyValue(prop, entity, outputType);
    }
    
    public boolean setPropertyValue(Tag tag, Entity entity, ContentType inputType, Object val) {
        Property prop = findProperty(tag);
        if (prop == null) {
            return false;
        }
        setPropertyValue(prop, entity, inputType, val);
        return true;
        
    }
    
    public Object getPropertyValue(Entity entity, ContentType outputType, Tag... tags) {
        for (Tag tag : tags) {
            Property prop = findProperty(tag);
            if (prop == null) {
                continue;
            }
            return getPropertyValue(prop, entity, outputType);
        }
        return null;
    }
    
    public boolean setPropertyValue(Entity entity, ContentType inputType, Object val, Tag... tags) {
        for (Tag tag : tags) {
            Property prop = findProperty(tag);
            if (prop == null) {
                continue;
            }
            setPropertyValue(prop, entity, inputType, val);
            return true;
        }
        return false;
    }
    
     public Object getPropertyValue(Tag tag, Entity entity, ContentType outputType, Object defaultVal) {
        Property prop = findProperty(tag);
        if (prop == null) {
            return defaultVal;
        }
        return getPropertyValue(prop, entity, outputType);
    }
    
    public String getText(Tag tag, Entity entity) {
        return (String)getPropertyValue(tag, entity, ContentType.Text);
    }
    
    public boolean setText(Tag tag, Entity entity, String text) {
        return setPropertyValue(tag, entity, ContentType.Text, text);
    }
    
    public String getText(Entity entity, Tag... tags) {
        return (String)getPropertyValue(entity, ContentType.Text, tags);
    }
    
    public boolean setText(Entity entity, String text, Tag... tags) {
        return setPropertyValue(entity, ContentType.Text, text, tags);
    }
    
    public Integer getInt(Tag prop, Entity entity) {
        return (Integer)getPropertyValue(prop, entity, ContentType.IntegerType);
    }
    
    public boolean setInt(Tag prop, Entity entity, int val) {
        return setPropertyValue(prop, entity, ContentType.IntegerType, val);
    }
    
    public Integer getInt(Entity entity, Tag... tags) {
        return (Integer)getPropertyValue(entity, ContentType.IntegerType, tags);
    }
    
    public boolean setInt(Entity entity, int val, Tag... tags) {
        return setPropertyValue(entity, ContentType.IntegerType, val, tags);
    }
    
    public Double getDouble(Entity entity, Tag... tags) {
        return (Double)getPropertyValue(entity, ContentType.DoubleType, tags);
    }
    
    public boolean setDouble(Entity entity, double val, Tag... tags) {
        return setPropertyValue(entity, ContentType.DoubleType, val, tags);
    }
    
    public Double getDouble(Tag prop, Entity entity) {
        return (Double)getPropertyValue(prop, entity, ContentType.DoubleType);
    }
    
    public boolean setDouble(Tag prop, Entity entity, double val) {
        return setPropertyValue(prop, entity, ContentType.DoubleType, val);
    }
    
    public Float getFloat(Tag prop, Entity entity) {
        return (Float)getPropertyValue(prop, entity, ContentType.FloatType);
    }
    
    public boolean setFloat(Tag prop, Entity entity, float val) {
        return setPropertyValue(prop, entity, ContentType.FloatType, val);
    }
    
    public Float getFloat(Entity entity, Tag... tags) {
        return (Float)getPropertyValue(entity, ContentType.FloatType, tags);
    }
    
    public boolean setFloat(Entity entity, float val, Tag... tags) {
        return setPropertyValue(entity, ContentType.FloatType, val, tags);
    }
    
    public Boolean getBoolean(Tag prop, Entity entity) {
        return (Boolean)getPropertyValue(prop, entity, ContentType.BooleanType);
    }
    
    public boolean setBoolean(Tag prop, Entity entity, boolean val) {
        return setPropertyValue(prop, entity, ContentType.BooleanType, val);
    }
    
    public Boolean getBoolean(Entity entity, Tag... tags) {
        return (Boolean)getPropertyValue(entity, ContentType.BooleanType, tags);
    }
    
    
    public boolean setBoolean(Entity entity, boolean val, Tag... tags) {
        return setPropertyValue(entity, ContentType.BooleanType, val, tags);
    }
    
    public String getText(Tag tag, Entity entity, String defaultVal) {
        return (String)getPropertyValue(tag, entity, ContentType.Text, defaultVal);
    }
    
     public Integer getInt(Tag prop, Entity entity, Integer defaultVal) {
        return (Integer)getPropertyValue(prop, entity, ContentType.IntegerType, defaultVal);
    }
    
    public Double getDouble(Tag prop, Entity entity, Double defaultVal) {
        return (Double)getPropertyValue(prop, entity, ContentType.DoubleType, defaultVal);
    }
    
    public Float getFloat(Tag prop, Entity entity, Float defaultVal) {
        return (Float)getPropertyValue(prop, entity, ContentType.FloatType, defaultVal);
    }
    
    public Boolean getBoolean(Tag prop, Entity entity, Boolean defaultVal) {
        return (Boolean)getPropertyValue(prop, entity, ContentType.BooleanType, defaultVal);
    }
    

    private boolean frozen;
    void freeze() {
        if (frozen) {
            return;
        }
        frozen = true;
        for (Property p : propertiesSet) {
            p.freeze();
        }
    }
}
