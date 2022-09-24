/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.attributes.ViewControllerAttribute;
import com.codename1.rad.controllers.ViewController;
import com.codename1.rad.models.*;
import com.codename1.rad.nodes.Node;
import com.codename1.ui.CN;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.events.ActionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static com.codename1.ui.ComponentSelector.$;

/**
 * A base class for a view that can bind to an entity.  Sublasses just need toi implement {@link #update() }.
 * @author shannah
 */
public abstract class AbstractEntityView<T extends Entity> extends Container implements EntityView<T>, Activatable, Bindable {
    //private T entity;
    private int bindCount;
    private boolean bindOnPropertyChangeEvents = true;
    //private Node node;
    private ViewContext<T> context;
    private List<Runnable> updateListeners;
    private java.util.List<Runnable> bindListeners;
    private java.util.List<Runnable> unbindListeners;
    
    
    
    private ActionListener<PropertyChangeEvent> pcl = pce -> {
        update();
        if (updateListeners != null && !updateListeners.isEmpty()) {
            for (Runnable r : updateListeners) {
                r.run();
            }
        }
    };
    // Switching to using Observer pattern instead of property change listeners
    // to reduce noise.
    private Observer observer = (o, arg) -> {
        if (!CN.isEdt()) {
            CN.callSerially(()-> {
                update();
                if (updateListeners != null && !updateListeners.isEmpty()) {
                    for (Runnable r : updateListeners) {
                        r.run();
                    }
                }
            });
            return;
        }
        update();
        if (updateListeners != null && !updateListeners.isEmpty()) {
            for (Runnable r : updateListeners) {
                r.run();
            }
        }
    };

    public void addUpdateListener(Runnable l) {
        if (updateListeners == null) {
            updateListeners = new ArrayList<>();
        }
        updateListeners.add(l);
    }

    public void removeUpdateListener(Runnable l) {
        if (updateListeners == null) return;
        updateListeners.remove(l);
    }

    public AbstractEntityView(@Inject ViewContext<T> context) {
        this.context = context;
        if (context.getEntityView() == null) {
            context.setEntityView(this);
        }
        T entity = context.getEntity();
        if (entity == null) {
            throw new IllegalArgumentException("AbstractEntityView requires non-null entity, but received null");
        }

        if (context.getNode() != null) {
            ViewController ctl = (ViewController)context.getNode().findAttributeValue(ViewControllerAttribute.class, ViewController.class);
            if (ctl != null && ViewController.getViewController(this) != ctl) {
                ctl.startController();
                if (context.getController() == null) {
                    context.setController(ctl);
                }
                ctl.setView(this);
            }
        }
    }

    /**
     * @deprecated Use use {@link #AbstractEntityView(ViewContext)}
     * @param entity
     */
    public AbstractEntityView(@Inject T entity) {
        this(entity, null);
    }

    /**
     * @deprecated Use {@link #AbstractEntityView(ViewContext)}
     * @param entity
     * @param node
     */
    public AbstractEntityView(@Inject T entity, @Inject Node node) {
        this(new ViewContext<T>(null, entity, node));
    }
    
    /**
     * Set whether to bind to the model on PropertyChangeEvents.  Default value is {@literal true},
     * which results in very eager updates.  Setting this value to {@literal false} will cause
     * the binding to use the Observer pattern so that {@link #update() } will only be triggered
     * in response to a {@link BaseEntity#notifyObservers() } call.
     * 
     * 
     * 
     * @param bindOnPcl {@literal true} to trigger {@link #update() } in response
     * to PropertyChangeEvents on the mode.  {@literal false} to trigger {@link #update() }
     * in response to {@link BaseEntity#notifyObservers() }
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
                context.getEntity().getEntity().addPropertyChangeListener(pcl);
            }
            context.getEntity().getEntity().addObserver(observer);
            if (bindListeners != null && !bindListeners.isEmpty()) {
                for (Runnable r : bindListeners) r.run();
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
            if (unbindListeners != null && !unbindListeners.isEmpty()) {
                for (Runnable r : unbindListeners) r.run();
            }
            if (bindOnPropertyChangeEvents) {
                context.getEntity().getEntity().removePropertyChangeListener(pcl);
            }
                
            context.getEntity().getEntity().deleteObserver(observer);

           
        }
    }
    public void addBindListener(Runnable r) {
        if (bindListeners == null) bindListeners = new java.util.ArrayList<>();
        bindListeners.add(r);
    }

    public void addUnbindListener(Runnable r) {
        if (unbindListeners == null) unbindListeners = new java.util.ArrayList<>();
        unbindListeners.add(r);
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
        if (getEntity() instanceof Observable) {
            observer.update((Observable) getEntity(), null);
        } else {
            observer.update(null, null);
        }
    }

    @Override
    protected void deinitialize() {
        unbind();
        super.deinitialize();
    }
    
    public T getEntity() {
        return context.getEntity();
    }
    
    public void setEntity(T entity) {
        context.setEntity(entity);
    }
    
    protected Property findProperty(Tag... tags) {
        return getEntity().getEntity().getEntityType().findProperty(tags);
    }

    @Override
    public Node getViewNode() {
        return context.getNode();
    }


    
    public <V> V getParam(ViewProperty<V> property, V defaultValue) {
        return (V)context.getNode().getViewParameter(property, ViewPropertyParameter.createValueParam(property, defaultValue)).getValue(getEntity());
    }


    @Override
    public void activate() {
        if (context.getController() == null) {
            context.setController(ViewController.getViewController(this));
        }
    }

    public ViewContext<T> getContext() {
        return context;
    }


}
