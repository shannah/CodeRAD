/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui.menus;

import com.codename1.rad.ui.ActionStyle;
import com.codename1.rad.ui.Actions;
import com.codename1.rad.nodes.ActionNode;
import ca.weblite.shared.components.PopupMenu;
import com.codename1.rad.models.Entity;
import com.codename1.ui.Component;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.Layout;

/**
 * A popup menu for rendering a list of actions.
 * @author shannah
 */
public class PopupActionsMenu extends PopupMenu {
    private Actions actions;
    
    
    public PopupActionsMenu(Actions actions, Entity entity, Component source) {
        
        this.actions = actions;
        boolean includesText = false;
        boolean includesIcon = false;
        for (ActionNode action : actions) {
            
            addCommand(action.getViewFactory().createActionView(entity, action));
            
            
        }
        if (includesIcon && !includesText) {
            setCommandsLayout(BoxLayout.x());
        }
        
        
        
    }
}
