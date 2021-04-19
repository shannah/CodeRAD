/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.controllers;

import com.codename1.ui.CN;
import com.codename1.ui.Component;
import com.codename1.ui.Form;
import com.codename1.ui.events.ActionEvent;
import com.codename1.util.AsyncResource;
import com.codename1.util.SuccessCallback;

/**
 * A base event for all controller events.  This is the fundamental building block for how information
 * propagates up the controller hierarchy from views or child-controllers.
 * @author shannah
 */
public class ControllerEvent extends ActionEvent {

    private AsyncResource asyncResource;
    
    /**
     * Creates a new controller event.
     */
    public ControllerEvent() {
        super(null);
    }
    
    /**
     * Creates a new controller event with a specified source.
     * @param source 
     */
    public ControllerEvent(Object source) {
        super(source);
    }
    
    /**
     * Returns this event as the given type - or null if it is not that type.
     * @param <T>
     * @param type The type of event to convert it to.
     * @return This event casted to the given type - or null if it cannot be.
     */
    public <T extends ControllerEvent> T as(Class<T> type) {
        if (type.isAssignableFrom(this.getClass())) {
            return (T)this;
        }
        return null;
    }

    public <T extends ControllerEvent> boolean as(Class<T> type, SuccessCallback<T> callback) {
        T cevt = as(type);
        if (cevt == null) return false;
        callback.onSucess(cevt);
        return true;
    }

    public <T extends AsyncResource> T getAsyncResource(Class<T> type) {
        if (!isConsumed() || asyncResource == null || !type.isAssignableFrom(asyncResource.getClass())) {
           return null;
        }
        return (T) asyncResource;
    }

    public void setAsyncResource(AsyncResource task) {
        asyncResource = task;
    }

    public AsyncResource getAsyncResource() {
        return asyncResource;
    }

    public ViewController getViewController() {
        Component cmp = getComponent();
        if (cmp == null) {
            return null;
        }
        return ViewController.getViewController(cmp);
    }

    public FormController getFormController() {
        ViewController ctl = getViewController();
        if (ctl == null) return null;
        return ctl.getFormController();
    }

    public AppSectionController getAppSectionController() {
        ViewController ctl = getViewController();
        if (ctl == null) return null;
        return ctl.getSectionController();
    }

    public ApplicationController getApplicationController() {
        ViewController ctl = getViewController();
        if (ctl == null) {
            Form f = CN.getCurrentForm();
            if (f == null) {
                return null;
            }
            ctl = ViewController.getViewController(f);

        }

        if (ctl == null) return null;
        return ctl.getApplicationController();
    }

    
}
