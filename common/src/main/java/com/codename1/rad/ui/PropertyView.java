/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.annotations.RADDoc;
import com.codename1.rad.attributes.UIID;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.models.Property;
import com.codename1.rad.models.PropertySelector;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Editable;
import com.codename1.ui.layouts.BorderLayout;
import java.util.Objects;
import com.codename1.rad.models.Entity;

/**
 * Wrapper around a component that supports binding to a property.
 * @author shannah
 */
public abstract class PropertyView<T extends Component> extends Container implements Editable {
    private T component;
    private Entity entity;
    private FieldNode field;
    private PropertySelector propertySelector;
    private int bindCounter;
    
    public PropertyView(T component, Entity entity, FieldNode field) {
        setLayout(new BorderLayout());
        getStyle().stripMarginAndPadding();
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
    
    

    @Override
    protected void initComponent() {
        super.initComponent();
        bind();
        update();

    }

    @Override
    protected void deinitialize() {
        unbind();
        super.deinitialize();
    }
    
    /**
     * Binds view to the model listeners. {@link #bind() } calls must be balanced with {@link #unbind() }
     * calls.  {@link #bind() } is called inside {@link #initComponent() } (i.e. when the component
     * is added to the UI.  {@link #unbind() } is called inside {@link #deinitialize() } (i.e. when
     * the component is removed from the UI.
     * 
     * If you want to maintain binding when view is offscreen, you can call {@link #bind() } explicitly,
     * but you'll need to also call {@link #unbind() } later on when you no longer need the binding, otherwise
     * you may introduce a memory leak.
     * 
     * Subclasses should implement {@link #bindImpl() } and {@link #unbindImpl() }
     */
    public final void bind() {
        bindCounter++;
        if (bindCounter == 1) {
            bindImpl();
        }
    }
    
    /**
     * Unbinds view from the model listeners. {@link #bind() } calls must be balanced with {@link #unbind() }
     * calls.  {@link #bind() } is called inside {@link #initComponent() } (i.e. when the component
     * is added to the UI.  {@link #unbind() } is called inside {@link #deinitialize() } (i.e. when
     * the component is removed from the UI.
     * 
     * If you want to maintain binding when view is offscreen, you can call {@link #bind() } explicitly,
     * but you'll need to also call {@link #unbind() } later on when you no longer need the binding, otherwise
     * you may introduce a memory leak.
     * 
     * Subclasses should implement {@link #bindImpl() } and {@link #unbindImpl() }.
     */
    public final void unbind() {
        bindCounter--;
        if (bindCounter == 0) {
            unbindImpl();
        }
    }
    
    /**
     * Subclasses should override this method to bind to the model.
     */
    protected abstract void bindImpl();
    
    
    /**
     * Subclasses should override this method to unbind from the model.
     */
    protected abstract void unbindImpl();
    

    @RADDoc(
            generateSubattributeHints = true
    )
    public T getComponent() {
        return component;
    }
    
    public Property getProperty() {
        return getField().getProperty(entity.getEntity().getEntityType());
    }
    
    public PropertySelector getPropertySelector() {
        if (propertySelector == null) {
            propertySelector = field.getPropertySelector(entity);
        }
        return propertySelector;
    }
    
    public FieldNode getField() {
        return field;
    }
    
    
    public Entity getEntity() {
        return entity;
    }
    
    public void update() {
        String oldUiid = getComponent().getUIID();
        UIID newUIID = (UIID)getField().findAttribute(UIID.class);
        if (newUIID != null) {
            String newUIIDStr = newUIID.getValue(getEntity());
            if (newUIIDStr != null && !Objects.equals(newUIIDStr, oldUiid)) {
                getComponent().setUIID(newUIIDStr);
            }
        }
    }
    public abstract void commit();
    
}
