/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;


import com.codename1.rad.models.Property.Description;
import com.codename1.rad.models.Property.Label;
import com.codename1.rad.models.Property.Widget;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Encapsulates an entity "type" for an {@link Entity} class.  This is sort of like a "meta-class" that provides run-time support for data conversion, 
 * property lookup, and property binding.  An EntityType should create its {@link Property}s in its instance initializer.  Typically, the entity type
 * is declared as a static final anonymous class inside the {@link Entity} class definition as follows:
 * 
[source,java]
----
public class UserProfile extends Entity {
    public static StringProperty name, description; <1>
    public static final EntityType TYPE = new EntityType(){{ <2>
        name = string(); <3>
        description = string();
    }};
    {
        setEntityType(TYPE); <4>
    }
}
----
<1> We define 2 properties of type {@link com.codename1.rad.models.StringProperty} on the class.  A `StringProperty` is simply a property that contains a {@link java.lang.String}.  These are defined `public static` so that we can access them conveniently from anywhere.
<2> We define an {@link com.codename1.rad.models.EntityType} for the class.  This is also `public static` because it is class-level (all objects of this class should share the same entity type).
<3> We create `name` and `description` properties on this entity type.  Notice that this code runs in the *instance intializer* of the EntityType (the `{{` and `}}` braces are not a typo).  Running this code inside the instance initializer will ensure that the properties are added to the `EntityType`'s property index.
<4> Inside the `UserProfile` instance initializer, we set the entity type to the entity type that we created above.
* 
* Technically, you don't need to provide direct property access to your entity properties at all.  In our above `UserProfile` class we retained explicit references to the `name` and `description` properties, but we could have simply omitted this.  I.e. The following is also a perfectly valid entity type definition:

.An entity type that doesn't retain explicit references to its properties.  The properties can still be accessed via their assigned tags.
[source,java]
----
public class UserProfile extends Entity {
    public static final EntityType TYPE = new EntityType(){{
        string(tags(Thing.name));
        string(tags(Thing.description));
    }};
    {
        setEntityType(TYPE);
    }
}
----
* 
* == Property Types
* 
* {@link EntityType} includes convenience methods for creating the standard property types:
* 
* . {@link #string(com.codename1.rad.models.Attribute...) }
* . {@link #Integer(com.codename1.rad.models.Attribute...) }
* . {@link #Boolean(com.codename1.rad.models.Attribute...) }
* . {@link #Double(com.codename1.rad.models.Attribute...) }
* . {@link #date(com.codename1.rad.models.Attribute...) }
* . {@link #entity(Class, Attribute[])} - an entity
* . {@link #list(java.lang.Class, com.codename1.rad.models.Attribute...) } - A list of entities.
* 
* These methods will create the corresponding property and add it to the {@link EntityType} using {@link #addProperty(com.codename1.rad.models.Property) }.  You can also create custom property types by subclassing {@link AbstractProperty}
* 
 * @author shannah
 */
public class EntityType implements Iterable<Property>, EntityFactory {
    private Class entityClass;
    private EntityType superType;
    private ContentType contentType;
    private EntityFactory factory;
    // Factory for creating wrapper objects.
    private EntityWrapperFactory wrapperFactory;
    //private Map<String,Property> properties = new HashMap<>();
    private final Set<Property> propertiesSet = new LinkedHashSet<>();
    private static Map<Class<?>, EntityType> types = new HashMap<>();
    
    private Class listType, rowType;
    
    public EntityType getRowEntityType() {
        if (rowType == null) {
            return null;
        }
        if (types.containsKey(rowType)) {
            return types.get(rowType);
        }
        return null;
    }
    
    public EntityType getListEntityType() {
        if (listType == null) {
            return null;
        }
        if (types.containsKey(listType)) {
            return types.get(listType);
        }
        return null;
    }
   
    public EntityFactory getRowFactory() {
        if (rowType == null) {
            throw new IllegalStateException("Cannot get row factory for entity type "+this+" because no row type is defined");
        }
        EntityType rowInfo = types.get(rowType);
        if (rowInfo == null) {
            throw new IllegalStateException("Cannot get row factory for entity type "+this+" because no row factory is registered for row type "+rowType);
        }
        return rowInfo.getFactory(rowType);
    }
        
    public EntityFactory getListFactory() {
        if (listType == null) {
            throw new IllegalStateException("Cannot get list factory for entity type "+this+" because not list type is defined");

        }
        EntityType listInfo = types.get(listType);
        if (listInfo == null) {
            throw new IllegalStateException("Cannot get list factory for entity type "+this+" because no list factory is registered for list type "+listType);
        }
        return listInfo.getFactory(listType);
    }
    
    
    public static EntityFactory getFactory(Class type) {
        EntityType info = types.get(type);
        if (info == null) {
            throw new IllegalStateException("Cannot get factory for type "+type+" because "+type+" is not a registered entity type");
        }
        return info.getFactory();
    }
    
    public static EntityFactory getRowFactory(Class type) {
        EntityType info = types.get(type);
        if (info == null) {
            throw new IllegalStateException("Cannot get row factory for type "+type+" because "+type+" is not a registered entity type");
        }
        return info.getRowFactory();
    }
    
    public static EntityFactory getListFactory(Class type) {
        EntityType info = types.get(type);
        if (info == null) {
            throw new IllegalStateException("Cannot get list factory for type "+type+" because "+type+" is not a registered entity type");
        }
        return info.getListFactory();
    }
    
    /**
     * Creates an entity for the given class.
     * @param type A class to create the entity for.  This can be either an EntityType class or an EntityClass
     * @return A new entity of the given type.
     * @throws IllegalArgumentException if either the entity type isn't registered or it fails to create an instance of it.
     */
    public static Entity createEntityForClass(Class type) {
        EntityFactory factory = getFactory(type);
        if (factory != null) {
            Entity out = factory.createEntity(type);
            if (out != null) {
                return out;
            }
        }
        Throwable ex = null;
        if (Entity.class.isAssignableFrom(type)) {
            try {
                return (Entity)type.newInstance();
            } catch (Throwable t){
                ex = t;
            }
        }
        if (EntityType.class.isAssignableFrom(type)) {
            
            EntityType et = (EntityType)types.get(type);
            if (et != null) {
                
                
                return et.newInstance();
            }
            throw new IllegalArgumentException("EntityType not found for class "+type);
        }
        if (ex != null) {
            throw new IllegalStateException("Tried to instantiate entity with its constructor but failed.  Likely this entity class is either an internal class, a private class, has a private constructor, or has no no-arg constructor.   You can fix most of these issues by registering a factory for this entity type: "+type+".  Caused by error: "+ex.getMessage());
        }
        throw new IllegalArgumentException("createEntityForClass() expects either an Entity or EntityType subclass as a parameter, but received "+type);
    }
    
   
    
    /**
     * Gets the singleton EntityType instance for the given EntityType class.
     * @param type The EntityType or Entity class.
     * @return The singleton EntityType instance for the given EntityType class.
     */
    public static EntityType getEntityType(Class<?> type) {
        EntityType t = types.get(type);

        if (t == null) {
            try {
                if (EntityType.class.isAssignableFrom(type)) {
                    t = (EntityType)type.newInstance();
                } else if (Entity.class.isAssignableFrom(type)) {
                    Entity e = createEntityForClass(type);
                    t = e.getEntityType();
                }
                
                types.put(type, t);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return t;
        
    }
    
    
    public Class getEntityClass() {
        return entityClass;
    }
    
    public static boolean isRegisteredEntityType(Class type) {
        return types.containsKey(type);
    }
    
    /**
     * Gets the ContentType of this EntityType.  Default is {@link ContentType#EntityType}.
     * @return The ContentType of this entity type.
     */
    public ContentType getContentType() {
        if (contentType == null) {
            return ContentType.EntityType;
        }
        return contentType;
    }
    
    /**
     * Creates a new instance of this entity type.
     * @return 
     */
    public Entity newInstance() {
        return createEntity(entityClass);
    }
    
    public static void deregister(Class... classes) {
        for (Class type : classes) {
            Entity e = (Entity)createEntityForClass(type);
            EntityType et = e.getEntityType();
            
            types.remove(et.getClass());
            types.remove(type);
        }
    }
    
    public static <T extends EntityList, V extends Entity> void registerList(Class<T> listClass, Class<V> rowType){
        registerList(listClass, rowType, (EntityFactory)null);
    }
    
    public static <T extends EntityList, V extends Entity> void registerList(Class<T> listClass, Class<V> rowType, EntityFactory factory) {
        register(listClass, factory);
        EntityType rowEntityType = EntityType.getEntityType(rowType);
        if (rowEntityType == null) {
            throw new IllegalStateException("Row type "+rowType+" must be registered before using it as a row type for registering "+listClass);
        }
        rowEntityType.setListType(listClass);
    }
    
    public static void register(Class... classes) {
        for (Class type : classes) {
            register(type, (EntityType)null, (EntityFactory)null);
        }
    }
    
    public  static  <T extends Entity> void register(Class<T> cls, EntityFactory factory) {
        if (factory instanceof EntityType) {
            register(cls, (EntityType)factory, (EntityFactory)null);
        } else {
            register(cls, (EntityType)null, factory);
        }
    }
    
    public void setRowType(Class type) {
        if (entityClass == null) {
            throw new IllegalStateException("Cannot set row type on entity type that has no representation class specified");
        }
        if (EntityList.class.isAssignableFrom(entityClass)) {
            throw new IllegalStateException("setRowType() only applicable to list entity types.  "+this.getClass()+" is not a list entity type");
        }
        EntityType info = types.get(entityClass);
        if (info == null) {
            info = this;
            types.put(entityClass, info);
        }
        
        EntityType rowTypeInfo = types.get(type);
        if (rowTypeInfo == null) {
            throw new IllegalStateException("The type "+type+" is not registered.  setRowType() requires a registered entity class as an argument");
        }
        
        if (rowTypeInfo.listType == null) {
            rowTypeInfo.listType = entityClass;
        }
        info.rowType = type;
    }
    
    public void setListType(Class type) {

        if (entityClass == null) {
            throw new IllegalStateException("Cannot set list type on entity type that has no representation class specified");
        }
        if (!EntityList.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException("setListType() requires a list entity type as argument.  "+type+" is not a list entity type");
        }
        EntityType info = types.get(entityClass);
        if (info == null) {
            info = this;
            types.put(entityClass, info);
        }
        
        EntityType listTypeInfo = types.get(type);
        if (listTypeInfo == null) {
           throw new IllegalStateException(type+" is not a registered entity type.  setListType() requires a registered entity type as an argument");
        }
        
        if (listTypeInfo.rowType == null) {
            listTypeInfo.rowType = entityClass;
        }
        info.listType = type;
    }
    
    /**
     * Register an entity class and its optional factory.  This should be called before 
     * using an Entity class.
     * @param <T> 
     * @param cls The entity class to register.
     * @param factory The optional factory to create instances of this class. 
     * If the class is public and has a no-arg constructor, then a factory isn't 
     * necessary.  But if it is private or doesn't have a public no-arg constructor
     *  you should provide a factory.
     */
    public  static  <T extends Entity> void register(Class<T> cls, EntityType et, EntityFactory factory) {
        if (cls == Entity.class || cls == EntityList.class) {
            throw new IllegalArgumentException("Cannot register Entity or EntityList directly.  Must register a subclass");
        }
        Entity e = null;
        if (factory != null) {
            e = factory.createEntity(cls);
        } else {
            try {
                e = (Entity)cls.newInstance();
                
            } catch (Throwable t) {
                throw new IllegalStateException("Failed to register entity type "+cls+" because calling its no-arg constructor failed.  Ensure that either this is a public class with a no-arg constructor, or provide a factory to be able to instantiate this class.  Caused by: "+t.getMessage());
            }
        }
       
        if (et == null) {
            et = e.getEntityType();
        }
        if (et == null) {
            et = new EntityTypeBuilder()
                    .build();
            
            
            
        }
        if (et.entityClass == null) {
            et.entityClass = cls;
        }
        types.put(cls, et);
        
        et.factory(factory);
        
    }
    
    public EntityType factory(EntityFactory factory) {
        if (factory == this) {
            throw new IllegalArgumentException("Cannot set EntityType as its own factory.");
        }
        this.factory = factory;
        
        return this;
    }
    
    public EntityFactory getFactory() {
        return this.factory;
    }

    public EntityType wrapperFactory(EntityWrapperFactory factory) {
        this.wrapperFactory = factory;
        return this;
    }

    public EntityWrapperFactory getWrapperFactory() {
        return wrapperFactory;
    }

    <T> T createWrapperFor(Entity e, Class<T> wrapperType) {
        if (wrapperFactory == null) {
            throw new IllegalStateException("EntityType cannot create wrapper for entity because it doesn't have a wrapper factory set");
        }
        return wrapperFactory.createWrapperFor(e, wrapperType);
    }
    
    /**
     * Initializes content type of an entity.
     * @param e 
     */
    void initContentType(Entity e) {
        if (contentType == null) {
            contentType = ContentType.createObjectType(e.getClass());
        }
        if (entityClass == null) {
            if (e.getClass() != Entity.class) {
                setEntityClass(e.getClass());
            }
        }
        if (entityClass != null && !types.containsKey(entityClass)) {
            types.put(e.getClass(), this);
        }
    }
    
    /**
     * Adds a property to this EntityType.  
     * @param property The property to add.
     */
    public void addProperty(Property property) {
        propertiesSet.add(property);
    }
    
    /**
     * Adds a set of properties to this EntityType.
     * @param properties The properties to add.
     */
    public void addAllProperties(Property... properties) {
        for (Property p : properties) {
            addProperty(p);
        }
    }
    
    
    /**
     * Removes a property from this EntityType.
     * @param property The property to remove.
     * @return True if the property was removed.   False if no changes were made (i.e. it didn't have this property).
     */
    public boolean removeProperty(Property property) {
        if (propertiesSet.contains(property)) {
            propertiesSet.remove(property);
            return true;
        }
        return false;
    }

    @Override
    public Iterator<Property> iterator() {
        return propertiesSet.iterator();
    }

    /**
     * Flag indicating whether this is a dynamic entity type.  Dynamic entity types are types that
     * have no specified schema (you can add any properties, to entities of this type, without being 
     * constrained by the properties defined in the EntityType.
     * 
     * Entities that don't have a type specified with {@link Entity#setEntityType(com.codename1.rad.models.EntityType) }
     * will, by default have a dynamic entity type.
     * @return 
     */
    public boolean isDynamic() {
        return false;
    }
    
    /**
     * Checks if this entity type contains the given property.
     * @param property
     * @return 
     */
    public boolean contains(Property property) {
        return propertiesSet.contains(property);
    }
    
    /**
     * Creates a new string property on this entity type, and adds it to the property set.
     * @param atts The attributes of this property.
     * @return The resulting property.
     */
    public StringProperty string(Attribute... atts) {
        StringProperty out = new StringProperty();
        out.setAttributes(atts);
        propertiesSet.add(out);
        return out;
    }
    
    /**
     * Creates a new property containing a specific Object type, and adds it to the property set.
     * @param <T> The type of object
     * @param type The Type of object.
     * @param atts Attributes for the property
     * @return The property.
     */
    public <T> SimpleProperty<T> object(Class<T> type, Attribute... atts) {
        SimpleProperty<T> out = new SimpleProperty<T>(type);
        out.setAttributes(atts);
        propertiesSet.add(out);
        return out;
    }
    
    /**
     * Creates a new date property, and adds it to the property set.
     * @param atts Attributes of the property
     * @return The property.
     */
    public DateProperty date(Attribute... atts) {
        DateProperty out = new DateProperty();
        out.setAttributes(atts);
        propertiesSet.add(out);
        return out;
    }
    
    /**
     * Creates a new Integer property, and adds it to the property set.
     * @param atts Attributes of the property
     * @return The property.
     */
    public IntProperty Integer(Attribute... atts) {
        IntProperty p = new IntProperty();
        p.setAttributes(atts);
        propertiesSet.add(p);
        
        return p;
     
    }
    
    /**
     * Creates a new Double property, and adds it to the property set.
     * @param atts Attributes of the property
     * @return The property.
     */
    public DoubleProperty Double(Attribute... atts) {
        DoubleProperty d = new DoubleProperty();
        d.setAttributes(atts);
        propertiesSet.add(d);
        return d;
    }
    
    /**
     * Creates a new Boolean property, and adds it to the property set.
     * @param atts Attributes of the property
     * @return The property.
     */
    public BooleanProperty Boolean(Attribute... atts) {
        BooleanProperty b = new BooleanProperty();
        b.setAttributes(atts);
        propertiesSet.add(b);
        return b;
    }
    
    /**
     * Creates a new ListProperty (i.e. a property containing an EntityList and adds it to the property set.
     * @param <T> The property type.  Subclass of EntityList.
     * @param type The property type.  Subclass of EntityList
     * @param atts THe attributes of the property
     * @return The property.
     */
    public <T extends EntityList> ListProperty<T> compose(Class<T> type, Attribute... atts) {
        ListProperty p = new ListProperty(type);
        p.setAttributes(atts);
        propertiesSet.add(p);
        return p;
    }
    
    /**
     * Alias of {@link #compose(java.lang.Class, com.codename1.rad.models.Attribute...) }.
     * @param <T> The property type.  Subclass of EntityList
     * @param type The property type.  Subclass of EntityList
     * @param atts The attributes of the property
     * @return The property.
     */
    public <T extends EntityList> ListProperty<T> list(Class<T> type, Attribute... atts) {
        return compose(type, atts);
    }

    public ListProperty entityList(Attribute... atts) {
        return list(EntityList.class, atts);
    }
    
    /**
     * Creates a new EntityProperty (i.e. a property containing an Entity and adds it to the property set.
     * @param <T> The property type.  Subclass of Entity.
     * @param type The property type.  Subclass of Entity
     * @param atts THe attributes of the property
     * @return The property.
     */
    public <T extends Entity> EntityProperty<T> entity(Class<T> type, Attribute... atts) {
        EntityProperty<T> p =  new EntityProperty(type);
        p.setAttributes(atts);
        propertiesSet.add(p);
        return p;
    }

    public EntityProperty entity(Attribute... atts) {
        EntityProperty p = new EntityProperty(Entity.class);
        p.setAttributes(atts);
        propertiesSet.add(p);
        return p;
    }

    
    
    
    /**
     * Creates a label attribute.
     * @param label
     * @return The label attribute.
     */
    public static Label label(String label) {
        return new Label(label);
    }
    
    /**
     * Creates a description attribute.
     * @param description
     * @return The description attribute.
     */
    public static Description description(String description) {
        return new Description(description);
    }
    
    /**
     * Creates a widget attribute
     * @param atts
     * @return The widget attribute.
     */
    public static Widget widget(Attribute... atts) {
        WidgetDescriptor desc = new WidgetDescriptor();
        desc.setAttributes(atts);
        return new Widget(desc);
    }
    
    /**
     * Creates a Tags attribute.
     * @param atts
     * @return 
     */
    public static Tags tags(Tag... atts) {
        return new Tags(atts);
    }
    
    /**
     * Finds a property in the entity type matching the given tags.  The first tag resolving to a property is used.
     * @param tags The tags to look for.
     * @return The matching property, or null, if none found.
     */
    public Property findProperty(Tag... tags) {
        for (Tag tag : tags) {
            for (Property prop : propertiesSet) {
                if (prop.getTags().contains(tag)) {
                    return prop;
                }
            }
        }
        return null;
    }
    
    /**
     * Gets a property value of an Entity.
     * @param prop The property
     * @param entity The entity.
     * @param outputType The output type to coerce the value to.
     * @return 
     */
    public Object getPropertyValue(Property prop, Entity entity, ContentType outputType) {
        return ContentType.convert(prop.getContentType(), prop.getValue(entity), outputType);
    }
    
    /**
     * Sets a property value of an entity
     * @param prop The property
     * @param entity The entity.
     * @param inputType The input type from which to coerce the value.
     * @param data The value to set.s
     */
    public void setPropertyValue(Property prop, Entity entity, ContentType inputType, Object data) {
        prop.setValue(entity, ContentType.convert(inputType, data, prop.getContentType()));
    }
    
    
    /**
     * Gets property value of an entity as a string.
     * @param prop The property
     * @param entity The entity
     * @return The string value.
     */
    public String getText(Property prop, Entity entity) {
        return (String)getPropertyValue(prop, entity, ContentType.Text);
    }
    
    /**
     * Sets the property value of an entity as a string.
     * @param prop The property
     * @param entity The entity
     * @param text The text to set.
     */
    public void setText(Property prop, Entity entity, String text) {
        setPropertyValue(prop, entity, ContentType.Text, text);
    }
    
    /**
     * Gets property value of an entity as an int.
     * @param prop The property
     * @param entity The entity
     * @return The int value.
     */
    public Integer getInt(Property prop, Entity entity) {
        return (Integer)getPropertyValue(prop, entity, ContentType.IntegerType);
    }
    
    public void setInt(Property prop, Entity entity, int value) {
        setPropertyValue(prop, entity, ContentType.IntegerType, value);
    }
    
    /**
     * Gets property value of an entity as a Double.
     * @param prop The property
     * @param entity The entity
     * @return The double value.
     */
    public Double getDouble(Property prop, Entity entity) {
        return (Double)getPropertyValue(prop, entity, ContentType.DoubleType);
    }
    
    public void setDouble(Property prop, Entity entity, double val) {
        setPropertyValue(prop, entity, ContentType.DoubleType, val);
    }

    public void setLong(Property prop, Entity entity, long val) {
        setPropertyValue(prop, entity, ContentType.LongType, val);
    }
    
    /**
     * Gets property value of an entity as a long.
     * @param prop The property
     * @param entity The entity
     * @return The long value.
     */
    public Long getLong(Property prop, Entity entity) {
        return (Long)getPropertyValue(prop, entity, ContentType.LongType);
    }
    
    /**
     * Gets property value of an entity as a float.
     * @param prop The property
     * @param entity The entity
     * @return The float value.
     */
    public Float getFloat(Property prop, Entity entity) {
        return (Float)getPropertyValue(prop, entity, ContentType.FloatType);
    }
    
    public void setFloat(Property prop, Entity entity, float val) {
        setPropertyValue(prop, entity, ContentType.FloatType, val);
    }
    
    /**
     * Gets property value of an entity as a boolean.
     * @param prop The property
     * @param entity The entity
     * @return The boolean value.
     */
    public Boolean getBoolean(Property prop, Entity entity) {
        return (Boolean)getPropertyValue(prop, entity, ContentType.BooleanType);
    }
    
    public void setBoolean(Property prop, Entity entity, boolean val) {
        setPropertyValue(prop, entity, ContentType.BooleanType, val);
    }
    
    
    public Object getPropertyValue(Tag tag, Entity entity, ContentType outputType) {
        Property prop = findProperty(tag);
        if (prop == null) {
            return null;
        }
        return getPropertyValue(prop, entity, outputType);
    }
    
    public boolean setPropertyValue(Tag tag, Entity entity, ContentType inputType, Object val) {
        Property prop = findProperty(tag);
        if (prop == null) {
            return false;
        }
        
        setPropertyValue(prop, entity, inputType, val);
        return true;
        
    }
    
    public Object getPropertyValue(Entity entity, ContentType outputType, Tag... tags) {
        for (Tag tag : tags) {
            Property prop = findProperty(tag);
            if (prop == null) {
                continue;
            }
            return getPropertyValue(prop, entity, outputType);
        }
        return null;
    }
    
    public boolean setPropertyValue(Entity entity, ContentType inputType, Object val, Tag... tags) {
        for (Tag tag : tags) {
            Property prop = findProperty(tag);
            if (prop == null) {
                continue;
            }
            setPropertyValue(prop, entity, inputType, val);
            return true;
        }
        return false;
    }
    
    public Object getPropertyValue(Tag tag, Entity entity, ContentType outputType, Object defaultVal) {
        Property prop = findProperty(tag);
        if (prop == null) {
            return defaultVal;
        }
        return getPropertyValue(prop, entity, outputType);
    }
    
    public String getText(Tag tag, Entity entity) {
        return (String)getPropertyValue(tag, entity, ContentType.Text);
    }
    
    public boolean setText(Tag tag, Entity entity, String text) {
        return setPropertyValue(tag, entity, ContentType.Text, text);
    }
    
    public String getText(Entity entity, Tag... tags) {
        return (String)getPropertyValue(entity, ContentType.Text, tags);
    }
    
    public boolean setText(Entity entity, String text, Tag... tags) {
        return setPropertyValue(entity, ContentType.Text, text, tags);
    }
    
    public Integer getInt(Tag prop, Entity entity) {
        return (Integer)getPropertyValue(prop, entity, ContentType.IntegerType);
    }
    
    public boolean setInt(Tag prop, Entity entity, int val) {
        return setPropertyValue(prop, entity, ContentType.IntegerType, val);
    }
    
    public Integer getInt(Entity entity, Tag... tags) {
        return (Integer)getPropertyValue(entity, ContentType.IntegerType, tags);
    }
    
    public boolean setInt(Entity entity, int val, Tag... tags) {
        return setPropertyValue(entity, ContentType.IntegerType, val, tags);
    }
    
    public Double getDouble(Entity entity, Tag... tags) {
        return (Double)getPropertyValue(entity, ContentType.DoubleType, tags);
    }
    
    public boolean setDouble(Entity entity, double val, Tag... tags) {
        return setPropertyValue(entity, ContentType.DoubleType, val, tags);
    }
    
    public Double getDouble(Tag prop, Entity entity) {
        return (Double)getPropertyValue(prop, entity, ContentType.DoubleType);
    }
    
    public boolean setDouble(Tag prop, Entity entity, double val) {
        return setPropertyValue(prop, entity, ContentType.DoubleType, val);
    }
    
    public Long getLong(Entity entity, Tag... tags) {
        return (Long)getPropertyValue(entity, ContentType.LongType, tags);
    }
    
    public boolean setLong(Entity entity, long val, Tag... tags) {
        return setPropertyValue(entity, ContentType.LongType, val, tags);
    }
    
    public Long getLong(Tag prop, Entity entity) {
        return (Long)getPropertyValue(prop, entity, ContentType.LongType);
    }
    
    public boolean setLong(Tag prop, Entity entity, long val) {
        return setPropertyValue(prop, entity, ContentType.LongType, val);
    }
    
    public Float getFloat(Tag prop, Entity entity) {
        return (Float)getPropertyValue(prop, entity, ContentType.FloatType);
    }
    
    public boolean setFloat(Tag prop, Entity entity, float val) {
        return setPropertyValue(prop, entity, ContentType.FloatType, val);
    }
    
    public Float getFloat(Entity entity, Tag... tags) {
        return (Float)getPropertyValue(entity, ContentType.FloatType, tags);
    }
    
    public boolean setFloat(Entity entity, float val, Tag... tags) {
        return setPropertyValue(entity, ContentType.FloatType, val, tags);
    }
    
    public Boolean getBoolean(Tag prop, Entity entity) {
        return (Boolean)getPropertyValue(prop, entity, ContentType.BooleanType);
    }
    
    public boolean setBoolean(Tag prop, Entity entity, boolean val) {
        return setPropertyValue(prop, entity, ContentType.BooleanType, val);
    }
    
    public Boolean getBoolean(Entity entity, Tag... tags) {
        return (Boolean)getPropertyValue(entity, ContentType.BooleanType, tags);
    }
    
    
    public boolean setBoolean(Entity entity, boolean val, Tag... tags) {
        return setPropertyValue(entity, ContentType.BooleanType, val, tags);
    }
    
    public String getText(Tag tag, Entity entity, String defaultVal) {
        return (String)getPropertyValue(tag, entity, ContentType.Text, defaultVal);
    }
    
     public Integer getInt(Tag prop, Entity entity, Integer defaultVal) {
        return (Integer)getPropertyValue(prop, entity, ContentType.IntegerType, defaultVal);
    }
    
    public Double getDouble(Tag prop, Entity entity, Double defaultVal) {
        return (Double)getPropertyValue(prop, entity, ContentType.DoubleType, defaultVal);
    }
    
    public Float getFloat(Tag prop, Entity entity, Float defaultVal) {
        return (Float)getPropertyValue(prop, entity, ContentType.FloatType, defaultVal);
    }
    
    public Boolean getBoolean(Tag prop, Entity entity, Boolean defaultVal) {
        return (Boolean)getPropertyValue(prop, entity, ContentType.BooleanType, defaultVal);
    }
     
    public Date getDate(Property prop, Entity entity) {
        return (Date)getPropertyValue(prop, entity, ContentType.DateType);
    }
    
    public Date getDate(Tag tag, Entity entity) {
        return (Date)getPropertyValue(tag, entity, ContentType.DateType);
    }
    
    public Date getDate(Entity entity, Tag... tags) {
        return (Date)getPropertyValue(entity, ContentType.DateType, tags);
    }
    
    public void setDate(Property prop, Entity entity, Date date) {
        setPropertyValue(prop, entity, ContentType.DateType, date);
    }
    
    public boolean setDate(Tag tag, Entity entity, Date date) {
        return setPropertyValue(tag, entity, ContentType.DateType, date);
    }
    
    public boolean setDate(Entity entity, Date date, Tag... tags) {
        return setPropertyValue(entity, ContentType.DateType, date, tags);
    }
    

    private boolean frozen;
    
    /**
     * Freezes the entity type.  After the entity type has been frozen, it can no longer have properties added to it
     * and properties in the entity type can no longer be modified.
     */
    void freeze() {
        if (frozen) {
            return;
        }
        frozen = true;
        for (Property p : propertiesSet) {
            p.freeze();
        }
    }
    
    /**
     * Creates an entity type.
     */
    public EntityType() {
        
    }
    
    /**
     * Creates an entity type with the given properties.
     * @param properties 
     */
    public EntityType(Property... properties) {
        for (Property prop : properties) {
            propertiesSet.add(prop);
        }
    }
    
    /**
     * Sets the entity class for this entity type.
     * @param cls 
     */
    void setEntityClass(Class cls) {
        if (cls != Entity.class) {
            entityClass = cls;
        }
    }

    @Override
    public Entity createEntity(Class type) {
        if (type == null) {
            type = entityClass;
        }
        if (factory != null) {
            Entity out = factory.createEntity(type);
            if (out != null) {
                return out;
            }
        }
        if (entityClass == null) {
            Entity out = new Entity();
            out.setEntityType(this);
            return (Entity)out;
        }
        try {
            Entity out = (Entity)entityClass.newInstance();
            out.setEntityType(this);
            return (Entity)out;
        } catch (Throwable t) {
            Entity out = new Entity();
            out.setEntityType(this);
            return (Entity)out;
        }
    }
    
    
    
    
    
    
}
