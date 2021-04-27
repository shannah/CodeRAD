/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.tests;

import com.codename1.rad.models.*;

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
    public String toString() {
        return "EntityTypeTests";
    }

    
    
    
    @Override
    public boolean runTest() throws Exception {
        testCreateInstance();
        testCreateEntityTypesWithNoFactories();
        return true;
    }
    
    
    private void testCreateInstance() throws Exception {
        EntityType personType = new EntityTypeBuilder()
                .string(Thing.name)
                .build();
        class Person extends BaseEntity {
            {
                setEntityType(personType);
            }
        }
        EntityType.register(Person.class, cls->{
           return new Person();
        });

        
        Person p = (Person)EntityType.createEntityForClass(Person.class);
        assertTrue(p instanceof Person);
        
        class People extends EntityList<Person>{}
        EntityType.registerList(People.class, Person.class, cls->{return new People();});
        
        People p2 = (People)EntityType.createEntityForClass(People.class);
        assertTrue(p2 instanceof People);
        
        EntityType personType2 = p.getEntityType();
        assertTrue(personType2 == personType);
        
        EntityType peopleType = p2.getEntityType();
        assertTrue(peopleType.getRowEntityType() == personType);
        
        assertEqual(personType.getListEntityType(),peopleType);
        
        People p3 = (People)peopleType.newInstance();
        assertTrue(p3 instanceof People);
        
        
        
    }
    
    public static class StaticPerson extends BaseEntity {
        
    }
    static {
        EntityType.register(StaticPerson.class, new EntityTypeBuilder()
                .string(Thing.name).build()
        );
    }
    
    public static class StaticPeople extends EntityList<StaticPerson> {}
    static {
        EntityType.registerList(StaticPeople.class, StaticPerson.class);
    }
    
    
    private void testCreateEntityTypesWithNoFactories() {
        StaticPerson p = (StaticPerson)EntityType.createEntityForClass(StaticPerson.class);
        assertTrue(p instanceof StaticPerson);
        StaticPeople p2 = (StaticPeople)EntityType.createEntityForClass(StaticPeople.class);
        assertTrue(p2 instanceof StaticPeople);
        
        EntityType personType = p.getEntityType();
        
        EntityType peopleType = p2.getEntityType();
        assertTrue(peopleType.getRowEntityType() == personType);
        assertTrue(personType.getListEntityType() == peopleType);
        
        StaticPeople p3 = (StaticPeople)peopleType.newInstance();
        assertTrue(p3 instanceof StaticPeople);
    }
}
