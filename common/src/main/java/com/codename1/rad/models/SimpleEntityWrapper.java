package com.codename1.rad.models;

import com.codename1.ui.EncodedImage;
import com.codename1.ui.Image;
import com.codename1.ui.URLImage;
import com.codename1.ui.events.ActionListener;
import com.codename1.util.SuccessCallback;
import java.util.Date;
import java.util.Map;

public class SimpleEntityWrapper implements Entity {
    protected final Entity entity;

    protected SimpleEntityWrapper(Entity entity) {
        this.entity = entity;
    }

    @Override
    public BaseEntity getEntity() {
        return entity.getEntity();
    }

    @Override
    public void add(Property prop, Entity value) {
        entity.add(prop, value);
    }

    @Override
    public void addPropertyChangeListener(Property property, ActionListener<PropertyChangeEvent> l) {
        entity.addPropertyChangeListener(property, l);
    }

    @Override
    public void addPropertyChangeListener(ActionListener<PropertyChangeEvent> l) {
        entity.addPropertyChangeListener(l);
    }

    @Override
    public void addVetoablePropertyChangeListener(Property property, ActionListener<VetoablePropertyChangeEvent> l) {
        entity.addVetoablePropertyChangeListener(l);
    }

    @Override
    public void addVetoablePropertyChangeListener(ActionListener<VetoablePropertyChangeEvent> l) {
        entity.addVetoablePropertyChangeListener(l);
    }

    @Override
    public <T> boolean as(Class<T> cls, SuccessCallback<T> callback) {
        return entity.as(cls, callback);
    }

    @Override
    public Image createImageToFile(Tag tag, EncodedImage placeholder, URLImage.ImageAdapter adapter) {
        return entity.createImageToFile(tag, placeholder, adapter);
    }

    @Override
    public Image createImageToFile(Property prop, EncodedImage placeholder, URLImage.ImageAdapter adapter) {
        return entity.createImageToFile(prop, placeholder, adapter);
    }

    @Override
    public Image createImageToFile(Tag tag, EncodedImage placeholder) {
        return entity.createImageToFile(tag, placeholder);
    }

    @Override
    public Image createImageToFile(Property prop, EncodedImage placeholder) {
        return entity.createImageToFile(prop, placeholder);
    }

    @Override
    public Image createImageToFile(Tag tag, EncodedImage placeholder, String file) {
        return entity.createImageToFile(tag, placeholder, file);
    }

    @Override
    public Image createImageToFile(Property prop, EncodedImage placeholder, String file) {
        return entity.createImageToFile(prop, placeholder, file);
    }

    @Override
    public Image createImageToFile(Tag tag, EncodedImage placeholder, String file, URLImage.ImageAdapter adapter) {
        return entity.createImageToFile(tag, placeholder, file, adapter);
    }

    @Override
    public Image createImageToFile(Property prop, EncodedImage placeholder, String file, URLImage.ImageAdapter adapter) {
        return entity.createImageToFile(prop, placeholder, file, adapter);
    }

    @Override
    public Image createImageToStorage(Tag tag, EncodedImage placeholder, URLImage.ImageAdapter adapter) {
        return entity.createImageToStorage(tag, placeholder, adapter);
    }

    @Override
    public Image createImageToStorage(Property prop, EncodedImage placeholder, URLImage.ImageAdapter adapter) {
        return entity.createImageToStorage(prop, placeholder, adapter);
    }

    @Override
    public Image createImageToStorage(Tag tag, EncodedImage placeholder) {
        return entity.createImageToFile(tag, placeholder);
    }

    @Override
    public Image createImageToStorage(Property prop, EncodedImage placeholder) {
        return entity.createImageToStorage(prop, placeholder);
    }

    @Override
    public Image createImageToStorage(Tag tag, EncodedImage placeholder, String storageFile) {
        return entity.createImageToStorage(tag, placeholder, storageFile);
    }

    @Override
    public Image createImageToStorage(Property prop, EncodedImage placeholder, String storageFile) {
        return entity.createImageToStorage(prop, placeholder, storageFile);
    }

    @Override
    public Image createImageToStorage(Tag tag, EncodedImage placeholder, String storageFile, URLImage.ImageAdapter adapter) {
        return entity.createImageToStorage(tag, placeholder, storageFile, adapter);
    }

    @Override
    public Image createImageToStorage(Property prop, EncodedImage placeholder, String storageFile, URLImage.ImageAdapter adapter) {
        return entity.createImageToStorage(prop, placeholder, storageFile, adapter);
    }

    @Override
    public Property findProperty(Tag... tags) {
        return entity.findProperty(tags);
    }

    @Override
    public Object get(Object key) {
        return entity.get(key);
    }

    @Override
    public <T> T get(Property<T> prop) {
        return entity.get(prop);
    }

    @Override
    public <V> V get(Property prop, ContentType<V> contentType) {
        return entity.get(prop, contentType);
    }

    @Override
    public Aggregate getAggregate() {
        return entity.getAggregate();
    }

    @Override
    public <T> T getAs(Property prop, Class<T> cls) {
        return entity.getAs(prop, cls);
    }

    @Override
    public <T> T getAs(Tag tag, Class<T> cls) {
        return entity.getAs(tag, cls);
    }

    @Override
    public <T> boolean getAs(Property prop, Class<T> cls, SuccessCallback<T> callback) {
        return entity.getAs(prop, cls, callback);
    }

    @Override
    public <T> boolean getAs(Tag tag, Class<T> cls, SuccessCallback<T> callback) {
        return entity.getAs(tag, cls, callback);
    }

    @Override
    public Boolean getBoolean(Property prop) {
        return entity.getBoolean(prop);
    }

    @Override
    public Boolean getBoolean(Tag... tags) {
        return entity.getBoolean(tags);
    }

    @Override
    public Date getDate(Property prop) {
        return entity.getDate(prop);
    }

    @Override
    public Date getDate(Tag... tags) {
        return entity.getDate(tags);
    }

    @Override
    public Double getDouble(Property prop) {
        return entity.getDouble(prop);
    }

    @Override
    public Double getDouble(Tag... tags) {
        return entity.getDouble(tags);
    }

    @Override
    public Entity getEntity(Tag... tag) {
        return entity.getEntity(tag);
    }

    @Override
    public Entity getEntity(Property prop) {
        return entity.getEntity(prop);
    }

    @Override
    public EntityList getEntityList(Tag... tag) {
        return entity.getEntityList(tag);
    }

    @Override
    public EntityList getEntityList(Property prop) {
        return entity.getEntityList(prop);
    }

    @Override
    public EntityList getEntityListNonNull(Property prop) {
        return entity.getEntityListNonNull(prop);
    }

    @Override
    public Entity getEntityNonNull(Property prop) {
        return entity.getEntityNonNull(prop);
    }

    @Override
    public EntityType getEntityType() {
        return entity.getEntityType();
    }

    @Override
    public Integer getInt(Property prop) {
        return entity.getInt(prop);
    }

    @Override
    public Integer getInt(Tag... tags) {
        return entity.getInt(tags);
    }

    @Override
    public Long getLong(Property prop) {
        return entity.getLong(prop);
    }

    @Override
    public Long getLong(Tag... tags) {
        return entity.getLong(tags);
    }

    @Override
    public String getText(Property prop) {
        return entity.getText(prop);
    }

    @Override
    public String getText(Tag... tags) {
        return entity.getText(tags);
    }


    @Override
    public boolean hasPropertyChangeListeners(Property prop) {
        return entity.hasPropertyChangeListeners(prop);
    }

    @Override
    public boolean hasVetoablePropertyChangeListeners(Property prop) {
        return entity.hasVetoablePropertyChangeListeners(prop);
    }

    @Override
    public boolean isEmpty(Property prop) {
        return entity.isEmpty(prop);
    }

    @Override
    public boolean isEmpty(Tag tag) {
        return entity.isEmpty(tag);
    }

    @Override
    public boolean isEntity(Property prop) {
        return entity.isEntity(prop);
    }

    @Override
    public boolean isEntity(Tag tag) {
        return entity.isEntity(tag);
    }

    @Override
    public boolean isFalsey(Property prop) {
        return entity.isFalsey(prop);
    }

    @Override
    public boolean isFalsey(Tag tag) {
        return entity.isFalsey(tag);
    }

    @Override
    public void removePropertyChangeListener(Property property, ActionListener<PropertyChangeEvent> l) {
        entity.removePropertyChangeListener(property, l);
    }

    @Override
    public void removePropertyChangeListener(ActionListener<PropertyChangeEvent> l) {
        entity.removePropertyChangeListener(l);
    }

    @Override
    public void removeVetoablePropertyChangeListener(Property property, ActionListener<VetoablePropertyChangeEvent> l) {
        entity.removeVetoablePropertyChangeListener(property, l);
    }

    @Override
    public void removeVetoablePropertyChangeListener(ActionListener<VetoablePropertyChangeEvent> l) {
        entity.removeVetoablePropertyChangeListener(l);
    }

    @Override
    public void set(Object key, Object value) {
        entity.set(key, value);
    }

    @Override
    public void set(Property prop, ContentType inputType, Object val) {
        entity.set(prop, inputType, val);
    }

    @Override
    public boolean set(Tag tag, ContentType inputType, Object val) {
        return entity.set(tag, inputType, val);
    }

    @Override
    public boolean set(ContentType inputType, Object val, Tag... tags) {
        return entity.set(inputType, val, tags);
    }

    @Override
    public void setBoolean(Property prop, boolean val) {
        entity.setBoolean(prop, val);
    }

    @Override
    public boolean setBoolean(Tag tag, boolean val) {
        return entity.setBoolean(tag, val);
    }

    @Override
    public boolean setBoolean(boolean val, Tag... tags) {
        return entity.setBoolean(val, tags);
    }

    @Override
    public void setChanged(Property prop, boolean firePropertyChange) {
        entity.setChanged(prop, firePropertyChange);
    }

    @Override
    public void setDate(Property prop, Date date) {
        entity.setDate(prop, date);
    }

    @Override
    public boolean setDate(Tag tag, Date date) {
        return entity.setDate(tag, date);
    }

    @Override
    public boolean setDate(Date date, Tag... tags) {
        return entity.setDate(date, tags);
    }

    @Override
    public void setDouble(Property prop, double val) {
        entity.setDouble(prop, val);
    }

    @Override
    public boolean setDouble(Tag tag, double val) {
        return entity.setDouble(tag, val);
    }

    @Override
    public boolean setDouble(double val, Tag... tags) {
        return entity.setDouble(val, tags);
    }

    @Override
    public void setEntity(Property prop, Entity e) {
        entity.setEntity(prop, e);
    }

    @Override
    public boolean setEntity(Tag tag, Entity e) {
        return entity.setEntity(tag, e);
    }

    @Override
    public boolean setEntity(Entity e, Tag... tags) {
        return entity.setEntity(e, tags);
    }

    @Override
    public void setEntityType(EntityType entityType) {
        entity.setEntityType(entityType);
    }

    @Override
    public void setFloat(Property prop, float val) {
        entity.setFloat(prop, val);
    }

    @Override
    public boolean setFloat(Tag tag, float val) {
        return entity.setFloat(tag, val);
    }

    @Override
    public boolean setFloat(float val, Tag... tags) {
        return entity.setFloat(val, tags);
    }

    @Override
    public void setInt(Property prop, int val) {
        entity.setInt(prop, val);
    }

    @Override
    public boolean setInt(Tag tag, int val) {
        return entity.setInt(tag, val);
    }

    @Override
    public boolean setInt(int val, Tag... tags) {
        return entity.setInt(val, tags);
    }

    @Override
    public void setLong(Property prop, long val) {
        entity.setLong(prop, val);
    }

    @Override
    public boolean setLong(Tag tag, long val) {
        return entity.setLong(tag, val);
    }

    @Override
    public boolean setLong(long val, Tag... tags) {
        return entity.setLong(val, tags);
    }

    @Override
    public void setText(Property prop, String text) {
        entity.setText(prop, text);
    }

    @Override
    public boolean setText(Tag tag, String text) {
        return entity.setText(tag, text);
    }

    @Override
    public boolean setText(String text, Tag... tags) {
        return entity.setText(text, tags);
    }

    @Override
    public Map toMap(Object... keys) {
        return entity.toMap(keys);
    }

    
}
