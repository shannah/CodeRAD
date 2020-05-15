/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.tests;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.EntityList;
import com.codename1.rad.models.EntityType;
import com.codename1.rad.schemas.ChatMessage;
import com.codename1.testing.AbstractTest;
import java.util.Date;
import java.util.List;
/**
 *
 * @author shannah
 */
public class EntityListTest extends AbstractTest {
    
    static class MyEntity extends Entity {
        public static final EntityType TYPE = new EntityType(){{
            date(ChatMessage.dateCreated);
        }};
        
        {
            setEntityType(TYPE);
        }
    }
    
    // https://github.com/shannah/CodeRAD/issues/1
    private void testList() throws Exception {
        MyEntity e = new MyEntity();
        EntityList list = new EntityList();
        assertTrue(list.size() == 0);
        list.add(e);
        assertTrue(list.size() == 1);
        
        assertEqual(e, list.get(0));
        assertEqual(e, list.iterator().next());
        list.remove(e);
        assertTrue(list.size() == 0);
        
        
    }

    @Override
    public boolean runTest() throws Exception {
        testList();
        return true;
    }

    @Override
    public boolean shouldExecuteOnEDT() {
        return true;
    }



}




