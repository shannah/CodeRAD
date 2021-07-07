/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

import com.codename1.rad.controllers.ControllerEvent;
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
public class EntityList<T extends Entity> extends BaseEntity implements Iterable<T> {

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
    
    
    public static class EntityListEvent extends ControllerEvent {
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
     *     if (addedEvent != null && addedEvent.getTransaction() == null) {
     *         // This was an add event and NOT inside a transaction.
     *         // Respond to this add directly.
     *         Entity addedEntity = addedEvent.getEntity();
     *         // .... etc...
     *         return;
     *     }
     *     EntityRemovedEvent removedEvent = evt.as(EntityRemovedEvent.class);
     *     if (removedEvent != null && removedEvent.getTransaction() == null) {
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
        
        /**
         * Creates a new TransactionEvent with the given EntityList source.  Generally you don't call this
         * method directly.  Start a transaction with {@link #startTransaction() }.  The TransactionEvent will
         * be delivered to listeners both on {@link #startTransaction() } and {@link #commitTransaction() }
         * @param source 
         */
        public TransactionEvent(EntityList source) {
            super(source);
        }
        
        /**
         * Checks if this transaction has no add/remove events.
         * @return True if this transaction is empty.
         */
        public boolean isEmpty() {
            return events.isEmpty();
        }
        
        /**
         * Gets the number of add/remove events in this transaction.
         * @return 
         */
        public int size() {
            return events.size();
        }
        
        /**
         * Gets the add or remove event at the given index.
         * @param index The index of the add/remove event.
         * @return The event at the given index.  Either a {@link EntityAddedEvent} or {@link EntityRemovedEvent}
         */
        public EntityEvent get(int index) {
            return events.get(index);
        }
        
        /**
         * Adds an event to the transaction.  Generally don't call this method directly.  The 
         * entity list will automatically add events to the current transaction as they happen.
         * @param evt 
         */
        public void addEvent(EntityEvent evt) {
            if (evt.getTransaction() != null) {
                throw new IllegalArgumentException("Attempt to add EntityEvent to transaction that is already a part of another transaction.");
            }
            evt.setTransaction(this);
            events.add(evt);
        }
        
        /**
         * Removes an event from the transaction.  Generally don't call this method directly.
         * @param evt 
         */
        public void removeEvent(EntityEvent evt) {
            if (evt.getTransaction() != this) {
                throw new IllegalArgumentException("Attempt to remove EntityEvent from transaction that it is not a part of.");
            }
            evt.setTransaction(null);
            events.remove(evt);
        }
        
        /**
         * Marks the transaction as complete.
         */
        private void complete() {
            squash();
            this.complete = true;
        }
        
        // We need to squash the events - which means to only share 
        // events that are relevant to views, and to provide relevant indices
        // for "Add" events.
        private void squash() {
            List<EntityEvent> adds = new ArrayList<EntityEvent>();
            List<EntityEvent> removes = new ArrayList<EntityEvent>();
            for (EntityEvent e : events) {
                if (e instanceof EntityAddedEvent) {
                    EntityList el = (EntityList)e.getSource();
                    int index = el.indexOf(e.getEntity());
                    if (index >= 0) {
                        adds.add(new EntityAddedEvent(el, e.getEntity(), index));
                    }
                } else if (e instanceof EntityRemovedEvent) {
                    EntityList el = (EntityList)e.getSource();
                    int index = el.indexOf(el);
                    if (index < 0) {
                        removes.add(new EntityRemovedEvent(el, e.getEntity(), index));
                    }
                }
            }
            events.clear();
            events.addAll(removes);
            events.addAll(adds);
        }
        
        /**
         * Checks if the transaction is complete.  A TransactionEvent is fired twice: once on {@link #startTransaction() } and
         * then again on {@link #commitTransaction() }.  {@link #isComplete() } will return {@literal false} in {@link #startTransaction() }
         * and {@literal true} in {@link #commitTransaction() }.
         * @return 
         */
        public boolean isComplete() {
            return complete;
        }
        
        
        /**
         * Iterator to iterate over the add/remove events within this transaction.
         * @return 
         */
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
        
        /**
         * Sets the transaction that this event is part of.  Generally don't call this method directly. Use
         * {@link TransactionEvent#addEvent(com.codename1.rad.models.EntityList.EntityEvent) } instead.
         * @param transaction 
         */
        public void setTransaction(TransactionEvent transaction) {
            this.transaction = transaction;
        }
        
        /**
         * Gets the transaction that this event is part of, or {@literal null} if it is 
         * not part of a transaction.
         * @return 
         */
        @Override
        public TransactionEvent getTransaction() {
            return transaction;
        }
    }
    
    /**
     * A type of entity event which is fired *before* the add/remove occurs, and allows
     * the listener to veto/cancel the add/remove.
     * 
     * This event type can be handy if you need to be notified of a change before it is made, if, for
     * example you need to know the state of the list before the change.  This is used by the {@link PropertySelector}
     * to detect when sub-properties may have been invalidated because a parent entity has been removed from a list.
     */
    public static class VetoableEntityEvent extends EntityEvent {
        private boolean vetoed;
        private String reason;
         public VetoableEntityEvent(EntityList source, Entity entity, int index) {
            super(source, entity, index);
        }
        
        /**
         * Veto the add/remove.
         * @param reason The reason for the veto.
         */
        public void veto(String reason) {
            vetoed = true;
        }
        
        /**
         * Checks if the add/remove has been vetoed.
         */
        public boolean isVetoed() {
            return vetoed;
        }
        
        /**
         * Gets the reason for the veto, or null, if no veto was made.
         * @return 
         */
        public String getReason() {
            return reason;
        }
    }
    
    /**
     * Event fired *before* an item is added to the list.
     * 
     * @see EntityAddedEvent
     */
    public static class VetoableEntityAddedEvent extends VetoableEntityEvent {
        
        public VetoableEntityAddedEvent(EntityList source, Entity entity, int index) {
            super(source, entity, index);
        }
    }
    
    /**
     * Event fired *before* an item is removed from the list.
     * 
     * @see EntityRemovedEvent
     */
    public static class VetoableEntityRemovedEvent extends VetoableEntityEvent {
        public VetoableEntityRemovedEvent(EntityList source, Entity entity, int index) {
            super(source, entity, index);
        }
    }
    
    /**
     * Event fired *after* an item is added to the list.
     * @see VetoableEntityAddedEvent
     * @see TransactionEvent For handling bulk-add events.
     */
    public static class EntityAddedEvent extends EntityEvent {
        public EntityAddedEvent(EntityList source, Entity entity, int index) {
            super(source, entity, index);
        }
    }
    
    /**
     * Event fired *after* an item is removed from the list.
     * @see VetoableEntityRemovedEvent
     * @see TransactionEvent For handling bulk-remove events.
     */
    public static class EntityRemovedEvent extends EntityEvent {
        public EntityRemovedEvent(EntityList source, Entity entity, int index) {
            super(source, entity, index);
        }
    }
    
    /**
     * An event that is fired when the statue of the list has changed in ways
     * that cannot be synchronized using Add/Remove events.  Listeners should 
     * resynchronize their state with the state of the list.
     */
    public static class EntityListInvalidatedEvent extends EntityListEvent {
        public EntityListInvalidatedEvent(EntityList source) {
            super(source);
        }
    }
    
    /**
     * Triggers an {@link EntityListInvalidatedEvent} to listeners to instruct 
     * them that the list has changed in ways that cannot be reconstructed by
     * the typical Add/Remove events, and that views should resynchronize their
     * state with the list.
     * 
     * This method should not be called inside a transaction.
     * @throws IllegalStateException if called inside a transaction.
     */
    public void invalidate() {
        if (currentTransaction != null) {
            throw new IllegalStateException("invalidate() cannot be called inside a transaction");
        }
        if (listeners != null && listeners.hasListeners()) {
            listeners.fireActionEvent(new EntityListInvalidatedEvent(this));
        }
        setChanged();
    }
    
    /**
     * Exception thrown if a veto listener vetos and add/remove.
     */
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
    
    /**
     * Adds an item to the list.  This will trigger a {@link VetoableEntityAddedEvent} before the add, and a
     * {@link EntityAddedEvent} after the add.
     * @param link 
     */
    public void add(T link) {
        if (maxLen > 0 && entities().size() >= maxLen) {
            throw new IllegalStateException("Nary composition has max length "+maxLen+".  Cannot add another.");
        }
        if (getRowType() == null) {
            setRowType(link.getEntity().getEntityType());
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

    /**
     * Removes an item from the list.  This will trigger a {@link VetoableEntityRemovedEvent} before the removal, 
     * and a {@link EntityRemovedEvent} after the removal.
     * 
     * @param link The item to add
     * @return True if the item was removed.  False if the item wasn't found or was not removed.
     */
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

  
    
    /**
     * Removes all items from the list.  This will fire {@link VetoableEntityRemovedEvent} and {@link EntityRemovedEvent} events
     * before/after each item is removed.
     */
    public void clear() {
        List<T> toRemove = new ArrayList<>();
        for (T e : this) {
            toRemove.add(e);
        }
        for (T e : toRemove) {
            remove(e);
        }
    }

    /**
     * Returns the number of items in the list.
     * @return 
     */
    public int size() {
        return entities().size();
    }
    
    /**
     * Gets item at index.
     * @param index The index of the item to get.
     * @return 
     */
    public T get(int index) {
        return entities().get(index);
    }
    
    /**
     * Gets the current index of in the list of the given item.
    */
    public int indexOf(T item) {
        return entities().indexOf(item);
    }

    /**
     * Fires {@link EntityAddedEvent} event to listeners.  
     * @param e
     * @param index 
     */
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
    
    /**
     * Fires {@link EntityRemovedEvent} to listeners.
     * @param e
     * @param index 
     */
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
   
    
    /**
     * Add listener to be notified of events on this list.  Listeners will be notified of
     * the following event types:
     * 
     * . {@link EntityAddedEvent} - After an item is added.
     * . {@link EntityRemovedEvent} - After an item is removed.
     * . {@link VetoableEntityAddedEvent} - Before an item is added.
     * . {@link VetoableEntityRemovedEvent} - Before an item is removed.
     * . {@link TransactionEvent} - On {@link #startTransaction() } and {@link #commitTransaction() }.
     * 
     * @param l 
     * @see #removeActionListener(com.codename1.ui.events.ActionListener) 
     */
    public void addActionListener(ActionListener<EntityListEvent> l) {
        if (listeners == null) {
            listeners = new EventDispatcher();
        }
        listeners.addListener(l);
    }
    
    /**
     * Removes listener.
     * 
     * @param l 
     * @see #addActionListener(com.codename1.ui.events.ActionListener) 
     */
    public void removeActionListener(ActionListener<EntityListEvent> l) {
        if (listeners != null) {
            listeners.removeListener(l);
        }
    }

    public boolean contains(Object o) {
        return entities().contains(o);
    }
    
}
