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
        manualExampleTest();
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
            EntityType.register(
                    PersonModel.class, 
                    PublicationModel.class,
                    Publications.class,
                    People.class
            );
   
            

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
    
    
    private void manualExampleTest() throws Exception {
        String xml = "<?xml version=\"1.0\"?>\n" +
"<catalog>\n" +
"   <book id=\"bk101\">\n" +
"      <author>Gambardella, Matthew</author>\n" +
"      <title>XML Developer's Guide</title>\n" +
"      <genre>Computer</genre>\n" +
"      <price>44.95</price>\n" +
"      <publish_date>2000-10-01</publish_date>\n" +
"      <description>An in-depth look at creating applications \n" +
"      with XML.</description>\n" +
"   </book>\n" +
"   <book id=\"bk102\">\n" +
"      <author>Ralls, Kim</author>\n" +
"      <title>Midnight Rain</title>\n" +
"      <genre>Fantasy</genre>\n" +
"      <price>5.95</price>\n" +
"      <publish_date>2000-12-16</publish_date>\n" +
"      <description>A former architect battles corporate zombies, \n" +
"      an evil sorceress, and her own childhood to become queen \n" +
"      of the world.</description>\n" +
"   </book>\n" +
"   <book id=\"bk103\">\n" +
"      <author>Corets, Eva</author>\n" +
"      <title>Maeve Ascendant</title>\n" +
"      <genre>Fantasy</genre>\n" +
"      <price>5.95</price>\n" +
"      <publish_date>2000-11-17</publish_date>\n" +
"      <description>After the collapse of a nanotechnology \n" +
"      society in England, the young survivors lay the \n" +
"      foundation for a new society.</description>\n" +
"   </book>\n" +
"   <book id=\"bk104\">\n" +
"      <author>Corets, Eva</author>\n" +
"      <title>Oberon's Legacy</title>\n" +
"      <genre>Fantasy</genre>\n" +
"      <price>5.95</price>\n" +
"      <publish_date>2001-03-10</publish_date>\n" +
"      <description>In post-apocalypse England, the mysterious \n" +
"      agent known only as Oberon helps to create a new life \n" +
"      for the inhabitants of London. Sequel to Maeve \n" +
"      Ascendant.</description>\n" +
"   </book>\n" +
"   <book id=\"bk105\">\n" +
"      <author>Corets, Eva</author>\n" +
"      <title>The Sundered Grail</title>\n" +
"      <genre>Fantasy</genre>\n" +
"      <price>5.95</price>\n" +
"      <publish_date>2001-09-10</publish_date>\n" +
"      <description>The two daughters of Maeve, half-sisters, \n" +
"      battle one another for control of England. Sequel to \n" +
"      Oberon's Legacy.</description>\n" +
"   </book>\n" +
"   <book id=\"bk106\">\n" +
"      <author>Randall, Cynthia</author>\n" +
"      <title>Lover Birds</title>\n" +
"      <genre>Romance</genre>\n" +
"      <price>4.95</price>\n" +
"      <publish_date>2000-09-02</publish_date>\n" +
"      <description>When Carla meets Paul at an ornithology \n" +
"      conference, tempers fly as feathers get ruffled.</description>\n" +
"   </book>\n" +
"   <book id=\"bk107\">\n" +
"      <author>Thurman, Paula</author>\n" +
"      <title>Splish Splash</title>\n" +
"      <genre>Romance</genre>\n" +
"      <price>4.95</price>\n" +
"      <publish_date>2000-11-02</publish_date>\n" +
"      <description>A deep sea diver finds true love twenty \n" +
"      thousand leagues beneath the sea.</description>\n" +
"   </book>\n" +
"   <book id=\"bk108\">\n" +
"      <author>Knorr, Stefan</author>\n" +
"      <title>Creepy Crawlies</title>\n" +
"      <genre>Horror</genre>\n" +
"      <price>4.95</price>\n" +
"      <publish_date>2000-12-06</publish_date>\n" +
"      <description>An anthology of horror stories about roaches,\n" +
"      centipedes, scorpions  and other insects.</description>\n" +
"   </book>\n" +
"   <book id=\"bk109\">\n" +
"      <author>Kress, Peter</author>\n" +
"      <title>Paradox Lost</title>\n" +
"      <genre>Science Fiction</genre>\n" +
"      <price>6.95</price>\n" +
"      <publish_date>2000-11-02</publish_date>\n" +
"      <description>After an inadvertant trip through a Heisenberg\n" +
"      Uncertainty Device, James Salway discovers the problems \n" +
"      of being quantum.</description>\n" +
"   </book>\n" +
"   <book id=\"bk110\">\n" +
"      <author>O'Brien, Tim</author>\n" +
"      <title>Microsoft .NET: The Programming Bible</title>\n" +
"      <genre>Computer</genre>\n" +
"      <price>36.95</price>\n" +
"      <publish_date>2000-12-09</publish_date>\n" +
"      <description>Microsoft's .NET initiative is explored in \n" +
"      detail in this deep programmer's reference.</description>\n" +
"   </book>\n" +
"   <book id=\"bk111\">\n" +
"      <author>O'Brien, Tim</author>\n" +
"      <title>MSXML3: A Comprehensive Guide</title>\n" +
"      <genre>Computer</genre>\n" +
"      <price>36.95</price>\n" +
"      <publish_date>2000-12-01</publish_date>\n" +
"      <description>The Microsoft MSXML3 parser is covered in \n" +
"      detail, with attention to XML DOM interfaces, XSLT processing, \n" +
"      SAX and more.</description>\n" +
"   </book>\n" +
"   <book id=\"bk112\">\n" +
"      <author>Galos, Mike</author>\n" +
"      <title>Visual Studio 7: A Comprehensive Guide</title>\n" +
"      <genre>Computer</genre>\n" +
"      <price>49.95</price>\n" +
"      <publish_date>2001-04-16</publish_date>\n" +
"      <description>Microsoft Visual Studio 7 is explored in depth,\n" +
"      looking at how Visual Basic, Visual C++, C#, and ASP+ are \n" +
"      integrated into a comprehensive development \n" +
"      environment.</description>\n" +
"   </book>\n" +
"</catalog>";
        
        EntityType bookType = new EntityTypeBuilder()
            .string(Thing.identifier)
            .string(Thing.name)
            .string(Thing.description)
            .build();
        class Book extends Entity {}
        EntityType.register(Book.class, bookType, cls->{return new Book();});
        class Books extends EntityList<Book> {}
        EntityType.registerList(Books.class, Book.class, cls->{return new Books();});
        
        
        
        Tag BOOKS = new Tag("Books");
        EntityType catalogType = new EntityTypeBuilder()
            .list(Books.class, BOOKS)
            .build();
        class Catalog extends Entity {{setEntityType(catalogType);}}
        EntityType.register(Catalog.class, cls->{return new Catalog();});
       
        
        
        ResultParser parser = new ResultParser(catalogType)
            .property("./book", BOOKS)
            .entityType(bookType)
            .property("@id", Thing.identifier)
            .property("title", Thing.name)
            .property("description", Thing.description);


        Catalog catalog = (Catalog)parser.parseXML(xml, new Catalog());
        Books books = (Books)catalog.get(BOOKS);
        assertEqual("bk101", books.get(0).get(Thing.identifier));
        assertEqual("bk112", books.get(books.size()-1).get(Thing.identifier));
        assertEqual("XML Developer's Guide", books.get(0).get(Thing.name));
        
        
        
    }

}
