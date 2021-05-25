/*
 * Copyright 2020 shannah.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codename1.rad.io;

import com.codename1.io.Log;
import com.codename1.l10n.ParseException;
import com.codename1.l10n.SimpleDateFormat;
import com.codename1.rad.processing.Result;
import com.codename1.rad.models.ContentType;

import com.codename1.rad.models.EntityFactory;
import com.codename1.rad.models.EntityList;
import com.codename1.rad.models.EntityProperty;
import com.codename1.rad.models.EntityType;
import com.codename1.rad.models.Property;
import com.codename1.rad.models.Tag;
import com.codename1.xml.Element;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.codename1.rad.models.Entity;

/**
 * A class that can parse XML or JSON data structures into Entity objects.
 * 
 * === Example 1:
 * 
 * [source,java]
 * ----
 * 
 *   public static final Tag publications = new Tag("Publications");
        
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
        ...
        * 
 *  private void nestedXMLAttsTest() throws Exception {
        
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
    ----
 * 
 * @author shannah
 */
public class ResultParser implements EntityFactory {
    private String rowsSelector="root";
    private ArrayList<PropertyParser> propertyParsers = new ArrayList<>();
    private ResultParser parent;
    private final EntityType entityType;
    private Map<EntityType,ResultParser> parserMap;
    
    /**
     * Creates a new ResultParser for the given entity type.
     * @param type The entity type that this parser can parse to.
     */
    public ResultParser(EntityType type) {
        this.entityType = type;
        
    }
   
    /**
     * Gets the root parser of this parser group.  Multiple parsers can be grouped togther so that XML or JSON structures
     * which include more than one type of entity can all be parsed with one parser group.  Parsers are grouped together using the 
     * {@link #add(com.codename1.rad.io.ResultParser...) } method, but the "first" parser in a group is always considered to 
     * be the root parser.
     * 
     * If you call {@link #add(com.codename1.rad.io.ResultParser...) } on a child parser of the group, it will actually add the
     * parser to the root parser, so that the parser group forms a single level tree with a root and a number of leaves.
     * 
     * @return The root result parser in the group.
     */
    public ResultParser getRootParser() {
        if (parent == null) {
            return this;
        }
        return parent.getRootParser();
    }
    
    /**
     * Adds parsers to the parser group  Parsers can be grouped together so that XML/JSON documents that contain more than
     * one type of entity can be parsed correctly.
     * @param parsers The The parsers to add.
     * @return Self for chaining.
     */
    public ResultParser add(ResultParser... parsers) {
        ResultParser root = getRootParser();
        if (root.parserMap == null) {
            root.parserMap = new HashMap<>();
        }
        for (ResultParser p : parsers) {
            if (p.parent != null) {
                throw new IllegalStateException("Parser "+p+" already added to a parser group.");
            }
            p.parent = root;
            root.parserMap.put(p.entityType, p);
        }
        return this;
    }
    
    /**
     * Gets the parser that is registered for the given entity type.  Each parser can only be registered for a single entity type, but
     * parsers can be grouped together using {@link #add(com.codename1.rad.io.ResultParser...) }.  
     * @param type The entity type to retrieve a parser for.
     * @return The corresponding parser, or null if none could be found.
     */
    public ResultParser getParserFor(EntityType type) {
        ResultParser root = getRootParser();
        if (root.entityType == type) {
            return root;
        }
        if (root.parserMap != null) {
            return root.parserMap.get(type);
        }
        return null;
        
    }

    @Override
    public Entity createEntity(Class type) {
        return EntityType.createEntityForClass(type);
        
    }
    
    
    /**
     * Interface to get a property for a provided selector.
     */
    private interface Getter {
        Object get(Result res, String selector);
    }
    
    /**
     * Creates a getter for a given property.
     * @param property
     * @return 
     */
    private static Getter createGetter(Property property) {
        Getter getter = null;
        ContentType propType = property.getContentType();
        if (propType == ContentType.BooleanType) {
            getter = (rs, sel)->{
                return rs.getAsBoolean(sel);
            };
        } else if (propType == ContentType.DateType) {
            getter = (rs, sel) ->{
                return rs.getAsString(sel);
            };
        } else if (propType == ContentType.DoubleType || propType == ContentType.FloatType) {
            getter = (rs, sel) ->{
                return rs.getAsDouble(sel);
            };
        } else if (propType == ContentType.IntegerType) {
            getter = (rs, sel) ->{
                return rs.getAsInteger(sel);
            };
        } else if (propType == ContentType.LongType) {
            getter = (rs, sel) ->{
                return rs.getAsLong(sel);
            };
        } else if (propType == ContentType.Text) {
            getter = (rs, sel) ->{
                return rs.getAsString(sel);
            };
        } else if (propType.isEntityList()) {
            getter = (rs, sel) ->{
                try {
                    return rs.getAsArray(sel);
                    
                } catch (Throwable t) {
                    Log.p("Failed to get selector "+sel+" from result "+rs);
                    Log.e(t);
                    return null;
                }
                
            };
        } else if (propType.isEntity()) {
            getter = (rs, sel) ->{
                return rs.get(sel);
            };
        }
        return getter;
    }
    
    /**
     * A class that knows how to parse a particular property.
     */
    private static class PropertyParser {
        private String resultPropertySelector;
        private Property property;
        private Tag[] tags;
        private PropertyParserCallback parserCallback;
        private Getter getter;
        private ResultParser entityParser;

        public PropertyParser(Property property, ResultParser entityParser) {
            this.property = property;
            this.entityParser = entityParser;
        }

        public PropertyParser(Tag tag, ResultParser entityParser) {
            this.tags = new Tag[]{tag};
            this.entityParser = entityParser;
        }

        public PropertyParser(ResultParser entityParser, Tag... tags) {
            this.tags = tags;
            this.entityParser = entityParser;
        }
        
        public PropertyParser(String resultPropertySelector, Getter getter, Property prop, PropertyParserCallback parserCallback) {
            this.resultPropertySelector = resultPropertySelector;
            this.property = prop;
            this.parserCallback = parserCallback;
            this.getter = getter;
            if (getter == null) {
                this.getter = createGetter(prop);
            }
        }
        
        public PropertyParser(String resultPropertySelector, Getter getter, PropertyParserCallback parserCallback, Tag... tags) {
            this.resultPropertySelector = resultPropertySelector;
            this.parserCallback = parserCallback;
            this.tags = tags;
            this.getter = getter;
        }
        
    }
    
    /**
     * Add support for parsing a particular property.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param getter The getter to use for "Getting" the property.
     * @param property The property to bind to.
     * @param parserCallback A callback to use for parsing the property.
     * @return Self for chaining.
     */
    public ResultParser property(String resultPropertySelector, Getter getter, Property property, PropertyParserCallback parserCallback) {
        propertyParsers.add(new PropertyParser(resultPropertySelector, getter, property, parserCallback));
        return this;
    }

    public ResultParser entity(Property property, ResultParser subEntityParser) {
        propertyParsers.add(new PropertyParser(property, subEntityParser));
        return this;
    }

    public ResultParser entity(Tag property, ResultParser subEntityParser) {
        propertyParsers.add(new PropertyParser(property, subEntityParser));
        return this;
    }

    public ResultParser entity(ResultParser subEntityParser, Tag... tags) {
        propertyParsers.add(new PropertyParser(subEntityParser, tags));
        return this;
    }


    
    /**
     * Add support for parsing a particular property.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param property The property to bind to.
     * @param parserCallback A callback to use for parsing the property.
     * @return Self for chaining.
     */
    public ResultParser property(String resultPropertySelector, Property property, PropertyParserCallback parserCallback) {
        propertyParsers.add(new PropertyParser(resultPropertySelector, null, property, parserCallback));
        return this;
    }
    
    /**
     * Add support for parsing a particular property.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param getter The getter to use for "Getting" the property.
     * @param property The property to bind to.
     * @return Self for chaining.
     */
    public ResultParser property(String resultPropertySelector, Getter getter, Property property) {
        return property(resultPropertySelector, getter, property, null);
    }
    
    /**
     * Add support for parsing a particular property.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param property The property to bind to.
     * @return Self for chaining.
     */
    public ResultParser property(String resultPropertySelector, Property property) {
        return property(resultPropertySelector, null, property, null);
    }
    
    /**
     * Add support for parsing a particular property.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param getter The getter to use for "Getting" the property.
     * @param parserCallback A callback to use for parsing the property.
     * @param tags Tags for resolving the property to bind to.
     * @return Self for chaining.
     */
    private ResultParser property(String resultPropertySelector, Getter getter, PropertyParserCallback parserCallback, Tag... tags) {
        propertyParsers.add(new PropertyParser(resultPropertySelector, getter, parserCallback, tags));
        return this;
    }
    
    /**
     * Add support for parsing a particular property.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param parserCallback A callback to use for parsing the property.
     * @param tags Tags for resolving the property to bind to.
     * @return Self for chaining.
     */
    public ResultParser property(String resultPropertySelector, PropertyParserCallback parserCallback, Tag... tags) {
        propertyParsers.add(new PropertyParser(resultPropertySelector, null, parserCallback, tags));
        return this;
    }
    
    /**
     * Add support for parsing a particular property.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param getter The getter to use for "Getting" the property.
     * @param tags Tags for resolving the property to bind to.
     * @return Self for chaining.
     */
    private ResultParser property(String resultPropertySelector, Getter getter, Tag... tags) {
        return property(resultPropertySelector, getter, null, tags);
    }
    
    /**
     * Add support for parsing a particular property.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param tags Tags for resolving the property to bind to.
     * @return Self for chaining.
     */
    public ResultParser property(String resultPropertySelector, Tag... tags) {
        return property(resultPropertySelector, null, null, tags);
    }
    
    /**
     * Add support for parsing a particular property.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param getter The getter to use for "Getting" the property.
     * @param parserCallback A callback to use for parsing the property.
     * @param tag Tag for resolving the property to bind to.
     * @return Self for chaining.
     */
    private ResultParser property(String resultPropertySelector, Getter getter, Tag tag, PropertyParserCallback parserCallback) {
        return property(resultPropertySelector, getter, parserCallback, tag);
    }
    
    /**
     * Add support for parsing a particular property.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param parserCallback A callback to use for parsing the property.
     * @param tag Tag for resolving the property to bind to.
     * @return Self for chaining.
     */
    public ResultParser property(String resultPropertySelector, Tag tag, PropertyParserCallback parserCallback) {
        return property(resultPropertySelector, null, parserCallback, tag);
    }
    
    /**
     * Add support for parsing a particular property.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param getter The getter to use for "Getting" the property.
     * @param parserCallback A callback to use for parsing the property.
     * @param tag Tag for resolving the property to bind to.
     * @return Self for chaining.
     */
    private ResultParser property(String resultPropertySelector, Getter getter, Tag tag, final SimpleDateFormat dateFormat) {
        return property(resultPropertySelector, getter, tag, dateStr -> {
            if (!(dateStr instanceof String)) {
                return null;
            }
            String str = (String)dateStr;
            if (str.isEmpty()) {
                return null;
            }
            try {
                return dateFormat.parse(str);
            } catch (ParseException ex) {
                Log.e(ex);
                return null;
            }

        });
    }
    
    /**
     * Add support for parsing a particular property.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param tag Tag for resolving the property to bind to.
     * @param dateFormat Date format for formatting date data in  property.
     * @return Self for chaining.
     */
    public ResultParser property(String resultPropertySelector, Tag tag, final SimpleDateFormat dateFormat) {
        return property(resultPropertySelector, null, tag, dateStr -> {
            if (!(dateStr instanceof String)) {
                return null;
            }
            String str = (String)dateStr;
            if (str.isEmpty()) {
                return null;
            }
            try {
                return dateFormat.parse(str);
            } catch (ParseException ex) {
                Log.e(ex);
                return null;
            }

        });
    }
    
    // String properties----------------------------------------------------------
    /**
     * Adds a string property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param tag The tag to map to in the entity.
     * @param dateFormat DateFormat for parsing dates.
     * @return 
     */
    public ResultParser string(String resultPropertySelector, Tag tag, final SimpleDateFormat dateFormat) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsString(sel);}, 
                tag, 
                dateFormat
                
        );
    }
    
    /**
     * Adds a string property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param tag The tag to map to in the entity.
     * @param callback A custom parser to use for parsing the content.
     * @return Self for chaining.
     */
    public ResultParser string(String resultPropertySelector, Tag tag, PropertyParserCallback callback) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsString(sel);}, 
                tag, 
                callback
                
        );
    }
    
    /**
     * Adds a string property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param tags The tags to map to in the entity.
     * @return Self for chaining.
     */
    public ResultParser string(String resultPropertySelector, Tag... tags) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsString(sel);}, 
                tags
        );
    }
    
    /**
     * Adds a string property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param callback Custom parser to use for parsing the property value.
     * @param tags The tags to map to in the entity.
     * @return Self for chaining.
     */
    public ResultParser string(String resultPropertySelector, PropertyParserCallback callback, Tag... tags) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsString(sel);},
                callback,
                tags
        );
    }
    
    /**
     * Adds a string property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param prop The property to bind to.
     * @return Self for chaining.
     */
    public ResultParser string(String resultPropertySelector, Property prop) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsString(sel);}, 
                prop
        );
    }


    
    /**
     * Adds a string property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param prop The property to bind to.
     * @param callback Custom parser to use for parsing the property value.
     * @return Self for chaining.
     */
    public ResultParser string(String resultPropertySelector, Property prop, PropertyParserCallback callback) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsString(sel);}, 
                prop,
                callback
        );
    }
    
    // End String properties------------------------------------------------------
    
    // Integer properties----------------------------------------------------------
    
    /**
     * Adds an integer property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param tag The tag for the property to bind to in the entity.
     * @param dateFormat Date format for parsing dates.
     * @return Self for chaining.
     */
    public ResultParser Integer(String resultPropertySelector, Tag tag, final SimpleDateFormat dateFormat) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsInteger(sel);}, 
                tag, 
                dateFormat
                
        );
    }
    
    /**
     * Adds an integer property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param tag The tag for the property to bind to in the entity.
     * @param callback Custom parser to use for parsing the property value.
     * @return Self for chaining.
     */
    public ResultParser Integer(String resultPropertySelector, Tag tag, PropertyParserCallback callback) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsInteger(sel);}, 
                tag, 
                callback
                
        );
    }
    
    /**
     * Adds an integer property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param tags The tags for the property to bind to in the entity.
     * @return Self for chaining.
     */
    public ResultParser Integer(String resultPropertySelector, Tag... tags) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsInteger(sel);}, 
                tags
        );
    }
    
    /**
     * Adds an integer property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param tags The tags for the property to bind to in the entity.
     * @param callback Custom parser to use for parsing the property value.
     * @return Self for chaining.
     */
    public ResultParser Integer(String resultPropertySelector, PropertyParserCallback callback, Tag... tags) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsInteger(sel);},
                callback,
                tags
        );
    }
    
    /**
     * Adds an integer property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param prop The property to bind to in the entity.
     * @return Self for chaining.
     */
    public ResultParser Integer(String resultPropertySelector, Property prop) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsInteger(sel);}, 
                prop
        );
    }
    
    /**
     * Adds an integer property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param prop The property to bind to in the entity.
     * @param callback Custom parser to use for parsing the property value.
     * @return Self for chaining.
     */
    public ResultParser Integer(String resultPropertySelector, Property prop, PropertyParserCallback callback) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsInteger(sel);}, 
                prop,
                callback
        );
    }
    
    // End Integer properties------------------------------------------------------
    
    // Double properties----------------------------------------------------------
    
    /**
     * Adds an double property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param tag The tag for the property to bind to in the entity.
     * @param dateFormat Date format for parsing dates.
     * @return Self for chaining.
     */
    public ResultParser Double(String resultPropertySelector, Tag tag, final SimpleDateFormat dateFormat) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsDouble(sel);}, 
                tag, 
                dateFormat
                
        );
    }
    
    /**
     * Adds an double property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param tag The tag for the property to bind to in the entity.
     * @param callback Custom parser to use for parsing the property value.
     * @return Self for chaining.
     */
    public ResultParser Double(String resultPropertySelector, Tag tag, PropertyParserCallback callback) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsDouble(sel);}, 
                tag, 
                callback
                
        );
    }
    
    /**
     * Adds an double property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param tags The tags for the property to bind to in the entity.
     * @return Self for chaining.
     */
    public ResultParser Double(String resultPropertySelector, Tag... tags) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsDouble(sel);}, 
                tags
        );
    }
    
    /**
     * Adds an double property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param callback The custom parser to use for parsing the property value.
     * @param tags The tags for the property to bind to in the entity.
     * @return Self for chaining.
     */
    public ResultParser Double(String resultPropertySelector, PropertyParserCallback callback, Tag... tags) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsDouble(sel);},
                callback,
                tags
        );
    }
    

    /**
     * Adds an double property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param prop The property to bind to in the entity.
     * @return Self for chaining.
     */
    public ResultParser Double(String resultPropertySelector, Property prop) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsDouble(sel);}, 
                prop
        );
    }
    
    /**
     * Adds an double property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param prop The property to bind to in the entity.
     * @param callback Custom parser to parse the property value.
     * @return Self for chaining.
     */
    public ResultParser Double(String resultPropertySelector, Property prop, PropertyParserCallback callback) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsDouble(sel);}, 
                prop,
                callback
        );
    }
    
    // End Double properties------------------------------------------------------
    
    // Boolean properties----------------------------------------------------------
    
    /**
     * Adds a boolean property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param tag The tag to bind to in the entity.
     * @param dateFormat Date format for parsing dates.
     * @return Self for chaining.
     */
    public ResultParser Boolean(String resultPropertySelector, Tag tag, final SimpleDateFormat dateFormat) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsBoolean(sel);}, 
                tag, 
                dateFormat
                
        );
    }
    
    /**
     * Adds a boolean property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param tag The tag to bind to in the entity.
     * @param callback Custom parser to parse the property.
     * @return Self for chaining.
     */
    public ResultParser Boolean(String resultPropertySelector, Tag tag, PropertyParserCallback callback) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsBoolean(sel);}, 
                tag, 
                callback
                
        );
    }
    
    /**
     * Adds a boolean property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param tags The tags to bind to in the entity.
     * @return Self for chaining.
     */
    public ResultParser Boolean(String resultPropertySelector, Tag... tags) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsBoolean(sel);}, 
                tags
        );
    }
    
    /**
     * Adds a boolean property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param callback Custom parser for parsing the property.
     * @param tags The tags to bind to in the entity.
     * @return Self for chaining.
     */
    public ResultParser Boolean(String resultPropertySelector, PropertyParserCallback callback, Tag... tags) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsBoolean(sel);},
                callback,
                tags
        );
    }
    
    /**
     * Adds a boolean property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param prop Property to bind to in the entity.
     * @return Self for chaining.
     */
    public ResultParser Boolean(String resultPropertySelector, Property prop) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsBoolean(sel);}, 
                prop
        );
    }
    
    /**
     * Adds a boolean property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param prop Property to bind to in the entity.
     * @param callback Custom parser for parsing the property.
     * @return Self for chaining.
     */
    public ResultParser Boolean(String resultPropertySelector, Property prop, PropertyParserCallback callback) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsBoolean(sel);}, 
                prop,
                callback
        );
    }
    // End Boolean properties ---------------------------------------------------
    
    // Long properties----------------------------------------------------------
    
    
    /**
     * Adds a long property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param tag The tag to bind to in the entity.
     * @param dateFormat Date format for parsing dates.
     * @return Self for chaining.
     */
    public ResultParser Long(String resultPropertySelector, Tag tag, final SimpleDateFormat dateFormat) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsLong(sel);}, 
                tag, 
                dateFormat
                
        );
    }
    
    /**
     * Adds a long property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param tag The tag to bind to in the entity.
     * @param callback Custom parser to parse the property.
     * @return Self for chaining.
     */
    public ResultParser Long(String resultPropertySelector, Tag tag, PropertyParserCallback callback) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsLong(sel);}, 
                tag, 
                callback
                
        );
    }
    
    /**
     * Adds a long property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param tags The tags to bind to in the entity.
     * @return Self for chaining.
     */
    public ResultParser Long(String resultPropertySelector, Tag... tags) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsLong(sel);}, 
                tags
        );
    }
    
    /**
     * Adds a long property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param callback Custom parser for parsing the property.
     * @param tags The tags to bind to in the entity.
     * @return Self for chaining.
     */
    public ResultParser Long(String resultPropertySelector, PropertyParserCallback callback, Tag... tags) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsLong(sel);},
                callback,
                tags
        );
    }
    
    /**
     * Adds a long property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param prop Property to bind to in the entity.
     * @return Self for chaining.
     */
    public ResultParser Long(String resultPropertySelector, Property prop) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsLong(sel);}, 
                prop
        );
    }
    
    
    /**
     * Adds a long property parser.
     * @param resultPropertySelector The selector (in the {@link Result} expression language for retrieving the property from a Result.
     * @param prop Property to bind to in the entity.
     * @param callback Custom parser for parsing the property.
     * @return Self for chaining.
     */
    public ResultParser Long(String resultPropertySelector, Property prop, PropertyParserCallback callback) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsLong(sel);}, 
                prop,
                callback
        );
    }
    
    // End Long properties------------------------------------------------------
    
   
    
    /**
     * Creates and adds a new parser for the given entity type.
     * @param type The type to create a parser for
     * @return The new parser.
     */
    public ResultParser entityType(EntityType type) {
        ResultParser p = new ResultParser(type);
        add(p);
        return p;
    }
    
    public ResultParser entityType(Class<? extends Entity> type) {
        ResultParser p = new ResultParser(EntityType.getEntityType(type));
        add(p);
        return p;
    }
  
    
    /**
     * Interface that can be implemented and passed to any of the property methods to provide
     * custom parsing of values retrieved from JSON/XML content.
     */
    public static interface PropertyParserCallback {
        public Object parse(Object val);
    }   
    
    
    /**
     * Parses a list of Maps or Elements into an EntityList.
     * @param rows The rows to parse.
     * @param out The entity list to append to.
     * @return The resulting entity list (same as input parameter).
     * @throws IOException If parsing failed.
     */
    public EntityList parse(List rows, EntityList out) throws IOException {
        
        for (Object row : rows) {
            Entity rowEntity = null;
            rowEntity = EntityType.createEntityForClass(out.getRowType().getEntityClass());
            if (rowEntity == null) {
                rowEntity = out.getRowType().newInstance();
            }
            Result rowResult;
            if (row instanceof Element) {
                
                rowResult = Result.fromContent((Element)row);
                
            } else if (row instanceof Map) {
                rowResult = Result.fromContent((Map)row);
            } else {
                throw new IOException("Row type "+row.getClass()+" not supported.  Expected Map or Element.");
            }
            Entity parsed = parseRow(rowResult, rowEntity);
            if (parsed != null) {
                out.add(parsed);
            }
        }
        return out;
    }
    
    /**
     * Parses a result into the provided EntityList.
     * @param result The result to parse. This should be rooted with a list/array.
     * @param out The entitylist to add results to.
     * @return
     * @throws IOException 
     */
    public EntityList parse(Result result, EntityList out) throws IOException {
        List rows = (List)result.getAsArray(rowsSelector);
        return parse(rows, out);
    }
    
    
    public Entity parseXML(String xml, Entity dest) throws IOException {
        return parseRow(Result.fromContent(xml, Result.XML), dest);
    }
    
    public Entity parseXML(InputStream xml, Entity dest) throws IOException {
        return parseRow(Result.fromContent(xml, Result.XML), dest);
    }
    
    public Entity parseXML(Reader xml, Entity dest) throws IOException {
        return parseRow(Result.fromContent(xml, Result.XML), dest);
    }
    
    public Entity parseJSON(String json, Entity dest) throws IOException {
        return parseRow(Result.fromContent(json, Result.JSON), dest);
    }
    
    public Entity parseJSON(InputStream json, Entity dest) throws IOException {
        return parseRow(Result.fromContent(json, Result.JSON), dest);
    }
    
    public Entity parseJSON(Reader json, Entity dest) throws IOException {
        return parseRow(Result.fromContent(json, Result.JSON), dest);
    }
    /**
     * Parse a single row of a result into the given row entity.
     * @param rowResult The rowResult.
     * @param rowEntity The row entity.
     * @return The resulting entity.  Same as input rowEntity.
     * @throws IOException if parsing fails.
     */
    public Entity parseRow(Result rowResult, Entity rowEntity) throws IOException {
        if (rowEntity.getEntity().getEntityType() != entityType) {
            ResultParser matchingParser = getParserFor(rowEntity.getEntity().getEntityType());
            if (matchingParser == null) {
                throw new IOException("No parser found for type "+rowEntity.getEntity().getEntityType());
            }
            return matchingParser.parseRow(rowResult, rowEntity);
        }
        for (PropertyParser propertyParser : propertyParsers) {
            
            String rs = propertyParser.resultPropertySelector;
            Property prop = propertyParser.property;
            if (prop == null) {
                if (propertyParser.tags != null) {
                    prop = rowEntity.getEntity().findProperty(propertyParser.tags);
                }
            }
            if (prop == null) {
                throw new IOException("Property not found for property selector when parsing selector "+rs);
            }

            if (propertyParser.entityParser != null) {
                // This property is an entity, and it will be parsed against the
                // same row data - not a sub-object in the dataset.
                if (prop.getContentType().isEntity()) {

                    EntityProperty eProp = (EntityProperty)prop;

                    Class cls = eProp.getRepresentationClass();
                    Entity e;
                    try {
                        e = createEntity(cls);
                    } catch (Throwable t) {
                        throw new IOException("Failed to create new entity instance for property "+prop+" of type "+cls);
                    }
                    e = propertyParser.entityParser.parseRow(rowResult, e);
                    rowEntity.getEntity().set(prop, e);
                } else {
                    throw new IOException("Property "+prop+" is assigned an EntityParser, but the property is not an entity type.");
                }
                continue;

            }
            // This is just a simple property selector
            Getter getter = propertyParser.getter;
            if (getter == null && propertyParser.parserCallback == null) {
                getter = createGetter(prop);
            }
            Object val;
            if (getter == null) {
                val = rowResult.get(rs);
            } else {
                val = getter.get(rowResult, rs);
            }
            
            if (propertyParser.parserCallback != null) {
                val = propertyParser.parserCallback.parse(val);
            }
            if (val == null) {
                rowEntity.getEntity().set(val, null);
            } else if (val.getClass() == Double.class) {
                rowEntity.getEntity().setDouble(prop, (Double)val);
            } else if (val.getClass() == Integer.class) {
                rowEntity.getEntity().setInt(prop, (Integer)val);
            } else if (val.getClass() == Long.class) {
                rowEntity.getEntity().setLong(prop, (Long)val);
            } else if (val.getClass() == Float.class) {
                rowEntity.getEntity().setFloat(prop, (Float)val);
            } else if (val.getClass() == Boolean.class) {
                rowEntity.getEntity().setBoolean(prop, (Boolean)val);
            } else if (val.getClass() == String.class) {
                rowEntity.getEntity().setText(prop, (String)val);
            } else if (val.getClass() == Date.class) {
                rowEntity.getEntity().setDate(prop, (Date)val);
            } else if (val instanceof List) {
                if (prop.getContentType().isEntityList()) {
                    
                    parse((List)val, rowEntity.getEntity().getEntityListNonNull(prop));
                } else {
                    throw new IOException("Property type mismatch.  Value "+val+" for property selector "+rs+" is a list, but the property "+prop+" is not an entity list type.");
                }
            } else if (val instanceof Map) {
                if (prop.getContentType().isEntity()) {
                    EntityProperty eProp = (EntityProperty)prop;
                    Class cls = eProp.getRepresentationClass();
                    Entity e;
                    try {
                        e = createEntity(cls);
                    } catch (Throwable t) {
                        throw new IOException("Failed to create new entity instance for property "+prop+" of type "+cls);
                    }
                    e = parseRow(Result.fromContent((Map)val), e);
                    rowEntity.getEntity().set(prop, e);
                } else {
                    throw new IOException("Property type mismatch.  Value "+val+" for property selector "+rs+" is a map, but the property "+prop+" is not an entity type.");
                }
            } else if (val instanceof Element) {
                if (prop.getContentType().isEntity()) {
                    EntityProperty eProp = (EntityProperty)prop;
                    Class cls = eProp.getRepresentationClass();
                    Entity e;
                    try {
                        e = createEntity(cls);
                    } catch (Throwable t) {
                        throw new IOException("Failed to create new entity instance for property "+prop+" of type "+cls);
                    }
                    e = parseRow(Result.fromContent((Element)val), e);
                    rowEntity.getEntity().set(prop, e);
                } else {
                    throw new IOException("Property type mismatch.  Value "+val+" for property selector "+rs+" is a map, but the property "+prop+" is not an entity type.");
                }
            } else {
                throw new IOException("Unsupported content type for property "+prop+".  Value was "+val+" of type "+val.getClass());
            }

                
            
            
        }
        return rowEntity;
    }
    
    
    public static ResultParser resultParser(Class<? extends Entity> type) {
        return new ResultParser(EntityType.getEntityType(type));
    }
    
    public static ResultParser resultParser(EntityType type) {
        return new ResultParser(type);
    }
   
    
}
