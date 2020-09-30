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
import com.codename1.rad.models.Attribute;
import com.codename1.rad.models.DateFormatterAttribute;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.EntityList;
import com.codename1.rad.models.EntityType;
import com.codename1.rad.models.Property;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.ui.NodeList;
import com.codename1.xml.Element;
import java.io.IOException;
import java.util.Date;

/**
 * A parser that understands how to parse an XML Element into an Entity.  The actual parsing should
 * by done by the {@link XMLDocumentParser}, which would have multiple XMLElementParser registered.
 * @author shannah
 */
public class XMLElementParser extends Node {
    
    /**
     * Creates a new XMLElementParser.
     * @param atts Attributes.  This requires {@link FieldNode} attributes to define how fields should be parsed.
     */
    public XMLElementParser(Attribute... atts) {
        super(null);
        setAttributes(atts);
    }
    
    
    
    /**
     * A setting used by {@link XMLElementParser} to dictate how it handles setting values on a list property.
     * @see ListPropertySettingAttribute
     */
    public static enum ListPropertySetting {
        
        /**
         * Replace all existing values in the list.
         */
        Replace,
        
        /**
         * Update corresponding values in the list.
         */
        Update,
        
        /**
         * Append values to the list.
         */
        Append
        
    }
    
    /**
     * A setting used by {@link EntityMappe} to dictate how it handles setting values on entity properties.
     * 
     * @see EntityPropertySettingAttribute
     */
    public static enum EntityPropertySetting {
        /**
         * Replace the existing entity with a new entity with the parsed values.
         */
        Replace,
        
        /**
         * Update the existing entity with the parsed values.
         */
        Update
    }
    
    public XMLDocumentParser getDocumentParser() {
        return (XMLDocumentParser)this.getAncestor(XMLDocumentParser.class);
    }
    
    
    
    
    
    
    
    
    
   
    
    
    
    
    private NodeList fields() {
        return getChildNodes(FieldNode.class);
    }
   
    
    /**
     * Parses an element into an entity.
     * @param entity The entity to be populated by the data that is parsed from the element.
     * @param el The element that is being parsed.
     * @throws IOException 
     */
    public void parse(Entity entity, Element el) throws IOException {
        ElementSelector sel = new ElementSelector(el);
        
        
        for (FieldNode fn : fields().as(FieldNode.class)) {
            Property prop = fn.getProperty(entity.getEntityType());
            if (prop == null) {
                continue;
            }
            
            ElementPropertyParserAttribute parserAtt = (ElementPropertyParserAttribute)fn.findAttribute(ElementPropertyParserAttribute.class);
            if (parserAtt != null) {
                parserAtt.getValue().parse(entity, el);
            } else {
                Key key = (Key)fn.findAttribute(Key.class);
                if (key != null) {
                    String str = sel.getString(key.getValue(), null);
                    if (prop.getContentType().getRepresentationClass() == Date.class) {
                        DateFormatterAttribute format = fn.getDateFormatter();
                        if (format != null && format.getValue().supportsParse()) {
                            try {
                                entity.setDate(prop, format.getValue().parse(str));
                            } catch (ParseException ex) {
                                Log.e(ex);
                                throw new IOException("Failed to parse element "+el+".", ex);
                            }
                            continue;
                        }
                    } 
                    
                    
                    if (prop.getContentType().isEntityList()) {
                        // The property is an entity list
                        
                        // Find out how we treat this property.  Do we append to the list,
                        // update the existing list object, or replace the list
                        ListPropertySettingAttribute listSettingAtt = (ListPropertySettingAttribute)fn.findInheritedAttribute(ListPropertySettingAttribute.class);
                        ListPropertySetting listSetting = listSettingAtt == null ? ListPropertySetting.Update : listSettingAtt.getValue();
                        
                        // We need to obtain a mapper for the rows of this list
                        //XMLElementParser mapper = getMapperFor(entity, fn, el);
                        
                        
                        Object existingValue = entity.get(prop);
                        EntityFactory rowFactory = null;
                        
                        // Obtain a factory to create rows in this list.
                        ListRowEntityFactory rowFactoryAtt = (ListRowEntityFactory)fn.findInheritedAttribute(ListRowEntityFactory.class);
                        if (rowFactoryAtt != null) {
                            rowFactory = rowFactoryAtt.getValue();
                        }
                        if (rowFactory == null) {
                            // No factory was defined.
                            // We need to try to infer the row types
                            if (existingValue instanceof EntityList) {
                                // If there is an existing list, we can get the row type from it
                                // and generate a factory for the rows that produces entities
                                // of that type.
                                EntityList elist = (EntityList)existingValue;
                                EntityType rowType = elist.getRowType();
                                if (rowType != null) {
                                    rowFactory = (parentEntity, theProp) -> {
                                        return rowType.newInstance();
                                    };
                                }

                            }
                        }
                        
                        if (rowFactory == null) {
                            // We couldn't find a factory for rows.
                            // We'll fail here.
                            throw new IOException("No row factory was available specified for the property "+prop+" of entity "+entity+".");
                        }
                        
                        
                        boolean cleared = false; // flag to indicate if we cleared the existing list yet
                        boolean created = false; // flag to indicate if we created a new list yet
                        for (Element child : sel.find(key.getValue())) {
                            // We loop through all matching XML elements
                            // and we'll try to parse them into entities to add to the list
                            
                            if (listSetting == ListPropertySetting.Replace && !created) {
                                // If we are supposed to replace the list with this property,
                                // we'll set the existing value to null, which will cause
                                // the next "if" block to create the list.
                                existingValue = null;
                                
                                
                            }
                            if (existingValue == null) {
                                // Can't yet append because the list is null
                                // We'll see if there is a factory.
                                EntityFactory fact = null;
                                PropertyEntityFactory pef = (PropertyEntityFactory)fn.findInheritedAttribute(PropertyEntityFactory.class);
                                if (pef != null) {
                                    fact = pef.getValue();
                                }
                                if (fact == null) {
                                    // No entity factory was specified, so we'll try to create a factory
                                    // based on the representation class defined in the property.
                                    Class cls = prop.getContentType().getRepresentationClass();
                                    fact = (parentEntity, theProp)->{
                                        try {
                                            return (EntityList)cls.newInstance();
                                        } catch (Throwable t) {
                                            throw new RuntimeException("Attempt to create entity list for property "+prop+" but failed to create instance of type "+cls, t);
                                        }
                                    };
                                }
                                existingValue = fact.createEntity(entity, prop);
                                created = true;

                            }
                            if (listSetting == ListPropertySetting.Update && !cleared) {
                                // We are supposed to "update" the list - i.e. replace its contents
                                // so we clear the list.
                                cleared = true;
                                ((EntityList)existingValue).clear();
                            }
                            
                            // Use the row factory to create the row.
                            // Don't need to worry about NPE here because if we failed to get a row factory
                            // it would have raised an IOException earlier.
                            Entity rowEntity = rowFactory.createEntity(entity, prop);
                            
                            // Use the mapper to populate the row.
                            //apper.parse(rowEntity, child);
                            getDocumentParser().parse(rowEntity, child);
                            
                            // Add the entity to the list
                            ((EntityList)existingValue).add(rowEntity);

                        }
                        continue;      
                        
                    }
                    
                    if (prop.getContentType().isEntity()) {
                        // This property is an entity type
                        
                        // First check if there is a factory defined for creating entities on this property
                        EntityFactory fact = null;
                        PropertyEntityFactory pef = (PropertyEntityFactory)fn.findInheritedAttribute(PropertyEntityFactory.class);
                        if (pef != null) {
                            fact = pef.getValue();
                        }
                        if (fact == null) {
                            //No factory was defined.  Use the representation class
                            // to generate a factory.
                            Class cls = prop.getContentType().getRepresentationClass();
                            fact = (parentEntity, theProp)->{
                                try {
                                    return (EntityList)cls.newInstance();
                                } catch (Throwable t) {
                                    throw new RuntimeException("Attempt to create entity list for property "+prop+" but failed to create instance of type "+cls, t);
                                }
                            };
                        }
                        
                        
                        for (Element propEl : sel.find(key.getValue())) {
                            // We use a for loop here since that's the quickest way to get the
                            // first element.
                            // Entity properties are always sub-tags.
                            Entity newValue = fact.createEntity(entity, prop);
                            
                            
                            getDocumentParser().parse(newValue, propEl);
                            entity.set(prop, newValue);
                            break;
                        }
                        continue;
                    }
                    
                    entity.setText(prop, str);
                }
            }
            
            
        }
    }
    
    
    
}
