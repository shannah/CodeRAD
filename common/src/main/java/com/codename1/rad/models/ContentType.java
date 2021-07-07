/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

import com.codename1.rad.models.Property.Name;
import com.codename1.rad.text.AllFormatsDateFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Encapsulates a content type.  This class is the basis of all data conversions in CodeRAD.  All properties have an assigned
 * {@link ContentType}.  When a property is bound to a view, the view will use the content type to convert the property's 
 * value into a form that it can handle.
 * 
 * == Base Content Types
 * 
 * . {@link #Text} - Plain text, stored in a {@link String}.
 * . {@link #BooleanType} - A {@link Boolean} value.
 * . {@link #IntegerType} - An {@link Integer} value.
 * . {@link #DoubleType} - A {@link Double} value.
 * . {@link #FloatType} - A {@link Float} value.
 * . {@link #LongType} - A {@link Long} value.
 * . {@link #EntityType} - An {@link Entity} value.
 * . {@link #EntityListType} - An {@link EntityList} value.
 * . {@link #DateType} - A {@link java.util.Date} value.
 * 
 * == Converting Data From One Type to Another
 * 
 * Use {@link #convert(com.codename1.rad.models.ContentType, java.lang.Object, com.codename1.rad.models.ContentType) } to convert data from one type to another.  For example, to convert a String to an Integer, you could do:
 * 
 * [source,java]
 * ----
 * int value = ContentType.convert(ContentType.Text, "10", ContentType.IntegerType);
 * ----
 * 
 * {@link Property}, {@link EntityType}, and {@link Entity} all include convenience methods for retrieving property values in the basic content types.  
 * E.g. {@link Entity#getText(com.codename1.rad.models.Property) }, which will return the property value as a `String` no matter what content type the property actually stores.
 * 
 * == Custom Content Types
 * 
 * If you have a property that has a custom content type, you can implement it by extending {@link ContentType} and providing implementations for {@link #to(com.codename1.rad.models.ContentType, java.lang.Object) } and
 * {@link #canConvertTo(com.codename1.rad.models.ContentType) }; or {@link #from(com.codename1.rad.models.ContentType, java.lang.Object) } and {@link #canConvertFrom(com.codename1.rad.models.ContentType) }; or all of these methods.
 * 
 * @author shannah
 */
public class ContentType<T> {
    
    private static List<DataTransformer> registeredTransformers = new ArrayList<>();
    private boolean isNumber;
    

    /**
     * @return the representationClass
     */
    public Class<T> getRepresentationClass() {
        return representationClass;
    }
    
    public boolean isEntity() {
        return Entity.class.isAssignableFrom(getRepresentationClass());
    }
    
    public boolean isEntityList() {
        return EntityList.class.isAssignableFrom(getRepresentationClass());
    }

    /**
     * @return the name
     */
    public Name getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }
    
    
    
    private Class<T> representationClass;
    private Name name;
    
    public ContentType(Name name, Class<T> representationClass) {
        this.name = name;
        this.representationClass = representationClass;
        this.isNumber = Number.class.isAssignableFrom(representationClass);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        ContentType t = (ContentType)obj;
        return Objects.equals(t.name, name) && Objects.equals(t.representationClass, representationClass);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.representationClass);
        hash = 47 * hash + Objects.hashCode(this.name);
        return hash;
    }
    
    
    
    public <V> V to(ContentType<V> otherType, T data) {
        if (this.equals(otherType)) {
            return (V)data;
        }
        throw new IllegalArgumentException("Cannot convert type "+this+" to type "+otherType);
    } 
    
    public boolean canConvertTo(ContentType otherType) {
        if (this.equals(otherType)) {
            return true;
        }
        return false;
    }
    
    public <V> T from(ContentType<V> otherType, V data) {
        if (this.equals(otherType)) {
            return (T)data;
        }
        if (data == null) {
            return null;
        }
        if (otherType.isNumber) {
            if (representationClass == Integer.class) {
                return (T)(Integer)((Number)data).intValue();
            }
            if (representationClass == Double.class) {
                return (T)(Double)((Number)data).doubleValue();
            }
            if (representationClass == Long.class) {
                return (T)(Long)((Number)data).longValue();
            }
            if (representationClass == Float.class) {
                return (T)(Float)((Number)data).floatValue();
            }
            if (representationClass == Byte.class) {
                return (T)(Byte)(byte)((Number)data).intValue();
            }
            if (representationClass == Short.class) {
                return (T)(Short)((Number)data).shortValue();
            }
            if (representationClass == Boolean.class) {
                return (T)(Boolean)(((Number)data).intValue() != 0);
            }
        }
        if (otherType.representationClass == Boolean.class) {
            int out = ((boolean)(Boolean)data) ? 1 : 0;
            if (representationClass == Integer.class) {
                return (T)(Integer)(int)out;
            }
            if (representationClass == Double.class) {
                return (T)(Double)(double)out;
            }
            if (representationClass == Long.class) {
                return (T)(Long)(long)out;
            }
            if (representationClass == Float.class) {
                return (T)(Float)(float)out;
            }
            if (representationClass == Byte.class) {
                return (T)(Byte)(byte)out;
            }
            if (representationClass == Short.class) {
                return (T)(Short)(short)out;
            }
            if (representationClass == Boolean.class) {
                return (T)data;
            }
        }
        
        throw new IllegalArgumentException("Cannot convert type "+this+" from type "+otherType);
    }
    
    public boolean canConvertFrom(ContentType otherType) {
        if (this.equals(otherType)) {
            return true;
        }
        if (otherType == null) {
            return false;
        }
        if (otherType.isNumber || otherType.representationClass == Boolean.class) {
            if (representationClass == Boolean.class || isNumber) {
                return true;
            }
        }
        return false;
    }
    
    public static <T,V> T convert(ContentType<V> sourceType, V sourceData, ContentType<T> targetType) {
        if (targetType.canConvertFrom(sourceType)) {
            return targetType.from(sourceType, sourceData);
        } else if (sourceType.canConvertTo(targetType)) {
            return sourceType.to(targetType, sourceData);
        } else {
            
            for (DataTransformer dt : registeredTransformers) {
                if (dt.supports(sourceType, targetType)) {
                    return (T)dt.transform(sourceType, targetType, sourceData);
                }
            }
        }
        throw new IllegalArgumentException("No supported conversions from "+sourceType+" to "+targetType);
    }
    

    
    public static final ContentType<String> Text = new ContentType<String>(new Name("text/plain;java.lang.String"), String.class) {
        
        @Override
        public <V> V to(ContentType<V> otherType, String data) {
            if (super.canConvertTo(otherType)) {
                return super.to(otherType, data);
            }
            data = data.trim();
            Class cls = otherType.getRepresentationClass();
            
            if (cls == Integer.class) {
                if (data == null || data.length() == 0) {
                    return (V)(Integer)0;
                }
                if (data.startsWith("0x") && data.length() > 2) {
                    return (V)(Integer)Integer.parseInt(data.substring(2), 16);
                }
                return (V)(Integer)Integer.parseInt(data);
            }
            if (cls == Double.class) {
                if (data == null || data.length() == 0) {
                    return (V)(Double)0.0;
                }
                return (V)(Double)Double.parseDouble(data);
            }
            if (cls == Float.class) {
                if (data == null || data.length() == 0) {
                    return (V)(Float)0f;
                }
                return (V)(Float)Float.parseFloat(data);
            }
            if (cls == Boolean.class) {
                if (data == null || data.length() == 0) {
                    return (V)(Boolean)false;
                }
                return (V)(Boolean)Boolean.parseBoolean(data);
            }
            
            return super.to(otherType, data);
            
        }

        @Override
        public boolean canConvertTo(ContentType otherType) {
            Class cls = otherType.getRepresentationClass();
            return super.canConvertTo(otherType) ||
                    cls == Integer.class ||
                    cls == Double.class ||
                    cls == Float.class ||
                    cls == Boolean.class;
                    
        }

        @Override
        public <V> String from(ContentType<V> otherType, V data) {
            if (super.canConvertFrom(otherType)) {
                return super.from(otherType, data);
            }
            Class cls = otherType.getRepresentationClass();
            if (cls == Integer.class || cls == Double.class || cls == Float.class || cls == Boolean.class) {
                return String.valueOf(data);
            }
            if (super.canConvertFrom(otherType)) {
                return super.from(otherType, data);
            } else {
                return String.valueOf(data);
            }
            
            
        }

        @Override
        public boolean canConvertFrom(ContentType otherType) {
            return true;
            /*
            Class cls = otherType.getRepresentationClass();
            return super.canConvertFrom(otherType) ||
                    cls == Integer.class ||
                    cls == Double.class ||
                    cls == Float.class ||
                    cls == Boolean.class;
                    */

        }
        
 
    };
    
    
    
    public static final ContentType<Boolean> BooleanType = new ContentType<Boolean>(new Name("Boolean"), Boolean.class) {
        
    };
    
    public static final ContentType<Integer> IntegerType = new ContentType<Integer>(new Name("Integer"), Integer.class) {
        
    };
    
    public static final ContentType<Long> LongType = new ContentType<Long>(new Name("Long"), Long.class) {
        
    };
    
    
    
    public static final ContentType<Float> FloatType = new ContentType<Float>(new Name("Float"), Float.class) {};
    public static final ContentType<Double> DoubleType = new ContentType<Double>(new Name("Double"), Double.class){};
    public static final ContentType<Entity> EntityType = new ContentType<Entity>(new Name("Entity"), Entity.class){
        @Override
        public boolean canConvertFrom(ContentType otherType) {
            return otherType.isEntity();
            
        }

        @Override
        public boolean isEntity() {
            return true;
        }
        
        
        

        @Override
        public <V> V to(ContentType<V> otherType, Entity data) {
            if (data == null) {
                return null;
            }
            if (otherType.getRepresentationClass().isAssignableFrom(data.getClass())) {
                return (V)data;
            }
            return super.to(otherType, data);
        }

        @Override
        public <V> Entity from(ContentType<V> otherType, V data) {
            if (data instanceof Entity) {
                return (Entity)data;
            }
            return super.from(otherType, data); 
        }

        
        
        
    }; 
    public static final ContentType<EntityList> EntityListType = new ContentType<EntityList>(new Name("EntityList"), EntityList.class) {
        @Override
        public boolean canConvertFrom(ContentType otherType) {
            return otherType.isEntityList();
        }

        @Override
        public boolean isEntityList() {
            return true;
        }
        
        

         @Override
        public <V> V to(ContentType<V> otherType, EntityList data) {
            if (data == null) {
                return null;
            }
            if (otherType.getRepresentationClass().isAssignableFrom(data.getClass())) {
                return (V)data;
            }
            return super.to(otherType, data);
        }
        
        @Override
        public <V> EntityList from(ContentType<V> otherType, V data) {
            if (data == null) {
                return null;
            }
            if (data instanceof EntityList) {
                return (EntityList)data;
            }
            return super.from(otherType, data);
        }
        
        
        
    };
    public static <V> ContentType<V> createObjectType(Class<V> representationClass) {
        if (representationClass == Entity.class) {
            return (ContentType<V>)EntityType;
        }
        if (representationClass == EntityList.class) {
            return (ContentType<V>)EntityListType;
        }
        return new ContentType<V>(new Name(representationClass.getName()), representationClass) {
            
            private boolean isEntity;
            private boolean isEntityList;
            
            {
                isEntity = Entity.class.isAssignableFrom(representationClass);
                isEntityList =  EntityList.class.isAssignableFrom(representationClass);;
            }
            
            @Override
            public boolean equals(Object obj) {
                return obj.getClass() == this.getClass() && ((ContentType)obj).getRepresentationClass() == representationClass;
                
            }

            @Override
            public boolean isEntity() {
                return isEntity;
            }

            @Override
            public boolean isEntityList() {
                return isEntityList;
            }
            
            
            
            

            @Override
            public int hashCode() {
                return representationClass.hashCode();
            }

            @Override
            public boolean canConvertFrom(ContentType otherType) {
                
                return representationClass.isAssignableFrom(otherType.representationClass);
            }

            @Override
            public <U> V from(ContentType<U> otherType, U data) {
                if (representationClass.isAssignableFrom(otherType.representationClass)) {
                    return (V)data;
                }
                return super.from(otherType, data);
            }

            @Override
            public <U> U to(ContentType<U> otherType, V data) {
                if (otherType.representationClass.isAssignableFrom(representationClass)) {
                    return (U)data;
                }
                return super.to(otherType, data);
            }
            
            
            
            
            
            
        };
    }
    
    
    
    public static final ContentType<Date> DateType = new ContentType<Date>(new Name("Date"), Date.class){
        @Override
        public <V> V to(ContentType<V> otherType, Date data) {
            if (otherType.getRepresentationClass() == Date.class) {
                return (V)data;
            }
            
            if (otherType == Text) {
                return (V)new AllFormatsDateFormatter().format(data);
            }
            if (otherType == LongType) {
                return (V)(Long)data.getTime();
            }
            
            return super.to(otherType, data);
        }

        @Override
        public boolean canConvertTo(ContentType otherType) {
            return (otherType.getRepresentationClass() == Date.class)  || otherType == Text || otherType == LongType;
            
        }

        @Override
        public <V> Date from(ContentType<V> otherType, V data) {
            if (otherType.getRepresentationClass() == Date.class) {
                return (Date)data;
            }
            
            if (otherType == Text) {
                try {
                    return new AllFormatsDateFormatter().parse((String)data);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
            if (otherType == LongType) {
                return new Date((Long)data);
            }
            return super.from(otherType, data);

        }
        
        @Override
        public boolean canConvertFrom(ContentType otherType) {
            return (otherType.getRepresentationClass() == Date.class)  || otherType == Text || otherType == LongType;
            
        }
        
        
        
    
    };
    
    
    //public static <T> ContentType<T> createPojoType(Class<T> type) {
    //    return new ContentType<T>(new Name(type.getName()), type);
    //}
            
   
    
    
}
