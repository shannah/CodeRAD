/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.events;

import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.models.Entity;
import com.codename1.ui.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author shannah
 */
public class EventContext {

    public EventContext(Entity entity, Component source, ActionNode action) {
        this.entity = entity;
        this.eventSource = source;
        this.action = action;
    }
    
    public EventContext() {
        
    }
    
    /**
     * @return the entity
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * @param entity the entity to set
     */
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    /**
     * @return the eventSource
     */
    public Component getEventSource() {
        return eventSource;
    }

    /**
     * @param eventSource the eventSource to set
     */
    public void setEventSource(Component eventSource) {
        this.eventSource = eventSource;
    }

    /**
     * @return the action
     */
    public ActionNode getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(ActionNode action) {
        this.action = action;
    }
    
    public void putExtra(Object key, Object val) {
        if (extraData == null) {
            extraData = new HashMap();
        }
        extraData.put(key, val);
    }
    
    public Object getExtra(Object key) {
        if (extraData == null) {
            return null;
        }
        return extraData.get(key);
    }
    
    public Iterable getExtraDataKeys() {
        if (extraData == null) {
            return new ArrayList();
        }
        return extraData.keySet();
    }
    
    public boolean hasExtraData() {
        return extraData != null && !extraData.isEmpty();
    }
    
    private Entity entity;
    private Component eventSource;
    private ActionNode action;
    private Map extraData;
}
