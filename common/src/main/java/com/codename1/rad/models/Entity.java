/*
 * The MIT License
 *
 * Copyright 2021 shannah.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.codename1.rad.models;

import com.codename1.ui.EncodedImage;
import com.codename1.ui.Image;
import com.codename1.ui.URLImage;
import com.codename1.ui.events.ActionListener;
import com.codename1.util.SuccessCallback;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author shannah
 */
public interface Entity extends EntityWrapper {

    /**
     * Adds an item to the given property.  Assumes that the property value is an EntityList.  If property is currently
     * null, it will attempt to create an EntityList for the property, and add a value.
     * @param prop The property to set.
     * @param value The entity to add to the property's entity list.
     */
    void add(Property prop, Entity value);

    /**
     * Adds a listener to be notified of changes to the given property.
     * @param property The property to listen on.
     * @param l The listener.
     */
    void addPropertyChangeListener(Property property, ActionListener<PropertyChangeEvent> l);

    /**
     * Adds a listener to listen to all property changes on all properties.
     * @param l The listener.
     */
    void addPropertyChangeListener(ActionListener<PropertyChangeEvent> l);

    /**
     * Adds a listener to be notified of changes to the given property.
     * @param property The property to listen on.
     * @param l The listener.
     */
    void addVetoablePropertyChangeListener(Property property, ActionListener<VetoablePropertyChangeEvent> l);

    /**
     * Adds a listener to listen to all property changes on all properties.
     * @param l The listener.
     */
    void addVetoablePropertyChangeListener(ActionListener<VetoablePropertyChangeEvent> l);

    <T> boolean as(Class<T> cls, SuccessCallback<T> callback);

    /**
     * Creates an image for a given property to file system. This behaves similarly to the {@link URLImage#createToFileSystem(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String, com.codename1.ui.URLImage.ImageAdapter) }
    except the URL is retrieved from the entity's property, instead of as a parameter.
     *
     * @param tag The tag used to look up the property to store.
     * @param placeholder The placeholder image.
     * @param adapter Adapter to manipulate the image when loading.
     * @return The Image, usually a URLImage.
     */
    Image createImageToFile(Tag tag, EncodedImage placeholder, URLImage.ImageAdapter adapter);

    /**
     * Creates an image for a given property to file system. This behaves similarly to the {@link URLImage#createToFileSystem(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String, com.codename1.ui.URLImage.ImageAdapter) }
    except the URL is retrieved from the entity's property, instead of as a parameter.
     *
     * @param prop The property where the image url should be retrieved from.
     * @param placeholder The placeholder image.
     * @param adapter Adapter to manipulate the image when loading.
     * @return The Image, usually a URLImage.
     */
    Image createImageToFile(Property prop, EncodedImage placeholder, URLImage.ImageAdapter adapter);

    /**
     * Creates an image for a given property to file system. This behaves similarly to the {@link URLImage#createToFileSystem(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String, com.codename1.ui.URLImage.ImageAdapter) }
    except the URL is retrieved from the entity's property, instead of as a parameter.
     *
     * @param tag The tag used to look up the property to store.
     * @param placeholder The placeholder image.
     * @return The Image, usually a URLImage.
     */
    Image createImageToFile(Tag tag, EncodedImage placeholder);

    /**
     * Creates an image for a given property to file system. This behaves similarly to the {@link URLImage#createToFileSystem(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String, com.codename1.ui.URLImage.ImageAdapter) }
    except the URL is retrieved from the entity's property, instead of as a parameter.
     *
     * @param prop The property where the image url should be retrieved from.
     * @param placeholder The placeholder image.
     * @return The Image, usually a URLImage.
     */
    Image createImageToFile(Property prop, EncodedImage placeholder);

    /**
     * Creates an image for a given property to file system.This behaves similarly to the {@link URLImage#createToFileSystem(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String, com.codename1.ui.URLImage.ImageAdapter)}
    except the URL is retrieved from the entity's property, instead of as a parameter.
     *
     * @param tag The tag used to look up the property to store.
     * @param placeholder The placeholder image.
     * @param file The path where to cache the image in the file system.
     * @return The Image, usually a URLImage.
     */
    Image createImageToFile(Tag tag, EncodedImage placeholder, String file);

    /**
     * Creates an image for a given property to file system.This behaves similarly to the {@link URLImage#createToFileSystem(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String, com.codename1.ui.URLImage.ImageAdapter)}
    except the URL is retrieved from the entity's property, instead of as a parameter.
     *
     * @param prop The property where the image URL should be retrieved from.
     * @param placeholder The placeholder image.
     * @param file The path where to cache the image in the file system.
     * @return The Image, usually a URLImage.
     */
    Image createImageToFile(Property prop, EncodedImage placeholder, String file);

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
    Image createImageToFile(Tag tag, EncodedImage placeholder, String file, URLImage.ImageAdapter adapter);

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
    Image createImageToFile(Property prop, EncodedImage placeholder, String file, URLImage.ImageAdapter adapter);

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
    Image createImageToStorage(Tag tag, EncodedImage placeholder, URLImage.ImageAdapter adapter);

    /**
     * Creates an image to storage on the given property. This behaves similarly to the {@link URLImage#createToStorage(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String) }
     * except the URL is retrieved from the entity's property, instead of as a parameter.
     * @param prop The property to store the image in.
     * @param placeholder The placeholder image.
     * @param adapter
     * @return The Image
     */
    Image createImageToStorage(Property prop, EncodedImage placeholder, URLImage.ImageAdapter adapter);

    /**
     * Creates an image to storage on the given property. This behaves similarly to the {@link URLImage#createToStorage(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String) }
     * except the URL is retrieved from the entity's property, instead of as a parameter.
     * @param tag The tag used to look up the property to store.
     * @param placeholder The placeholder image.
     * @return The Image
     */
    Image createImageToStorage(Tag tag, EncodedImage placeholder);

    /**
     * Creates an image to storage on the given property. This behaves similarly to the {@link URLImage#createToStorage(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String) }
     * except the URL is retrieved from the entity's property, instead of as a parameter.
     * @param prop The property to store the image in.
     * @param placeholder The placeholder image.
     * @return The Image
     */
    Image createImageToStorage(Property prop, EncodedImage placeholder);

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
    Image createImageToStorage(Tag tag, EncodedImage placeholder, String storageFile);

    /**
     * Creates an image to storage on the given property.This behaves similarly to the {@link URLImage#createToStorage(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String)}
    except the URL is retrieved from the entity's property, instead of as a parameter.
     * @param prop The property to store the image in.
     * @param placeholder The placeholder image.
     * @param storageFile The storage key where the image should be cached.
     * @return The Image
     */
    Image createImageToStorage(Property prop, EncodedImage placeholder, String storageFile);

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
    Image createImageToStorage(Tag tag, EncodedImage placeholder, String storageFile, URLImage.ImageAdapter adapter);

    /**
     * Creates an image to storage on the given property.This behaves similarly to the {@link URLImage#createToStorage(com.codename1.ui.EncodedImage, java.lang.String, java.lang.String)}
    except the URL is retrieved from the entity's property, instead of as a parameter.
     * @param prop The property to store the image in.
     * @param placeholder The placeholder image.
     * @param storageFile The storage key where the image should be cached.
     * @param adapter Adapter to manipulate the image when loading.
     * @return The Image
     */
    Image createImageToStorage(Property prop, EncodedImage placeholder, String storageFile, URLImage.ImageAdapter adapter);

    /**
     * Finds the property corresponding to the given tags.
     * @param tags The tags to search for.  The first tag resolving to a property is used.
     * @return The matching property, or null if none found.
     */
    Property findProperty(Tag... tags);

    /**
     * Gets a property value.
     * @param key The lookup key for the property.  This can be either a {@link Property}, a {@link Tag}, a {@link Tags},
     * or anything else.
     *
     * If {@link Tags} is supplied, then the first tag resolving to a property is used.
     *
     * @return The property value.
     */
    Object get(Object key);

    /**
     * Gets the value of the specified property.
     * @param <T>
     * @param prop The property to retrieve.
     * @return
     */
    <T> T get(Property<T> prop);

    /**
     * Gets property value as given content type.
     * @param <V> Content Type
     * @param prop The property to get
     * @param contentType The content type.
     * @return The value as the given content type.
     */
    <V> V get(Property prop, ContentType<V> contentType);

    /**
     * Gets the aggregate that this entity is a part of, or creates a new Aggregate with this entity as a its root.
     * @return the aggregate
     */
    Aggregate getAggregate();

    <T> T getAs(Property prop, Class<T> cls);

    <T> T getAs(Tag tag, Class<T> cls);

    <T> boolean getAs(Property prop, Class<T> cls, SuccessCallback<T> callback);

    <T> boolean getAs(Tag tag, Class<T> cls, SuccessCallback<T> callback);

    /**
     * Gets property value as Boolean.  Consider using {@link #isFalsey(com.codename1.rad.models.Property) } instead
     * of this method if it is possible that the property is null, or stores a type other than Boolean.
     * @param prop The property.
     * @return The property value as boolean.
     *
     */
    Boolean getBoolean(Property prop);

    /**
     * Gets property value as Boolean. Consider using {@link #isFalsey(com.codename1.rad.models.Tag)  } instead
     * of this method if it is possible that the property is null, or stores a type other than Boolean.
     * @param tags Tags to search for.  First tag resolving to a property is used.
     * @return The property value as a Boolean.
     */
    Boolean getBoolean(Tag... tags);

    /**
     * Gets property as date
     * @param prop The property to set
     * @return The property as a date.
     *
     */
    Date getDate(Property prop);

    /**
     * Gets property as date.
     * @param tags Tags used to lookup property.
     * @return The property as a Date.
     */
    Date getDate(Tag... tags);

    /**
     * Gets a property value as a Double.
     * @param prop The property.
     * @return The property value as a Double
     */
    Double getDouble(Property prop);

    /**
     * Gets property value as a Double.
     * @param tags Tags to search for.  First tag resolving to a property is used.
     * @return Property value as Double
     */
    Double getDouble(Tag... tags);

    /**
     * Gets the a property as an Entity.
     * @param tag Tag list to search for matching properties.
     * @return The property value as an entity, or null if no such property found, or if matching property is not an entity.
     */
    Entity getEntity(Tag... tag);

    /**
     * Gets the a property value as an Entity.
     * @param prop The property whose value we wish to retrieve.
     * @return The property value as an entity, or null if the property value is not an entity.
     */
    Entity getEntity(Property prop);

    BaseEntity getEntity();

    /**
     * Gets a property as an EntityList.
     * @param tag The tag used to look up the property.  More than one tag can be supplied.  The first matching
     * tag (i.e. for which the entity has a corresponding property) will be used.
     *
     * @return The property value as an EntityList or null if no property was found matching any of the tags.
     */
    EntityList getEntityList(Tag... tag);

    /**
     * Gets property as an EntityList
     * @param prop The property to retrieve.
     * @return The property as an EntityList, or null if the property value is not an EntityList.
     */
    EntityList getEntityList(Property prop);

    /**
     * Gets property as an EntityList.  If the property is currently null, then this will attempt to create a
     * new EntityList at this property, and return that.
     * @param prop The property.
     * @return The property value as an entity list.
     * @throws IllegalStateException If the property type is not an EntityList type.
     */
    EntityList getEntityListNonNull(Property prop);

    /**
     * Gets a property value as an Entity.  If the property value is currently null, then this will attempt to create
     * a new Entity at this property, and return that.
     * @param prop The property to get.
     * @return The property value as an Entity.
     * @throws IllegalStateException If the property type is not an Entity type.
     */
    Entity getEntityNonNull(Property prop);

    /**
     * Gets the entity type of this entity.
     *
     * @return The entity type.
     */
    EntityType getEntityType();

    /**
     * Gets property value as Integer
     * @param prop Th property
     * @return The property value as Integer.
     */
    Integer getInt(Property prop);

    /**
     * Gets property value as an Integer.
     * @param tags Tags to search for.  First tag resolving to a property is used.
     * @return Property value as Integer
     */
    Integer getInt(Tag... tags);

    /**
     * Gets property value as Long
     * @param prop Th property
     * @return The property value as Long.
     */
    Long getLong(Property prop);

    /**
     * Gets property value as a Long.
     * @param tags Tags to search for.  First tag resolving to a property is used.
     * @return Property value as Long
     */
    Long getLong(Tag... tags);

    /**
     * Gets a property as text.
     * @param prop The property
     * @return The property value as text.
     */
    String getText(Property prop);

    /**
     * Gets a property value as text.
     * @param tags The tags to search for.  The first tag resolving to a property is used.
     * @return The property value as text.
     */
    String getText(Tag... tags);


    boolean hasPropertyChangeListeners(Property prop);

    boolean hasVetoablePropertyChangeListeners(Property prop);

    /**
     * Checks if property is empty.  This includes if property is null, an empty string, or an EntityList that is empty.
     * @param prop The property
     * @return True if property value is empty.
     */
    boolean isEmpty(Property prop);

    /**
     * Checks if property is empty. This includes if property is null, an empty string, or an EntityList that is empty.
     * @param tag The tag used to lookup property.
     * @return True if property is empty.
     */
    boolean isEmpty(Tag tag);

    /**
     * Checks if property is an Entity.
     * @param prop The property.
     * @return True only if the property value is an Entity.
     */
    boolean isEntity(Property prop);

    /**
     * Checks if property is an entity.
     * @param tag The tag to lookup property.
     * @return True only if the property value is an entity.
     */
    boolean isEntity(Tag tag);

    /**
     * Checks if property value is falsey.  This includes if the entity doesn't contain the property, if the property value is null,
     * or an empty string, or a boolean `false` value, is numeric with a `0` value, or is an empty EntityList.
     * @param prop The property to check.
     * @return True if the property value is "falsey".
     */
    boolean isFalsey(Property prop);

    /**
     * Checks if property value is falsey.  This includes if the entity doesn't contain the property, if the property value is null,
     * or an empty string, or a boolean `false` value, is numeric with a `0` value, or is an empty EntityList.
     * @param tag The tag to lookup the property.
     * @return True if property value is "falsey".
     */
    boolean isFalsey(Tag tag);

    /**
     * Removes a property change listener.
     * @param property The property to listen to.
     * @param l The listener.
     */
    void removePropertyChangeListener(Property property, ActionListener<PropertyChangeEvent> l);

    /**
     * Removes property change listener.
     * @param l The listener.
     */
    void removePropertyChangeListener(ActionListener<PropertyChangeEvent> l);

    /**
     * Removes a property change listener.
     * @param property The property to listen to.
     * @param l The listener.
     */
    void removeVetoablePropertyChangeListener(Property property, ActionListener<VetoablePropertyChangeEvent> l);

    /**
     * Removes property change listener.
     * @param l The listener.
     */
    void removeVetoablePropertyChangeListener(ActionListener<VetoablePropertyChangeEvent> l);

    /**
     * Sets a property value.
     *
     * @param key The lookup key for the property.  This can be a {@link Property}, {@link Tag}, or {@link Tags}.
     *
     * If {@link Tags} is supplied, then the first tag resolving to a property is used.
     *
     * @param value The value to set.
     */
    void set(Object key, Object value);

    /**
     * Sets property as given content type.
     * @param prop The property to set
     * @param inputType The content type of the input data
     * @param val The value to set.
     */
    void set(Property prop, ContentType inputType, Object val);

    /**
     * Sets the property value as the given content type.
     * @param tag The tag used to lookup the property.
     * @param inputType The content type of the value that is being set.
     * @param val The value.
     * @return true if it was successfully set.  false if it was not, generally due to
     * no property being found matching the tag.
     */
    boolean set(Tag tag, ContentType inputType, Object val);

    /**
     * Sets the property value as the given content type.
     * @param inputType The content type of the input data.
     * @param val The value to set.
     * @param tags The tags used to lookup the property.  The first tag resolving to a property is used.
     * @return True if the value was set.  False if not. Usually false means that there was no property
     * matching the provided tags.
     */
    boolean set(ContentType inputType, Object val, Tag... tags);

    /**
     * Sets property as boolean
     * @param prop The property to set
     * @param val The value to set.
     */
    void setBoolean(Property prop, boolean val);

    /**
     * Sets property as boolean.
     * @param tag Tag used to lookup property.
     * @param val The value to set.
     * @return True if successful.  False if no matching properties were found for tag.
     */
    boolean setBoolean(Tag tag, boolean val);

    /**
     * Sets property as boolean.
     * @param val The value to set.
     * @param tags Tags used to lookup property.
     * @return True if successful.  False if no matching properties were found for tags.
     */
    boolean setBoolean(boolean val, Tag... tags);

    /**
     * Marks a property value as "changed".  This will propagate a call to {@link #setChanged() }
     * (marking the whole object as changed), and it will fire a property change event, if the `firePropertyChange` argument
     * is true.
     * @param prop The property to mark as changed.
     * @param firePropertyChange True to fire a property change event to all listeners of this property.
     */
    void setChanged(Property prop, boolean firePropertyChange);

    /**
     * Sets property as Date
     * @param prop The property to set
     * @param date The value to set.
     */
    void setDate(Property prop, Date date);

    /**
     * Sets property as Date.
     * @param tag Tag used to lookup property.
     * @param date The value to set.
     * @return True if successful.  False if no matching properties were found for tag.
     */
    boolean setDate(Tag tag, Date date);

    /**
     * Sets property as Date.
     * @param date The value to set.
     * @param tags Tags used to lookup property.
     * @return True if successful.  False if no matching properties were found for tags.
     */
    boolean setDate(Date date, Tag... tags);

    /**
     * Sets property as Double
     * @param prop The property to set
     * @param val The value to set.
     */
    void setDouble(Property prop, double val);

    /**
     * Sets property as double.
     * @param tag Tag used to lookup property.
     * @param val The value to set.
     * @return True if successful.  False if no matching properties were found for tag.
     */
    boolean setDouble(Tag tag, double val);

    /**
     * Sets property as double.
     * @param val The value to set.
     * @param tags Tags used to lookup property.
     * @return True if successful.  False if no matching properties were found for tags.
     */
    boolean setDouble(double val, Tag... tags);

    /**
     * Sets property as Entity
     * @param prop The property to set
     * @param e The value to set.
     */
    void setEntity(Property prop, Entity e);

    /**
     * Sets property as Entity.
     * @param tag Tag used to lookup property.
     * @param e The value to set.
     * @return True if successful.  False if no matching properties were found for tag.
     */
    boolean setEntity(Tag tag, Entity e);

    /**
     * Sets property as Entity.
     * @param e The value to set.
     * @param tags Tags used to lookup property.
     * @return True if successful.  False if no matching properties were found for tags.
     */
    boolean setEntity(Entity e, Tag... tags);

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
    void setEntityType(EntityType entityType);

    /**
     * Sets property as Float
     * @param prop The property to set
     * @param val The value to set.
     */
    void setFloat(Property prop, float val);

    /**
     * Sets property as float.
     * @param tag Tag used to lookup property.
     * @param val The value to set.
     * @return True if successful.  False if no matching properties were found for tag.
     */
    boolean setFloat(Tag tag, float val);

    /**
     * Sets property as float.
     * @param val The value to set.
     * @param tags Tags used to lookup property.
     * @return True if successful.  False if no matching properties were found for tags.
     */
    boolean setFloat(float val, Tag... tags);

    /**
     * Sets property as int
     * @param prop The property to set
     * @param val The value to set.
     */
    void setInt(Property prop, int val);

    /**
     * Sets property as int.
     * @param tag Tag used to lookup property.
     * @param val The value to set.
     * @return True if successful.  False if no matching properties were found for tag.
     */
    boolean setInt(Tag tag, int val);

    /**
     * Sets property as int.
     * @param val The value to set.
     * @param tags Tags used to lookup property.
     * @return True if successful.  False if no matching properties were found for tags.
     */
    boolean setInt(int val, Tag... tags);

    /**
     * Sets property as Long
     * @param prop The property to set
     * @param val The value to set.
     */
    void setLong(Property prop, long val);

    /**
     * Sets property as long.
     * @param tag Tag used to lookup property.
     * @param val The value to set.
     * @return True if successful.  False if no matching properties were found for tag.
     */
    boolean setLong(Tag tag, long val);

    /**
     * Sets property as long.
     * @param val The value to set.
     * @param tags Tags used to lookup property.
     * @return True if successful.  False if no matching properties were found for tags.
     */
    boolean setLong(long val, Tag... tags);

    void setText(Property prop, String text);

    /**
     * Sets the given property as text.
     * @param tag The tag used to lookup the property.
     * @param text The text to set.
     * @return True if it was successful.  False otherwise.  False generally means that there was no
     * matching property.
     */
    boolean setText(Tag tag, String text);

    /**
     * Sets property as text
     * @param text Text to set as property value.
     * @param tags Tags used to lookup property.
     * @return True if successful. False if there were no matching properties.
     */
    boolean setText(String text, Tag... tags);

    /**
     * Converts the entity to a Map.  This will return a Map whose keys correspond to the
     * keys supplied, and values are the correponding value in the entity.
     * @param keys The keys to use for lookup.  Can be {@link Tag}, {@link Property}, or {@link Tags}.
     * @return A Map with the specified properties.
     */
    Map toMap(Object... keys);

    
    
}
