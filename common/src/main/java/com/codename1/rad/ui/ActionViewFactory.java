/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.nodes.ActionNode;

import com.codename1.ui.Component;
import com.codename1.rad.models.Entity;

/**
 * A factory to create a view for a given action, which acts on the provided entity.
 * @author shannah
 * 
 * @see DefaultActionViewFactory
 * @see com.codename1.rad.ui.UI#setDefaultActionViewFactory(com.codename1.rad.ui.ActionViewFactory) 
 * @see com.codename1.rad.ui.UI#getDefaultActionViewFactory() 
 * @see ActionNode
 * @see Entity
 */
public interface ActionViewFactory {
    public Component createActionView(Entity entity, ActionNode action);
}
