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
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.GridLayout;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shannah
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
