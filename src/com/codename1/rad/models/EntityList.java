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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    private EventDispatcher listeners, vetoableListeners;
    private EntityType rowType;
    private List<T> entities;
    private int maxLen = -1;
    
    
    public static class EntityListEvent extends ActionEvent {
        public EntityListEvent(EntityList source) {
            super(source);
            EntityType et = source.getEntityType();
            if (et != null) {
                source.setRowType(et.getRowEntityType());        
            }
            
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
    
    public static class EntityEvent extends EntityListEvent {
        private Entity entity;
        private int index;
        
        public EntityEvent(EntityList source, Entity entity, int index) {
            super(source);
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
    
    public static class VetoableEntityEvent extends EntityEvent {
        private boolean vetoed;
        private String reason;
         public VetoableEntityEvent(EntityList source, Entity entity, int index) {
            super(source, entity, index);
        }
         
        public void veto(String reason) {
            vetoed = true;
        }
        
        public boolean isVetoed() {
            return vetoed;
        }
        
        public String getReason() {
            return reason;
        }
    }
    public static class VetoableEntityAddedEvent extends VetoableEntityEvent {
        
        public VetoableEntityAddedEvent(EntityList source, Entity entity, int index) {
            super(source, entity, index);
        }
    }
    
    public static class VetoableEntityRemovedEvent extends VetoableEntityEvent {
        public VetoableEntityRemovedEvent(EntityList source, Entity entity, int index) {
            super(source, entity, index);
        }
    }
    
    public static class EntityAddedEvent extends EntityEvent {
        public EntityAddedEvent(EntityList source, Entity entity, int index) {
            super(source, entity, index);
        }
    }
    
    public static class EntityRemovedEvent extends EntityEvent {
        public EntityRemovedEvent(EntityList source, Entity entity, int index) {
            super(source, entity, index);
        }
    }
    
    public static class VetoException extends RuntimeException {
        private VetoableEntityEvent vetoableEntityEvent;
        
        public VetoException(VetoableEntityEvent evt) {
            super(evt.getReason());
            vetoableEntityEvent = evt;
        }
        
        public VetoableEntityEvent getVetoableEntityEvent() {
            return vetoableEntityEvent;
        }
    }
    
    
    
    public EntityList(int maxLen) {
        this(null, maxLen);

    }
    public EntityList(EntityType rowType, int maxLen) {
        this(rowType, maxLen, (List<T>)null);
    }
    public EntityList(EntityType rowType, int maxLen, List<T> internalList) {
        if (rowType == null) {
            EntityType et = getEntityType();
            if (et != null) {
                rowType = et.getRowEntityType();
            }
        }
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
        if (listeners != null && listeners.hasListeners()) {
            VetoableEntityAddedEvent vevt = new VetoableEntityAddedEvent(this, link, entities().size());
            listeners.fireActionEvent(vevt);
            if (vevt.isVetoed()) {
                throw new VetoException(vevt);
            }
        }
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
            if (listeners != null && listeners.hasListeners()) {
                VetoableEntityRemovedEvent vevt = new VetoableEntityRemovedEvent(this, link, index);
                listeners.fireActionEvent(vevt);
                if (vevt.isVetoed()) {
                    throw new VetoException(vevt);
                }
            }
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
            listeners.fireActionEvent(new EntityAddedEvent(this, e, index));
        }
    }
    
    protected void fireEntityRemoved(Entity e, int index) {
        if (listeners != null && listeners.hasListeners()) {
            listeners.fireActionEvent(new EntityRemovedEvent(this, e, index));
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
