/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.attributes.ViewType;
import com.codename1.rad.ui.entityviews.MultiButtonEntityView;
import com.codename1.rad.ui.entityviews.WrapperEntityView;
import com.codename1.rad.nodes.EntityViewFactoryNode;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.nodes.SwipeContainer;
import com.codename1.rad.nodes.ViewNode;
import com.codename1.rad.models.Entity;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.SwipeableContainer;
import com.codename1.ui.layouts.BoxLayout;
import java.util.HashMap;

/**
 *
 * @author shannah
 */
public class DefaultEntityViewFactory implements EntityViewFactory {
    
    private HashMap<ViewType,EntityViewFactory> registry = new HashMap<>();
    {
        registry.put(ViewType.MULTIBUTTON, (entity, node) -> {
            return new MultiButtonEntityView(entity, (ViewNode)node);
        });
    }
    
    
    
    

    @Override
    public EntityView createView(Entity entity, ViewNode node) {
        ViewType type = (ViewType)node.findAttribute(ViewType.class);
        if (type == null) {
            type = ViewType.MULTIBUTTON;
        }
        EntityViewFactory f = registry.get(type);
        if (f == null) {
            throw new IllegalArgumentException("Factory doesn't know how to build view "+type);
        }
        EntityView out = f.createView(entity, node);
        
        return makeSwipeable(entity, node, (Component)out);
        
    }
    
    
    private EntityView makeSwipeable(Entity entity, ViewNode node, Component view) {
         // Check for swipeable container
        SwipeContainer swipe = (SwipeContainer)node.findAttribute(SwipeContainer.class);
        if (swipe != null) {
            EntityView leftCnt = null;
            EntityView rightCnt = null;
            ViewNode leftNode = swipe.getLeft();
            if (leftNode != null) {
                leftCnt = leftNode.createView(entity, this);
            }
            ViewNode rightNode = swipe.getRight();
            if (rightNode != null) {
                rightCnt = rightNode.createView(entity, this);
            }
            
            SwipeableContainer swipeWrapper = new SwipeableContainer((Component)leftCnt, (Component)rightCnt, view);
            return new WrapperEntityView(swipeWrapper, entity, node);
        }
        
        Actions leftSwipeActions = node.getActions(ActionCategories.LEFT_SWIPE_MENU);
        Actions rightSwipeActions = node.getActions(ActionCategories.RIGHT_SWIPE_MENU);
        if (!leftSwipeActions.isEmpty() || !rightSwipeActions.isEmpty()) {
            Container leftCnt = null;
            Container rightCnt = null;
            if (!leftSwipeActions.isEmpty()) {
                leftCnt = new Container(BoxLayout.y());
                NodeUtilFunctions.buildActionsBar(node, leftCnt, entity, null, leftSwipeActions, null);
            }
            if (!rightSwipeActions.isEmpty()) {
                rightCnt = new Container(BoxLayout.y());
                NodeUtilFunctions.buildActionsBar(node, leftCnt, entity, rightSwipeActions, null, null);
            }
            SwipeableContainer swipeWrapper = new SwipeableContainer((Component)leftCnt, (Component)rightCnt, view);
            return new WrapperEntityView(swipeWrapper, entity, node);
            
        }
        
        return (EntityView)view;
    }
    
}
