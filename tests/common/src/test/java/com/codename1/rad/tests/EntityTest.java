/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.tests;
import com.codename1.rad.models.BaseEntity;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.EntityType;
import com.codename1.rad.schemas.ChatMessage;
import com.codename1.testing.AbstractTest;
import java.util.Date;
/**
 *
 * @author shannah
 */
public class EntityTest extends AbstractTest {
    
    static class MyEntity extends BaseEntity {
        public static final EntityType TYPE = new EntityType(){{
            date(ChatMessage.dateCreated);
        }};
        
        {
            setEntityType(TYPE);
        }
    }
    
    // https://github.com/shannah/CodeRAD/issues/1
    private void testSetDate1() throws Exception {
        MyEntity e = new MyEntity();
        Date d = new Date();
        e.setDate(ChatMessage.dateCreated, d);
        assertEqual(d, e.get(ChatMessage.dateCreated));
        assertEqual(d, e.getDate(ChatMessage.dateCreated));
    }

    @Override
    public boolean runTest() throws Exception {
        testSetDate1();
        return true;
    }

    @Override
    public boolean shouldExecuteOnEDT() {
        return true;
    }



}




