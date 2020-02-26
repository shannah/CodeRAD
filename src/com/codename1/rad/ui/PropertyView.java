/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.Property;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Editable;
import com.codename1.ui.layouts.BorderLayout;

/**
 * Wrapper around a component that supports binding to a property.
 * @author shannah
 */
public abstract class PropertyView<T extends Component> extends Container implements Editable {
    private T component;
    private Entity entity;
    private FieldNode field;
    
    public PropertyView(T component, Entity entity, FieldNode field) {
        setLayout(new BorderLayout());
        this.component = component;
        setEditingDelegate(component);
        this.entity = entity;
        this.field = field;
        add(BorderLayout.CENTER, component);
        update();
    }

   
    @Override
    public void setNextFocusLeft(Component nextFocusLeft) {
        component.setNextFocusLeft(nextFocusLeft);
    }

    @Override
    public void setNextFocusRight(Component nextFocusRight) {
        component.setNextFocusRight(nextFocusRight);
    }
    /*
    @Override
    public boolean isFocusable() {
        if (component == null) {
            return false;
        }
        return component.isFocusable();
    }

    @Override
    public void setFocus(boolean focused) {
        component.setFocus(focused);
    }

    @Override
    public void addFocusListener(FocusListener l) {
        component.addFocusListener(l);
    }

    @Override
    public void removeFocusListener(FocusListener l) {
        component.removeFocusListener(l);
    }
    
    
    
    
    */
    
    
    
    
    
    
    
    
    

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
    
    
    public abstract void bind();
    
    public abstract void unbind();
    
    
    public T getComponent() {
        return component;
    }
    
    public Property getProperty() {
        return getField().getProperty(entity.getEntityType());
    }
    
    public FieldNode getField() {
        return field;
    }
    
    
    public Entity getEntity() {
        return entity;
    }
    
    public abstract void update();
    public abstract void commit();
    
}
