/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.events;

import com.codename1.rad.nodes.ActionNode;
import com.codename1.ui.events.ActionEvent;

/**
 * The default factory used for creating an ActionEvent from an action context.  When view triggers 
 * an action, it will look up the action's view factory via {@link ActionNode#getEventFactory() }.  If the view descriptor
 * includes a {@link com.codename1.rad.nodes.EventFactoryNode}, then it will use the factory specified in that node.  Otherwise
 * it will use the default event factory that it obtains from {@link com.codename1.rad.ui.UI#getDefaultEventFactory() }, which, by default
 * is an instance of {@link DefaultEventFactory}.
 * 
 * You can change the default event factory using {@link com.codename1.rad.ui.UI#setDefaultEventFactory(com.codename1.rad.events.EventFactory)  } 
 * @author shannah
 * 
 * @see ActionNode#getEventFactory() 
 * @see com.codename1.rad.ui.UI#getDefaultEventFactory()
 * @see com.codename1.rad.ui.UI#setDefaultEventFactory(com.codename1.rad.events.EventFactory) 
 */
public class DefaultEventFactory implements EventFactory {

    @Override
    public ActionEvent createEvent(EventContext context) {
        return new ActionNode.ActionNodeEvent(context);
    }
    
}
