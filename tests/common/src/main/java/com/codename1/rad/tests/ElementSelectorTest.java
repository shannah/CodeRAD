/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.tests;

import com.codename1.rad.io.ElementSelector;
import com.codename1.testing.AbstractTest;
import com.codename1.xml.Element;
import com.codename1.xml.XMLParser;
import java.io.StringReader;

/**
 *
 * @author shannah
 */
public class ElementSelectorTest extends AbstractTest {

    @Override
    public String toString() {
        return "ElementSelectorTest";
    }

    
    
    @Override
    public boolean runTest() throws Exception {
        String json = "<person name=\"Paul\" email=\"paul@example.com\" dob=\"December 27, 1978\">"
                + "<children><person name=\"Jim\" email=\"jim@example.com\" dob=\"January 10, 1979\"/>"
                + "<person name=\"Jill\" email=\"jill@example.com\" dob=\"January 11, 1979\"/></children></person>";
        XMLParser xparser = new XMLParser();
        Element root = xparser.parse(new StringReader("<?xml version='1.0'?>\n"+json));
        ElementSelector found = new ElementSelector("person", root);
        assertEqual(2, found.size(), "There should be 2 person elements");
        assertEqual(1, new ElementSelector("person[name=Jill]", root).size(), "Wrong count on search for Jill");
        assertEqual(1, new ElementSelector("person[name=Jim]", root).size(), "Wrong count on search for Jim");
        assertEqual(0, new ElementSelector("person[name=John]", root).size(), "Wrong count on search for John");
        assertEqual(1, new ElementSelector("person[name='Jill']", root).size(), "Wrong count on search for 'Jill'");
        assertEqual(1, new ElementSelector("person[name=\"Jill\"]", root).size(), "Wrong count on search for \"Jill\"");
        assertEqual(1, new ElementSelector("person[name!=\"Jill\"]", root).size(), "Wrong count on search for != \"Jill\"");
        assertEqual("Jim", new ElementSelector("person[name!=Jill]", root).getAttribute("name"), "Wrong name for query name!=Jill");
        assertEqual("Paul", new ElementSelector(root).getAttribute("name"), "Wrong name for root wrapped");
        assertEqual(1, new ElementSelector("person[name=Jill OR name=John]", root).size(), "Wrong count on search for Jill or John");
        assertEqual(2, new ElementSelector("person[name=Jill OR name=Jim]", root).size(), "Wrong count on search for Jill or John");
        assertEqual(2, new ElementSelector("person[name=Jill], person[name=Jim]", root).size(), "Wrong count compound search for Jill and John");
        assertEqual(0, new ElementSelector("person[name=Jill AND name=Jim]", root).size(), "Wrong count on search for Jill and John");
        assertEqual("Jill", new ElementSelector(root).getString("children/person[name=Jill]/@name", null), "getString() returned wrong value");
        assertEqual("Jill", new ElementSelector(root).getString(">children/person[name=Jill]/@name", null), "getString() returned wrong value which child selector");
        assertEqual("Jill", new ElementSelector(root).getString(">*/person[name=Jill]/@name", null), "getString() returned wrong value with > * selector");
        assertEqual("Jill", new ElementSelector(root).getString("*[name=Jill]/@name", null), "getString() returned wrong value with * selector");
        assertEqual("Jim", new ElementSelector(root).getString("*[name=Jim]/@name", null), "getString() returned wrong value with * selector");
        assertEqual("Jim", new ElementSelector(root).getString("person/@name", null), "Should return Jim because it is the first person.");
        assertEqual(1, new ElementSelector("person", root).getParent().size(), "All people have same parent, so size of getParent() should be 1");
        assertEqual("Paul", new ElementSelector("person", root).getParent().getParent().getString("@name", null), "Double parent should have name paul");
        return true;
        
    }
    
}
