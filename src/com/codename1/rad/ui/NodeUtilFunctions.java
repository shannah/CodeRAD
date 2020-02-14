/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.events.EventContext;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.ActionsNode;
import com.codename1.rad.nodes.EventFactoryNode;
import com.codename1.rad.nodes.FormNode;
import com.codename1.rad.nodes.Node;
import ca.weblite.shared.components.PopupMenu;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.Property;
import com.codename1.ui.Button;
import static com.codename1.ui.CN.NORTH;
import static com.codename1.ui.CN.SOUTH;
import com.codename1.ui.Command;
import static com.codename1.ui.Component.RIGHT;
import static com.codename1.ui.ComponentSelector.$;
import com.codename1.ui.Container;
import com.codename1.rad.controllers.ActionSupport;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.Layout;

/**
 *
 * @author shannah
 */
class NodeUtilFunctions {
    static void buildBottomActionsBar(Node node, Container target, Entity entity) {
        Layout targetLayout = target.getLayout();
        if (targetLayout instanceof BorderLayout) {
            Container south = new Container(BoxLayout.y());
            target.add(SOUTH, south);
            target = south;
        }
        buildActionsBar(
                node, target, entity,
                node.getActions(FormNode.BOTTOM_RIGHT_MENU), 
                node.getActions(FormNode.BOTTOM_LEFT_MENU), 
                null);
    }
    
    static  void buildTopActionsBar(Node node, Container target, Entity entity) {
        Layout targetLayout = target.getLayout();
        if (targetLayout instanceof BorderLayout) {
            Container north = new Container(BoxLayout.y());
            target.add(NORTH, north);
            target = north;
        }
        buildActionsBar(
                node,
                target,
                entity,
                node.getActions(FormNode.TOP_RIGHT_MENU), 
                node.getActions(FormNode.TOP_LEFT_MENU), 
                node.getActions(FormNode.OVERFLOW_MENU)
                );
    }
    static  void buildActionsBar(Node node, Container target, Entity entity, Actions right, Actions left, Actions overflow) {
        Container actionsBar = new Container(new BorderLayout());
        Container actionsBarRight = new Container(new BorderLayout());
        
        if (left != null) {
            Container cnt = new Container(BoxLayout.x());
            for (ActionNode action : left) {
                cnt.add(action.getViewFactory().getValue().createActionView(entity, action));
            }
            actionsBar.add(BorderLayout.CENTER, cnt);
        }
        
        if (right != null) {
            Container cnt = new Container(BoxLayout.x());
            $(cnt).setAlignment(RIGHT);
            for (ActionNode action : right) {
                System.out.println("right node "+action);
                cnt.add(action.getViewFactory().getValue().createActionView(entity, action));
            }
            System.out.println("Adding to right "+cnt);
            actionsBarRight.add(BorderLayout.CENTER, cnt);
        }
        
        if (overflow != null && !overflow.isEmpty()) {
            PopupMenu popup = new PopupMenu();
            for (ActionNode action : overflow) {
                Property.Label label = action.getLabel();
                String labelStr = label != null ? label.getValue() : "";
                
                Command cmd = new Command(labelStr) {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        action.fireEvent(entity, target);
                    }
                    
                };
                if (action.getImageIcon() != null) {
                    cmd.setIcon(action.getImageIcon().getValue());
                    
                } else if (action.getMaterialIcon() != null) {
                    cmd.setMaterialIcon(action.getMaterialIcon().getValue());
                }
                
                popup.addCommand(cmd);
            }
            actionsBarRight.add(BorderLayout.EAST, new Button(popup.getCommand()));
            
        }
        if (actionsBarRight.getComponentCount() > 0) {
            actionsBar.add(BorderLayout.EAST, actionsBarRight);
        }
        
        
        if (actionsBar.getComponentCount() > 0) {
            target.add(actionsBar);
        }
    }
    
}
