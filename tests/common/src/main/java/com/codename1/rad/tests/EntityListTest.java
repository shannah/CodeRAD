/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.tests;

import com.codename1.rad.models.Entity;
import com.codename1.rad.models.EntityList;
import com.codename1.rad.models.EntityList.EntityAddedEvent;
import com.codename1.rad.models.EntityList.EntityListEvent;
import com.codename1.rad.models.EntityList.EntityRemovedEvent;
import com.codename1.rad.models.EntityList.TransactionEvent;
import com.codename1.testing.AbstractTest;
import com.codename1.ui.events.ActionListener;
import java.util.Observer;
import com.codename1.rad.models.Entity;

/**
 *
 * @author shannah
 */
public class EntityListTest extends AbstractTest {

    @Override
    public String toString() {
        return "EntityListTest";
    }

    
    
    @Override
    public boolean shouldExecuteOnEDT() {
        return true;
    }

    
    
    @Override
    public boolean runTest() throws Exception {
        testGenericList();
        testTransactions();
        return true;
    }
    
    private void testGenericList() throws Exception {
        EntityList list = new EntityList();
        assertEqual(0, list.size());
        list.add(new Entity());
        list.add(new Entity());
        list.add(new Entity());
        assertEqual(3, list.size());
        Entity firstEntity = list.get(0);
        assertNotNull(firstEntity);
        list.remove(firstEntity);
        assertEqual(2, list.size());
        
        class Stats {
            int counter=0;
            Entity lastAdd;
            Entity lastRemove;
            int added;
            int removed;
        }
        
        Stats stats = new Stats();
        Observer observer = (o,arg) -> {
            stats.counter++;
        };
        
        list.addObserver(observer);
        list.notifyObservers();
        assertEqual(stats.counter, 1);
        list.notifyObservers();
        assertEqual(1, stats.counter);
        list.add(new Entity());
        assertEqual(1, stats.counter);
        list.notifyObservers();
        assertEqual(2, stats.counter);
        
        list.addActionListener(evt->{
            if (evt instanceof EntityAddedEvent) {
                EntityAddedEvent eae = (EntityAddedEvent)evt;
                stats.added++;
                stats.lastAdd = eae.getEntity();
            } else if (evt instanceof EntityRemovedEvent) {
                EntityRemovedEvent ere = (EntityRemovedEvent)evt;
                stats.removed++;
                stats.lastRemove = ere.getEntity();
            }
        });
        
        list.add(firstEntity);
        assertEqual(1, stats.added);
        assertEqual(firstEntity, stats.lastAdd);
        list.remove(firstEntity);
        assertEqual(1, stats.removed);
        assertEqual(firstEntity, stats.lastRemove);
        
        
        
        
        
    }
    
    private void testTransactions() throws Exception {
        EntityList el = new EntityList();
        Entity bob = new Entity();
        Entity gary = new Entity();
        Entity june = new Entity();
        
        class Stats {
            private Object data;
            private int count;
            
            private void reset() {
                data = null;
                count = 0;
            }
        }
        
        // Warm it up.. make sure no NPEs or anything
        
        el.add(bob);
        el.add(gary);
        el.remove(bob);
        el.remove(gary);
        
        Stats stats = new Stats();
        ActionListener<EntityListEvent> l1 = evt -> {
            if (evt instanceof EntityAddedEvent || evt instanceof EntityRemovedEvent) {
                stats.count++;
                stats.data = evt;
            }
        };
        el.addActionListener(l1);
        el.add(bob);
        assertEqual(1, stats.count);
        assertTrue(stats.data instanceof EntityAddedEvent);
        assertEqual(1, el.size());
        EntityAddedEvent eae = (EntityAddedEvent)stats.data;
        assertEqual(bob, eae.getEntity());
        assertNull(eae.getTransaction(), "EntityAddedEvent not in transaction should return null for getTransaction()");
        
        stats.reset();
        el.startTransaction();
        assertEqual(0, stats.count);
        assertNull(stats.data);
        el.remove(bob);
        assertEqual(1, stats.count);
        assertTrue(stats.data instanceof EntityRemovedEvent);
        assertEqual(0, el.size());
        EntityRemovedEvent ere = (EntityRemovedEvent)stats.data;
        assertEqual(bob, ere.getEntity());
        assertNotNull(ere.getTransaction(), "EntityRemovedEvent inside transaction should return non-null for getTransaction()");
        
        Throwable t = null;
        try {
            el.startTransaction();
        } catch (IllegalStateException ex) {
            t = ex;
        }
        
        
        assertTrue(t instanceof IllegalStateException, "Starting a transaction while another one is active should raise an IllegalStateException");
        
        el.commitTransaction();
        
        el.removeActionListener(l1);
        l1 = evt -> {
            if (evt instanceof EntityAddedEvent || evt instanceof EntityRemovedEvent || evt instanceof TransactionEvent) {
                stats.count++;
                stats.data = evt;
            }
        };
        el.addActionListener(l1);
        
        stats.reset();
        el.startTransaction();
        assertEqual(1, stats.count);
        assertTrue(stats.data instanceof TransactionEvent);
        TransactionEvent te = (TransactionEvent)stats.data;
        assertTrue(te.isEmpty());
        assertTrue(!te.isComplete());
        stats.reset();
        
        el.add(bob);
        assertEqual(1, stats.count);
        assertTrue(stats.data instanceof EntityAddedEvent);
        eae = (EntityAddedEvent)stats.data;
        assertEqual(bob, eae.getEntity());
        assertEqual(eae.getTransaction(), te, "EntityAddedEvent.getTransaction() should return same TransactionEvent from startTransaction");
        
        stats.reset();
        el.add(gary);
        assertEqual(1, stats.count);
        assertTrue(stats.data instanceof EntityAddedEvent);
        eae = (EntityAddedEvent)stats.data;
        assertEqual(gary, eae.getEntity());
        
        stats.reset();
        el.commitTransaction();
        assertEqual(1, stats.count);
        assertTrue(stats.data instanceof TransactionEvent);
        te = (TransactionEvent)stats.data;
        assertEqual(2, te.size());
        assertTrue(te.isComplete());
        assertEqual(bob, te.get(0).getEntity());
        assertEqual(gary, te.get(1).getEntity());
        
        t = null;
        try {
            el.commitTransaction();
        
        } catch (IllegalStateException ex) {
            t = ex;
        }
        assertTrue(t instanceof IllegalStateException, "commitTransaction() on list with no current transaction should raise an IllegalStateException");
        
        el.startTransaction();
        stats.reset();
        el.clear();
        assertEqual(0, el.size(), "EntityList.add() and EntityList.remove() should immediately change the list even if transaction is in progress");
        assertEqual(2, stats.count, "EntityList.clear() should trigger remove events for each item in list");
        el.commitTransaction();
        assertEqual(0, el.size());
        assertEqual(3, stats.count);
        assertTrue(stats.data instanceof TransactionEvent, "commitTransaction() didn't fire a transaction event");
        te = (TransactionEvent)stats.data;
        assertEqual(2, te.size(), "TransactionEvent size() should reflect the number of add or remove operations in transaction.");
        assertEqual(bob, te.get(0).getEntity());
        assertEqual(gary, te.get(1).getEntity());
        
        
        
        
        
        
        
        
        
        
        
        
    }
    
}
