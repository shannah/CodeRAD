/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.controllers;

import com.codename1.ui.events.ActionListener;
import com.codename1.ui.util.EventDispatcher;

/**
 * A base class for all Controller classes.  
 * @author shannah
 */
public class Controller implements ActionListener<ControllerEvent> {
    
    private Controller parent;
    private EventDispatcher listeners = new EventDispatcher();
    {
        listeners.addListener(this);
    }
    
    /**
     * Creates a controller with a given parent controller.
     * @param parent The parent controller of this controller.  
     */
    public Controller(Controller parent) {
        this.parent = parent;
    }
    
    /**
     * Adds a controller listener.  Controller listeners are notified of Controller
     * events that are dispatched using {@link #dispatchEvent(com.codename1.ui.controllers.ControllerEvent) }.
     * 
     * This is the means by which information propagates up the controller hierarchy from views and 
     * sub-controllers.
     * @param l 
     */
    public void addEventListener(ActionListener<ControllerEvent> l) {
        listeners.addListener(l);
    }
    
    public void removeEventListener(ActionListener<ControllerEvent> l) {
        listeners.removeListener(l);
    }
    
    /**
     * Dispatches an event first to listeners of this controller, and then, if not consumed yet,
     * to listeners of the parent controller.  The event will propagate up the controller hierarchy
     * until it is either consumed, or until it reaches the top of the hierarchy (i.e. parent == null).
     * @param evt The event to be dispatched.
     */
    protected void dispatchEvent(ControllerEvent evt) {
        listeners.fireActionEvent(evt);
        if (!evt.isConsumed() && parent != null) {
            parent.dispatchEvent(evt);
        }
    }
    
   
    /**
     * Gets the parent controller for this controller.  All controllers except for the ApplicationController
     * should have a parent.  This controller hierarchy is used to keep a navigation history also.  For example,
     * the parent controller of a FormController is the "previous" form.  Hence the back command of a FormController
     * will go "back" to the parent controller's form.
     * @return 
     */
    public Controller getParent() {
        return parent;
    }
    
    /**
     * Should be overridden by subclasses to handle ControllerEvent s.  This is the
     * cornerstone of how information is passed "up" the controller hierarchy, from the view.  The view
     * or sub-controller, dispatches an event.  The event propagates up the controller hierarchy, until
     * a controller consumes the event.
     * @param evt 
     */
    @Override
    public void actionPerformed(ControllerEvent evt) {
        
    }
    
    /**
     * Gets the FormController for the current controller context. This will walk up the 
     * controller hierarchy (i.e. {@link #getParent()} until it finds an instance of {@link FormController}.
     * If none is found, it returns null.
     * @return The FormController, or null if none found.
     */
    public FormController getFormController() {
        if (this instanceof FormController) {
            return (FormController)this;
        }
        if (parent != null) {
            return parent.getFormController();
        }
        return null;
    }
    
    /**
     * Gets the ApplicationController for the current controller context.  This will walk up
     * the controller hierarchy (i.e. {@link #getParent()} until it finds an instance of {@link ApplicationController}.
     * @return The ApplicationController or null if none found.
     */
    public ApplicationController getApplicationController() {
        if (this instanceof ApplicationController) {
            return (ApplicationController)this;
        }
        if (parent != null) {
            return parent.getApplicationController();
        }
        return null;
    }
    
}
