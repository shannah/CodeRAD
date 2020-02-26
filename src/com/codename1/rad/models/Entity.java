/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

import com.codename1.ui.CN;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.Image;
import com.codename1.ui.URLImage;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.util.EventDispatcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Observable;
import java.util.Set;

/**
 * The base class for *Model* objects in CodeRAD.  The {@link Entity} class contains all of the plumbing
 * necessary for property binding, change listeners, property lookup, and data conversion.  Subclasses of {@link Entity}
 * should define a static {@link EntityType} object which serves as a sort of meta-class for the entity class.  The {@link EntityType}
 * keeps track of the properties in the entity, and provides property lookup via {@link Tag}s, which is necessary for loose-coupling.
 * 
==== Example Entity Class

The following figure shows the definition of a very simple entity class:

[source,java]
----
public class UserProfile extends Entity {
    public static StringProperty name, description; <1>
    public static final EntityType TYPE = new EntityType(){{ <2>
        name = string(); <3>
        description = string();
    }};
    {
        setEntityType(TYPE); <4>
    }
}
----
<1> We define 2 properties of type {@link com.codename1.rad.models.StringProperty} on the class.  A `StringProperty` is simply a property that contains a {@link java.lang.String}.  These are defined `public static` so that we can access them conveniently from anywhere.
<2> We define an {@link com.codename1.rad.models.EntityType} for the class.  This is also `public static` because it is class-level (all objects of this class should share the same entity type).
<3> We create `name` and `description` properties on this entity type.  Notice that this code runs in the *instance intializer* of the EntityType (the `{{` and `}}` braces are not a typo).  Running this code inside the instance initializer will ensure that the properties are added to the `EntityType`'s property index.
<4> Inside the `UserProfile` instance initializer, we set the entity type to the entity type that we created above.

[NOTE]
====
*Why can't we just use POJOs for our models?*

The {@link com.codename1.rad.models.Entity} class provides a lot of useful plumbing that is necessary for building reusable components that can bind to each other.  This includes property lookup, property binding, change events, and data conversion.
====

==== Adding Tags to Properties

In the above entity class, we haven't "tagged" any of the properties so it can't be used as a view model for any view, unless that view has been specifically designed for this class, which would limit its reusability.  This is simple to remedy, though. Let's tag the `name` property with {@link com.codename1.rad.schemas.Thing#name}, and `description` with {@link com.codename1.rad.schemas.Thing#description}:

[source,java]
----
name = string(tags(Thing.name));
description = string(tags(Thing.description));
----

[TIP]
====
Properties can contain multiple tags.  E.g. If we want the name field to also be treated as the "ID" field, we could do:

[source,java]
----
name = string(tags(Thing.name, Thing.identifier));
----
====

==== Accessing Property Values

We can access a property value using its property directly.  E.g.

[source,java]
----
String name = model.get(UserProfile.name);
----

Notice here we didn't need to cast the return value to "String" because the `Profile.name` property is declared as a string property.  

We can also access the "name" property using the `Thing.name` tag, which is what allows us to use this as a loosely coupled view model:

[source,java]
----
String name = (String)model.get(Thing.name);
----

[WARNING]
====
When using tags to access properties, it is best to use one of the `getXXX(Tag)` variants that explicitly converts the content type.  E.g. {@link com.codename1.rad.models.Entity#getText(com.codename1.rad.models.Tag)}.  This is because there is no guarantee that a given entity is storing its `Thing.name` property as a String.  It could use any type of property.  Using `getText()` or `getBoolean()` will automatically handle data-conversion if possible.

See {@link com.codename1.rad.models.ContentType} for more information about data conversion in properties.
====

Using the convenience wrapper `getText()` and `setText()` we can then set the values on the `name` property in a generic way:

[source,java]
----
model.setText(Thing.name, "Steve");
String name = model.getText(Thing.name); // "Steve"
----

[TIP]
====
Technically, you don't need to provide direct property access to your entity properties at all.  In our above `UserProfile` class we retained explicit references to the `name` and `description` properties, but we could have simply omitted this.  I.e. The following is also a perfectly valid entity type definition:

.An entity type that doesn't retain explicit references to its properties.  The properties can still be accessed via their assigned tags.
[source,java]
----
public class UserProfile extends Entity {
    public static final EntityType TYPE = new EntityType(){{
        string(tags(Thing.name));
        string(tags(Thing.description));
    }};
    {
        setEntityType(TYPE);
    }
}
----
====
 * 
 * 
 * @author shannah
 */
public class Entity extends Observable  {
    Map<Object,Object> properties;
    private EntityType entityType;
    private Map<Property,Set<ActionListener>> propertyChangeListenersMap;
    private EventDispatcher propertyChangeListeners;
    
    
    public void addPropertyChangeListener(Property property, ActionListener<PropertyChangeEvent>  l)  {
        if (propertyChangeListenersMap == null) {
            propertyChangeListenersMap = new HashMap<>();
        }
        Set<ActionListener> propertyListenerSet = propertyChangeListenersMap.get(property);
        if (propertyListenerSet == null) {
            propertyListenerSet = new LinkedHashSet<>();
            propertyChangeListenersMap.put(property, propertyListenerSet);
        }
        propertyListenerSet.add(l);
    }
    
    public void removePropertyChangeListener(Property property, ActionListener<PropertyChangeEvent> l) {
        if (propertyChangeListenersMap == null) {
            return;
        }
        Set<ActionListener> propertyListenerSet = propertyChangeListenersMap.get(property);
        if (propertyListenerSet == null) {
            return;
        }
        propertyListenerSet.remove(l);
    }
    
    public void addPropertyChangeListener(ActionListener<PropertyChangeEvent> l) {
        if (propertyChangeListeners == null) {
            propertyChangeListeners = new EventDispatcher();
        }
        propertyChangeListeners.addListener(l);
    }
    
    public void removePropertyChangeListener(ActionListener<PropertyChangeEvent> l) {
        if (propertyChangeListeners == null) {
            return;
        }
        propertyChangeListeners.removeListener(l);
    }
    
    protected void firePropertyChangeEvent(PropertyChangeEvent pce) {
        if (!CN.isEdt()) {
            CN.callSerially(()->firePropertyChangeEvent(pce));
            return;
        }
        if (propertyChangeListenersMap != null) {
            Set<ActionListener> listeners = propertyChangeListenersMap.get(pce.getProperty());
            if (listeners != null) {
                ArrayList<ActionListener> toSend = new ArrayList<>(listeners);
                for (ActionListener l : toSend) {
                    l.actionPerformed(pce);
                    if (pce.isConsumed()) {
                        return;
                    }
                }
            }
        }
        if (propertyChangeListeners != null) {
            propertyChangeListeners.fireActionEvent(pce);
        }
    }
   
    void initProperties() {
        if (properties == null) {
            properties = new HashMap<>();
        }
    }
    
    
    
    public Image createImageToStorage(Tag tag, EncodedImage placeholder, URLImage.ImageAdapter adapter) {
        return createImageToStorage(tag, placeholder, null, adapter);
    }
    
    public Image createImageToStorage(Property prop, EncodedImage placeholder, URLImage.ImageAdapter adapter) {
        return createImageToStorage(prop, placeholder, null, adapter);
    }
    
    public Image createImageToStorage(Tag tag, EncodedImage placeholder) {
        return createImageToStorage(tag, placeholder, null, null);
    }
    
    public Image createImageToStorage(Property prop, EncodedImage placeholder) {
        return createImageToStorage(prop, placeholder, null, null);
    }
    
    public Image createImageToStorage(Tag tag, EncodedImage placeholder, String storageFile) {
        return createImageToStorage(tag, placeholder, storageFile, null);
    }
    
    public Image createImageToStorage(Property prop, EncodedImage placeholder, String storageFile) {
        return createImageToStorage(prop, placeholder, storageFile, null);
    }
    
    public Image createImageToStorage(Tag tag, EncodedImage placeholder, String storageFile, URLImage.ImageAdapter adapter) {
        Property prop = getEntityType().findProperty(tag);
        if (prop == null) {
            return placeholder;
        }
        return createImageToStorage(prop, placeholder, storageFile, adapter);
    }
    
    public Image createImageToStorage(Property prop, EncodedImage placeholder, String storageFile, URLImage.ImageAdapter adapter) {
        String str = getText(prop);
        if (str == null || str.length() == 0) {
            return placeholder;
        }
        
        if (str.indexOf(" ") > 0) {
            str = str.substring(0, str.indexOf(" "));
        }
        if (storageFile == null) {
            storageFile = str + "@"+placeholder.getWidth()+"x"+placeholder.getHeight(); 
        } else if (storageFile.indexOf("@") == 0) {
            storageFile = str + storageFile;
        }
        return URLImage.createToStorage(placeholder, storageFile, str, adapter);
        
        
        
    }
    public Image createImageToFile(Tag tag, EncodedImage placeholder, URLImage.ImageAdapter adapter) {
        return createImageToFile(tag, placeholder, null, adapter);
    }
    
    public Image createImageToFile(Property prop, EncodedImage placeholder, URLImage.ImageAdapter adapter) {
        return createImageToFile(prop, placeholder, null, adapter);
    }
    
    public Image createImageToFile(Tag tag, EncodedImage placeholder) {
        return createImageToFile(tag, placeholder, (String)null);
    }
    
    public Image createImageToFile(Property prop, EncodedImage placeholder) {
        return createImageToFile(prop, placeholder, (String)null);
    }
    
    public Image createImageToFile(Tag tag, EncodedImage placeholder, String file) {
        
    
        return createImageToFile(tag, placeholder, file, null);
    }
    public Image createImageToFile(Property prop, EncodedImage placeholder, String file) {
        return createImageToFile(prop, placeholder, file, null);
    }
    public Image createImageToFile(Tag tag, EncodedImage placeholder, String file, URLImage.ImageAdapter adapter) {
        Property prop = getEntityType().findProperty(tag);
        if (prop == null) {
            return placeholder;
        }
        return createImageToFile(prop, placeholder, file, adapter);
    }
    public Image createImageToFile(Property prop, EncodedImage placeholder, String file, URLImage.ImageAdapter adapter) {
        String str = getText(prop);
        if (str == null || str.length() == 0) {
            return placeholder;
        }
        
        if (str.indexOf(" ") > 0) {
            str = str.substring(0, str.indexOf(" "));
        }
        if (file == null) {
            file = str + "@"+placeholder.getWidth()+"x"+placeholder.getHeight(); 
        } else if (file.indexOf("@") == 0) {
            file = str + file;
        }
        return URLImage.createToFileSystem(placeholder, file, str, adapter);
        
    }
    
    
    public void setEntityType(EntityType entityType) {
        entityType.freeze();
        this.entityType = entityType;
    }
    
    public EntityType getEntityType() {
        if (entityType == null) {
            entityType = new DynamicEntityType();
        }
        return entityType;
    }
    
    public EntityList getEntityList(Tag tag) {
        Property prop = getEntityType().findProperty(tag);
        if (prop == null) {
            return null;
        }
        return getEntityList(prop);
    }
    
    public EntityList getEntityList(Property prop) {
        Object o = get(prop);
        if (o instanceof EntityList) {
            return (EntityList)o;
        }
        
        return null;
    }
    
    
    public Entity getEntity(Tag tag) {
        Property prop = getEntityType().findProperty(tag);
        if (prop == null) {
            return null;
        }
        return getEntity(prop);
    }
    
    public Entity getEntity(Property prop) {
        Object o = get(prop);
        if (o instanceof Entity) {
            return (Entity)o;
        }
        
        return null;
    }
   
    /**
     * @return the aggregate
     */
    public Aggregate getAggregate() {
        if (aggregate == null) {
            // Avoid NPEs
            aggregate = new Aggregate(this);
        }
        return aggregate;
    }
    
    void setAggregate(Aggregate aggregate) {
        this.aggregate = aggregate;
    }

    private transient Aggregate aggregate;
    
    @Override
    protected synchronized void clearChanged() {
        super.clearChanged(); 
    }
    
    public Object get(Object key) {
        if (properties == null) {
            return null;
        }
        if (key instanceof Property) {
            Property prop = (Property)properties.get(key);
            return prop.getValue(this);
        }
        if (key instanceof Tag) {
            Property prop = getEntityType().findProperty((Tag)key);
            if (prop == null) {
                return null;
            }
            
            return get(prop);
        }
        return properties.get(key);
    }
    
    public void set(Object key, Object value) {
        if (properties == null) {
            if (entityType == null) {
                entityType = new DynamicEntityType();
            }
            initProperties();
        }
        if (key instanceof Property) {
            ((Property)key).setValue(this, value);
        }
        if (key instanceof Tag) {
            Property prop = findProperty((Tag)key);
            set(prop, value);
            return;
        }
        Object existing = properties.get(key);
        if (!Objects.equals(existing, value)) {
            properties.put(key, value);
            
            setChanged();
        }
    }
    
    public <T> T get(Property<T> prop) {
        if (entityType == null) {
            entityType = new DynamicEntityType();
        }
        if (!entityType.contains(prop)) {
            throw new IllegalArgumentException("Entity type "+entityType+" does not contain property "+prop);
        }
        if (properties == null) {
            return null;
        }
        
        return prop.getValue(this);
    }
    
    public Property findProperty(Tag... tags) {
        return getEntityType().findProperty(tags);
    }
    
    public String getText(Property prop) {
        return getEntityType().getText(prop, this);
    }
    
    public String getText(Tag... tags) {
        return getEntityType().getText(this, tags);
    }
    
    public Boolean getBoolean(Property prop) {
        return getEntityType().getBoolean(prop, this);
    }
    
    public Boolean getBoolean(Tag... tags) {
        return getEntityType().getBoolean(this, tags);
    }
    
    public <V> V get(Property prop, ContentType<V> contentType) {
        return (V)getEntityType().getPropertyValue(prop, this, contentType);
    }
    
    public void set(Property prop, ContentType inputType, Object val) {
        getEntityType().setPropertyValue(prop, this, inputType, val);
    }
    
    public boolean set(Tag tag, ContentType inputType, Object val) {
        return getEntityType().setPropertyValue(tag, this, inputType, val);
    }
    
    public boolean set(ContentType inputType, Object val, Tag... tags) {
        return getEntityType().setPropertyValue(this, inputType, val, tags);
    }
    
    public void setText(Property prop, String text) {
        getEntityType().setText(prop, this, text);
    }
    
    public boolean setText(Tag tag, String text) {
        return getEntityType().setText(tag, this, text);
    }
    
    public boolean setText(String text, Tag... tags) {
        return getEntityType().setText(this, text, tags);
    }
    
    public void setBoolean(Property prop, boolean val) {
        getEntityType().setBoolean(prop, this, val);
    }
    
    public boolean setBoolean(Tag tag, boolean val) {
        return getEntityType().setBoolean(tag, this, val);
    }
    
    public boolean setBoolean(boolean val, Tag... tags) {
        return getEntityType().setBoolean(this, val, tags);
    }
    
    public boolean isEmpty(Property prop) {
        if (prop == null) {
            return true;
        }
        Object val = get(prop);
        if (val instanceof EntityList) {
            return ((EntityList)val).size() == 0;
        }
        return val == null || "".equals(String.valueOf(val));
    }
    
    public boolean isEmpty(Tag tag) {
        Object val = get(tag);
        return val == null || "".equals(String.valueOf(val));
    }
    
    public boolean isFalsey(Property prop) {
        if (prop == null) {
            return true;
        }
        Object val = get(prop);
        if (val == null) {
            return true;
        }
        if (Number.class.isAssignableFrom(prop.getContentType().getRepresentationClass())) {
            return ((Number)val).intValue() == 0;
        }
        if (prop.getContentType().getRepresentationClass() == Boolean.class) {
            return !((Boolean)val);
        }
        return isEmpty(prop);
        
    }
    
    public boolean isFalsey(Tag tag) {
        Property prop = getEntityType().findProperty(tag);
        if (prop == null) {
            return true;
        }
        return isFalsey(prop);
    }
    
    
    void setChangedInternal() {
        super.setChanged();
    }
    
    
    
}
