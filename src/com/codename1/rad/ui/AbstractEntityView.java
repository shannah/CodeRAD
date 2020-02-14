/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.models.Entity;
import com.codename1.rad.models.PropertyChangeEvent;
import com.codename1.ui.Container;
import com.codename1.ui.events.ActionListener;

/**
 *
 * @author shannah
 */
public abstract class AbstractEntityView<T extends Entity> extends Container implements EntityView<T> {
    private T entity;
    
    
    private ActionListener<PropertyChangeEvent> pcl = pce -> {
        update();
    };
    
    public AbstractEntityView(T entity) {
        this.entity = entity;
    }
    
    public void bind() {
        entity.addPropertyChangeListener(pcl);
    }
    public void unbind() {
        entity.removePropertyChangeListener(pcl);
    }

    @Override
    protected void initComponent() {
        super.initComponent();
        bind();
    }

    @Override
    protected void deinitialize() {
        unbind();
        super.deinitialize();
    }
    
    public T getEntity() {
        return entity;
    }
    
    public void setEntity(T entity) {
        this.entity = entity;
    }
    
    
}
