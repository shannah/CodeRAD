/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.ui.Image;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A cache that keeps a strong reference to its items.
 * @author shannah
 */
public class StrongCache implements Iterable<String> {
    private Map<String,Object> cache = new HashMap<>();
    
    
    public Object get(String name) {
        return cache.get(name);
    }
    
   
    public void set(String name, Object o) {
        cache.put(name, o);
    }
    
    public void clear() {
        cache.clear();
    }

    @Override
    public Iterator<String> iterator() {
        return cache.keySet().iterator();
    }
    
    public void remove(String name) {
        cache.remove(name);
    }
}
