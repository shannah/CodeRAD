/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.util.EventDispatcher;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Encapsulates a list of entities. This list is observable, as it will fire {@link EntityListEvent} events when items are added 
 * and removed from this list.
 * @author shannah
 */
public class EntityList<T extends Entity> extends Entity implements Iterable<T> {

    /**
     * @return the rowType
     */
    public EntityType getRowType() {
        return rowType;
    }

    /**
     * @param rowType the rowType to set
     */
    public void setRowType(EntityType rowType) {
        this.rowType = rowType;
    }
    private EventDispatcher listeners;
    private EntityType rowType;
    private List<T> entities;
    private int maxLen = -1;
    
    
    public class EntityListEvent extends ActionEvent {
        public EntityListEvent() {
            super(EntityList.this);
            
        }
    }
    
    /**
     * Can be overridden by subclasses to create an alternate collection type
     * for the entity list.  Default implementation uses an ArrayList.
     * @return 
     */
    protected List<T> createInternalList() {
        return new ArrayList<>();
    }
    
    /**
     * Lazily creates internal entity list and returns it.
     * @return 
     */
    private List<T> entities() {
        if (entities == null) {
            entities = createInternalList();
        }
        return entities;
    }
    
    public class EntityEvent extends EntityListEvent {
        private Entity entity;
        private int index;
        
        public EntityEvent(Entity entity, int index) {
            this.index = index;
            this.entity = entity;
        }
        
        public Entity getEntity() {
            return entity;
        }
        
        public int getIndex() {
            return index;
        }
    }
    
    
    public class EntityAddedEvent extends EntityEvent {
        public EntityAddedEvent(Entity entity, int index) {
            super(entity, index);
        }
    }
    
    public class EntityRemovedEvent extends EntityEvent {
        public EntityRemovedEvent(Entity entity, int index) {
            super(entity, index);
        }
    }
    
    public EntityList(int maxLen) {
        this(null, maxLen);

    }
    public EntityList(EntityType rowType, int maxLen) {
        this(rowType, maxLen, (List<T>)null);
    }
    public EntityList(EntityType rowType, int maxLen, List<T> internalList) {
        this.rowType = rowType;
        this.maxLen = maxLen;
        if (internalList != null) {
            entities = internalList;
        }
    }
    
    public EntityList() {
        this(null, -1);
    }
    
    @Override
    public Iterator<T> iterator() {
        return entities().iterator();
    }
    
    public void add(T link) {
        if (maxLen > 0 && entities().size() >= maxLen) {
            throw new IllegalStateException("Nary composition has max length "+maxLen+".  Cannot add another.");
        }
        if (getRowType() == null) {
            setRowType(link.getEntityType());
        }
        link = beforeAdd(link);
        //int len = entities().size();
        boolean success = entities().add(link);
        if (success) {
            fireEntityAdded(link, entities().indexOf(link));
            setChanged();
        }
    }
    
    protected T beforeAdd(T link) {
        return link;
    }
    
    protected T beforeRemove(T link) {
        return link;
    }

    public boolean remove(T link) {
        if (entities().contains(link)) {
            link = beforeRemove(link);
            int index = entities().indexOf(link);
            if (entities().remove(link)) {
                fireEntityRemoved(link, index);
                setChanged();
                return true;
            }
        }
        return false;
    }
    
    public void clear() {
        List<T> toRemove = new ArrayList<>();
        for (T e : this) {
            toRemove.add(e);
        }
        for (T e : toRemove) {
            remove(e);
        }
    }

    public int size() {
        return entities().size();
    }
    
    public T get(int index) {
        return entities().get(index);
    }

    protected void fireEntityAdded(Entity e, int index) {
        if (listeners != null && listeners.hasListeners()) {
            listeners.fireActionEvent(new EntityAddedEvent(e, index));
        }
    }
    
    protected void fireEntityRemoved(Entity e, int index) {
        if (listeners != null && listeners.hasListeners()) {
            listeners.fireActionEvent(new EntityRemovedEvent(e, index));
        }
    }
    
    public void addActionListener(ActionListener<EntityListEvent> l) {
        if (listeners == null) {
            listeners = new EventDispatcher();
        }
        listeners.addListener(l);
    }
    
    public void removeActionListener(ActionListener<EntityListEvent> l) {
        if (listeners != null) {
            listeners.removeListener(l);
        }
    }
    
}
