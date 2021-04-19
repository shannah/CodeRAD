/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.controllers;

import com.codename1.ui.Component;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.ActionSource;
import com.codename1.ui.util.EventDispatcher;

/**
 * A utility class that can provide action support for a class.  This is mainly a thin wrapper around {@link EventDispatcher}, but it also includes some static utility methods for event dispatch which are used
 * for dispatching action events in CodeRAD.
 * @author shannah
 */
public class ActionSupport<T extends ActionEvent> {
    private EventDispatcher listeners = new EventDispatcher();
    public void addActionListener(ActionListener<T> l) {
        listeners.addListener(l);
    }
    
    public void removeActionListener(ActionListener<T> l) {
        listeners.removeListener(l);
    }
    
    public void fireActionEvent(T evt) {
        listeners.fireActionEvent(evt);
    }
    
    public static void addActionListener(Component cmp, ActionListener l) {
        if (cmp instanceof EventProducer) {
            ((EventProducer)cmp).getActionSupport().addActionListener(l);
            return;
        }
        if (cmp instanceof ActionSource) {
            ((ActionSource)cmp).addActionListener(l);
        }
    }
    
    public static void removeActionListener(Component cmp, ActionListener l) {
        if (cmp instanceof EventProducer) {
            ((EventProducer)cmp).getActionSupport().removeActionListener(l);
            return;
        }
        if (cmp instanceof ActionSource) {
            ((ActionSource)cmp).removeActionListener(l);
        }
    }
    
    /**
     * Dispatches an event, first using the source's action support,
     * if it implements EventProducer interface.  If event not consumed,
     * it will find the nearest ViewController and dispatch the event 
     * up the controller chain. Latter also requires that event is ControllerEvent
     * @param evt 
     */
    public static void dispatchEvent(ActionEvent evt) {
        if (evt.isConsumed()) {
            return;
        }
        
        Object source = evt.getSource();
        if (source instanceof EventProducer) {
            
            EventProducer ep = (EventProducer)source;
            ep.getActionSupport().fireActionEvent(evt);
        }
        if (evt.isConsumed()) {
            return;
        }
        if (evt instanceof ControllerEvent) {
            if (source instanceof Component) {
                Component cmp = (Component)source;
                ViewController controller = ViewController.getViewController(cmp);
                if (controller != null) {
                    controller.dispatchEvent((ControllerEvent)evt);
                }

            } else if (source instanceof Controller) {
                Controller ctrl = (Controller)source;
                ctrl.dispatchEvent((ControllerEvent)evt);
            }
        }
    }

    public static <T extends ActionEvent>  T as(ActionEvent e, Class<T> type) {
        if (type.isAssignableFrom(e.getClass())) {
            return (T)e;
        }
        return null;
    }
    
    
    
    
}
