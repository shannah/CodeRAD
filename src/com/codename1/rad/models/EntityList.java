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
    private TransactionEvent currentTransaction;
    
    
    /**
     * Starts a transaction to keep track of adds and removes so that listeners
     * can be notified in bulk.
     */
    public void startTransaction() {
        if (currentTransaction != null) {
            throw new IllegalStateException("Transaction already in progress");
        }
        currentTransaction = new TransactionEvent(this);
        fireTransactionEvent(currentTransaction);
        
    }
    
    /**
     * Commits a transaction, and notifies listeners that the transaction is complete.
     * 
     */
    public void commitTransaction() {
        if (currentTransaction == null) {
            throw new IllegalStateException("No transaction found.");
        }
        TransactionEvent evt = currentTransaction;
        evt.complete();
        currentTransaction = null;
        fireTransactionEvent(evt);
    }
    
    protected void fireTransactionEvent(TransactionEvent evt) {
        if (listeners != null && listeners.hasListeners()) {
            listeners.fireActionEvent(evt);
        }
    }
    
    
    public static class EntityListEvent extends ActionEvent {
        public EntityListEvent(EntityList source) {
            super(source);
            EntityType et = source.getEntityType();
            if (et != null) {
                source.setRowType(et.getRowEntityType());        
            }
            
        }
        
        public TransactionEvent getTransaction() {
            return null;
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
    
    /**
     * An event to encapsulate transactions on a list.  
     * 
     * A transaction can be used to 
     * group together multiple add/remove events so that listeners can choose to handle
     * them in bulk. The {@link #startTransaction() } method can be used to start a 
     * transaction on a list.   It will fire the {@link TransactionEvent} to the listeners.  
     * The {@link #commitTransaction() } method can be used to end a transaction.  It will again
     * fire a {@link TransactionEvent} to the listeners.  Listeners can distinguish between
     * the start and end of a transaction using the {@link #isComplete() } method, which 
     * will return {@literal true} if the transaction has been completed.
     * 
     * Listeners can check to see if an "add" or "remove" event was part of a transaction
     * using {@link EntityEvent#getTransaction() }.  If it finds that it is part of a transaction
     * the view may decide to wait until it receives the TransactionEvent before it responds to
     * the change.
     * 
     * === Example
     * 
     * [source,java]
     * ----
     * myEntityList.addActionListener(evt -> {
     *     EntityAddedEvent addedEvent = evt.as(EntityAddedEvent.class);
     *     if (addedEvent != null && addedEvent.getTransaction() != null) {
     *         // This was an add event and NOT inside a transaction.
     *         // Respond to this add directly.
     *         Entity addedEntity = addedEvent.getEntity();
     *         // .... etc...
     *         return;
     *     }
     *     EntityRemovedEvent removedEvent = evt.as(EntityRemovedEvent.class);
     *     if (removedEvent != null && removedEvent.getTransaction() != null) {
     *         // This was an add event and NOT inside a transaction.
     *         // Respond to this add directly.
     *         Entity removedEntity = removedEvent.getEntity();
     *         // .... etc...
     *         return;
     *     }
     *     TransactionEvent transactionEvent = evt.as(TransactionEvent.class);
     *     if (transactionEvent != null && transactionEvent.isComplete() && 
     *             !transactionEvent.isEmpty()) {
     *         // This event marked the end of a transaction.  Let's process
     *         // all of the Add and Remove events in this transaction together
     *         for (EntityEvent e : transactionEvent) {
     *             addedEvent = e.as(EntityAddedEvent.class);
     *             if (addedEvent != null) {
     *                 // Process add event
     *                 // ...
     *                 continue;
     *             }
     *             removedEvent = e.as(EntityRemovedEvent.class);
     *             if (removedEvent != null) {
     *                 // Process remove event
     *                 // ...
     *                 continue;
     *             }
     *         }
     *         return;
     *     }
     *             
     *     
     * });
     * ----
     * 
     * 
     */
    public static class TransactionEvent extends EntityListEvent implements Iterable<EntityEvent> {
        private List<EntityEvent> events = new ArrayList<EntityEvent>();
        private boolean complete;
        private TransactionEvent parent;
        public TransactionEvent(EntityList source) {
            super(source);
        }
        
        public boolean isEmpty() {
            return events.isEmpty();
        }
        
        public int size() {
            return events.size();
        }
        
        public EntityEvent get(int index) {
            return events.get(index);
        }
        
        public void addEvent(EntityEvent evt) {
            if (evt.getTransaction() != null) {
                throw new IllegalArgumentException("Attempt to add EntityEvent to transaction that is already a part of another transaction.");
            }
            evt.setTransaction(this);
            events.add(evt);
        }
        
        public void removeEvent(EntityEvent evt) {
            if (evt.getTransaction() != this) {
                throw new IllegalArgumentException("Attempt to remove EntityEvent from transaction that it is not a part of.");
            }
            evt.setTransaction(null);
            events.remove(evt);
        }
        
        private void complete() {
            this.complete = true;
        }
        
        public boolean isComplete() {
            return complete;
        }
        
        public TransactionEvent getParent() {
            return parent;
        }
        
        public void setParent(TransactionEvent parent) {
            this.parent = parent;
        }

        @Override
        public Iterator<EntityEvent> iterator() {
            return events.iterator();
        }
    }
    
    public static class EntityEvent extends EntityListEvent {
        private Entity entity;
        private int index;
        private TransactionEvent transaction;
        
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
        
        public void setTransaction(TransactionEvent transaction) {
            this.transaction = transaction;
        }
        
        @Override
        public TransactionEvent getTransaction() {
            return transaction;
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
        boolean shouldFireEvent = listeners != null && listeners.hasListeners();
        boolean shouldCreateEvent = shouldFireEvent || currentTransaction != null;
        if (shouldFireEvent || shouldCreateEvent) {
            EntityAddedEvent evt = new EntityAddedEvent(this, e, index);
            if (currentTransaction != null) {
                currentTransaction.addEvent(evt);
            }
            if (shouldFireEvent) {
                listeners.fireActionEvent(evt);
            }
        }
    }
    
    protected void fireEntityRemoved(Entity e, int index) {
        boolean shouldFireEvent = listeners != null && listeners.hasListeners();
        boolean shouldCreateEvent = shouldFireEvent || currentTransaction != null;
        if (shouldFireEvent || shouldCreateEvent) {
            EntityRemovedEvent evt = new EntityRemovedEvent(this, e, index);
            if (currentTransaction != null) {
                currentTransaction.addEvent(evt);
            }
            if (shouldFireEvent) {
                listeners.fireActionEvent(evt);
            }
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
