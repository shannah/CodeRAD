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
import java.util.Objects;

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
    
    /**
     * The listChangeListener which is added to parent selectors to listen for any changes to lists in those selectors
     * that would invalidate this selector's event listeners.  If such a change is received, this will revalidate
     * the listeners with the new leaf entity/property.
     * 
     */
    private ActionListener<EntityList.EntityListEvent> listChangeListener;
    private ActionListener<EntityList.EntityListEvent> listChangeListener() {
        if (listChangeListener == null) {
            listChangeListener = evt -> {
                if (evt instanceof EntityList.VetoableEntityEvent) {
                    // Because this is a veto event, it has occurred before the list was mutated.
                    if (parent != null && vpcl != null) {
                        parent.removeVetoablePropertyChangeListener(vpcl);
                    }
                    if (parent != null) {
                        parent.removeListChangeListener(listChangeListener);
                    }
                    Entity oldLeafEntity = getLeafEntity();
                    Property oldLeafProperty = getLeafProperty();
                    Object oldValue;
                    if (oldLeafEntity != null && oldLeafProperty != null) {
                        oldLeafEntity.getEntity().removePropertyChangeListener(oldLeafProperty, pcl);
                        oldValue = oldLeafEntity.getEntity().get(oldLeafProperty);
                    } else {
                        oldValue = null;
                    }
                    
                    EntityList source = (EntityList)evt.getSource();
                    ActionListener<EntityList.EntityListEvent> afterChangeListener = new ActionListener<EntityList.EntityListEvent>() {
                        public void actionPerformed(EntityList.EntityListEvent afterChangeEvent) {
                            source.removeActionListener(this);
                            Entity newLeafEntity = getLeafEntity();
                            Property newLeafProperty = getLeafProperty();
                            Object newValue;
                            if (newLeafEntity != null && newLeafProperty != null) {
                                newLeafEntity.getEntity().addPropertyChangeListener(newLeafProperty, pcl);
                                newValue = newLeafEntity.getEntity().get(newLeafProperty);
                            } else {
                                newValue = null;
                            }
                            if (parent != null && vpcl != null) {
                                parent.addVetoablePropertyChangeListener(vpcl);
                            }
                            if (parent != null) {
                                parent.addListChangeListener(listChangeListener);
                            }
                            if (!Objects.equals(oldValue, newValue)) {
                                 listeners.fireActionEvent(new PropertyChangeEvent(newLeafEntity, newLeafProperty, oldValue, newValue));
                            }
                            
                        }
                    };
                    source.addActionListener(afterChangeListener);
                    
                }
            };
        }
        return listChangeListener;
    }
    
    /**
     * Removes listener from list change listeners.  This propagates all the way up to the root selector.
     * @param l 
     */
    private void removeListChangeListener(ActionListener<EntityList.EntityListEvent> l) {
        if (isIndexSelector() && getLeafEntity() instanceof EntityList) {
            EntityList el = (EntityList)getLeafEntity();
            el.removeActionListener(l);
        }
        if (parent != null) {
            parent.removeListChangeListener(l);
        }
    }
    
    
    /**
     * For internal use.  Adds a listener to be notified if items are added/removed from 
     * a list (used for Index property selectors).  This propagates all the way up
     * to the root.
     * @param l 
     */
    private void addListChangeListener(ActionListener<EntityList.EntityListEvent> l) {
        if (isIndexSelector() && getLeafEntity() instanceof EntityList) {
            EntityList el = (EntityList)getLeafEntity();
            el.addActionListener(l);
        }
        if (parent != null) {
            parent.addListChangeListener(l);
        }
    }
    
    /**
     * The property change listener that is added to the the leaf entity/property
     */
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
     * For internal use.  Removes a vetoable change listener from this selector. This 
     * propagates all the way up to the root selector.
     * @param l 
     */
    private void removeVetoablePropertyChangeListener(ActionListener<VetoablePropertyChangeEvent> l) {
        if (root != null) {
            Property prop = property;
            if (prop == null && tags != null) {
                prop = root.getEntity().findProperty(tags);
            }
            if (prop != null) {
                root.getEntity().addVetoablePropertyChangeListener(prop, l);
            }
        } else {
            Entity leafEntity = getLeafEntity();
            Property leafProperty = getLeafProperty();
            if (leafEntity != null && leafProperty != null) {
                leafEntity.getEntity().addVetoablePropertyChangeListener(leafProperty, l);
            }
            parent.addVetoablePropertyChangeListener(l);
        }
    }
    
    /**
     * For internal use.  Adds a vetoable change listener to this selector.  This is used
     * to be notified if a change is made in a parent selector that invalidates our selector's event
     * listeners.
     * 
     * This propagates all the way up to the root selector.
     * @param l 
     */
    private void addVetoablePropertyChangeListener(ActionListener<VetoablePropertyChangeEvent> l) {
        if (root != null) {
            Property prop = property;
            if (prop == null && tags != null) {
                prop = root.getEntity().findProperty(tags);
            }
            if (prop != null) {
                root.getEntity().addVetoablePropertyChangeListener(prop, l);
            }
        } else {
            Entity leafEntity = getLeafEntity();
            Property leafProperty = getLeafProperty();
            if (leafEntity != null && leafProperty != null) {
                leafEntity.getEntity().addVetoablePropertyChangeListener(leafProperty, l);
            }
            parent.addVetoablePropertyChangeListener(l);
        }
        
    }
    
   
    /**
     * The VetoablePropertyChangeListener is attached all to every entity in the "up-line"
     * of this property selector, up to the root.  This is attachment is done lazily when/if
     * a property change listener is added to this property selector.  Its job is to 
     * listen for changes to any "parent" entities that might cause the value of this
     * property to change *other than* the property being changed directly.
     * 
     * I.e. If this property selector is a sub-selector, but its leaf entity is removed
     * from its parent, then that would effectively change the property value, even
     * though the current leaf property was not changed.  Such a change would mean that 
     * we need to remove the property change listener that we previously added to the leaf entity
     * on the leaf property.  Additionally, we need to remove the vetoable property change listener
     * that was added to the up-line.  These must be re-added when the change is complete (e.g.
     * attaching a property change listener to the new leaf entity/property.
     * 
     * We use VetoablePropertyChangeEvent because these are fired *before* the value is changed.  This 
     * listener will add a one-off PropertyChangeEvent listener to run after the change is complete
     * to attach listeners to the new leaf entity/property.
     */
    private ActionListener<VetoablePropertyChangeEvent> vpcl;
    private ActionListener<VetoablePropertyChangeEvent> vpcl() {
        if (vpcl == null) {
            vpcl = evt -> {
                
                if (parent == null) {
                    throw new IllegalStateException("VetoablePropertyChangeEvent should only be added for sub-property selectors, here we have received an event in a root selector");
                }
                
                // A relevant property was changed in one of our parent selectors
                // so this will invalidate our selector.
                // We need to remove all of the listeners.
                
                // Remove vetoable change listener from the parent selector
                // This propagates up to the root
                parent.removeVetoablePropertyChangeListener(vpcl);
                if (listChangeListener != null) {
                    // Remove list change listener from the parent selector
                    // This propagates up to the root.
                    parent.removeListChangeListener(listChangeListener);
                }
                
                
                // Get the value of this selector *before* the change
                // so that we can compare it to the *after* value and
                // fire a property change event if necessary.
                Entity leafEntity = getLeafEntity();
                Property leafProperty = getLeafProperty();
                Object oldValue;
                if (leafEntity != null && leafProperty != null && pcl != null) {
                    leafEntity.getEntity().removePropertyChangeListener(leafProperty, pcl);
                    oldValue = leafEntity.getEntity().get(leafProperty);
                } else {
                    oldValue = null;
                }
                
                // Attach a PropertyChangeListener to the source of this event 
                // which should receive a notification after the change is complete
                Entity source = (Entity)evt.getSource();
                Property prop = evt.getProperty();
                ActionListener<PropertyChangeEvent>  afterChangeListener = new ActionListener<PropertyChangeEvent>() {
                    public void actionPerformed(PropertyChangeEvent afterEvent) {
                        
                        // Remove this one-off listener
                        source.getEntity().removePropertyChangeListener(prop, this);
                        
                        // Re-add vetoable change and list change listeners to the parent
                        parent.addVetoablePropertyChangeListener(vpcl);
                        parent.addListChangeListener(listChangeListener());

                        // Get the new  value
                        Entity newLeafEntity = getLeafEntity();
                        Property newLeafProperty = getLeafProperty();

                        Object newValue;
                        if (newLeafEntity != null && newLeafProperty != null) {
                            
                            // Re-add the property change listener to the leaf entity
                            newLeafEntity.getEntity().addPropertyChangeListener(newLeafProperty, pcl);
                            newValue = newLeafEntity.getEntity().get(newLeafProperty);
                            
                        } else {
                            newValue = null;
                        }
                        if (!Objects.equals(oldValue, newValue)) {
                            // We need to explicitly fire the property change event
                            // if the new value has changed, because otherwise we won't receive the event.
                            listeners.fireActionEvent(new PropertyChangeEvent(newLeafEntity, newLeafProperty, oldValue, newValue));
                        }
                        


                    }
                };
                // Add the one-off listener to fire after the change is complete.
                source.getEntity().addPropertyChangeListener(prop, afterChangeListener);
                
            };
        }
        return vpcl;
    }
    
    /**
     * Adds a change listener on property.
     * @param l The listener to add.
     */
    public void addPropertyChangeListener(ActionListener<PropertyChangeEvent> l) {
        if (root != null) {
            Property prop = property;
            if (prop == null && tags != null) {
                prop = root.getEntity().findProperty(tags);
            }
            if (prop != null) {
                root.getEntity().addPropertyChangeListener(prop, pcl());
            }
        } else {
            Entity leafEntity = getLeafEntity();
            Property leafProperty = getLeafProperty();
            if (leafEntity != null && leafProperty != null) {
                leafEntity.getEntity().addPropertyChangeListener(leafProperty, pcl());
            }
            parent.addVetoablePropertyChangeListener(vpcl());
            parent.addListChangeListener(listChangeListener());
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
                    if (prop == null && tags != null) {
                        prop = root.getEntity().findProperty(tags);
                    }
                    if (prop != null) {
                        root.getEntity().removePropertyChangeListener(prop, pcl);
                    }
                } else {
                    Entity leafEntity = getLeafEntity();
                    Property leafProperty = getLeafProperty();
                    if (leafEntity != null && leafProperty != null) {
                        leafEntity.getEntity().removePropertyChangeListener(leafProperty, pcl());
                    }
                    parent.removeVetoablePropertyChangeListener(vpcl());
                    parent.removeListChangeListener(listChangeListener());
                }
            }
        }
    }
    
    
    
    private Entity root;
    private Tag[] tags;
    private IndexSelector index;
    
    private Property property;
    private PropertySelector parent;
    
    public <T> boolean set(ContentType<T> type, T value) {
        Entity e = null;
        if (parent != null) {
            e = parent.get(ContentType.EntityType, null);
        } else if (root != null) {
            e = root;
        }
        if (e != null) {
            if (isIndexSelector()) {
                if (!(e instanceof EntityList)) {
                    return false;
                }
                if (!type.isEntity()) {
                    return false;
                }
                EntityList el = (EntityList)e;
                return index.set(el, (Entity)value);
                
            }
            Property prop = property;
            if (prop == null) {
                prop = e.getEntity().getEntityType().findProperty(tags);
            }
            if (prop != null) {
                //if (type == ContentType.EntityType && !e.isEntity(prop)) {
                //    return defaultValue;
                //}
                if (!prop.getContentType().canConvertTo(type) && !type.canConvertFrom(prop.getContentType())) {
                    return false;
                }
                e.getEntity().set(prop, type, value);
                return true;
            }
        }
        return false;
    }
    
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
            if (isIndexSelector()) {
                if (!(e instanceof EntityList)) {
                    return null;
                }
                if (!type.isEntity()) {
                    return null;
                }
                EntityList el = (EntityList)e;
                return (T)index.get(el);
                
            }
            Property prop = property;
            if (prop == null) {
                prop = e.getEntity().getEntityType().findProperty(tags);
            }
            if (prop != null) {
                //if (type == ContentType.EntityType && !e.isEntity(prop)) {
                //    return defaultValue;
                //}
                if (!prop.getContentType().canConvertTo(type) && !type.canConvertFrom(prop.getContentType())) {
                    return defaultValue;
                }
                T out = e.getEntity().get(prop, type);
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
    
    private PropertySelector(PropertySelector parent, int index) {
        this.parent = parent;
        this.index = new IndexSelector(index);
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
    
    public PropertySelector createChildSelector(int index, Property property) {
        PropertySelector p1 = new PropertySelector(this, index);
        return new PropertySelector(p1, property);
    }
    
    public PropertySelector createChildSelector(int index, Tag... tags) {
        PropertySelector p1 = new PropertySelector(this, index);
        return new PropertySelector(p1, tags);
    }
    
    /**
     * Alias of {@link #createChildSelector(com.codename1.rad.models.Property) }
     * @param prop
     * @return 
     */
    public PropertySelector child(Property prop) {
        return createChildSelector(prop);
    }
    
    public PropertySelector child(int index, Property prop) {
        return createChildSelector(index, prop);
    }
    
    /**
     * Alias of {@link #createChildSelector(com.codename1.rad.models.Tag...) }
     * @param tags
     * @return 
     */
    public PropertySelector child(Tag... tags) {
        return createChildSelector(tags);
    }
    
    public PropertySelector child(int index, Tag... tags) {
        return createChildSelector(index, tags);
    }
    
    /**
     * Gets the selected property value as text.
     * @param defaultValue The value to return if the property value was null
     * @return The property value as text.
     */
    public String getText(String defaultValue) {
        return get(ContentType.Text, defaultValue);
    }
    
    public boolean setText(String value) {
        return set(ContentType.Text, value);
    }


    
    
    /**
     * Gets the selected property as boolean
     * @param defaultVal The value to return if the property value was null.
     * @return The property as boolean.
     */
    public Boolean getBoolean(boolean defaultVal) {
        return get(ContentType.BooleanType, defaultVal);
    }
    
    public boolean setBoolean(boolean val) {
        return set(ContentType.BooleanType, val);
    }
    
    /**
     * Gets the selected property value as Date.
     * @param defaultVal The value to return if the property value was null
     * @return The property value as date.
     */
    public Date getDate(Date defaultVal) {
        return get(ContentType.DateType, defaultVal);
    }
    
    public boolean setDate(Date val) {
        return set(ContentType.DateType, val);
    }
    
    /**
     * Gets the selected property value as Entity.
     * @param defaultVal The value to return if the property value was null
     * @return The property value as entity.
     */
    public Entity getEntity(Entity defaultVal) {
        return get(ContentType.EntityType, defaultVal);
    }
    
    public boolean setEntity(Entity val) {
        return set(ContentType.EntityType, val);
    }
    
    /**
     * Gets the selected property value as EntityList.
     * @param defaultVal The value to return if the property value was null
     * @return The property value as EntityList.
     */
    public EntityList getEntityList(EntityList defaultVal) {
        return get(ContentType.EntityListType, defaultVal);
    }
    
    public boolean setEntityList(EntityList val) {
        return set(ContentType.EntityListType, val);
    }
    
    /**
     * Gets the selected property value as float.
     * @param defaultVal The value to return if the property value was null
     * @return The property value as float.
     */
    public Float getFloat(float defaultVal) {
        return get(ContentType.FloatType, defaultVal);
    }
    
    public boolean setFloat(float val) {
        return set(ContentType.FloatType, val);
    }
    
    /**
     * Gets the selected property value as double.
     * @param defaultVal The value to return if the property value was null
     * @return The property value as double.
     */
    public Double getDouble(double defaultVal) {
        return get(ContentType.DoubleType, defaultVal);
    }
    
    public boolean setDouble(double val) {
        return set(ContentType.DoubleType, val);
    }
    
    /**
     * Gets the selected property value as int.
     * @param defaultVal The value to return if the property value was null
     * @return The property value as int.
     */
    public Integer getInt(int defaultVal) {
        return get(ContentType.IntegerType, defaultVal);
    }
    
    public boolean setInt(int val) {
        return set(ContentType.IntegerType, val);
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
            if (isIndexSelector()) {
                if (!(e instanceof EntityList)) {
                    return true;
                }
                EntityList el = (EntityList)e;
                return index.isEmpty(el);
                
            }
            Property prop = property;
            if (prop == null) {
                prop = e.getEntity().getEntityType().findProperty(tags);
            }
            if (prop != null) {
                return e.getEntity().isEmpty(prop);
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
            
            if (isIndexSelector()) {
                if (!(e instanceof EntityList)) {
                    return true;
                }
                EntityList el = (EntityList)e;
                return index.isFalsey(el);
                
            }
            
            Property prop = property;
            if (prop == null) {
                prop = e.getEntity().getEntityType().findProperty(tags);
            }
            if (prop != null) {
                return e.getEntity().isFalsey(prop);
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
        if (encodedStr.length() > 20) encodedStr = encodedStr.substring(0, 20) + str.hashCode();
        
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
        if (isIndexSelector()) {
            return null;
        }
        Property prop = property;
        if (prop == null) {
            prop = e.getEntity().getEntityType().findProperty(tags);
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
        
        if (isIndexSelector()) {
            return e instanceof EntityList;
        }
        
        if (e.getEntity().getEntityType().isDynamic()) {
            return true;
        }
        Property prop = property;
        if (prop == null) {
            prop = e.getEntity().getEntityType().findProperty(tags);
        }
        return prop != null;
    }
    
    private boolean isIndexSelector() {
        return index != null;
    }
    
    private class IndexSelector {
        private int index=-1;
        //private String expression;
        
        private IndexSelector(int index){
            this.index = index;
        }
        
        //private IndexSelector(String expression) {
        //    this.expression = expression;
        //}
        
        private boolean isFalsey(EntityList el) {
            return !(index >= 0 && index < el.size());
        }
        
        private boolean isEmpty(EntityList el) {
            return !(index >= 0 && index < el.size());
        }
        
        private Entity get(EntityList el) {
            if (el != null && index >= 0 && index < el.size()) {
                return (Entity) el.get(index);
            }
            return null;
            
        }
        
        private boolean set(EntityList list, Entity value) {
            return false;
        }
        
        
        
    }
    
    public static PropertySelector propertySelector(Entity root, Property prop) {
        return new PropertySelector(root, prop);
    }

    

    public static PropertySelector propertySelector(Entity root, Tag... tags) {
        return new PropertySelector(root, tags);
    }

   
}
