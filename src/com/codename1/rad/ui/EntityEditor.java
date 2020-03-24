/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import static com.codename1.rad.ui.NodeUtilFunctions.buildBottomActionsBar;
import static com.codename1.rad.ui.NodeUtilFunctions.buildTopActionsBar;
import com.codename1.rad.ui.entityviews.EntityListView;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.nodes.FormNode;
import com.codename1.rad.nodes.ListNode;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.nodes.SectionNode;
import com.codename1.rad.nodes.ViewNode;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.EntityList;
import com.codename1.rad.models.Property;
import com.codename1.rad.models.Property.Name;
import com.codename1.components.SpanLabel;
import com.codename1.rad.models.Attribute;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.GridLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * A View that can render forms for editing an entity.  The form can be customized with a view descriptor to specify
 * the sections, actions, layout, and fields to include in the form.
 * 
 * .A UI descriptor for a form to edit a "Person" entity
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
 * 
 */
public class EntityEditor extends Container {
    private EntityForm form;
    private Entity entity;
    private Node rootNode;
    //private UI uiDescriptor;
    private boolean built;
    public static final ActionNode.Category TOP_LEFT_TOOLBAR = new ActionNode.Category(new Name("top_left_toolbar"));
    public static final ActionNode.Category TOP_RIGHT_TOOLBAR = new ActionNode.Category(new Name("top_right_toolbar"));
    public static final ActionNode.Category MORE_MENU = new ActionNode.Category(new Name("more_menu"));
    public static final ActionNode.Category BOTTOM_LEFT_TOOLBAR = new ActionNode.Category(new Name("bottom_left_toolbar"));
    public static final ActionNode.Category BOTTOM_RIGHT_TOOLBAR = new ActionNode.Category(new Name("bottom_right_toolbar"));
    public static final ActionNode.Category INLINE_LEFT = new ActionNode.Category(new Name("inline_left"));
    public static final ActionNode.Category INLINE_RIGHT = new ActionNode.Category(new Name("inline_right"));
    public static final ActionNode.Category INLINE_LEFT_LABEL = new ActionNode.Category(new Name("inline_left_label"));
    public static final ActionNode.Category INLINE_RIGHT_LABEL = new ActionNode.Category(new Name("inline_right_label"));
    
    /**
     * The style used for rendering field labels.  Can be set on individual field nodes or on a parent node.
     */
    public static enum LabelStyle {
        /**
         * Do not display label
         */
        None,
        
        /**
         * Display the label.
         */
        Default
    }
    
    /**
     * Attribute for setting {@link LabelStyle}.
     */
    public static class LabelStyleAttribute extends Attribute<LabelStyle> {
        public LabelStyleAttribute(LabelStyle value) {
            super(value);
        }
    }
    
    /**
     * Attribute for setting {@link DescriptionStyle}
     */
    public static enum DescriptionStyle {
        
        /**
         * Don't display description
         */
        None,
        
        /**
         * Display field description as a {@link HelpButton}
         */
        HelpButton,
        
        /**
         * Display description as text
         */
        SpanLabel
    }
    
    public static class DescriptionStyleAttribute extends Attribute<DescriptionStyle> {
        public DescriptionStyleAttribute(DescriptionStyle style) {
            super(style);
        }
    }
    
    public EntityEditor(Entity entity, UI uiDescriptor, EntityForm form) {
        this(entity, uiDescriptor.getRoot(), form);
    }
    
    public EntityEditor(Entity entity, UI uiDescriptor) {
        this(entity, uiDescriptor, null);
    }
  
    
    public EntityEditor(Entity entity, Node rootNode, EntityForm form) {
        this.entity = entity;
        //this.uiDescriptor = uiDescriptor;
        this.rootNode = rootNode;
        this.form = form;
    }
    
    public EntityEditor(Entity entity, Node rootNode) {
        this(entity, rootNode, null);
    }
    
    
    
    private void buildSections() {
        for (SectionNode section : (FormNode)rootNode) {
            SectionEditor sectionEditor = new SectionEditor(entity, section);
            sectionEditor.build();
            add(sectionEditor);
        }
    }
    
    private ViewNode getRootView() {
        if (rootNode instanceof ViewNode) {
            return (ViewNode)rootNode;
        }
        if (rootNode instanceof FormNode) {
            return ((FormNode)rootNode).getRootView();
        }
        return null;
    }
    
    private ListNode getRootList() {
        if (rootNode instanceof ListNode) {
            return (ListNode)rootNode;
        }
        if (rootNode instanceof FormNode) {
            return ((FormNode)rootNode).getRootList();
        }
        return null;
    }
    
    private void build() {
        if (built) {
            return;
        }
        built = true;
        removeAll();
        
        buildTopActionsBar(rootNode, this, entity);
        if (getRootView() != null) {
            setLayout(new BorderLayout());
            buildView();
        } else if (getRootList() != null) {
            setLayout(new BorderLayout());
            buildList();
        } else {
            setLayout(BoxLayout.y());
            buildSections();
        }
        buildBottomActionsBar(rootNode, this, entity);
        
        
        
        
        
    }
    
    private void buildView() {
        EntityViewFactory factory = getRootView().getViewFactory();
        if (factory != null) {
            add(BorderLayout.CENTER, (Component)factory.createView(entity, getRootView()));
        }
    }
    
    private void buildList() {
        if (entity instanceof EntityList) {
            EntityListView listView = new EntityListView((EntityList)entity, getRootList());
            add(BorderLayout.CENTER, listView);
        }
    }

    @Override
    protected void initComponent() {
        build();
        super.initComponent();
        
        
    }
    
    
    
    
    public class SectionEditor extends Container {
        Entity entity;
        SectionNode sectionDescriptor;
        private boolean built;
        public SectionEditor(Entity entity, SectionNode section) {
            this.entity = entity;
            this.sectionDescriptor = section;         
        }

        @Override
        protected void initComponent() {
            build();
            super.initComponent();
        }
        
        private void buildSections() {
            List<FieldEditor> currRow = new ArrayList<>();
            for (FieldNode field : sectionDescriptor) {
               if (field.getProperty(entity.getEntityType()) == null) {
                   continue;
               }
               FieldEditor propertyEditor = new FieldEditor(entity, field);
               propertyEditor.build();
               currRow.add(propertyEditor);
               if (currRow.size() == sectionDescriptor.getColumns().getValue()) {
                   add(GridLayout.encloseIn(currRow.size(), currRow.toArray(new Component[currRow.size()])));
                   currRow.clear();
               }
            }
            if (!currRow.isEmpty()) {
                add(GridLayout.encloseIn(sectionDescriptor.getColumns().getValue(), currRow.toArray(new Component[currRow.size()])));
            }
        }
        
        private void build() {
            if (built) {
                return;
            }
            built = true;
            removeAll();
            setLayout(BoxLayout.y());
            Property.Label label = sectionDescriptor.getLabel();
            Property.Description description = sectionDescriptor.getDescription();
            if (label != null) {
                add(new SpanLabel(label.getValue()));
            }
            if (description != null) {
                add(new SpanLabel(description.getValue()));
            }
            
            buildTopActionsBar(sectionDescriptor, this, entity);
            buildSections();
            buildBottomActionsBar(sectionDescriptor, this, entity);
            
            
            if (form != null) {
                form.setLayout(new BorderLayout());
                form.add(CENTER, this);
            }
            
        }
    }
    
}
