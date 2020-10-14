/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.tests;

import com.codename1.rad.models.Entity;
import com.codename1.rad.models.EntityType;
import static com.codename1.rad.models.EntityType.tags;
import com.codename1.rad.models.EntityTypeBuilder;
import com.codename1.rad.models.Property;
import com.codename1.rad.models.StringProperty;
import com.codename1.rad.schemas.ChatMessage;
import com.codename1.rad.schemas.Comment;
import com.codename1.rad.schemas.Person;
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
        testChatMessageEntities();
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
    
    private void testChatMessageEntities() throws Exception {
        ChatMessageModel chatMessage = new ChatMessageModel();
        
        Object res = chatMessage.get(ChatMessage.datePublished);
        assertNull(res);
        
        Property p = chatMessage.TYPE.findProperty(ChatMessage.datePublished);
        assertNotNull(p);
        
        res = chatMessage.get(p);
        assertNull(res);
        Date dt = new Date();
        chatMessage.set(p, dt);
        
        res = chatMessage.get(p);
        assertEqual(dt, res);
        
        res = chatMessage.get(ChatMessage.datePublished);
        assertEqual(dt, res);
        
        p = chatMessage.findProperty(Comment.datePublished, Comment.dateCreated, Comment.dateModified);
        
        assertNotNull(p);
        res = chatMessage.get(p);
        assertEqual(dt, res);
        
    }
    
}



class ChatAccount extends Entity {
    
    // The name property
    public static StringProperty name, thumbnailUrl, phone;
    
    private static final EntityType TYPE = new EntityType() {{
        name = string(tags(Thing.name));
        thumbnailUrl = string(tags(Thing.thumbnailUrl));
        phone = string(tags(Person.telephone));
    }};
    {
        setEntityType(TYPE);
    }
    
    public ChatAccount(String nm, String thumb, String phoneNum) {
        set(name, nm);
        set(thumbnailUrl, thumb);
        set(phone, phoneNum);
    }
    
}

class ChatMessageModel extends Entity {
    public static final EntityType TYPE = new EntityType(){{
        string(ChatMessage.text);
        date(ChatMessage.datePublished);
        entity(ChatAccount.class, ChatMessage.creator);
        Boolean(ChatMessage.isOwnMessage);
        Boolean(ChatMessage.isFavorite);
        string(ChatMessage.attachmentPlaceholderImage);
    }};
    {
        setEntityType(TYPE);
        
    }
    
}

