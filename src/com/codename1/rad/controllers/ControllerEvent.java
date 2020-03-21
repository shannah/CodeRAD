/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.controllers;

import com.codename1.ui.events.ActionEvent;

/**
 * A base event for all controller events.  This is the fundamental building block for how information
 * propagates up the controller hierarchy from views or child-controllers.
 * @author shannah
 */
public class ControllerEvent extends ActionEvent {
    
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
    
    public <T extends ControllerEvent> T as(Class<T> type) {
        return (T)this;
    }
    
}
