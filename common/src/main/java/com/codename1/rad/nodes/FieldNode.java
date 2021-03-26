/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.nodes;



import com.codename1.rad.attributes.WidgetType;
import com.codename1.rad.models.Attribute;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.EntityType;
import com.codename1.rad.models.Property;
import com.codename1.rad.models.Property.Description;
import com.codename1.rad.models.Property.Editable;
import com.codename1.rad.models.Property.Label;
import com.codename1.rad.models.Property.Widget;
import com.codename1.rad.models.PropertySelector;
import com.codename1.rad.models.Tags;
import com.codename1.rad.ui.PropertyViewFactory;
import com.codename1.rad.ui.UI;
import com.codename1.rad.attributes.PropertySelectorAttribute;
import com.codename1.rad.attributes.PropertyImageRendererAttribute;
import com.codename1.rad.models.PropertySelectorProvider;
import com.codename1.rad.models.Tag;
import com.codename1.rad.ui.EntityEditor.DescriptionStyle;
import com.codename1.rad.ui.EntityEditor.DescriptionStyleAttribute;
import com.codename1.rad.ui.EntityEditor.LabelStyle;
import com.codename1.rad.ui.EntityEditor.LabelStyleAttribute;
import com.codename1.rad.ui.image.PropertyImageRenderer;


/**
 * A {@link Node} that represents a field (usually editable) on a form.  It can be added to a {@link FormNode}, or {@link SectionNode}, and 
 * can be bound to a {@link Property} using either {@link Tags} or {@link PropertyNode} attributes.  
 * 
 * 
 * 
 * == Supported Attributes
 * 
 * As with any node, there isn't a fixed limit on the attributes that it can support, but common attributes that it accepts (and will be respected by the {@link EntityEditor} view,
 * include:
 * 
 * . {@link PropertyNode} - A property that this field is bound to.  Note that using a {@link PropertyNode} attribute produces strong coupling to the {@link Property}.  
 * It is almost always better to use {@link Tags} instead to bind the field to a list of tags, and allow dynamic field binding.
 * . {@link Tags} - A list of tags that this field will try to bind to dynamically.  When the {@link EntityEditor} is created, it will try lookup the property of the {@link Entity} that 
 * contains one of these tags, and bind to that property.
 * . {@link Label} - The widget label when rendered in the {@link EntityEditor}
 * . {@link Description} - The widget description or help text when rendered in the {@link EntityEditor}.
 * . {@link WidgetType} - The widget type to use for editing this property.  This is used by the {@link PropertyViewFactory} when creating the component.
 * . {@link OptionsNode} - For widgets that have "options" to choose from, such as comboboxes, this allows you to specify the options that are available to select.
 * . {@link Editable} - Whether this widget is editable.  (Though this attribute is often added to a parent node so that it is applied to all nodes in the branch.).
 * 
 * == Creating Fields
 * 
 * {@link com.codename1.rad.ui.UI} contains convenience methods for creating {@link FieldNode} nodes of specific types.  These are just thin wrappers around
 * {@link #FieldNode(com.codename1.rad.models.Attribute...) } with {@link WidgetType} attributes set.  
 * 
 * . {@link com.codename1.rad.ui.UI#textArea(com.codename1.rad.models.Attribute...)} - A multiline text area.
 * . {@link com.codename1.rad.ui.UI#textField(com.codename1.rad.models.Attribute...) } - Single-line text field.
 * . {@link com.codename1.rad.ui.UI#comboBox(com.codename1.rad.models.Attribute...) } - A combobox.
 * . {@link com.codename1.rad.ui.UI#table(com.codename1.rad.models.Attribute...) } - A table
 * 
 * NOTE: The {@link EntityEditor} class is still undergoing active development to add support for more widget types, improve styles, and add more features.
 * 
 * == Example
 * 
 * The following is an example of a UI descriptor for editing a "Person" entity.

.A UI descriptor for a form to edit a "Person" entity
[source,java]
----

package com.codename1.demos.ddddemo;

import com.codename1.rad.ui.UI;
import com.codename1.rad.nodes.ActionNode;
import static com.codename1.demos.ddddemo.PersonEntityType.*;
import com.codename1.ui.FontImage;
import static com.codename1.ui.FontImage.MATERIAL_DELETE;
import static com.codename1.rad.nodes.FormNode.OVERFLOW_MENU;
import static com.codename1.rad.nodes.FormNode.BOTTOM_RIGHT_MENU;
import static com.codename1.rad.nodes.FormNode.TOP_LEFT_MENU;


public class PersonEditor extends UI {
    
    // Define some actions
    public static ActionNode 
        deleteAction = action(
            label("Delete"),
            description("Delete this user"),
            icon(MATERIAL_DELETE)
        ),
        showContactsAction = action(
            label("Open Contacts"),
            description("Show all contacts"),
            icon(FontImage.MATERIAL_CONTACTS)
        ),
        printAction = action(
            label("Print"),
            description("Print this page"),
            icon(FontImage.MATERIAL_PRINT)
        );
    
    {
        

        // Define the root form.
        form(
            actions(OVERFLOW_MENU, deleteAction, printAction),  <1>
            actions(TOP_LEFT_MENU, deleteAction, printAction, showContactsAction), <2>
            actions(BOTTOM_RIGHT_MENU, deleteAction, printAction), <3>
            editable(true),
            description("Please edit the person's information in the fields below"),
            label("Person Details"),
            columns(2),
            textField(
                label("Name"),
                description("Please enter your name"),
                tags(Person.name)
            ),
            textField(
                tags(description)
            ),
            comboBox(
                tags(DemoTags.hairColor)
            ),
            section(
                actions(TOP_LEFT_MENU, deleteAction, printAction),
                columns(1),
                label("Section 2"),
                textArea(
                    tags(DemoTags.userProfile)
                ),
                table(actions(OVERFLOW_MENU, deleteAction, printAction),
                   label("Quick Links"),
                   description("Useful links related to this person"),
                   editable(true),
                   //property(quicklinks),
                   tags(com.codename1.rad.schemas.Person.url),
                   columns(new QuickLinkEditor().getAllFields())
                )
            )
            
        );
    
}}

----
<1> We add an overflow menu to the form.
<2> Add some actions to the top-left menu.
<3> Add some actions to the bottom-right menu.

Notice how succinct, yet readable this code is.  We can convert this into an actual view with the following:

[source,java]
----
new EntityEditor(entity, new PersonEditor());
----

And the result:

.The UI generated from the above UI descriptor.  All fields are bound to the entity, so changes to the entity will instantly update the UI, and vice-versa.
image::../../../../doc-files/EntityEditor.png[]
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
    
    @Override
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
    
    public LabelStyle getLabelStyle(LabelStyle defaultStyle) {
        LabelStyleAttribute att = (LabelStyleAttribute)findInheritedAttribute(LabelStyleAttribute.class);
        if (att == null) {
            return defaultStyle;
        }
        return att.getValue();
    }
    
    public DescriptionStyle getDescriptionStyle(DescriptionStyle defaultStyle) {
        DescriptionStyleAttribute att = (DescriptionStyleAttribute)findInheritedAttribute(DescriptionStyleAttribute.class);
        if (att == null) {
            return defaultStyle;
        }
        return att.getValue();
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
    
    
    public PropertyViewFactory getViewFactory() {
        PropertyViewFactoryNode n = (PropertyViewFactoryNode) findInheritedAttribute(PropertyViewFactoryNode.class);
        if (n != null) {
            return n.getValue();
        }
        return UI.getDefaultPropertyViewFactory();
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
    
    /**
     * Gets a property selector for this field node.  If the filed contained
     * a PropertyNode or a Tags node, then it will construct a selector from those.
     * 
     * Otherwise it will check for a {@link ProeprtySelectorAttribute}, and return 
     * a selector constructed form that, with the provided entity root.
     * @param context
     * @return A property selector, or null if no property or tag is set, and no property selector is set.
     */
    public PropertySelector getPropertySelector(Entity context) {
        if (context == null) {
            return null;
        }
        Property prop = getProperty(context.getEntityType());
        if (prop != null) {
            return new PropertySelector(context, prop);
        }
        Tags tags = getTags();
        if (tags != null) {
            return new PropertySelector(context, tags.toArray());
        }
        PropertySelectorAttribute selectorProvider = (PropertySelectorAttribute)findAttribute(PropertySelectorAttribute.class);
        if (selectorProvider != null) {
            return selectorProvider.getValue(context);
        }
        return null;
    }
    
    public PropertyImageRenderer getIconRenderer() {
        PropertyImageRendererAttribute att = (PropertyImageRendererAttribute)findInheritedAttribute(PropertyImageRendererAttribute.class);
        if (att == null) {
            return null;
        }
        return att.getValue();
    }
    
    public static FieldNode createWithPropertySelector(PropertySelector selector) {
        return new FieldNode(UI.property(selector));
    }
    
    public static FieldNode createWithTags(Tag... tags) {
        return new FieldNode(UI.tags(tags));
    }
    
    public static FieldNode createdWithProperty(Property prop) {
        return new FieldNode(UI.property(prop));
    }
    
}
