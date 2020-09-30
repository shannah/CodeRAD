/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.tests;

import com.codename1.io.Log;
import com.codename1.l10n.ParseException;
import com.codename1.l10n.SimpleDateFormat;
import com.codename1.rad.processing.Result;
import com.codename1.rad.io.ResultParser;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.EntityList;
import com.codename1.rad.models.EntityType;
import com.codename1.rad.models.EntityTypeBuilder;
import com.codename1.rad.models.Tag;
import com.codename1.rad.schemas.Person;
import com.codename1.rad.schemas.Thing;
import com.codename1.testing.AbstractTest;
import com.codename1.xml.Element;
import com.codename1.xml.XMLParser;
import java.io.StringReader;

/**
 *
 * @author shannah
 */
public class ResultParserTest extends AbstractTest {

    @Override
    public String toString() {
        return "ResultParserTest";
    }

    
    
    @Override
    public boolean shouldExecuteOnEDT() {
        return true;
    }

    
    
    @Override
    public boolean runTest() throws Exception {
        testResult();
        testResultJSON();
        simpleJSONTest();
        nestedJSONTest();
        dateFormatTest();
        
        testGetTagBodyAsString();
        dateFormatXMLTest();
        dateFormatXMLAttsTest();
        nestedXMLAttsTest();
        return true;
    }
    
    private void nestedJSONTest() throws Exception {
        EntityType personType = new EntityTypeBuilder()
                .string(Person.name)
                .string(Person.email)
                .Date(Person.birthDate)
                .list(Publications.class, publications)
                .build();
        
        ResultParser parser = new ResultParser(personType)
                .property("name", Person.name)
                .property("email", Person.email)
                .property("dob", Person.birthDate, dateStr -> {
                    if (!(dateStr instanceof String)) {
                        return null;
                    }
                    String str = (String)dateStr;
                    if (str.isEmpty()) {
                        return null;
                    }
                    SimpleDateFormat fmt = new SimpleDateFormat("MMM d, yyyy");
                    try {
                        return fmt.parse(str);
                    } catch (ParseException ex) {
                        Log.e(ex);
                        return null;
                    }
                    
                })
                .property("publications", publications)
                ;
        ResultParser publicationParser = new ResultParser(publicationType)
                .property("name", Thing.name)
                .property("description", Thing.description);
        parser.add(publicationParser);
        
        
        
        String json = "{\"name\":\"Paul\", "
                + "\"email\":\"paul@example.com\", "
                + "\"dob\" : \"December 27, 1978\", "
                + "\"publications\" : ["
                + "  {\"name\":\"Time Magazine\", \"description\" : \"Political and current event stories\"}, "
                + "  {\"name\":\"Vancouver Sun\"}"
                + "]"
                + "}";
        
        Entity person = parser.parseRow(Result.fromContent(json, Result.JSON), personType.newInstance());
        assertEqual("Paul", person.getText(Person.name));
        assertEqual("paul@example.com", person.getText(Person.email));
        
        
        Publications pubs = (Publications)person.get(publications);
        assertEqual(2, pubs.size());
        assertEqual("Time Magazine", pubs.get(0).get(Thing.name));
        assertEqual("Vancouver Sun", pubs.get(1).get(Thing.name));
        assertEqual("Political and current event stories", pubs.get(0).get(Thing.description));
        
    }
    
    private void simpleJSONTest() throws Exception {
        EntityType personType = new EntityTypeBuilder()
                .string(Person.name)
                .string(Person.email)
                .Date(Person.birthDate)
                .build();
        
        ResultParser parser = new ResultParser(personType)
                .property("name", Person.name)
                .property("email", Person.email)
                .property("dob", Person.birthDate, dateStr -> {
                    if (!(dateStr instanceof String)) {
                        return null;
                    }
                    String str = (String)dateStr;
                    if (str.isEmpty()) {
                        return null;
                    }
                    SimpleDateFormat fmt = new SimpleDateFormat("MMM d, yyyy");
                    try {
                        return fmt.parse(str);
                    } catch (ParseException ex) {
                        Log.e(ex);
                        return null;
                    }
                    
                })
                ;
        
        String json = "{\"name\":\"Paul\", \"email\":\"paul@example.com\", \"dob\" : \"December 27, 1978\"}";
        Entity person = parser.parseRow(Result.fromContent(json, Result.JSON), personType.newInstance());
        assertEqual("Paul", person.getText(Person.name));
        assertEqual("paul@example.com", person.getText(Person.email));
        
    }
    
    private void dateFormatTest() throws Exception {
        EntityType personType = new EntityTypeBuilder()
                .string(Person.name)
                .string(Person.email)
                .Date(Person.birthDate)
                .build();
        
        ResultParser parser = new ResultParser(personType)
                .property("name", Person.name)
                .property("email", Person.email)
                .property("dob", Person.birthDate, new SimpleDateFormat("MMM d, yyyy"))
                ;
        
        String json = "{\"name\":\"Paul\", \"email\":\"paul@example.com\", \"dob\" : \"December 27, 1978\"}";
        Entity person = parser.parseRow(Result.fromContent(json, Result.JSON), personType.newInstance());
        assertEqual("Paul", person.getText(Person.name));
        assertEqual("paul@example.com", person.getText(Person.email));
        
    }
    
    private void dateFormatXMLTest() throws Exception {
        EntityType personType = new EntityTypeBuilder()
                .string(Person.name)
                .string(Person.email)
                .Date(Person.birthDate)
                .build();
        
        ResultParser parser = new ResultParser(personType)
                .property("/person/name", Person.name)
                .property("/person/email", Person.email)
                .property("/person/dob", Person.birthDate, new SimpleDateFormat("MMM d, yyyy"))
                ;
        
        String json = "<person><name>Paul</name> <email>paul@example.com</email> <dob>December 27, 1978</dob></person>";
        XMLParser xparser = new XMLParser();
        Element root = xparser.parse(new StringReader("<?xml version='1.0'?>\n"+json));
        Entity person = parser.parseRow(Result.fromContent(root), personType.newInstance());
        assertEqual("Paul", person.getText(Person.name));
        assertEqual("paul@example.com", person.getText(Person.email));
        
    }
    
    private void dateFormatXMLAttsTest() throws Exception {
        EntityType personType = new EntityTypeBuilder()
                .string(Person.name)
                .string(Person.email)
                .Date(Person.birthDate)
                .build();
        
        ResultParser parser = new ResultParser(personType)
                .property("/person[0]/@name", Person.name)
                .property("/person[0]/@email", Person.email)
                .property("/person[0]/@dob", Person.birthDate, new SimpleDateFormat("MMM d, yyyy"))
                ;
        
        String json = "<person name=\"Paul\" email=\"paul@example.com\" dob=\"December 27, 1978\"></person>";
        XMLParser xparser = new XMLParser();
        Element root = xparser.parse(new StringReader("<?xml version='1.0'?>\n"+json));
        Entity person = parser.parseRow(Result.fromContent(root), personType.newInstance());
        assertEqual("Paul", person.getText(Person.name));
        assertEqual("paul@example.com", person.getText(Person.email));
        
    }
        public static final Tag publications = new Tag("Publications");
        
        public static EntityType personType = new EntityTypeBuilder()
            .string(Person.name)
            .string(Person.email)
            .Date(Person.birthDate)
            .list(People.class, Person.children)
            .list(Publications.class, publications)
            .build();
        
        public static EntityType publicationType = new EntityTypeBuilder()
                .string(Thing.name)
                .string(Thing.description)
                .build();
        
        
        public static class PersonModel extends Entity {
            
            {
                setEntityType(personType);
            }
        }
        
        
        public static class People extends EntityList<PersonModel> {
            {
                setRowType(personType);
            }
        }
        
        public static class PublicationModel extends Entity {
            {
                setEntityType(publicationType);
            }
        }
        
        public static class Publications extends EntityList<PublicationModel> {
            {
                setRowType(publicationType);
            }
        }
        
        static {
            new PersonModel();
            new PublicationModel();
        }
        
        
    private void testResult() throws Exception {
        String json = "<person name=\"Paul\" email=\"paul@example.com\" dob=\"December 27, 1978\">"
                + "<children><person name=\"Jim\" email=\"jim@example.com\" dob=\"January 10, 1979\"/>"
                + "<person name=\"Jill\" email=\"jill@example.com\" dob=\"January 11, 1979\"/></children></person>";
        XMLParser xparser = new XMLParser();
        Element root = xparser.parse(new StringReader("<?xml version='1.0'?>\n"+json));
        assertEqual("Paul", root.getAttribute("name"));
        Result r = Result.fromContent(root);
        
        assertEqual("Paul", r.get("/person/@name"));
        assertEqual("Paul", r.getAsString("./@name"));
        assertEqual("Paul", r.getAsString("@name"));
        assertEqual("Paul", r.get("/person[0]/@name"));
        assertEqual("Jim", r.get("./children/person[0]/@name"));
        assertEqual("Jim", r.getAsString("./children/person/@name"));
        assertEqual("Jim", r.getAsString("./children[0]/person/@name"));
        assertEqual(2, r.getAsStringArray("./children/person/@name").length);
        assertEqual("Jim", r.get("/person/children/person[0]/@name"));
        assertEqual("Jim", r.getAsString("/person[0]/children/person/@name"));
        assertEqual("Jim", r.getAsString("children[0]/person/@name"));
        assertEqual(2, r.getAsStringArray("children/person/@name").length);
        
        
        
    }
    
    private void testResultJSON() throws Exception {
        String json = "{\"name\":\"Paul\", \"email\":\"paul@example.com\", \"dob\":\"December 27, 1978\" "
                + ", \"children\": [{\"name\":\"Jim\", \"email\":\"jim@example.com\", \"dob\":\"January 10, 1979\"},"
                + "{\"name\"=\"Jill\", \"email\"=\"jill@example.com\", \"dob\":\"January 11, 1979\"}]}";
        
        
        Result r = Result.fromContent(json, Result.JSON);
        
        assertEqual("Paul", r.get("name"));
        assertEqual("Paul", r.getAsString("name"));
        assertEqual("Paul", r.get("name"));
        assertEqual("Jim", r.get("./children[0]/name"));
        assertEqual("Jim", r.get("children[0]/name"));
        assertNull(r.get("./children/person[0]/name"));
        assertNull(r.getAsString("./children/person/name"));
        assertEqual("Jim", r.getAsString("./children[0]/name"));
        assertEqual(2, r.getAsStringArray("./children/name").length);
        assertArrayEqual(new String[]{"Jim", "Jill"}, r.getAsStringArray("./children/name"));
        assertEqual("Jim", r.get("./children/name"));
        assertNull(r.getAsString("children/person/name"));
        assertNull(r.getAsString("children[0]/person/name"));
        assertEqual(0, r.getAsStringArray("children/person/name").length);
        
        
        
    }
    
    private void testGetTagBodyAsString() throws Exception {
        String xml = "<?xml version=\"1.0\"?>\n<content>This is text content</content>";
        Result r = Result.fromContent(xml, Result.XML);
        String content = "This is text content";
        assertEqual(content, r.getAsString("/content"));
        assertEqual(content, r.getAsString("/content/text()"));
        assertEqual(content, r.get("/content/text()"));
        assertEqual(content, r.getAsString("./text()"));
        assertEqual(content, r.getAsString("."));
        
        xml = "<?xml version=\"1.0\"?>\n<a><b><c>Hello</c><d>World</d></b</a>";
        r = Result.fromContent(xml, Result.XML);
        assertEqual("Hello", r.getAsString("./b/c"));
        assertEqual("World", r.getAsString("./b/d"));
        assertEqual("Hello", r.getAsString("/a/b/c"));
        assertEqual("Hello", r.getAsString("//a/b/c"));
        assertEqual("Hello", r.get("./b/c/text()"));
    }
    
    
    
        // Disabling nested XML Atts tests because I can't figure out how
        // Result works with XML...
    private void nestedXMLAttsTest() throws Exception {
        
        assertTrue(personType.newInstance() instanceof PersonModel, "persontype should create a Person, but created "+personType.newInstance());
        
        ResultParser parser = new ResultParser(personType)
                .property("@name", Person.name)
                .property("@email", Person.email)
                .property("@dob", Person.birthDate, new SimpleDateFormat("MMM d, yyyy"))
                .property("./children/person", Person.children)
                .property("./publication", publications)
                ;
        
        ResultParser publicationParser = new ResultParser(publicationType)
                .property("@name", Thing.name)
                .property(".", Thing.description);
        parser.add(publicationParser);
        
        String json = "<person name=\"Paul\" email=\"paul@example.com\" dob=\"December 27, 1978\">"
                + "<children><person name=\"Jim\" email=\"jim@example.com\" dob=\"January 10, 1979\"/>"
                + "<person name=\"Jill\" email=\"jill@example.com\" dob=\"January 11, 1979\"/></children><publication name=\"Time Magazine\">Political and current event stories</publication>"
                + "<publication name=\"Vancouver Sun\"/></person>";
        XMLParser xparser = new XMLParser();
        Element root = xparser.parse(new StringReader("<?xml version='1.0'?>\n"+json));
        Entity person = parser.parseRow(Result.fromContent(root), personType.newInstance());
        assertEqual("Paul", person.getText(Person.name));
        assertEqual("paul@example.com", person.getText(Person.email));
        
        People children = (People)person.get(Person.children);
        assertEqual(2, children.size());
        assertEqual("Jim", children.get(0).get(Person.name));
        assertEqual("jim@example.com", children.get(0).get(Person.email));
        assertEqual("Jill", children.get(1).get(Person.name));
        assertEqual("jill@example.com", children.get(1).get(Person.email));
        
        Publications pubs = (Publications)person.get(publications);
        assertEqual(2, pubs.size());
        assertEqual("Time Magazine", pubs.get(0).get(Thing.name));
        assertEqual("Vancouver Sun", pubs.get(1).get(Thing.name));
        assertEqual("Political and current event stories", pubs.get(0).get(Thing.description));
        
    }

}
