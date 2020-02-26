/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.ui.entityviews.MultiButtonEntityView;
import com.codename1.rad.ui.entityviews.WrapperEntityView;
import com.codename1.rad.ui.entityviews.EntityListView;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.ListNode;
import com.codename1.rad.nodes.SwipeContainer;
import com.codename1.rad.nodes.ViewNode;
import com.codename1.rad.models.Entity;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.SwipeableContainer;
import com.codename1.ui.layouts.GridLayout;

/**
 * The default renderer for rendering a row of the EntityListView.
 * @author shannah
 * @see com.codename1.rad.ui.UI#cellRenderer(com.codename1.rad.ui.EntityListCellRenderer)
 * @see com.codename1.rad.ui.UI#getDefaultListCellRenderer() 
 * @see com.codename1.rad.ui.UI#setDefaultListCellRenderer(com.codename1.rad.ui.EntityListCellRenderer) 
 */
public class DefaultEntityListCellRenderer implements EntityListCellRenderer {

    @Override
    public EntityView getListCellRendererComponent(EntityListView list, Entity value, int index, boolean isSelected, boolean isFocused) {
        ListNode listNode = (ListNode)list.getViewNode();
        
        MultiButtonEntityView out =  new MultiButtonEntityView(value, listNode.getRowTemplate());
        ActionNode node = listNode.getAction(ActionCategories.LIST_SELECT_ACTION);
        if (node != null) {
            out.setAction(node);
        }
        
        return makeSwipeable(value, listNode.getRowTemplate(), out);
    }
    
    private EntityView makeSwipeable(Entity entity, ViewNode node, Component view) {
         // Check for swipeable container
        SwipeContainer swipe = (SwipeContainer)node.findAttribute(SwipeContainer.class);
        if (swipe != null) {
            EntityView leftCnt = null;
            EntityView rightCnt = null;
            ViewNode leftNode = swipe.getLeft();
            if (leftNode != null) {
                leftCnt = leftNode.createView(entity);
            }
            ViewNode rightNode = swipe.getRight();
            if (rightNode != null) {
                rightCnt = rightNode.createView(entity);
            }
            
            SwipeableContainer swipeWrapper = new SwipeableContainer((Component)leftCnt, (Component)rightCnt, view);
            return new WrapperEntityView(swipeWrapper, entity, node);
        }
        ActionNode deleteAction = node.getInheritedAction(ActionCategories.LIST_REMOVE_ACTION);
        Actions leftSwipeActions = node.getActions(ActionCategories.LEFT_SWIPE_MENU);
        if (deleteAction != null) {
            leftSwipeActions.add(deleteAction);
        }
        Actions rightSwipeActions = node.getActions(ActionCategories.RIGHT_SWIPE_MENU);
        if (!leftSwipeActions.isEmpty() || !rightSwipeActions.isEmpty()) {
            
            Container leftCnt = null;
            Container rightCnt = null;
            if (!leftSwipeActions.isEmpty()) {
                leftCnt = new Container(new GridLayout(leftSwipeActions.size()));
                for (ActionNode action : leftSwipeActions) {
                    leftCnt.add(action.getViewFactory().createActionView(entity, action));
                }
                
            }
            if (!rightSwipeActions.isEmpty()) {
               rightCnt = new Container(new GridLayout(rightSwipeActions.size()));
                for (ActionNode action : rightSwipeActions) {
                    rightCnt.add(action.getViewFactory().createActionView(entity, action));
                }
            }
            SwipeableContainer swipeWrapper = new SwipeableContainer((Component)leftCnt, (Component)rightCnt, view);
            return new WrapperEntityView(swipeWrapper, entity, node);
            
        } else {
            System.out.println("Swipe actions not present");
        }
        
        return (EntityView)view;
    }
    
}
