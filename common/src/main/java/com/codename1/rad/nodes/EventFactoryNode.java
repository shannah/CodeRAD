/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.nodes;

import com.codename1.rad.events.EventFactory;
import com.codename1.rad.models.Attribute;

/**
 * A node that wraps an {@link EventFactory}.  This can be added to any part of the Node hierarchy, as 
 * actions will walk up the tree until it finds a factory when it needs to generate events.
 * @author shannah
 * 
 * @see EventFactory
 * @see com.codename1.rad.ui.UI#eventFactory(com.codename1.rad.events.EventFactory, com.codename1.rad.models.Attribute...) 
 */
public class EventFactoryNode extends Node<EventFactory> {
    
    public EventFactoryNode(EventFactory value, Attribute... atts) {
        super(value, atts);
    }
    
}
