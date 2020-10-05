/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

import com.codename1.rad.ui.UI;
import com.codename1.ui.CN;
import com.codename1.ui.Display;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.Image;
import com.codename1.ui.URLImage;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.util.EventDispatcher;
import com.codename1.util.Base64;
import com.codename1.util.StringUtil;
import java.util.ArrayList;
import java.util.Date;
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
    private Map<Property,Set<ActionListener>> vetoablePropertyChangeListenersMap;
    private EventDispatcher propertyChangeListeners;
    private EventDispatcher vetoablePropertyChangeListeners;
    
    
    public Entity() {
        if (EntityType.isRegisteredEntityType(getClass())) {
            setEntityType(EntityType.getEntityType(getClass()));
        }
    }
    
    /**
     * Adds a listener to be notified of changes to the given property.
     * @param property The property to listen on.
     * @param l The listener.
     */
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
    
    public boolean hasPropertyChangeListeners(Property prop) {
        if (propertyChangeListenersMap != null) {
            Set<ActionListener> listeners = propertyChangeListenersMap.get(prop);
            if (listeners != null && !listeners.isEmpty()) {
                return true;
            }
        }
        if (propertyChangeListeners != null && propertyChangeListeners.hasListeners()) {
            return true;
        }
        return false;
        
    }
    
    public boolean hasVetoablePropertyChangeListeners(Property prop) {
        if (vetoablePropertyChangeListenersMap != null) {
            Set<ActionListener> listeners = vetoablePropertyChangeListenersMap.get(prop);
            if (listeners != null && !listeners.isEmpty()) {
                return true;
            }
        }
        if (vetoablePropertyChangeListeners != null && vetoablePropertyChangeListeners.hasListeners()) {
            return true;
        }
        return false;
        
    }
    
    /**
     * Removes a property change listener.
     * @param property The property to listen to.
     * @param l The listener.
     */
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
    
    /**
     * Adds a listener to listen to all property changes on all properties.
     * @param l The listener.
     */
    public void addPropertyChangeListener(ActionListener<PropertyChangeEvent> l) {
        if (propertyChangeListeners == null) {
            propertyChangeListeners = new EventDispatcher();
        }
        propertyChangeListeners.addListener(l);
    }
    
    /**
     * Removes property change listener.
     * @param l The listener.
     */
    public void removePropertyChangeListener(ActionListener<PropertyChangeEvent> l) {
        if (propertyChangeListeners == null) {
            return;
        }
        propertyChangeListeners.removeListener(l);
    }
    
    /**
     * Fires a property change event to registered listeners.
     * @param pce The event.
     */
    protected void firePropertyChangeEvent(PropertyChangeEvent pce) {
        if (!CN.isEdt()) {
            if (Display.isInitialized()) {
                CN.callSerially(()->firePropertyChangeEvent(pce));
                
                return;
            }
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
    
    /**
     * Adds a listener to be notified of changes to the given property.
     * @param property The property to listen on.
     * @param l The listener.
     */
    public void addVetoablePropertyChangeListener(Property property, ActionListener<VetoablePropertyChangeEvent>  l)  {
        if (vetoablePropertyChangeListenersMap == null) {
            vetoablePropertyChangeListenersMap = new HashMap<>();
        }
        Set<ActionListener> propertyListenerSet = vetoablePropertyChangeListenersMap.get(property);
        if (propertyListenerSet == null) {
            propertyListenerSet = new LinkedHashSet<>();
            vetoablePropertyChangeListenersMap.put(property, propertyListenerSet);
        }
        propertyListenerSet.add(l);
    }
    
    /**
     * Removes a property change listener.
     * @param property The property to listen to.
     * @param l The listener.
     */
    public void removeVetoablePropertyChangeListener(Property property, ActionListener<VetoablePropertyChangeEvent> l) {
        if (vetoablePropertyChangeListenersMap == null) {
            return;
        }
        Set<ActionListener> propertyListenerSet = vetoablePropertyChangeListenersMap.get(property);
        if (propertyListenerSet == null) {
            return;
        }
        propertyListenerSet.remove(l);
    }
    
    /**
     * Adds a listener to listen to all property changes on all properties.
     * @param l The listener.
     */
    public void addVetoablePropertyChangeListener(ActionListener<VetoablePropertyChangeEvent> l) {
        if (vetoablePropertyChangeListeners == null) {
            vetoablePropertyChangeListeners = new EventDispatcher();
        }
        vetoablePropertyChangeListeners.addListener(l);
    }
    
    /**
     * Removes property change listener.
     * @param l The listener.
     */
    public void removeVetoablePropertyChangeListener(ActionListener<VetoablePropertyChangeEvent> l) {
        if (vetoablePropertyChangeListeners == null) {
            return;
        }
        vetoablePropertyChangeListeners.removeListener(l);
    }
    
    /**
     * Fires a property change event to registered listeners.
     * @param pce The event.
     */
    protected void fireVetoablePropertyChangeEvent(VetoablePropertyChangeEvent pce) {
        if (!CN.isEdt()) {
            if (Display.isInitialized()) {
                CN.callSerially(()->fireVetoablePropertyChangeEvent(pce));
                
                return;
            }
        }
        if (vetoablePropertyChangeListenersMap != null) {
            Set<ActionListener> listeners = vetoablePropertyChangeListenersMap.get(pce.getProperty());
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
        if (vetoablePropertyChangeListeners != null) {
            vetoablePropertyChangeListeners.fireActionEvent(pce);
        }
    }
   
   
    /**
     * Initializes property map.
     */
    void initProperties() {
        if (properties == null) {
            properties = new HashMap<>();
        }
    }
    
    
    /**
     * Creates an image for a given property to storage.  This behaves similarly to the {@link URLImage#createToStorage(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String) }
     * except the URL is retrieved from the entity's property, instead of as a parameter.
     * 
     * @param tag The tag used to look up the property to store.
     * @param placeholder The placeholder image.
     * @param adapter Adapter for manipulating the image.
     * @return The Image, usually a URLImage.
     * @see URLImage#createToStorage(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String) 
     */
    public Image createImageToStorage(Tag tag, EncodedImage placeholder, URLImage.ImageAdapter adapter) {
        return createImageToStorage(tag, placeholder, null, adapter);
    }
    
    /**
     * Creates an image to storage on the given property. This behaves similarly to the {@link URLImage#createToStorage(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String) }
     * except the URL is retrieved from the entity's property, instead of as a parameter.
     * @param prop The property to store the image in.
     * @param placeholder The placeholder image.
     * @param adapter
     * @return The Image
     */
    public Image createImageToStorage(Property prop, EncodedImage placeholder, URLImage.ImageAdapter adapter) {
        return createImageToStorage(prop, placeholder, null, adapter);
    }
    
    
    /**
     * Creates an image to storage on the given property. This behaves similarly to the {@link URLImage#createToStorage(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String) }
     * except the URL is retrieved from the entity's property, instead of as a parameter.
     * @param tag The tag used to look up the property to store.
     * @param placeholder The placeholder image.
     * @return The Image
     */
    public Image createImageToStorage(Tag tag, EncodedImage placeholder) {
        return createImageToStorage(tag, placeholder, null, null);
    }
    
    /**
     * Creates an image to storage on the given property. This behaves similarly to the {@link URLImage#createToStorage(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String) }
     * except the URL is retrieved from the entity's property, instead of as a parameter.
     * @param prop The property to store the image in.
     * @param placeholder The placeholder image.
     * @return The Image
     */
    public Image createImageToStorage(Property prop, EncodedImage placeholder) {
        return createImageToStorage(prop, placeholder, null, null);
    }
    
    /**
     * Creates an image for a given property to storage.  This behaves similarly to the {@link URLImage#createToStorage(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String) }
     * except the URL is retrieved from the entity's property, instead of as a parameter.
     * 
     * @param tag The tag used to look up the property to store.
     * @param placeholder The placeholder image.
     * @param storageFile The storage key to store the image in.
     * @return The Image, usually a URLImage.
     * @see URLImage#createToStorage(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String) 
     */
    public Image createImageToStorage(Tag tag, EncodedImage placeholder, String storageFile) {
        return createImageToStorage(tag, placeholder, storageFile, null);
    }
    
    /**
     * Creates an image to storage on the given property.This behaves similarly to the {@link URLImage#createToStorage(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String)}
 except the URL is retrieved from the entity's property, instead of as a parameter.
     * @param prop The property to store the image in.
     * @param placeholder The placeholder image.
     * @param storageFile The storage key where the image should be cached.
     * @return The Image
     */
    public Image createImageToStorage(Property prop, EncodedImage placeholder, String storageFile) {
        return createImageToStorage(prop, placeholder, storageFile, null);
    }
    
    /**
     * Creates an image for a given property to storage.This behaves similarly to the {@link URLImage#createToStorage(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String)}
 except the URL is retrieved from the entity's property, instead of as a parameter.
     * 
     * @param tag The tag used to look up the property to store.
     * @param placeholder The placeholder image.
     * @param storageFile The storage key to store the image in.
     * @param adapter Adapter to manipulate the image when loading.
     * @return The Image, usually a URLImage.
     * @see URLImage#createToStorage(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String) 
     */
    public Image createImageToStorage(Tag tag, EncodedImage placeholder, String storageFile, URLImage.ImageAdapter adapter) {
        Property prop = getEntityType().findProperty(tag);
        if (prop == null) {
            return placeholder;
        }
        return createImageToStorage(prop, placeholder, storageFile, adapter);
    }
    
    /**
     * Creates an image to storage on the given property.This behaves similarly to the {@link URLImage#createToStorage(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String)}
 except the URL is retrieved from the entity's property, instead of as a parameter.
     * @param prop The property to store the image in.
     * @param placeholder The placeholder image.
     * @param storageFile The storage key where the image should be cached.
     * @param adapter Adapter to manipulate the image when loading.
     * @return The Image
     */
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
    
    /**
     * Creates an image for a given property to file system. This behaves similarly to the {@link URLImage#createToFileSystem(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String, com.codename1.ui.URLImage.ImageAdapter) }
 except the URL is retrieved from the entity's property, instead of as a parameter.
     * 
     * @param tag The tag used to look up the property to store.
     * @param placeholder The placeholder image.
     * @param adapter Adapter to manipulate the image when loading.
     * @return The Image, usually a URLImage.
     */
    public Image createImageToFile(Tag tag, EncodedImage placeholder, URLImage.ImageAdapter adapter) {
        return createImageToFile(tag, placeholder, null, adapter);
    }
    
    /**
     * Creates an image for a given property to file system. This behaves similarly to the {@link URLImage#createToFileSystem(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String, com.codename1.ui.URLImage.ImageAdapter) }
 except the URL is retrieved from the entity's property, instead of as a parameter.
     * 
     * @param prop The property where the image url should be retrieved from.
     * @param placeholder The placeholder image.
     * @param adapter Adapter to manipulate the image when loading.
     * @return The Image, usually a URLImage.
     */
    public Image createImageToFile(Property prop, EncodedImage placeholder, URLImage.ImageAdapter adapter) {
        return createImageToFile(prop, placeholder, null, adapter);
    }
    
    /**
     * Creates an image for a given property to file system. This behaves similarly to the {@link URLImage#createToFileSystem(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String, com.codename1.ui.URLImage.ImageAdapter) }
 except the URL is retrieved from the entity's property, instead of as a parameter.
     * 
     * @param tag The tag used to look up the property to store.
     * @param placeholder The placeholder image.
     * @return The Image, usually a URLImage.
     */
    public Image createImageToFile(Tag tag, EncodedImage placeholder) {
        return createImageToFile(tag, placeholder, (String)null);
    }
    
    /**
     * Creates an image for a given property to file system. This behaves similarly to the {@link URLImage#createToFileSystem(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String, com.codename1.ui.URLImage.ImageAdapter) }
 except the URL is retrieved from the entity's property, instead of as a parameter.
     * 
     * @param prop The property where the image url should be retrieved from.
     * @param placeholder The placeholder image.
     * @return The Image, usually a URLImage.
     */
    public Image createImageToFile(Property prop, EncodedImage placeholder) {
        return createImageToFile(prop, placeholder, (String)null);
    }
    
    /**
     * Creates an image for a given property to file system.This behaves similarly to the {@link URLImage#createToFileSystem(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String, com.codename1.ui.URLImage.ImageAdapter)}
 except the URL is retrieved from the entity's property, instead of as a parameter.
     * 
     * @param tag The tag used to look up the property to store.
     * @param placeholder The placeholder image.
     * @param file The path where to cache the image in the file system.
     * @return The Image, usually a URLImage.
     */
    public Image createImageToFile(Tag tag, EncodedImage placeholder, String file) {
        
    
        return createImageToFile(tag, placeholder, file, null);
    }
    
     /**
     * Creates an image for a given property to file system.This behaves similarly to the {@link URLImage#createToFileSystem(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String, com.codename1.ui.URLImage.ImageAdapter)}
 except the URL is retrieved from the entity's property, instead of as a parameter.
     * 
     * @param prop The property where the image URL should be retrieved from.
     * @param placeholder The placeholder image.
     * @param file The path where to cache the image in the file system.
     * @return The Image, usually a URLImage.
     */
    public Image createImageToFile(Property prop, EncodedImage placeholder, String file) {
        return createImageToFile(prop, placeholder, file, null);
    }
    
    /**
     * Creates an image for a given property to file system.This behaves similarly to the {@link URLImage#createToFileSystem(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String, com.codename1.ui.URLImage.ImageAdapter)}
 except the URL is retrieved from the entity's property, instead of as a parameter.
     * 
     * @param tag The tag used to look up the property to store.
     * @param placeholder The placeholder image.
     * @param file The path where to cache the image in the file system.
     * @param adapter Adapter to manipulate the image on load.
     * @return The Image, usually a URLImage.
     */
    public Image createImageToFile(Tag tag, EncodedImage placeholder, String file, URLImage.ImageAdapter adapter) {
        Property prop = getEntityType().findProperty(tag);
        if (prop == null) {
            return placeholder;
        }
        return createImageToFile(prop, placeholder, file, adapter);
    }
    
    /**
     * Creates an image for a given property to file system.This behaves similarly to the {@link URLImage#createToFileSystem(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String, com.codename1.ui.URLImage.ImageAdapter)}
 except the URL is retrieved from the entity's property, instead of as a parameter.
     * 
     * @param prop The property where the image URL should be retrieved from.
     * @param placeholder The placeholder image.
     * @param file The path where to cache the image in the file system.
     * @param adapter Adapter to manipulate the image on load.
     * @return The Image, usually a URLImage.
     */
    public Image createImageToFile(Property prop, EncodedImage placeholder, String file, URLImage.ImageAdapter adapter) {
        String str = getText(prop);
        if (str == null || str.length() == 0) {
            return placeholder;
        }
        
        if (str.indexOf(" ") > 0) {
            str = str.substring(0, str.indexOf(" "));
        }
        String encodedStr = Base64.encodeNoNewline(str.getBytes());
        
        if (file != null && file.indexOf("%") > 0) {
            file = StringUtil.replaceAll(file, "%", encodedStr);
        }
        
        if (file == null) {
            
            file = UI.getTempFile(encodedStr + "@"+placeholder.getWidth()+"x"+placeholder.getHeight()).getAbsolutePath(); 
        } else if (file.indexOf("@") == 0) {
            file = UI.getTempFile(encodedStr + file).getAbsolutePath();
        } else {
            file = UI.getTempFile(file).getAbsolutePath();
        }
        return URLImage.createToFileSystem(placeholder, file, str, adapter);
        
    }
    
    /**
     * Sets the entity type for this entity.  The general pattern for entity definitions is to call
     * this either in the constructor, or in the instance INIT section, AFTER defining the Entity Type.
     * 
     * E.g.
     * 
     * [source,java]
     * ----
     * public class Person extends Entity {
     *     public static final EntityType TYPE = new EntityType() {{
     *         string(Thing.name);
     *         string(Thing.description);
     *     }};
     *     {
     *         setEntityType(TYPE);
     *     }
     * }
     * ----
     * 
     * @param entityType 
     */
    public void setEntityType(EntityType entityType) {
        entityType.freeze();
        entityType.initContentType(this);
        this.entityType = entityType;
        
        
    }
    
    /**
     * Gets the entity type of this entity.
     * 
     * @return The entity type.
     */
    public EntityType getEntityType() {
        if (entityType == null) {
            if (EntityType.isRegisteredEntityType(getClass())) {
                entityType = EntityType.getEntityType(getClass());
            } else {
                entityType = new DynamicEntityType();
            }
        }
        return entityType;
    }
    
    /**
     * Gets a property as an EntityList. 
     * @param tag The tag used to look up the property.  More than one tag can be supplied.  The first matching
     * tag (i.e. for which the entity has a corresponding property) will be used.
     * 
     * @return The property value as an EntityList or null if no property was found matching any of the tags.
     */
    public EntityList getEntityList(Tag... tag) {
        Property prop = getEntityType().findProperty(tag);
        if (prop == null) {
            return null;
        }
        return getEntityList(prop);
    }
    
    /**
     * Gets property as an EntityList
     * @param prop The property to retrieve.
     * @return The property as an EntityList, or null if the property value is not an EntityList.
     */
    public EntityList getEntityList(Property prop) {
        Object o = get(prop);
        if (o instanceof EntityList) {
            return (EntityList)o;
        }
        
        return null;
    }
    
    
    /**
     * Gets the a property as an Entity.
     * @param tag Tag list to search for matching properties.
     * @return The property value as an entity, or null if no such property found, or if matching property is not an entity.
     */
    public Entity getEntity(Tag... tag) {
        Property prop = getEntityType().findProperty(tag);
        if (prop == null) {
            return null;
        }
        return getEntity(prop);
    }
    
    /**
     * Gets the a property value as an Entity.
     * @param prop The property whose value we wish to retrieve.
     * @return The property value as an entity, or null if the property value is not an entity.
     */
    public Entity getEntity(Property prop) {
        Object o = get(prop);
        if (o instanceof Entity) {
            return (Entity)o;
        }
        
        return null;
    }
   
    /**
     * Gets the aggregate that this entity is a part of, or creates a new Aggregate with this entity as a its root.
     * @return the aggregate
     */
    public Aggregate getAggregate() {
        if (aggregate == null) {
            // Avoid NPEs
            aggregate = new Aggregate(this);
        }
        return aggregate;
    }
    
    /**
     * Sets the aggregate that this entity is a part of. This is package private.  The proper 
     * way to add an Entity to an aggregate is to use {@link Aggregate#add(com.codename1.rad.models.Entity) }.
     * 
     * @param aggregate The aggregate set as this entity's aggregate.
     */
    void setAggregate(Aggregate aggregate) {
        this.aggregate = aggregate;
    }

    private transient Aggregate aggregate;
    
    /**
     * {@inheritDoc }
     */
    @Override
    protected synchronized void clearChanged() {
        super.clearChanged(); 
    }
    
    /**
     * Gets a property value.
     * @param key The lookup key for the property.  This can be either a {@link Property}, a {@link Tag}, a {@link Tags},
     * or anything else.
     * 
     * If {@link Tags} is supplied, then the first tag resolving to a property is used.
     * 
     * @return The property value.
     */
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
        if (key instanceof Tags) {
            Tags tags = (Tags)key;
            for (Tag tag : tags) {
                Property prop = getEntityType().findProperty((Tag)key);
                if (prop != null) {
                    return get(prop);
                }
            }
            return null;
        }
        return properties.get(key);
    }
    
    /**
     * Sets a property value.
     * 
     * @param key The lookup key for the property.  This can be a {@link Property}, {@link Tag}, or {@link Tags}.
     * 
     * If {@link Tags} is supplied, then the first tag resolving to a property is used.
     * 
     * @param value The value to set.
     */
    public void set(Object key, Object value) {
        if (properties == null) {
            if (entityType == null) {
                entityType = new DynamicEntityType();
            }
            initProperties();
        }
        if (key instanceof Property) {
            ((Property)key).setValue(this, value);
            return;
        } else if (key instanceof Tag) {
            Property prop = findProperty((Tag)key);
            set(prop, value);
            return;
        } else if (key instanceof Tags) {
            for (Tag tag : (Tags)key) {
                Property prop = findProperty((Tag)key);
                if (prop != null) {
                    set(prop, value);
                    return;
                }
            }
            return;
        }
        Object existing = properties.get(key);
        if (!Objects.equals(existing, value)) {
            properties.put(key, value);
            
            setChanged();
        }
    }
    
    
    /**
     * Converts the entity to a Map.  This will return a Map whose keys correspond to the
     * keys supplied, and values are the correponding value in the entity.
     * @param keys The keys to use for lookup.  Can be {@link Tag}, {@link Property}, or {@link Tags}.
     * @return A Map with the specified properties.
     */
    public Map toMap(Object... keys) {
        Map out = new HashMap();
        for (Object key : keys) {
            if (key.getClass() == Tag.class) {
                out.put(key, get((Tag)key));
            } else if (key.getClass() == Property.class){
                out.put(key, get((Property)key));
            } else if (key.getClass() == Tags.class) {
                out.put(key, get((Tags)key));
            }
        }
        return out;
    }
    
    /**
     * Gets the value of the specified property.
     * @param <T>
     * @param prop The property to retrieve.
     * @return 
     */
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
    
    /**
     * Finds the property corresponding to the given tags.
     * @param tags The tags to search for.  The first tag resolving to a property is used.
     * @return The matching property, or null if none found.
     */
    public Property findProperty(Tag... tags) {
        return getEntityType().findProperty(tags);
    }
    
    /**
     * Gets a property as text.
     * @param prop The property
     * @return The property value as text.
     */
    public String getText(Property prop) {
        return getEntityType().getText(prop, this);
    }
    
    /**
     * Gets a property value as text.
     * @param tags The tags to search for.  The first tag resolving to a property is used.
     * @return The property value as text.
     */
    public String getText(Tag... tags) {
        return getEntityType().getText(this, tags);
    }
    
    /**
     * Gets property value as Boolean.  Consider using {@link #isFalsey(com.codename1.rad.models.Property) } instead
     * of this method if it is possible that the property is null, or stores a type other than Boolean.  
     * @param prop The property.
     * @return The property value as boolean.
     * 
     */
    public Boolean getBoolean(Property prop) {
        return getEntityType().getBoolean(prop, this);
    }
    
    /**
     * Gets property value as Boolean. Consider using {@link #isFalsey(com.codename1.rad.models.Tag)  } instead
     * of this method if it is possible that the property is null, or stores a type other than Boolean. 
     * @param tags Tags to search for.  First tag resolving to a property is used.
     * @return The property value as a Boolean.
     */
    public Boolean getBoolean(Tag... tags) {
        return getEntityType().getBoolean(this, tags);
    }
    
    /**
     * Gets a property value as a Double.
     * @param prop The property.
     * @return The property value as a Double
     */
    public Double getDouble(Property prop) {
        return getEntityType().getDouble(prop, this);
    }
    
    /**
     * Gets property value as a Double.
     * @param tags Tags to search for.  First tag resolving to a property is used.
     * @return Property value as Double
     */
    public Double getDouble(Tag... tags) {
        return getEntityType().getDouble(this, tags);
    }
    
    /**
     * Gets property value as Integer
     * @param prop Th property
     * @return The property value as Integer.
     */
    public Integer getInt(Property prop) {
        return getEntityType().getInt(prop, this);
    }
    
    /**
     * Gets property value as an Integer.
     * @param tags Tags to search for.  First tag resolving to a property is used.
     * @return Property value as Integer
     */
    public Integer getInt(Tag... tags) {
        return getEntityType().getInt(this, tags);
    }
    
    /**
     * Gets property value as Long
     * @param prop Th property
     * @return The property value as Long.
     */
    public Long getLong(Property prop) {
        return getEntityType().getLong(prop, this);
    }
    
    /**
     * Gets property value as a Long.
     * @param tags Tags to search for.  First tag resolving to a property is used.
     * @return Property value as Long
     */
    public Long getLong(Tag... tags) {
        return getEntityType().getLong(this, tags);
    }
    
    /**
     * Gets property value as given content type.
     * @param <V> Content Type
     * @param prop The property to get
     * @param contentType The content type.
     * @return The value as the given content type.
     */
    public <V> V get(Property prop, ContentType<V> contentType) {
        return (V)getEntityType().getPropertyValue(prop, this, contentType);
    }
    
    /**
     * Sets property as given content type.
     * @param prop The property to set
     * @param inputType The content type of the input data
     * @param val The value to set.
     */
    public void set(Property prop, ContentType inputType, Object val) {
        getEntityType().setPropertyValue(prop, this, inputType, val);
    }
    
    /**
     * Sets the property value as the given content type.
     * @param tag The tag used to lookup the property.
     * @param inputType The content type of the value that is being set.
     * @param val The value.
     * @return true if it was successfully set.  false if it was not, generally due to 
     * no property being found matching the tag.
     */
    public boolean set(Tag tag, ContentType inputType, Object val) {
        return getEntityType().setPropertyValue(tag, this, inputType, val);
    }
    
    /**
     * Sets the property value as the given content type.
     * @param inputType The content type of the input data.
     * @param val The value to set.
     * @param tags The tags used to lookup the property.  The first tag resolving to a property is used.
     * @return True if the value was set.  False if not. Usually false means that there was no property
     * matching the provided tags.
     */
    public boolean set(ContentType inputType, Object val, Tag... tags) {
        return getEntityType().setPropertyValue(this, inputType, val, tags);
    }
    
    public void setText(Property prop, String text) {
        getEntityType().setText(prop, this, text);
    }
    
    /**
     * Sets the given property as text.
     * @param tag The tag used to lookup the property.
     * @param text The text to set.
     * @return True if it was successful.  False otherwise.  False generally means that there was no
     * matching property.
     */
    public boolean setText(Tag tag, String text) {
        return getEntityType().setText(tag, this, text);
    }
    
    /**
     * Sets property as text
     * @param text Text to set as property value.
     * @param tags Tags used to lookup property.
     * @return True if successful. False if there were no matching properties.
     */
    public boolean setText(String text, Tag... tags) {
        return getEntityType().setText(this, text, tags);
    }
    
    /**
     * Sets property as Double
     * @param prop The property to set
     * @param val The value to set.
     */
    public void setDouble(Property prop, double val) {
        getEntityType().setDouble(prop, this, val);
    }
    
    /**
     * Sets property as double.
     * @param tag Tag used to lookup property.
     * @param val The value to set.
     * @return True if successful.  False if no matching properties were found for tag.
     */
    public boolean setDouble(Tag tag, double val) {
        return getEntityType().setDouble(tag, this, val);
    }
    
    /**
     * Sets property as double.
     * @param val The value to set.
     * @param tags Tags used to lookup property.
     * @return True if successful.  False if no matching properties were found for tags.
     */
    public boolean setDouble(double val, Tag... tags) {
        return getEntityType().setDouble(this, val, tags);
    }
    
    /**
     * Sets property as Long
     * @param prop The property to set
     * @param val The value to set.
     */
    public void setLong(Property prop, long val) {
        getEntityType().setLong(prop, this, val);
    }
    
    /**
     * Sets property as long.
     * @param tag Tag used to lookup property.
     * @param val The value to set.
     * @return True if successful.  False if no matching properties were found for tag.
     */
    public boolean setLong(Tag tag, long val) {
        return getEntityType().setLong(tag, this, val);
    }
    
    /**
     * Sets property as long.
     * @param val The value to set.
     * @param tags Tags used to lookup property.
     * @return True if successful.  False if no matching properties were found for tags.
     */
    public boolean setLong(long val, Tag... tags) {
        return getEntityType().setLong(this, val, tags);
    }
    
    /**
     * Sets property as Float
     * @param prop The property to set
     * @param val The value to set.
     */
    public void setFloat(Property prop, float val) {
        getEntityType().setFloat(prop, this, val);
    }
    
     /**
     * Sets property as float.
     * @param tag Tag used to lookup property.
     * @param val The value to set.
     * @return True if successful.  False if no matching properties were found for tag.
     */
    public boolean setFloat(Tag tag, float val) {
        return getEntityType().setFloat(tag, this, val);
    }
    
    /**
     * Sets property as float.
     * @param val The value to set.
     * @param tags Tags used to lookup property.
     * @return True if successful.  False if no matching properties were found for tags.
     */
    public boolean setFloat(float val, Tag... tags) {
        return getEntityType().setFloat(this, val, tags);
    }
    
    /**
     * Sets property as int
     * @param prop The property to set
     * @param val The value to set.
     */
    public void setInt(Property prop, int val) {
        getEntityType().setInt(prop, this, val);
    }
    
    /**
     * Sets property as int.
     * @param tag Tag used to lookup property.
     * @param val The value to set.
     * @return True if successful.  False if no matching properties were found for tag.
     */
    public boolean setInt(Tag tag, int val) {
        return getEntityType().setInt(tag, this, val);
    }
    
    /**
     * Sets property as int.
     * @param val The value to set.
     * @param tags Tags used to lookup property.
     * @return True if successful.  False if no matching properties were found for tags.
     */
    public boolean setInt(int val, Tag... tags) {
        return getEntityType().setInt(this, val, tags);
    }
    
    /**
     * Sets property as boolean
     * @param prop The property to set
     * @param val The value to set.
     */
    public void setBoolean(Property prop, boolean val) {
        getEntityType().setBoolean(prop, this, val);
    }
    
    /**
     * Sets property as boolean.
     * @param tag Tag used to lookup property.
     * @param val The value to set.
     * @return True if successful.  False if no matching properties were found for tag.
     */
    public boolean setBoolean(Tag tag, boolean val) {
        return getEntityType().setBoolean(tag, this, val);
    }
    
    /**
     * Sets property as boolean.
     * @param val The value to set.
     * @param tags Tags used to lookup property.
     * @return True if successful.  False if no matching properties were found for tags.
     */
    public boolean setBoolean(boolean val, Tag... tags) {
        return getEntityType().setBoolean(this, val, tags);
    }
    
    /**
     * Gets property as date
     * @param prop The property to set
     * @return The property as a date.
     *
     */
    public java.util.Date getDate(Property prop) {
        return getEntityType().getDate(prop, this);
    }

    /**
     * Gets property as date.
     * @param tags Tags used to lookup property.
     * @return The property as a Date.
     */
    public java.util.Date getDate(Tag... tags) {
        return getEntityType().getDate(this, tags);
    }
    
    /**
     * Sets property as Date
     * @param prop The property to set
     * @param date The value to set.
     */
    public void setDate(Property prop, Date date) {
        getEntityType().setDate(prop, this, date);
    }
    
    /**
     * Sets property as Date.
     * @param tag Tag used to lookup property.
     * @param date The value to set.
     * @return True if successful.  False if no matching properties were found for tag.
     */
    public boolean setDate(Tag tag, Date date) {
        return getEntityType().setDate(this, date, tag);
    }
    
     /**
     * Sets property as Date.
     * @param date The value to set.
     * @param tags Tags used to lookup property.
     * @return True if successful.  False if no matching properties were found for tags.
     */
    public boolean setDate(Date date, Tag... tags) {
        return getEntityType().setDate(this, date, tags);
    }
    
    /**
     * Sets property as Entity
     * @param prop The property to set
     * @param e The value to set.
     */
    public void setEntity(Property prop, Entity e) {
        set(prop, e);
        
    }
    
    /**
     * Sets property as Entity.
     * @param tag Tag used to lookup property.
     * @param e The value to set.
     * @return True if successful.  False if no matching properties were found for tag.
     */
    public boolean setEntity(Tag tag, Entity e) {
        return set(tag, e == null ? ContentType.EntityType : e.getEntityType().getContentType(), e);
    }
    
    /**
     * Sets property as Entity.
     * @param e The value to set.
     * @param tags Tags used to lookup property.
     * @return True if successful.  False if no matching properties were found for tags.
     */
    public boolean setEntity(Entity e, Tag... tags) {
        return set(e == null ? ContentType.EntityType : e.getEntityType().getContentType(), e, tags);
    }
    
    /**
     * Checks if property is an Entity.
     * @param prop The property.
     * @return True only if the property value is an Entity.
     */
    public boolean isEntity(Property prop) {
        if (isEmpty(prop)) {
            return false;
        }
        return prop.getContentType().isEntity();
    }
    
    /**
     * Checks if property is an entity.
     * @param tag The tag to lookup property.
     * @return True only if the property value is an entity.
     */
    public boolean isEntity(Tag tag) {
        
        Property prop = getEntityType().findProperty(tag);
        if (prop == null) {
            return false;
        }
        return isEntity(prop);
    }
    
    /**
     * Checks if property is empty.  This includes if property is null, an empty string, or an EntityList that is empty.
     * @param prop The property
     * @return True if property value is empty.
     */
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
    
    /**
     * Checks if property is empty. This includes if property is null, an empty string, or an EntityList that is empty.
     * @param tag The tag used to lookup property.
     * @return True if property is empty.
     */
    public boolean isEmpty(Tag tag) {
        Object val = get(tag);
        return val == null || "".equals(String.valueOf(val));
    }
    
    /**
     * Checks if property value is falsey.  This includes if the entity doesn't contain the property, if the property value is null,
     * or an empty string, or a boolean `false` value, is numeric with a `0` value, or is an empty EntityList.
     * @param prop The property to check.
     * @return True if the property value is "falsey".
     */
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
    
    /**
     * Checks if property value is falsey.  This includes if the entity doesn't contain the property, if the property value is null,
     * or an empty string, or a boolean `false` value, is numeric with a `0` value, or is an empty EntityList.
     * @param tag The tag to lookup the property.
     * @return True if property value is "falsey".
     */
    public boolean isFalsey(Tag tag) {
        Property prop = getEntityType().findProperty(tag);
        if (prop == null) {
            return true;
        }
        return isFalsey(prop);
    }
    
    /**
     * Calls the super setChanged() method (in `Observable`)
     */
    void setChangedInternal() {
        super.setChanged();
    }
    
    /**
     * Marks a property value as "changed".  This will propagate a call to {@link #setChanged() }
     * (marking the whole object as changed), and it will fire a property change event, if the `firePropertyChange` argument
     * is true.
     * @param prop The property to mark as changed.
     * @param firePropertyChange True to fire a property change event to all listeners of this property.
     */
    public void setChanged(Property prop, boolean firePropertyChange) {
        super.setChanged();
        if (firePropertyChange) {
            firePropertyChangeEvent(new PropertyChangeEvent(this, prop, get(prop), get(prop)));
        }
        
    }
    
    /**
     * Adds an item to the given property.  Assumes that the property value is an EntityList.  If property is currently
     * null, it will attempt to create an EntityList for the property, and add a value.
     * @param prop The property to set.
     * @param value The entity to add to the property's entity list.
     */
    public void add(Property prop, Entity value) {
        
        if (prop.getContentType().isEntityList()) {
            EntityList l = getEntityListNonNull(prop);
            l.add(value);
        } else {
            throw new IllegalStateException("Cannot add value to property "+prop+" because the property is not an entity list");
        }
    }
    
    
    
    /**
     * Gets property as an EntityList.  If the property is currently null, then this will attempt to create a 
     * new EntityList at this property, and return that.
     * @param prop The property.
     * @return The property value as an entity list.
     * @throws IllegalStateException If the property type is not an EntityList type.
     */
    public EntityList getEntityListNonNull(Property prop) {
        if (!prop.getContentType().isEntityList()) {
            throw new IllegalStateException("Cannot add value to property "+prop+" because the property is not an entity list");
        }
        EntityList l = getEntityList(prop);
        if (l == null) {
            EntityListProperty eprop = (EntityListProperty)prop;
            
            Class cls = eprop.getRepresentationClass();
            
            try {
                l = (EntityList)EntityType.createEntityForClass(cls);
                
                
            } catch (Throwable t) {
                throw new IllegalStateException("Cannot add value to property "+prop+" because there is no list currently instantiated at that property.  Attempt to create a new list of type "+cls+" failed with message '"+t.getMessage()+"'");
            }
            set(prop, l);
        }
        return l;
    }
    
    /**
     * Gets a property value as an Entity.  If the property value is currently null, then this will attempt to create
     * a new Entity at this property, and return that.
     * @param prop The property to get.
     * @return The property value as an Entity.
     * @throws IllegalStateException If the property type is not an Entity type.
     */
    public Entity getEntityNonNull(Property prop) {
        if (!prop.getContentType().isEntity()) {
            throw new IllegalStateException("Cannot add value to property "+prop+" because the property is not an entity list");
        }
        Entity l = getEntity(prop);
        if (l == null) {
            EntityProperty eprop = (EntityProperty)prop;
            Class cls = eprop.getRepresentationClass();

            try {
                l = (EntityList)cls.newInstance();
            } catch (Throwable t) {
                throw new IllegalStateException("Cannot add value to property "+prop+" because there is no list currently instantiated at that property.  Attempt to create a new list of type "+cls+" failed with "+t.getMessage());
            }
            set(prop, l);
        }
        return l;
    }
}
