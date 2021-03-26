/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.tests;

import com.codename1.rad.models.Entity;
import static com.codename1.rad.models.Entity.entityTypeBuilder;
import com.codename1.rad.models.EntityList;
import com.codename1.rad.models.EntityType;
import com.codename1.rad.schemas.Thing;
import com.codename1.rad.ui.entityviews.ProfileListView;
import com.codename1.testing.AbstractTest;
import com.codename1.ui.Component;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

/**
 *
 * @author shannah
 */
public class EntityListViewTest extends AbstractTest {

    @Override
    public boolean shouldExecuteOnEDT() {
        return true;
    }

    
    
    @Override
    public boolean runTest() throws Exception {
        testSortedListTransactions();
        return true;
    }

    @Override
    public String toString() {
        return "EntityListViewTest";
    }
    
    
    
    public static class SortedList<E> extends AbstractList<E> {
        private Comparator comparator;

        private ArrayList<E> internalList = new ArrayList<E>();
        
        public SortedList(Comparator<E> comp) {
            comparator = comp;
        }

        // Note that add(E e) in AbstractList is calling this one
        @Override 
        public void add(int position, E e) {
            internalList.add(e);
            Collections.sort(internalList, comparator);
        }


        @Override
        public E get(int i) {
            return internalList.get(i);
        }

        @Override
        public int size() {
            return internalList.size();
        }

    }
    
    private void testSortedListTransactions() {
        SortedList<Entity> internal = new SortedList<Entity>(new Comparator<Entity>() {
            @Override
            public int compare(Entity a, Entity b) {
                String name1 = a.getText(Thing.name);
                if (name1 == null) name1 = "";
                String name2 = b.getText(Thing.name);
                if (name2 == null) name2 = "";
                return name1.compareTo(name2);
            }
            
        });
        
        EntityList el = new EntityList() {
            @Override
            protected List createInternalList() {
                return internal;
            }
            
        };
        
        class Person extends Entity {}
        entityTypeBuilder(Person.class)
                .string(Thing.name)
                .factory(cls -> {return new Person();})
                .build();
        
        Person a = new Person();
        a.set(Thing.name, "A");
        
        Person b = new Person();
        b.set(Thing.name, "B");
        
        Person c = new Person();
        c.set(Thing.name, "C");
        
        Person d = new Person();
        d.set(Thing.name, "D");
        
        
        ProfileListView view = new ProfileListView(el);
        view.bind();
        assertEqual(0, view.getScrollWrapper().getComponentCount());
        el.add(b);
        assertEqual(1, view.getScrollWrapper().getComponentCount());
        assertNotNull(view.getRowViewForEntity(b));
        assertNull(view.getRowViewForEntity(a));
        
        el.add(a);
        assertEqual(0, el.indexOf(a));
        assertEqual(1, el.indexOf(b));
        assertEqual(2, view.getScrollWrapper().getComponentCount());
        assertEqual(0, view.getScrollWrapper().getComponentIndex((Component)view.getRowViewForEntity(a)));
        assertEqual(1, view.getScrollWrapper().getComponentIndex((Component)view.getRowViewForEntity(b)));
        
        view.unbind();
        
             
    }
    
}
