/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.events;

import com.codename1.rad.nodes.ActionNode;
import com.codename1.ui.events.ActionEvent;

/**
 *
 * @author shannah
 */
public class DefaultEventFactory implements EventFactory {

    @Override
    public ActionEvent createEvent(EventContext context) {
        return new ActionNode.ActionNodeEvent(context);
    }
    
}
