/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.controllers;

import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;

/**
 * A controller for handling application logic related to a Form.
 * @author shannah
 */
public class FormController extends ViewController {
    
    /**
     * Base class for FormController events.
     */
    public static class FormControllerEvent extends ControllerEvent {}
    
    /**
     * Event that can be fired by any view to request that the current form go back 
     * to the previous form.
     */
    public static class FormBackEvent extends FormControllerEvent {}
    
    public FormController(Controller parent) {
        super(parent);
    }
    
    /**
     * Sets the form associated with this controller.  This will automatically set
     * the back command of the form to call back() on this controller.
     * @param form 
     */
    public void setView(Form form) {
        super.setView(form);
        if (getParent() instanceof FormController) {
            form.setBackCommand(new Command("") {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    back();
                }
                
            });
            Toolbar tb = form.getToolbar();
            if (tb != null) {
                tb.addMaterialCommandToLeftBar("", FontImage.MATERIAL_ARROW_BACK_IOS, evt->{
                    back();
                });
            }
        }
    }
    
    /**
     * Overrides parent setView().  Delegates to {@link #setView(com.codename1.ui.Form} if cmp is 
     * a form.  Throws IllegalArgumentException otherwise.
     * @param cmp 
     */
    public void setView(Component cmp) {
        if (cmp instanceof Form) {
            setView((Form)cmp);
        } else {
            throw new IllegalArgumentException("View must be a form");
        }
    }
    
    /**
     * Gets form associated with this controller.
     * @return 
     */
    @Override
    public Form getView() {
        return (Form)super.getView();
    }
    
    /**
     * Goes back to previous form.  The previous form is always the parent controller's
     * form, if the parent controller is a FormController.
     */
    public void back() {
        if (getParent() instanceof ViewController) {
            
            Component parentView = ((ViewController)getParent()).getView();
            if (parentView instanceof Form) {
                ((Form)parentView).showBack();
            }
        }
    }

    /**
     * Handles Controller event.  This implementation consumes {@link FormBackEvent} events.
     * @param evt 
     */
    @Override
    public void actionPerformed(ControllerEvent evt) {
        if (evt instanceof FormBackEvent) {
            evt.consume();
            back();
            return;
        }
    }

   
    
    
    
    
}
