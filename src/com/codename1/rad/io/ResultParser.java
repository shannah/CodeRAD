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
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.EntityList;
import com.codename1.rad.models.EntityProperty;
import com.codename1.rad.models.EntityType;
import com.codename1.rad.models.Property;
import com.codename1.rad.models.PropertySelector;
import com.codename1.rad.models.Tag;
import com.codename1.xml.Element;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author shannah
 */
public class ResultParser {
    private String rowsSelector="root";
    private ArrayList<PropertyParser> propertyParsers = new ArrayList<>();
    private ResultParser parent;
    private final EntityType entityType;
    private Map<EntityType,ResultParser> parserMap;
    
    
    public ResultParser(EntityType type) {
        this.entityType = type;
        
    }
   
    public ResultParser getRootParser() {
        if (parent == null) {
            return this;
        }
        return parent.getRootParser();
    }
    
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
    
    
    private interface Getter {
        Object get(Result res, String selector);
    }
    
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
    
    private static class PropertyParser {
        private String resultPropertySelector;
        private Property property;
        private Tag[] tags;
        private PropertyParserCallback parserCallback;
        private Getter getter;
        
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
    
    public ResultParser property(String resultPropertySelector, Getter getter, Property property, PropertyParserCallback parserCallback) {
        propertyParsers.add(new PropertyParser(resultPropertySelector, getter, property, parserCallback));
        return this;
    }
    public ResultParser property(String resultPropertySelector, Property property, PropertyParserCallback parserCallback) {
        propertyParsers.add(new PropertyParser(resultPropertySelector, null, property, parserCallback));
        return this;
    }
    
    public ResultParser property(String resultPropertySelector, Getter getter, Property property) {
        return property(resultPropertySelector, getter, property, null);
    }
    
    public ResultParser property(String resultPropertySelector, Property property) {
        return property(resultPropertySelector, null, property, null);
    }
    
    public ResultParser property(String resultPropertySelector, Getter getter, PropertyParserCallback parserCallback, Tag... tags) {
        propertyParsers.add(new PropertyParser(resultPropertySelector, getter, parserCallback, tags));
        return this;
    }
    
    public ResultParser property(String resultPropertySelector, PropertyParserCallback parserCallback, Tag... tags) {
        propertyParsers.add(new PropertyParser(resultPropertySelector, null, parserCallback, tags));
        return this;
    }
    
    public ResultParser property(String resultPropertySelector, Getter getter, Tag... tags) {
        return property(resultPropertySelector, getter, null, tags);
    }
    
    public ResultParser property(String resultPropertySelector, Tag... tags) {
        return property(resultPropertySelector, null, null, tags);
    }
    
    public ResultParser property(String resultPropertySelector, Getter getter, Tag tag, PropertyParserCallback parserCallback) {
        return property(resultPropertySelector, getter, parserCallback, tag);
    }
    
    public ResultParser property(String resultPropertySelector, Tag tag, PropertyParserCallback parserCallback) {
        return property(resultPropertySelector, null, parserCallback, tag);
    }
    
    public ResultParser property(String resultPropertySelector, Getter getter, Tag tag, final SimpleDateFormat dateFormat) {
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
    
    public ResultParser string(String resultPropertySelector, Tag tag, final SimpleDateFormat dateFormat) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsString(sel);}, 
                tag, 
                dateFormat
                
        );
    }
    
    public ResultParser string(String resultPropertySelector, Tag tag, PropertyParserCallback callback) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsString(sel);}, 
                tag, 
                callback
                
        );
    }
    
    public ResultParser string(String resultPropertySelector, Tag... tags) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsString(sel);}, 
                tags
        );
    }
    
    public ResultParser string(String resultPropertySelector, PropertyParserCallback callback, Tag... tags) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsString(sel);},
                callback,
                tags
        );
    }
    
    public ResultParser string(String resultPropertySelector, Property prop) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsString(sel);}, 
                prop
        );
    }
    
    public ResultParser string(String resultPropertySelector, Property prop, PropertyParserCallback callback) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsString(sel);}, 
                prop,
                callback
        );
    }
    
    // End String properties------------------------------------------------------
    
    // Integer properties----------------------------------------------------------
    
    public ResultParser Integer(String resultPropertySelector, Tag tag, final SimpleDateFormat dateFormat) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsInteger(sel);}, 
                tag, 
                dateFormat
                
        );
    }
    
    public ResultParser Integer(String resultPropertySelector, Tag tag, PropertyParserCallback callback) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsInteger(sel);}, 
                tag, 
                callback
                
        );
    }
    
    public ResultParser Integer(String resultPropertySelector, Tag... tags) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsInteger(sel);}, 
                tags
        );
    }
    
    public ResultParser Integer(String resultPropertySelector, PropertyParserCallback callback, Tag... tags) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsInteger(sel);},
                callback,
                tags
        );
    }
    
    public ResultParser Integer(String resultPropertySelector, Property prop) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsInteger(sel);}, 
                prop
        );
    }
    
    public ResultParser Integer(String resultPropertySelector, Property prop, PropertyParserCallback callback) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsInteger(sel);}, 
                prop,
                callback
        );
    }
    
    // End Integer properties------------------------------------------------------
    
    // Double properties----------------------------------------------------------
    
    public ResultParser Double(String resultPropertySelector, Tag tag, final SimpleDateFormat dateFormat) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsDouble(sel);}, 
                tag, 
                dateFormat
                
        );
    }
    
    public ResultParser Double(String resultPropertySelector, Tag tag, PropertyParserCallback callback) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsDouble(sel);}, 
                tag, 
                callback
                
        );
    }
    
    public ResultParser Double(String resultPropertySelector, Tag... tags) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsDouble(sel);}, 
                tags
        );
    }
    
    public ResultParser Double(String resultPropertySelector, PropertyParserCallback callback, Tag... tags) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsDouble(sel);},
                callback,
                tags
        );
    }
    
    public ResultParser Double(String resultPropertySelector, Property prop) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsDouble(sel);}, 
                prop
        );
    }
    
    public ResultParser Double(String resultPropertySelector, Property prop, PropertyParserCallback callback) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsDouble(sel);}, 
                prop,
                callback
        );
    }
    
    // End Double properties------------------------------------------------------
    
    // Boolean properties----------------------------------------------------------
    
    public ResultParser Boolean(String resultPropertySelector, Tag tag, final SimpleDateFormat dateFormat) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsBoolean(sel);}, 
                tag, 
                dateFormat
                
        );
    }
    
    public ResultParser Boolean(String resultPropertySelector, Tag tag, PropertyParserCallback callback) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsBoolean(sel);}, 
                tag, 
                callback
                
        );
    }
    
    public ResultParser Boolean(String resultPropertySelector, Tag... tags) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsBoolean(sel);}, 
                tags
        );
    }
    
    public ResultParser Boolean(String resultPropertySelector, PropertyParserCallback callback, Tag... tags) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsBoolean(sel);},
                callback,
                tags
        );
    }
    
    public ResultParser Boolean(String resultPropertySelector, Property prop) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsBoolean(sel);}, 
                prop
        );
    }
    
    public ResultParser Boolean(String resultPropertySelector, Property prop, PropertyParserCallback callback) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsBoolean(sel);}, 
                prop,
                callback
        );
    }
    // End Boolean properties ---------------------------------------------------
    
    // Long properties----------------------------------------------------------
    
    public ResultParser Long(String resultPropertySelector, Tag tag, final SimpleDateFormat dateFormat) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsLong(sel);}, 
                tag, 
                dateFormat
                
        );
    }
    
    public ResultParser Long(String resultPropertySelector, Tag tag, PropertyParserCallback callback) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsLong(sel);}, 
                tag, 
                callback
                
        );
    }
    
    public ResultParser Long(String resultPropertySelector, Tag... tags) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsLong(sel);}, 
                tags
        );
    }
    
    public ResultParser Long(String resultPropertySelector, PropertyParserCallback callback, Tag... tags) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsLong(sel);},
                callback,
                tags
        );
    }
    
    public ResultParser Long(String resultPropertySelector, Property prop) {
        return property(resultPropertySelector, 
                (res,sel)->{return res.getAsLong(sel);}, 
                prop
        );
    }
    
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
  
    
    
    public static interface PropertyParserCallback {
        public Object parse(Object val);
    }   
    
    
    
    public EntityList parse(List rows, EntityList out) throws IOException {
        
        for (Object row : rows) {
            Entity rowEntity = out.getRowType().newInstance();
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
    
    
    public EntityList parse(Result result, EntityList out) throws IOException {
        List rows = (List)result.getAsArray(rowsSelector);
        return parse(rows, out);
    }
    
    
    
    public Entity parseRow(Result rowResult, Entity rowEntity) throws IOException {
        if (rowEntity.getEntityType() != entityType) {
            ResultParser matchingParser = getParserFor(rowEntity.getEntityType());
            if (matchingParser == null) {
                throw new IOException("No parser found for type "+rowEntity.getEntityType());
            }
            return matchingParser.parseRow(rowResult, rowEntity);
        }
        for (PropertyParser propertyParser : propertyParsers) {
            
            String rs = propertyParser.resultPropertySelector;
            Property prop = propertyParser.property;
            if (prop == null) {
                if (propertyParser.tags != null) {
                    prop = rowEntity.findProperty(propertyParser.tags);
                }
            }
            if (prop == null) {
                throw new IOException("Property not found for property selector when parsing selector "+rs);
            }
            // This is just a simple property selector
            Getter getter = propertyParser.getter;
            if (getter == null) {
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
                rowEntity.set(val, null);
            } else if (val.getClass() == Double.class) {
                rowEntity.setDouble(prop, (Double)val);
            } else if (val.getClass() == Integer.class) {
                rowEntity.setInt(prop, (Integer)val);
            } else if (val.getClass() == Long.class) {
                rowEntity.setLong(prop, (Long)val);
            } else if (val.getClass() == Float.class) {
                rowEntity.setFloat(prop, (Float)val);
            } else if (val.getClass() == Boolean.class) {
                rowEntity.setBoolean(prop, (Boolean)val);
            } else if (val.getClass() == String.class) {
                rowEntity.setText(prop, (String)val);
            } else if (val.getClass() == Date.class) {
                rowEntity.setDate(prop, (Date)val);
            } else if (val instanceof List) {
                if (prop.getContentType().isEntityList()) {
                    parse((List)val, rowEntity.getEntityListNonNull(prop));
                } else {
                    throw new IOException("Property type mismatch.  Value "+val+" for property selector "+rs+" is a list, but the property "+prop+" is not an entity list type.");
                }
            } else if (val instanceof Map) {
                if (prop.getContentType().isEntity()) {
                    EntityProperty eProp = (EntityProperty)prop;
                    Class cls = eProp.getRepresentationClass();
                    Entity e;
                    try {
                        e = (Entity)cls.newInstance();
                    } catch (Throwable t) {
                        throw new IOException("Failed to create new entity instance for property "+prop+" of type "+cls);
                    }
                    e = parseRow(Result.fromContent((Map)val), e);
                    rowEntity.set(prop, e);
                } else {
                    throw new IOException("Property type mismatch.  Value "+val+" for property selector "+rs+" is a map, but the property "+prop+" is not an entity type.");
                }
            } else if (val instanceof Element) {
                if (prop.getContentType().isEntity()) {
                    EntityProperty eProp = (EntityProperty)prop;
                    Class cls = eProp.getRepresentationClass();
                    Entity e;
                    try {
                        e = (Entity)cls.newInstance();
                    } catch (Throwable t) {
                        throw new IOException("Failed to create new entity instance for property "+prop+" of type "+cls);
                    }
                    e = parseRow(Result.fromContent((Element)val), e);
                    rowEntity.set(prop, e);
                } else {
                    throw new IOException("Property type mismatch.  Value "+val+" for property selector "+rs+" is a map, but the property "+prop+" is not an entity type.");
                }
            } else {
                throw new IOException("Unsupported content type for property "+prop+".  Value was "+val+" of type "+val.getClass());
            }

                
            
            
        }
        return rowEntity;
    }
    
}
