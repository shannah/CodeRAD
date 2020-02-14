/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui.decorators;

import com.codename1.rad.ui.ActionCategories;
import com.codename1.rad.ui.ActionStyle;
import com.codename1.rad.ui.Actions;
import com.codename1.rad.ui.NodeDecorator;
import com.codename1.rad.ui.UI;
import static com.codename1.rad.ui.UI.iconRenderer;
import com.codename1.rad.attributes.UIID;
import com.codename1.rad.ui.entityviews.MultiButtonEntityView;
import com.codename1.rad.ui.image.FirstCharEntityImageRenderer;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.ActionsNode;
import com.codename1.rad.nodes.ListNode;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.schemas.Thing;

/**
 * A decorator that can be used with a {@link ListNode} to style it like a Contact list.
 * @author shannah
 */
public class ContactListDecorator implements NodeDecorator, ActionCategories {

    @Override
    public void decorate(Node node) {
        if (!(node instanceof ListNode)) {
            return;
        }
        ListNode listNode = (ListNode)node;
        Node rowTemplate = listNode.getRowTemplate();
        rowTemplate.setAttributes(UI.param(MultiButtonEntityView.LINE1_UIID, "ContactListLine1"));
        rowTemplate.setAttributes(UI.param(MultiButtonEntityView.LINE2_UIID, "ContactListLine2"));
        for (ActionNode n : rowTemplate.getActions(ActionCategories.LEFT_SWIPE_MENU)) {
            n.setAttributes(UI.actionStyle(ActionStyle.IconOnly));
            n.setAttributes(new UIID("SwipeableContainerButton"));
        }
        ActionNode removeAction = listNode.getAction(ActionCategories.LIST_REMOVE_ACTION);
        if (removeAction != null) {
            removeAction.setAttributes(UI.actionStyle(ActionStyle.IconOnly));
            removeAction.setAttributes(new UIID("SwipeableContainerButton"));
        }

        
        
        //swipeLeftNode.setAttributes(LEFT_SWIPE_MENU);
        //listNode.getRowTemplate().setAttributes(swipeLeftNode);
        node.setAttributes(iconRenderer(new FirstCharEntityImageRenderer(10)));
        node.setAttributes(UI.param(MultiButtonEntityView.ICON, Thing.name));
        
    }

    
}
