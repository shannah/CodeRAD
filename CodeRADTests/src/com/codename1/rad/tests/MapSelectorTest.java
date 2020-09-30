/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.tests;

import com.codename1.rad.io.MapSelector;
import com.codename1.testing.AbstractTest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author shannah
 */
public class MapSelectorTest extends AbstractTest {

    @Override
    public boolean runTest() throws Exception {
        Map root = new HashMap();
        root.put("name", "Paul");
        root.put("email", "paul@example.com");
        
        List children = new ArrayList();
        root.put("children", children);
        
        Map jim = new HashMap();
        jim.put("name", "Jim");
        jim.put("email", "jim@example.com");
        children.add(jim);
        
        Map jill = new HashMap();
        jim.put("name", "Jill");
        jim.put("email", "jill@example.com");
        children.add(jim);
        
        assertEqual(1, new MapSelector("children[name=Jim]", root).size());
        return true;
    }
    
}
