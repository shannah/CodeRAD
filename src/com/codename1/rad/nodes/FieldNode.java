/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.nodes;

;
import com.codename1.rad.attributes.WidgetType;
import com.codename1.rad.models.Attribute;
import com.codename1.rad.models.EntityType;
import com.codename1.rad.models.Property;
import com.codename1.rad.models.Property.Description;
import com.codename1.rad.models.Property.Editable;
import com.codename1.rad.models.Property.Label;
import com.codename1.rad.models.Property.Widget;
import com.codename1.rad.models.Tags;


/**
 *
 * @author shannah
 */
public class FieldNode extends Node implements Proxyable {
    public FieldNode(Attribute... atts) {
        super(null, atts);
        
    }
    
    
    
    public FieldNode copy() {
        FieldNode out = new FieldNode();
        for (Attribute att : attributes) {
            out.setAttributes(att);
        }
        out.setProxying(getProxying());
        out.setParent(getParent());
        return out;
    }
    
    public FieldNode createProxy(Node newParent) {
        FieldNode out = new FieldNode();
        out.setParent(newParent);
        out.setProxying(this);
        return out;
    }

    @Override
    public boolean canProxy() {
        return true;
    }
    
    
    
    /**
     * Gets the property for this field.  First this will check for an explicit
     * property setting using {@link #getProperty()}, and if none is foune, it will
     * resolve the tags against the given entity type to find the appropriate property
     * of the entity type.
     * @param context The entity type to find the property from, in case no property is explicitly set.
     * @return The property or null.
     */
    public Property getProperty(EntityType context) {
        PropertyNode explicitProperty = getProperty();
        if (explicitProperty != null) {
            return explicitProperty.getValue();
        }
        
        if (context == null) {
            return null;
        }
        Tags tags = getTags();
        if (tags != null && !tags.isEmpty()) {
            for (Property prop : context) {
                Tags propTags = prop.getTags();
                if (tags.intersects(propTags)) {
                    return prop;
                }
            }
        }
        
        return null;
    }
    
    
    public Tags getTags() {
        return (Tags)findAttribute(Tags.class);
    }
    
    public PropertyNode getProperty() {
        return (PropertyNode)findAttribute(PropertyNode.class);
    }

    public Attribute findAttribute(Class type, EntityType entityType) {
        Attribute out = super.findAttribute(type);
        if (out == null) {
            if (type == PropertyNode.class) {
                return null;
            }
            Property prop = getProperty(entityType);
            if (prop != null) {
                Widget w = (Widget)prop.getAttribute(Widget.class);
                if (w != null) {
                    out = w.getValue().getAttribute(type);
                }
                if (out == null) {
                    out = prop.getAttribute(type);
                }
            }
        }
        return out;
    }
    
    @Override
    public Attribute findAttribute(Class type) {
        Attribute out = super.findAttribute(type);
        if (out == null) {
            if (type == PropertyNode.class) {
                return null;
            }
            PropertyNode propNode = getProperty();
            if (propNode != null) {
                Widget w = (Widget)propNode.getValue().getAttribute(Widget.class);
                if (w != null) {
                    out = w.getValue().getAttribute(type);
                }
                if (out == null) {
                    out = propNode.getValue().getAttribute(type);
                }
            }
        }
        return out;
    }
    
    
    
    public Label getLabel() {
        return (Label)findAttribute(Label.class);
    }
    
    public Label getLabel(EntityType context) {
        return (Label)findAttribute(Label.class, context);
    }
    
    public Description getDescription() {
        return (Description)findAttribute(Description.class);
    }
    
    public Description getDescription(EntityType context) {
        return (Description)findAttribute(Description.class, context);
    }
    
    
    
    public WidgetType getWidgetType() {
        return (WidgetType)findAttribute(WidgetType.class);
    }
    
    public WidgetType getWidgetType(EntityType context) {
        return (WidgetType)findAttribute(WidgetType.class, context);
    }
    
    
    public PropertyViewFactoryNode getViewFactory() {
        PropertyViewFactoryNode n = (PropertyViewFactoryNode) findInheritedAttribute(PropertyViewFactoryNode.class);
        
        return n;
    }
    
    public OptionsNode getOptions() {
        return (OptionsNode)findAttribute(OptionsNode.class);
    }
    
    public OptionsNode getOptions(EntityType context) {
        return (OptionsNode)findAttribute(OptionsNode.class, context);
    }
    
    public boolean isEditable() {
        Editable editable = (Editable)findInheritedAttribute(Editable.class);
        if (editable == null) {
            return false;
        }
        return editable.getValue();
    }
    
    
    
}
