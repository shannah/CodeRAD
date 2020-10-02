/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.tests;

import com.codename1.rad.models.Entity;
import com.codename1.rad.models.EntityType;
import com.codename1.rad.models.EntityTypeBuilder;
import com.codename1.rad.schemas.Thing;
import com.codename1.testing.AbstractTest;

/**
 *
 * @author shannah
 */
public class EntityTypeTests extends AbstractTest {

    @Override
    public boolean shouldExecuteOnEDT() {
        return true;
    }

    
    
    @Override
    public boolean runTest() throws Exception {
        testCreateInstance();
        return true;
    }
    
    
    private void testCreateInstance() throws Exception {
        EntityType personType = new EntityTypeBuilder()
                .string(Thing.name)
                .build();
        class Person extends Entity {
            {
                setEntityType(personType);
            }
        }
        EntityType.register(Person.class, cls->{
            if (cls == Person.class) {
                return new Person();
            }
            return null;
        });

        
        Person p = (Person)EntityType.createEntityForClass(Person.class);
        assertTrue(p instanceof Person);
        
    }
    
}
