package com.codename1.rad.models;

import com.codename1.rad.ui.UI;
import com.codename1.ui.*;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.util.EventDispatcher;
import com.codename1.util.Base64;
import com.codename1.util.StringUtil;

import java.util.*;

public class EntityProxy extends Entity {

    private Entity inner;

    /**
     * Adds a listener to be notified of changes to the given property.
     * @param property The property to listen on.
     * @param l The listener.
     */
    public void addPropertyChangeListener(Property property, ActionListener<PropertyChangeEvent>  l)  {
        inner.addPropertyChangeListener(property, l);
    }

    public boolean hasPropertyChangeListeners(Property prop) {
        return inner.hasPropertyChangeListeners(prop);

    }

    public boolean hasVetoablePropertyChangeListeners(Property prop) {
        return inner.hasVetoablePropertyChangeListeners(prop);

    }

    /**
     * Removes a property change listener.
     * @param property The property to listen to.
     * @param l The listener.
     */
    public void removePropertyChangeListener(Property property, ActionListener<PropertyChangeEvent> l) {
        inner.removePropertyChangeListener(property, l);
    }

    /**
     * Adds a listener to listen to all property changes on all properties.
     * @param l The listener.
     */
    public void addPropertyChangeListener(ActionListener<PropertyChangeEvent> l) {
        inner.addPropertyChangeListener(l);
    }

    /**
     * Removes property change listener.
     * @param l The listener.
     */
    public void removePropertyChangeListener(ActionListener<PropertyChangeEvent> l) {
        inner.removePropertyChangeListener(l);
    }

    /**
     * Fires a property change event to registered listeners.
     * @param pce The event.
     */
    protected void firePropertyChangeEvent(PropertyChangeEvent pce) {
        inner.firePropertyChangeEvent(pce);
    }

    /**
     * Adds a listener to be notified of changes to the given property.
     * @param property The property to listen on.
     * @param l The listener.
     */
    public void addVetoablePropertyChangeListener(Property property, ActionListener<VetoablePropertyChangeEvent>  l)  {
        inner.addVetoablePropertyChangeListener(property, l);
    }

    /**
     * Removes a property change listener.
     * @param property The property to listen to.
     * @param l The listener.
     */
    public void removeVetoablePropertyChangeListener(Property property, ActionListener<VetoablePropertyChangeEvent> l) {
        inner.removeVetoablePropertyChangeListener(property, l);
    }

    /**
     * Adds a listener to listen to all property changes on all properties.
     * @param l The listener.
     */
    public void addVetoablePropertyChangeListener(ActionListener<VetoablePropertyChangeEvent> l) {
        inner.addVetoablePropertyChangeListener(l);
    }

    /**
     * Removes property change listener.
     * @param l The listener.
     */
    public void removeVetoablePropertyChangeListener(ActionListener<VetoablePropertyChangeEvent> l) {
        inner.removeVetoablePropertyChangeListener(l);
    }

    /**
     * Fires a property change event to registered listeners.
     * @param pce The event.
     */
    protected void fireVetoablePropertyChangeEvent(VetoablePropertyChangeEvent pce) {
        inner.fireVetoablePropertyChangeEvent(pce);
    }


    /**
     * Initializes property map.
     */
    void initProperties() {
        inner.initProperties();
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
        return inner.createImageToStorage(tag, placeholder, adapter);
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
        return inner.createImageToStorage(prop, placeholder, adapter);
    }


    /**
     * Creates an image to storage on the given property. This behaves similarly to the {@link URLImage#createToStorage(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String) }
     * except the URL is retrieved from the entity's property, instead of as a parameter.
     * @param tag The tag used to look up the property to store.
     * @param placeholder The placeholder image.
     * @return The Image
     */
    public Image createImageToStorage(Tag tag, EncodedImage placeholder) {
        return inner.createImageToStorage(tag, placeholder);
    }

    /**
     * Creates an image to storage on the given property. This behaves similarly to the {@link URLImage#createToStorage(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String) }
     * except the URL is retrieved from the entity's property, instead of as a parameter.
     * @param prop The property to store the image in.
     * @param placeholder The placeholder image.
     * @return The Image
     */
    public Image createImageToStorage(Property prop, EncodedImage placeholder) {
        return inner.createImageToStorage(prop, placeholder);
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
        return inner.createImageToStorage(tag, placeholder, storageFile);
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
        return inner.createImageToStorage(prop, placeholder, storageFile);
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
        return inner.createImageToStorage(tag, placeholder, storageFile, adapter);
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
        return inner.createImageToStorage(prop, placeholder, storageFile, adapter);



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
        return inner.createImageToStorage(tag, placeholder, adapter);
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
        return inner.createImageToStorage(prop, placeholder, adapter);
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
        return inner.createImageToFile(tag, placeholder);
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
        return inner.createImageToFile(prop, placeholder);
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


        return inner.createImageToFile(tag, placeholder, file);
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
        return inner.createImageToFile(prop, placeholder, file);
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
        return createImageToFile(tag, placeholder, file, adapter);
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
        return inner.createImageToFile(prop, placeholder, file, adapter);

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
        inner.setEntityType(entityType);
    }

    /**
     * Gets the entity type of this entity.
     *
     * @return The entity type.
     */
    public EntityType getEntityType() {
        return inner.getEntityType();
    }

    /**
     * Gets a property as an EntityList.
     * @param tag The tag used to look up the property.  More than one tag can be supplied.  The first matching
     * tag (i.e. for which the entity has a corresponding property) will be used.
     *
     * @return The property value as an EntityList or null if no property was found matching any of the tags.
     */
    public EntityList getEntityList(Tag... tag) {
        return inner.getEntityList(tag);
    }

    /**
     * Gets property as an EntityList
     * @param prop The property to retrieve.
     * @return The property as an EntityList, or null if the property value is not an EntityList.
     */
    public EntityList getEntityList(Property prop) {
        return inner.getEntityList(prop);
    }


    /**
     * Gets the a property as an Entity.
     * @param tag Tag list to search for matching properties.
     * @return The property value as an entity, or null if no such property found, or if matching property is not an entity.
     */
    public Entity getEntity(Tag... tag) {
        return inner.getEntity(tag);
    }

    /**
     * Gets the a property value as an Entity.
     * @param prop The property whose value we wish to retrieve.
     * @return The property value as an entity, or null if the property value is not an entity.
     */
    public Entity getEntity(Property prop) {
        return inner.getEntity(prop);
    }

    /**
     * Gets the aggregate that this entity is a part of, or creates a new Aggregate with this entity as a its root.
     * @return the aggregate
     */
    public Aggregate getAggregate() {
        return inner.getAggregate();
    }

    /**
     * Sets the aggregate that this entity is a part of. This is package private.  The proper
     * way to add an Entity to an aggregate is to use {@link Aggregate#add(com.codename1.rad.models.Entity) }.
     *
     * @param aggregate The aggregate set as this entity's aggregate.
     */
    void setAggregate(Aggregate aggregate) {
        inner.setAggregate(aggregate);
    }

    private transient Aggregate aggregate;

    /**
     * {@inheritDoc }
     */
    @Override
    protected synchronized void clearChanged() {
        inner.clearChanged();
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
        return inner.get(key);
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
        inner.set(key, value);
    }


    /**
     * Converts the entity to a Map.  This will return a Map whose keys correspond to the
     * keys supplied, and values are the correponding value in the entity.
     * @param keys The keys to use for lookup.  Can be {@link Tag}, {@link Property}, or {@link Tags}.
     * @return A Map with the specified properties.
     */
    public Map toMap(Object... keys) {
        return inner.toMap(keys);
    }

    /**
     * Gets the value of the specified property.
     * @param <T>
     * @param prop The property to retrieve.
     * @return
     */
    public <T> T get(Property<T> prop) {
        return inner.get(prop);
    }

    /**
     * Finds the property corresponding to the given tags.
     * @param tags The tags to search for.  The first tag resolving to a property is used.
     * @return The matching property, or null if none found.
     */
    public Property findProperty(Tag... tags) {
        return inner.findProperty(tags);
    }

    /**
     * Gets a property as text.
     * @param prop The property
     * @return The property value as text.
     */
    public String getText(Property prop) {
        return inner.getText(prop);
    }

    /**
     * Gets a property value as text.
     * @param tags The tags to search for.  The first tag resolving to a property is used.
     * @return The property value as text.
     */
    public String getText(Tag... tags) {
        return inner.getText(tags);
    }

    /**
     * Gets property value as Boolean.  Consider using {@link #isFalsey(com.codename1.rad.models.Property) } instead
     * of this method if it is possible that the property is null, or stores a type other than Boolean.
     * @param prop The property.
     * @return The property value as boolean.
     *
     */
    public Boolean getBoolean(Property prop) {
        return inner.getBoolean(prop);
    }

    /**
     * Gets property value as Boolean. Consider using {@link #isFalsey(com.codename1.rad.models.Tag)  } instead
     * of this method if it is possible that the property is null, or stores a type other than Boolean.
     * @param tags Tags to search for.  First tag resolving to a property is used.
     * @return The property value as a Boolean.
     */
    public Boolean getBoolean(Tag... tags) {
        return inner.getBoolean(tags);
    }

    /**
     * Gets a property value as a Double.
     * @param prop The property.
     * @return The property value as a Double
     */
    public Double getDouble(Property prop) {
        return inner.getDouble(prop);
    }

    /**
     * Gets property value as a Double.
     * @param tags Tags to search for.  First tag resolving to a property is used.
     * @return Property value as Double
     */
    public Double getDouble(Tag... tags) {
        return inner.getDouble(tags);
    }

    /**
     * Gets property value as Integer
     * @param prop Th property
     * @return The property value as Integer.
     */
    public Integer getInt(Property prop) {
        return inner.getInt(prop);
    }

    /**
     * Gets property value as an Integer.
     * @param tags Tags to search for.  First tag resolving to a property is used.
     * @return Property value as Integer
     */
    public Integer getInt(Tag... tags) {
        return inner.getInt(tags);
    }

    /**
     * Gets property value as Long
     * @param prop Th property
     * @return The property value as Long.
     */
    public Long getLong(Property prop) {
        return inner.getLong(prop);
    }

    /**
     * Gets property value as a Long.
     * @param tags Tags to search for.  First tag resolving to a property is used.
     * @return Property value as Long
     */
    public Long getLong(Tag... tags) {
        return inner.getLong(tags);
    }

    /**
     * Gets property value as given content type.
     * @param <V> Content Type
     * @param prop The property to get
     * @param contentType The content type.
     * @return The value as the given content type.
     */
    public <V> V get(Property prop, ContentType<V> contentType) {
        return inner.get(prop, contentType);
    }

    /**
     * Sets property as given content type.
     * @param prop The property to set
     * @param inputType The content type of the input data
     * @param val The value to set.
     */
    public void set(Property prop, ContentType inputType, Object val) {
        inner.set(prop, inputType, val);
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
        return inner.set(tag, inputType, val);
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
        return inner.set(inputType, val, tags);
    }

    public void setText(Property prop, String text) {
        inner.setText(prop, text);
    }

    /**
     * Sets the given property as text.
     * @param tag The tag used to lookup the property.
     * @param text The text to set.
     * @return True if it was successful.  False otherwise.  False generally means that there was no
     * matching property.
     */
    public boolean setText(Tag tag, String text) {
        return inner.setText(tag, text);
    }

    /**
     * Sets property as text
     * @param text Text to set as property value.
     * @param tags Tags used to lookup property.
     * @return True if successful. False if there were no matching properties.
     */
    public boolean setText(String text, Tag... tags) {
        return inner.setText(text, tags);
    }

    /**
     * Sets property as Double
     * @param prop The property to set
     * @param val The value to set.
     */
    public void setDouble(Property prop, double val) {
        inner.setDouble(prop, val);
    }

    /**
     * Sets property as double.
     * @param tag Tag used to lookup property.
     * @param val The value to set.
     * @return True if successful.  False if no matching properties were found for tag.
     */
    public boolean setDouble(Tag tag, double val) {
        return inner.setDouble(tag, val);
    }

    /**
     * Sets property as double.
     * @param val The value to set.
     * @param tags Tags used to lookup property.
     * @return True if successful.  False if no matching properties were found for tags.
     */
    public boolean setDouble(double val, Tag... tags) {
        return inner.setDouble(val, tags);
    }

    /**
     * Sets property as Long
     * @param prop The property to set
     * @param val The value to set.
     */
    public void setLong(Property prop, long val) {
        inner.setLong(prop, val);
    }

    /**
     * Sets property as long.
     * @param tag Tag used to lookup property.
     * @param val The value to set.
     * @return True if successful.  False if no matching properties were found for tag.
     */
    public boolean setLong(Tag tag, long val) {
        return inner.setLong(tag, val);
    }

    /**
     * Sets property as long.
     * @param val The value to set.
     * @param tags Tags used to lookup property.
     * @return True if successful.  False if no matching properties were found for tags.
     */
    public boolean setLong(long val, Tag... tags) {
        return inner.setLong(val, tags);
    }

    /**
     * Sets property as Float
     * @param prop The property to set
     * @param val The value to set.
     */
    public void setFloat(Property prop, float val) {
        inner.setFloat(prop, val);
    }

    /**
     * Sets property as float.
     * @param tag Tag used to lookup property.
     * @param val The value to set.
     * @return True if successful.  False if no matching properties were found for tag.
     */
    public boolean setFloat(Tag tag, float val) {
        return inner.setFloat(tag, val);
    }

    /**
     * Sets property as float.
     * @param val The value to set.
     * @param tags Tags used to lookup property.
     * @return True if successful.  False if no matching properties were found for tags.
     */
    public boolean setFloat(float val, Tag... tags) {
        return inner.setFloat(val, tags);
    }

    /**
     * Sets property as int
     * @param prop The property to set
     * @param val The value to set.
     */
    public void setInt(Property prop, int val) {
        inner.setInt(prop,val);
    }

    /**
     * Sets property as int.
     * @param tag Tag used to lookup property.
     * @param val The value to set.
     * @return True if successful.  False if no matching properties were found for tag.
     */
    public boolean setInt(Tag tag, int val) {
        return inner.setInt(tag, val);
    }

    /**
     * Sets property as int.
     * @param val The value to set.
     * @param tags Tags used to lookup property.
     * @return True if successful.  False if no matching properties were found for tags.
     */
    public boolean setInt(int val, Tag... tags) {
        return setInt(val, tags);
    }

    /**
     * Sets property as boolean
     * @param prop The property to set
     * @param val The value to set.
     */
    public void setBoolean(Property prop, boolean val) {
        inner.setBoolean(prop, val);
    }

    /**
     * Sets property as boolean.
     * @param tag Tag used to lookup property.
     * @param val The value to set.
     * @return True if successful.  False if no matching properties were found for tag.
     */
    public boolean setBoolean(Tag tag, boolean val) {
        return inner.setBoolean(tag, val);
    }

    /**
     * Sets property as boolean.
     * @param val The value to set.
     * @param tags Tags used to lookup property.
     * @return True if successful.  False if no matching properties were found for tags.
     */
    public boolean setBoolean(boolean val, Tag... tags) {
        return inner.setBoolean(val, tags);
    }

    /**
     * Gets property as date
     * @param prop The property to set
     * @return The property as a date.
     *
     */
    public java.util.Date getDate(Property prop) {
        return inner.getDate(prop);
    }

    /**
     * Gets property as date.
     * @param tags Tags used to lookup property.
     * @return The property as a Date.
     */
    public java.util.Date getDate(Tag... tags) {
        return inner.getDate(tags);
    }

    /**
     * Sets property as Date
     * @param prop The property to set
     * @param date The value to set.
     */
    public void setDate(Property prop, Date date) {
        inner.setDate(prop, date);
    }

    /**
     * Sets property as Date.
     * @param tag Tag used to lookup property.
     * @param date The value to set.
     * @return True if successful.  False if no matching properties were found for tag.
     */
    public boolean setDate(Tag tag, Date date) {
        return inner.setDate(tag, date);
    }

    /**
     * Sets property as Date.
     * @param date The value to set.
     * @param tags Tags used to lookup property.
     * @return True if successful.  False if no matching properties were found for tags.
     */
    public boolean setDate(Date date, Tag... tags) {
        return inner.setDate(date, tags);
    }

    /**
     * Sets property as Entity
     * @param prop The property to set
     * @param e The value to set.
     */
    public void setEntity(Property prop, Entity e) {
        inner.setEntity(prop, e);

    }

    /**
     * Sets property as Entity.
     * @param tag Tag used to lookup property.
     * @param e The value to set.
     * @return True if successful.  False if no matching properties were found for tag.
     */
    public boolean setEntity(Tag tag, Entity e) {
        return inner.setEntity(tag, e);
    }

    /**
     * Sets property as Entity.
     * @param e The value to set.
     * @param tags Tags used to lookup property.
     * @return True if successful.  False if no matching properties were found for tags.
     */
    public boolean setEntity(Entity e, Tag... tags) {
        return inner.setEntity(e, tags);
    }

    /**
     * Checks if property is an Entity.
     * @param prop The property.
     * @return True only if the property value is an Entity.
     */
    public boolean isEntity(Property prop) {
        return inner.isEntity(prop);
    }

    /**
     * Checks if property is an entity.
     * @param tag The tag to lookup property.
     * @return True only if the property value is an entity.
     */
    public boolean isEntity(Tag tag) {
        return inner.isEntity(tag);
    }

    /**
     * Checks if property is empty.  This includes if property is null, an empty string, or an EntityList that is empty.
     * @param prop The property
     * @return True if property value is empty.
     */
    public boolean isEmpty(Property prop) {
        return inner.isEmpty(prop);
    }

    /**
     * Checks if property is empty. This includes if property is null, an empty string, or an EntityList that is empty.
     * @param tag The tag used to lookup property.
     * @return True if property is empty.
     */
    public boolean isEmpty(Tag tag) {
        return inner.isEmpty(tag);
    }

    /**
     * Checks if property value is falsey.  This includes if the entity doesn't contain the property, if the property value is null,
     * or an empty string, or a boolean `false` value, is numeric with a `0` value, or is an empty EntityList.
     * @param prop The property to check.
     * @return True if the property value is "falsey".
     */
    public boolean isFalsey(Property prop) {
        return inner.isFalsey(prop);

    }

    /**
     * Checks if property value is falsey.  This includes if the entity doesn't contain the property, if the property value is null,
     * or an empty string, or a boolean `false` value, is numeric with a `0` value, or is an empty EntityList.
     * @param tag The tag to lookup the property.
     * @return True if property value is "falsey".
     */
    public boolean isFalsey(Tag tag) {
        return inner.isFalsey(tag);
    }

    /**
     * Calls the super setChanged() method (in `Observable`)
     */
    void setChangedInternal() {
        inner.setChangedInternal();
    }

    /**
     * Marks a property value as "changed".  This will propagate a call to {@link #setChanged() }
     * (marking the whole object as changed), and it will fire a property change event, if the `firePropertyChange` argument
     * is true.
     * @param prop The property to mark as changed.
     * @param firePropertyChange True to fire a property change event to all listeners of this property.
     */
    public void setChanged(Property prop, boolean firePropertyChange) {
        inner.setChanged(prop, firePropertyChange);
    }

    /**
     * Adds an item to the given property.  Assumes that the property value is an EntityList.  If property is currently
     * null, it will attempt to create an EntityList for the property, and add a value.
     * @param prop The property to set.
     * @param value The entity to add to the property's entity list.
     */
    public void add(Property prop, Entity value) {
        inner.add(prop, value);
    }



    /**
     * Gets property as an EntityList.  If the property is currently null, then this will attempt to create a
     * new EntityList at this property, and return that.
     * @param prop The property.
     * @return The property value as an entity list.
     * @throws IllegalStateException If the property type is not an EntityList type.
     */
    public EntityList getEntityListNonNull(Property prop) {
        return inner.getEntityListNonNull(prop);
    }

    /**
     * Gets a property value as an Entity.  If the property value is currently null, then this will attempt to create
     * a new Entity at this property, and return that.
     * @param prop The property to get.
     * @return The property value as an Entity.
     * @throws IllegalStateException If the property type is not an Entity type.
     */
    public Entity getEntityNonNull(Property prop) {
        return inner.getEntityNonNull(prop);
    }

}
