/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.tests;

import com.codename1.rad.controllers.ActionSupport;
import com.codename1.rad.controllers.Controller;
import com.codename1.rad.controllers.ControllerEvent;
import com.codename1.testing.AbstractTest;

/**
 *
 * @author shannah
 */
public class ControllerTest extends AbstractTest {

    @Override
    public boolean shouldExecuteOnEDT() {
        return true;
    }

    
    
    @Override
    public String toString() {
        return "ControllerTest";
    }

    
    
    @Override
    public boolean runTest() throws Exception {
        basicControllerTest();
        return true;
    }
    
    private void basicControllerTest() throws Exception {
        Controller parent = new Controller(null);
        Controller child = new Controller(parent);
        assertNull(parent.getParent());
        assertEqual(parent, child.getParent());
        
        class SharedObject {
            String name;
        }
        
        SharedObject shared = new SharedObject();
        shared.name = "Hello";
        parent.addLookup(shared);
        
        assertEqual(shared, child.lookup(SharedObject.class));
        assertEqual(shared, parent.lookup(SharedObject.class));
        child.setParent(null);
        assertNull(child.lookup(SharedObject.class));
        assertEqual(shared, parent.lookup(SharedObject.class));
        child.setParent(parent);
        
        class Stats {
            ControllerEvent lastEvent;
            
        }
        Stats stats = new Stats();
        
        parent.addEventListener(evt->{;
            stats.lastEvent = evt;
        });
        
        ControllerEvent e = new ControllerEvent(child);
        ActionSupport.dispatchEvent(e);
        
        assertNotNull(stats.lastEvent);
        assertEqual(stats.lastEvent, e);
        
        ControllerEvent e2 = new ControllerEvent(child);
        
        child.addEventListener(evt-> {
            if (evt == e2) {
                evt.consume();
            }
        });
        
        ActionSupport.dispatchEvent(e2);
        assertEqual(stats.lastEvent, e);
        assertTrue(e2.isConsumed());
        
        
        
    }
    
}
