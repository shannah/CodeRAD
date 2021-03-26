/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.nodes;

import com.codename1.rad.ui.ActionCategories;
import com.codename1.rad.models.Attribute;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A node that encapsulates form (not a {@link com.codename1.ui.Form}, a small "f" form) for editing the properties of an {@link Entity}.
 * Forms can contain sections ({@link SectionNode}), which can contain fields ({@link FieldNode}).  If fields are added directly to a {@link FormNode}, it actually wraps them in an implicit {@link SectionNode} which is the "global" 
 * section.
 * 
 * == Supported Attributes
 * 
 * There isn't a fixed list of supported attributes, but the following are some of the common attributes you'd find in a form.  
 * 
 * . {@link ActionsNode} - Actions to be added at various locations in the form.  Can contain multiple {@link ActionsNode} attributes.
 * .. Categories include: {@link #OVERFLOW_MENU}, {@link #BOTTOM_LEFT_MENU}, {@link #BOTTOM_RIGHT_MENU}, {@link #TOP_LEFT_MENU}, {@link #TOP_RIGHT_MENU}.
 * . {@link SectionNode} - Can contain multiple section nodes.
 * .. All attributes supported by {@link SectionNode} will be accepted by {@link FormNode}, as they will be wrapped in an implicit {@link SectionNode} which is the global section.
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
 * @author shannah
 */
public class FormNode extends Node implements Iterable<SectionNode>, ActionCategories {
    private Sections sections;
    private SectionNode globalSection;
    private ListNode rootList;
    private ViewNode rootView;
    
    public FormNode(Attribute... atts) {
        super(null, atts);
        if (sections == null) {
            sections = new Sections();
        }

    }

    @Override
    public Attribute findAttribute(Class type) {
        Attribute out = super.findAttribute(type);
        
        
        return out;
    }

    
    
    public void setAttributes(Attribute... atts) {
        if (sections == null) {
            sections = new Sections();
        }

        //super.setAttributes(atts);
        for (Attribute att : atts) {
            if (att.getClass() == SectionNode.class) {
                sections.add((SectionNode)att);
                ((SectionNode)att).setParent(this);
            } else if (att.getClass() == FieldNode.class) {
                if (globalSection == null) {
                    globalSection = new SectionNode();
                    globalSection.setParent(this);
                }
                globalSection.setAttributes(att);
            } else if (att instanceof ListNode) {
                rootList = (ListNode)att;
                super.setAttributes(att);
            } else if (att instanceof ViewNode) {
                rootView = (ViewNode)att;
                super.setAttributes(att);
            } else {
                super.setAttributes(att);
            }
        }
    }

    @Override
    public Iterator<SectionNode> iterator() {
        if (globalSection != null) {
            ArrayList<SectionNode> out = new ArrayList<>();
            out.add(globalSection);
            out.addAll(sections.sections);
            return out.iterator();
        }
            
        
        return sections.iterator();
    }
    
    public Sections getSections() {
        if (globalSection != null) {
            Sections out = new Sections();
            out.add(globalSection);
            for (SectionNode sec : sections) {
                out.add(sec);
            }
            return out;
        }
        return sections;
    }
    
    
    public class Sections implements Iterable<SectionNode> {
        private List<SectionNode> sections = new ArrayList<>();

        @Override
        public Iterator<SectionNode> iterator() {
            return sections.iterator();
        }
        
        public void add(SectionNode node) {
            sections.add(node);
        }
        
        public boolean isEmpty() {
            return sections.isEmpty();
        }
    }
    
    public FieldNode[] getAllFields() {
        List<FieldNode> out = new ArrayList<>();
        for (SectionNode section : this) {
            for (FieldNode fn : section) {
                out.add(fn);
            }
        }
        return out.toArray(new FieldNode[out.size()]);
    }
    
    

    
    
    
    
    public ListNode getRootList() {
        return rootList;
    }
    
    public ViewNode getRootView() {
        return rootView;
    }
    
}
