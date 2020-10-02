/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.tests;

import com.codename1.rad.models.Entity;
import com.codename1.rad.models.EntityType;
import com.codename1.rad.models.EntityTypeBuilder;
import com.codename1.rad.schemas.Product;
import com.codename1.rad.schemas.Thing;
import com.codename1.testing.AbstractTest;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author shannah
 */
public class EntityTests extends AbstractTest {

    @Override
    public boolean shouldExecuteOnEDT() {
        return true;
    }

    @Override
    public String toString() {
        return "EntityTests";
    }

    
    
    
    @Override
    public boolean runTest() throws Exception {
        gettersAndSettersTest();
        return true;
    }
    
    private void gettersAndSettersTest() throws Exception {
        class MyProduct extends Entity {}
        EntityType productType = new EntityTypeBuilder()
                .string(Thing.name)
                .Double(Product.width)
                .Double(Product.height)
                .Date(Product.releaseDate)
                .entity(MyProduct.class, Product.isRelatedTo)
                .entity(MyProduct.class, Product.isVariantOf)
                .build();
        EntityType.register(MyProduct.class, productType, cls->{
            return new MyProduct();
        });
        
        MyProduct p1 = new MyProduct();
        assertNull(p1.get(Thing.name));
        assertNull(p1.getText(Thing.name));
        assertTrue(p1.isFalsey(Thing.name));
        assertTrue(p1.isEmpty(Thing.name));
        
        String name = "Stove";
        p1.set(Thing.name, name);
        assertEqual(name, p1.get(Thing.name));
        assertEqual(name, p1.getText(Thing.name));
        assertTrue(!p1.isEmpty(Thing.name));
        assertTrue(!p1.isFalsey(Thing.name));
        
        p1.setText(Thing.name, "");
        assertEqual("", p1.getText(Thing.name));
        assertEqual("", p1.get(Thing.name));
        assertTrue(p1.isEmpty(Thing.name));
        assertTrue(p1.isFalsey(Thing.name));
        
        double w = 10;
        assertNull(p1.get(Product.width));
        assertTrue(p1.isFalsey(Product.width));
        assertTrue(p1.isEmpty(Product.width));
        p1.set(Product.width, w);
        assertEqual(w, p1.getDouble(Product.width));
        assertEqual(w, p1.get(Product.width));
        assertTrue(!p1.isFalsey(Product.width));
        assertTrue(!p1.isEmpty(Product.width));
        
        w = 0;
        
        p1.set(Product.width, w);
        assertEqual(w, p1.getDouble(Product.width));
        assertEqual(w, p1.get(Product.width));
        assertTrue(p1.isFalsey(Product.width));
        assertTrue(!p1.isEmpty(Product.width));
        
        
       
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2020);
        cal.set(Calendar.MONTH, 1); // Feb
        cal.set(Calendar.DAY_OF_MONTH, 15);
        cal.set(Calendar.HOUR_OF_DAY, 10);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 35);
        
        Date d = cal.getTime();
        
        p1.set(Product.releaseDate, d);
        assertEqual(d, p1.get(Product.releaseDate));
        assertEqual(d, p1.getDate(Product.releaseDate));
        
        MyProduct p2 = new MyProduct();
        p2.set(Thing.name, "Fridge");
        p1.set(Product.isRelatedTo, p2);
        assertEqual(p2, p1.get(Product.isRelatedTo));
        assertEqual(p2, p1.getEntity(Product.isRelatedTo));
        
        
    }
    
}
