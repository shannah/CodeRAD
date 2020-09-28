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

import com.codename1.rad.ui.UI;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.Image;
import com.codename1.ui.URLImage;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.util.EventDispatcher;
import com.codename1.util.Base64;
import com.codename1.util.StringUtil;
import java.util.Date;

/**
 * A class for retrieving properties from an entity hierarchy in a dynamic way.  E.g.
 * If you have an entity that is expected to have a property, which is itself an entity,
 * which has a property that we want to retrieve.  This class encapsulates the retrieval
 * of such a sub-property by specifying a "path" to the property.
 * 
 * @author shannah
 */
public class PropertySelector {
    
    private EventDispatcher listeners;
    
    
    private ActionListener<PropertyChangeEvent> pcl;
    private ActionListener<PropertyChangeEvent> pcl() {
        if (pcl == null) {
            pcl = evt -> {
                if (listeners != null && listeners.hasListeners() && !evt.isConsumed()) {
                    listeners.fireActionEvent(evt);
                }
            };
        }
        return pcl;
    }
    
    
    /**
     * Adds a change listener on property.
     * @param l The listener to add.
     */
    public void addPropertyChangeListener(ActionListener<PropertyChangeEvent> l) {
        if (root != null) {
            Property prop = property;
            if (prop == null) {
                prop = root.findProperty(tags);
            }
            if (prop != null) {
                root.addPropertyChangeListener(prop, pcl());
            }
        } else {
            parent.addPropertyChangeListener(pcl());
        }
        if (listeners == null) {
            listeners = new EventDispatcher();
        }
        listeners.addListener(l);
        
    }
    
    /**
     * Removes property change listener from property.
     * @param l 
     */
    public void removePropertyChangeListener(ActionListener<PropertyChangeEvent> l) {
        if (listeners == null || !listeners.hasListeners()) {
            return;
        }
        listeners.removeListener(l);
        if (!listeners.hasListeners()) {
            if (pcl != null) {
                if (root != null) {
                    Property prop = property;
                    if (prop == null) {
                        prop = root.findProperty(tags);
                    }
                    if (prop != null) {
                        root.removePropertyChangeListener(prop, pcl);
                    }
                } else {
                    parent.removePropertyChangeListener(pcl);
                }
            }
        }
    }
    
    
    
    private Entity root;
    private Tag[] tags;
    private Property property;
    private PropertySelector parent;
    
    /**
     * Gets the property value as the given type.
     * @param <T> The type to coerce the property value to.
     * @param type The type to coerce the property value to.
     * @param defaultValue The default value which will be returned if either the property is not found, or is null.
     * @return 
     */
    public <T> T get(ContentType<T> type, T defaultValue) {
        Entity e = null;
        if (parent != null) {
            e = parent.get(ContentType.EntityType, null);
        } else if (root != null) {
            e = root;
        }
        if (e != null) {
            Property prop = property;
            if (prop == null) {
                prop = e.getEntityType().findProperty(tags);
            }
            if (prop != null) {
                //if (type == ContentType.EntityType && !e.isEntity(prop)) {
                //    return defaultValue;
                //}
                if (!prop.getContentType().canConvertTo(type) && !type.canConvertFrom(prop.getContentType())) {
                    return defaultValue;
                }
                T out = e.get(prop, type);
                if (out == null) {
                    return defaultValue;
                }
                return out;
            }
        }
        return defaultValue;
    }
    
    /**
     * Creates a new property selector
     * @param root The root entity to select properties on.
     * @param tags The tags to use for property selection.
     */
    public PropertySelector(Entity root, Tag... tags) {
        this.root = root;
        this.tags = tags;
        
    }
    
    /**
     * Creates a new property selector.
     * @param root The root entity to select properties on.
     * @param property The property to get values from.
     */
    public PropertySelector(Entity root, Property property) {
        this.root = root;
        this.property = property;
    }
    
    /**
     * Creates a new property selector with the given parent selector.
     * @param parent The parent selector
     * @param tags The tags to select from.
     */
    public PropertySelector(PropertySelector parent, Tag... tags) {
        this.parent = parent;
        this.tags = tags;
    }
    
    /**
     * Creates a new property selector with the given parent selector.
     * @param parent THe parent selector
     * @param property The property.
     */
    public PropertySelector(PropertySelector parent, Property property) {
        this.parent = parent;
        this.property = property;
    }
    
    /**
     * Creates a child selector on this property selector.
     * @param property The property to select on.
     * @return The child property selector.
     */
    public PropertySelector createChildSelector(Property property) {
        return new PropertySelector(this, property);
    }
    
    /**
     * Creates a child selector for this property selector.
     * @param tags The tags used to lookup the property in the child selector.
     * @return The child property selector.
     */
    public PropertySelector createChildSelector(Tag... tags) {
        return new PropertySelector(this, tags);
    }
    
    /**
     * Alias of {@link #createChildSelector(com.codename1.rad.models.Property) }
     * @param prop
     * @return 
     */
    public PropertySelector child(Property prop) {
        return createChildSelector(prop);
    }
    
    /**
     * Alias of {@link #createChildSelector(com.codename1.rad.models.Tag...) }
     * @param tags
     * @return 
     */
    public PropertySelector child(Tag... tags) {
        return createChildSelector(tags);
    }
    
    /**
     * Gets the selected property value as text.
     * @param defaultValue The value to return if the property value was null
     * @return The property value as text.
     */
    public String getText(String defaultValue) {
        return get(ContentType.Text, defaultValue);
    }
    
    /**
     * Gets the selected property as boolean
     * @param defaultVal The value to return if the property value was null.
     * @return The property as boolean.
     */
    public Boolean getBoolean(boolean defaultVal) {
        return get(ContentType.BooleanType, defaultVal);
    }
    
    /**
     * Gets the selected property value as Date.
     * @param defaultVal The value to return if the property value was null
     * @return The property value as date.
     */
    public Date getDate(Date defaultVal) {
        return get(ContentType.DateType, defaultVal);
    }
    
    /**
     * Gets the selected property value as Entity.
     * @param defaultVal The value to return if the property value was null
     * @return The property value as entity.
     */
    public Entity getEntity(Entity defaultVal) {
        return get(ContentType.EntityType, defaultVal);
    }
    
    /**
     * Gets the selected property value as EntityList.
     * @param defaultVal The value to return if the property value was null
     * @return The property value as EntityList.
     */
    public EntityList getEntityList(EntityList defaultVal) {
        return get(ContentType.EntityListType, defaultVal);
    }
    
    /**
     * Gets the selected property value as float.
     * @param defaultVal The value to return if the property value was null
     * @return The property value as float.
     */
    public Float getFloat(float defaultVal) {
        return get(ContentType.FloatType, defaultVal);
    }
    
    /**
     * Gets the selected property value as double.
     * @param defaultVal The value to return if the property value was null
     * @return The property value as double.
     */
    public Double getDouble(double defaultVal) {
        return get(ContentType.DoubleType, defaultVal);
    }
    
    /**
     * Gets the selected property value as int.
     * @param defaultVal The value to return if the property value was null
     * @return The property value as int.
     */
    public Integer getInt(int defaultVal) {
        return get(ContentType.IntegerType, defaultVal);
    }
    
    /**
     * Checks if property is empty.
     * @return 
     */
    public boolean isEmpty() {
        Entity e = null;
        if (parent != null) {
            e = parent.get(ContentType.EntityType, null);
        } else if (root != null) {
            e = root;
        }
        
        if (e != null) {
            Property prop = property;
            if (prop == null) {
                prop = e.getEntityType().findProperty(tags);
            }
            if (prop != null) {
                return e.isEmpty(prop);
            }
        }
        return true;
    }
    
    /**
     * Checks if property is falsey.
     * @return 
     */
    public boolean isFalsey() {
        Entity e = null;
        if (parent != null) {
            e = parent.get(ContentType.EntityType, null);
        } else if (root != null) {
            e = root;
        }
        
        if (e != null) {
            Property prop = property;
            if (prop == null) {
                prop = e.getEntityType().findProperty(tags);
            }
            if (prop != null) {
                return e.isFalsey(prop);
            }
        }
        return true;
    }
    
    /**
     * Wrapper for {@link Entity#createImageToStorage(com.codename1.rad.models.Property, com.codename1.ui.EncodedImage) }
     * @param placeholder
     * @param adapter
     * @return Image
     */
     public Image createImageToStorage(EncodedImage placeholder, URLImage.ImageAdapter adapter) {
        return createImageToStorage(placeholder, null, adapter);
    }
    
   
    /**
     * Wrapper for {@link Entity#createImageToFile(com.codename1.rad.models.Tag, com.codename1.ui.EncodedImage) }
     * @param placeholder
     * @return 
     */
    public Image createImageToStorage(EncodedImage placeholder) {
        return createImageToStorage(placeholder, null, null);
    }
    
    
    /**
     * Wrapper for {@link Entity#createImageToStorage(com.codename1.rad.models.Property, com.codename1.ui.EncodedImage) }
     * @param placeholder
     * @param storageFile
     * @return 
     */
    public Image createImageToStorage(EncodedImage placeholder, String storageFile) {
        return createImageToStorage(placeholder, storageFile, null);
    }
    
    
    
    
    /**
     * Wrapper for {@link #createImageToStorage(com.codename1.ui.EncodedImage) }
     * @param placeholder
     * @param storageFile
     * @param adapter
     * @return 
     */
    public Image createImageToStorage(EncodedImage placeholder, String storageFile, URLImage.ImageAdapter adapter) {
        String str = getText(null);
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
     * Wrapper for {@link Entity#createImageToFile(com.codename1.rad.models.Property, com.codename1.ui.EncodedImage) }
     * @param placeholder
     * @param adapter
     * @return 
     */
    public Image createImageToFile(EncodedImage placeholder, URLImage.ImageAdapter adapter) {
        return createImageToFile(placeholder, null, adapter);
    }
    
   
    /**
     * Wrapper for {@link Entity#createImageToFile(com.codename1.rad.models.Property, com.codename1.ui.EncodedImage) }
     * @param placeholder
     * @return 
     */
    public Image createImageToFile(EncodedImage placeholder) {
        return createImageToFile(placeholder, (String)null);
    }
    
   
    /**
     * Wrapper for {@link Entity#createImageToFile(com.codename1.rad.models.Property, com.codename1.ui.EncodedImage) }
     * @param placeholder
     * @param file
     * @return 
     */
    public Image createImageToFile(EncodedImage placeholder, String file) {
        
    
        return createImageToFile(placeholder, file, null);
    }
    
    /**
     * Wrapper for {@link Entity#createImageToFile(com.codename1.rad.models.Property, com.codename1.ui.EncodedImage) }
     * @param placeholder
     * @param file
     * @param adapter
     * @return 
     */
    public Image createImageToFile(EncodedImage placeholder, String file, URLImage.ImageAdapter adapter) {
        String str = getText(null);
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
     * Resolves the property of this property selector.
     * @return The property resolved by this property selector.
     */
    public Property getLeafProperty() {
        Entity e = root;
        if (e == null) {
            if (parent != null) {
                e = parent.get(ContentType.EntityType, null);
            }
        }
        if (e == null) {
            return null;
        }
        Property prop = property;
        if (prop == null) {
            prop = e.getEntityType().findProperty(tags);
        }
        return prop;
    }
    
    /**
     * Resolves the entity of this property selector.
     * @return The entity of this property selector.
     */
    public Entity getLeafEntity() {
        Entity e = root;
        if (e == null) {
            if (parent != null) {
                e = parent.get(ContentType.EntityType, null);
            }
        }
        return e;
    }
    
    /**
     * Gets the parent property selector.
     * @return The parent property selector.
     */
    public PropertySelector getParent() {
        return parent;
    }
    
    /**
     * Gets the root property selector.  If this selector is the root selector, then this will just 
     * return itself.  It it is a child selector, it will walk up its parent tree until it reaches the root.
     * @return 
     */
    public PropertySelector getRoot() {
        if (parent == null) {
            return this;
        }
        return parent.getRoot();
    }
    
    /**
     * Checks if the property of this selector exists.
     * @return 
     */
    public boolean exists() {
        Entity e = root;
        if (e == null) {
            if (parent != null) {
                e = parent.get(ContentType.EntityType, null);
            }
        }
        if (e == null) {
            return false;
        }
        if (e.getEntityType().isDynamic()) {
            return true;
        }
        Property prop = property;
        if (prop == null) {
            prop = e.getEntityType().findProperty(tags);
        }
        return prop != null;
    }
    
}
