/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.attributes.ViewControllerAttribute;
import com.codename1.rad.controllers.ViewController;
import com.codename1.rad.models.*;
import com.codename1.rad.nodes.Node;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.events.ActionListener;
import java.util.Observer;

import static com.codename1.ui.ComponentSelector.$;

/**
 * A base class for a view that can bind to an entity.  Sublasses just need toi implement {@link #update() }.
 * @author shannah
 */
public abstract class AbstractEntityView<T extends Entity> extends Container implements EntityView<T> {
    private T entity;
    private int bindCount;
    private boolean bindOnPropertyChangeEvents = true;
    private Node node;
    private boolean activated;
    
    
    
    private ActionListener<PropertyChangeEvent> pcl = pce -> {
        update();
    };
    // Switching to using Observer pattern instead of property change listeners
    // to reduce noise.
    private Observer observer = (o, arg) -> {
        update();
    };
    
    public AbstractEntityView(T entity) {
        this(entity, null);
    }

    public AbstractEntityView(T entity, Node node) {
        if (entity == null) {
            throw new IllegalArgumentException("AbstractEntityView requires non-null entity, but received null");
        }
        this.entity = entity;
        this.node = node;
        if (node != null) {
            ViewController ctl = (ViewController)node.findAttributeValue(ViewControllerAttribute.class, ViewController.class);
            if (ctl != null && ViewController.getViewController(this) != ctl) {
                ctl.startController();
                ctl.setView(this);
            }
        }


    }
    
    /**
     * Set whether to bind to the model on PropertyChangeEvents.  Default value is {@literal true},
     * which results in very eager updates.  Setting this value to {@literal false} will cause
     * the binding to use the Observer pattern so that {@link #update() } will only be triggered
     * in response to a {@link Entity#notifyObservers() } call.
     * 
     * 
     * 
     * @param bindOnPcl {@literal true} to trigger {@link #update() } in response
     * to PropertyChangeEvents on the mode.  {@literal false} to trigger {@link #update() }
     * in response to {@link Entity#notifyObservers() }
     * 
     * @throws IllegalStateException If this method is called while the view is already bound.
     */
    public void setBindOnPropertyChangeEvents(boolean bindOnPcl) {
        if (bindCount > 0) {
            throw new IllegalStateException("Cannot change binding type of EntityView while it is already bound.");
        }
        bindOnPropertyChangeEvents = bindOnPcl;
        
    }
    
    
    /**
     * Checks whether this view is set to bind on PropertyChangeEvents.  
     * @return True if the view is bound on PropertyChangeEvents.
     * @see #setBindOnPropertyChangeEvents(boolean) 
     */
    public boolean isBindOnPropertyChangeEvents() {
        return bindOnPropertyChangeEvents;
    }
    
  
    /**
     * Binds listeners to model.  Subclasses should override {@link #bindImpl() }
     */
    public final void bind() {
        //entity.addPropertyChangeListener(pcl);
        bindCount++;
        if (bindCount == 1) {
            if (bindOnPropertyChangeEvents) {
                entity.getEntity().addPropertyChangeListener(pcl);
            } else {
                entity.getEntity().addObserver(observer);
            }
            bindImpl();
        }
    }
    
    /**
     * To be implemented by subclasses to register listeners on the model.
     */
    protected void bindImpl() {
        
    }
    
    /**
     * Unbinds listeners from the model.  Subclasses should override {@link #unbindImpl()}
     */
    public final void unbind() {
        //entity.removePropertyChangeListener(pcl);
        bindCount--;
        if (bindCount < 0) {
            throw new IllegalStateException("Unbalanced bind() to unbind() calls on "+this+". Bind count is "+bindCount);
        }
        if (bindCount == 0) {
            unbindImpl();
            if (bindOnPropertyChangeEvents) {
                entity.getEntity().removePropertyChangeListener(pcl);
            } else {
                
                entity.getEntity().deleteObserver(observer);
            }
           
        }
    }

    /**
     * Should be overridden by subclasses to unregister listeners from the model.
     */
    protected void unbindImpl() {
        
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
    
    protected Property findProperty(Tag... tags) {
        return getEntity().getEntity().getEntityType().findProperty(tags);
    }

    @Override
    public Node getViewNode() {
        return node;
    }


    
    public <V> V getParam(ViewProperty<V> property, V defaultValue) {
        return (V)node.getViewParameter(property, ViewPropertyParameter.createValueParam(property, defaultValue)).getValue(entity);
    }

}
