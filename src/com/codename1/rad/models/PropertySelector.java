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
    
    public PropertySelector(Entity root, Tag... tags) {
        this.root = root;
        this.tags = tags;
        
    }
    
    public PropertySelector(Entity root, Property property) {
        this.root = root;
        this.property = property;
    }
    
    public PropertySelector(PropertySelector parent, Tag... tags) {
        this.parent = parent;
        this.tags = tags;
    }
    
    public PropertySelector(PropertySelector parent, Property property) {
        this.parent = parent;
        this.property = property;
    }
    
    public PropertySelector createChildSelector(Property property) {
        return new PropertySelector(this, property);
    }
    
    public PropertySelector createChildSelector(Tag... tags) {
        return new PropertySelector(this, tags);
    }
    
    public PropertySelector child(Property prop) {
        return createChildSelector(prop);
    }
    
    public PropertySelector child(Tag... tags) {
        return createChildSelector(tags);
    }
    
    public String getText(String defaultValue) {
        return get(ContentType.Text, defaultValue);
    }
    
    public Boolean getBoolean(boolean defaultVal) {
        return get(ContentType.BooleanType, defaultVal);
    }
    
    public Date getDate(Date defaultVal) {
        return get(ContentType.DateType, defaultVal);
    }
    
    public Entity getEntity(Entity defaultVal) {
        return get(ContentType.EntityType, defaultVal);
    }
    
    public EntityList getEntityList(EntityList defaultVal) {
        return get(ContentType.EntityListType, defaultVal);
    }
    
    public Float getFloat(float defaultVal) {
        return get(ContentType.FloatType, defaultVal);
    }
    
    public Double getDouble(double defaultVal) {
        return get(ContentType.DoubleType, defaultVal);
    }
    
    public Integer getInt(int defaultVal) {
        return get(ContentType.IntegerType, defaultVal);
    }
    
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
    
     public Image createImageToStorage(EncodedImage placeholder, URLImage.ImageAdapter adapter) {
        return createImageToStorage(placeholder, null, adapter);
    }
    
   
    
    public Image createImageToStorage(EncodedImage placeholder) {
        return createImageToStorage(placeholder, null, null);
    }
    
    
    
    public Image createImageToStorage(EncodedImage placeholder, String storageFile) {
        return createImageToStorage(placeholder, storageFile, null);
    }
    
    
    
    
    
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
    public Image createImageToFile(EncodedImage placeholder, URLImage.ImageAdapter adapter) {
        return createImageToFile(placeholder, null, adapter);
    }
    
   
    
    public Image createImageToFile(EncodedImage placeholder) {
        return createImageToFile(placeholder, (String)null);
    }
    
   
    
    public Image createImageToFile(EncodedImage placeholder, String file) {
        
    
        return createImageToFile(placeholder, file, null);
    }
    
    
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
    
    public Entity getLeafEntity() {
        Entity e = root;
        if (e == null) {
            if (parent != null) {
                e = parent.get(ContentType.EntityType, null);
            }
        }
        return e;
    }
    
}
