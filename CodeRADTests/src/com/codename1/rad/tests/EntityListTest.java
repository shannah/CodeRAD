/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.tests;

import com.codename1.rad.models.Entity;
import com.codename1.rad.models.EntityList;
import com.codename1.rad.models.EntityList.EntityAddedEvent;
import com.codename1.rad.models.EntityList.EntityRemovedEvent;
import com.codename1.testing.AbstractTest;
import java.util.Observer;

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
    
}
