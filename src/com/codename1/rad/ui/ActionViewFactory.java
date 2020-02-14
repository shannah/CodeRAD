/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.models.Entity;
import com.codename1.ui.Component;

/**
 *
 * @author shannah
 */
public interface ActionViewFactory {
    public Component createActionView(Entity entity, ActionNode action);
}
