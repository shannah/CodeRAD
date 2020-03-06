/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui.entityviews;

import com.codename1.rad.ui.EntityListCellRenderer;
import com.codename1.rad.ui.AbstractEntityView;
import com.codename1.rad.ui.ActionCategories;
import com.codename1.rad.ui.ComplexSelection;
import com.codename1.rad.ui.EntityView;
import com.codename1.rad.ui.ViewProperty;
import com.codename1.rad.ui.ViewPropertyParameter;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.ListNode;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.EntityList;
import com.codename1.rad.models.EntityList.EntityListEvent;
import com.codename1.components.FloatingActionButton;
import com.codename1.rad.ui.UI;
import com.codename1.ui.Component;
import static com.codename1.ui.ComponentSelector.$;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.animations.ComponentAnimation;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Border;
import java.util.ArrayList;
import java.util.List;

/**
 * A view that renders an {@link EntityList} visually.  This will bind to the list's events so that rows will animate in and 
 * out appropriately when they are added to the model.  The list can be customized with a {@link EntityListCellRenderer}.
 * 
 * @see ListCellRendererAttribute
 * @see RowTemplateNode
 * 
 * @author shannah
 */
public class EntityListView<T extends EntityList> extends AbstractEntityView<T> {
    
    public static final ViewProperty<Boolean> SCROLLABLE_Y = ViewProperty.booleanProperty();
    private ListNode node;
    private EntityListCellRenderer renderer;
    private ComplexSelection selection = new ComplexSelection();
    private Container wrapper = new Container(BoxLayout.y()) {
        @Override
        public void pointerPressed(int x, int y) {
            super.pointerPressed(x, y); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void pointerDragged(int x, int y) {
            super.pointerDragged(x, y); //To change body of generated methods, choose Tools | Templates.
        }
        
    };
    boolean firstUpdate = true;
    private ActionListener<EntityListEvent> listListener = evt-> {
        if (evt instanceof EntityList.EntityAddedEvent) {
            if (firstUpdate) {
                update();
                return;
            }
            EntityList.EntityAddedEvent eae = (EntityList.EntityAddedEvent)evt;
            Entity e = eae.getEntity();
            EntityView rowView = renderer.getListCellRendererComponent(this, e, wrapper.getComponentCount(), selection.isSelected(wrapper.getComponentCount(), 0), false);
            Component cmp = (Component)rowView;
            
            wrapper.add(cmp);
            if (getComponentForm() != null) {
                cmp.setX(0);
                cmp.setY(wrapper.getHeight() + wrapper.getScrollY());
                cmp.setWidth(getWidth());
                cmp.setHeight(cmp.getPreferredH());
                
                ComponentAnimation anim = wrapper.createAnimateHierarchy(300);
                anim.addOnCompleteCall(()->{
                    wrapper.scrollComponentToVisible(cmp);
                });
                getComponentForm().getAnimationManager().addAnimation(anim);
                
                
            }
            
        } else if (evt instanceof EntityList.EntityRemovedEvent) {
            if (firstUpdate) {
                update();
                return;
            }
            
            EntityList.EntityRemovedEvent ere = (EntityList.EntityRemovedEvent)evt;
            Component toRemove = null;
            for (Component child : wrapper) {
                if (child instanceof EntityView) {
                    EntityView ev = (EntityView)child;
                    if (ev.getEntity() == ere.getEntity()) {
                        toRemove = child;
                    }
                }
            }
            if (toRemove != null) {
                wrapper.removeComponent(toRemove);
                if (getComponentForm() != null) {
                    wrapper.animateHierarchy(300);
                }
            }
 
        }
    };
    

    public EntityListView(T list, ListNode node) {
        super(list);
        setUIID("EntityListView");
        setName("EntityListView");
        this.getStyle().stripMarginAndPadding();
        this.node = node;
        renderer = node.getListCellRenderer();
        if (renderer == null) {
            renderer = UI.getDefaultListCellRenderer();
        }
        
        Boolean scrollableY = (Boolean)node.getViewParameter(SCROLLABLE_Y, ViewPropertyParameter.createValueParam(SCROLLABLE_Y, false)).getValue(list);
        if (scrollableY != null) {
            wrapper.setScrollableY(scrollableY);
            wrapper.setGrabsPointerEvents(true);
        }
        setLayout(new BorderLayout());
        //add(BorderLayout.CENTER, wrapper);
        $(wrapper).setPadding(0).setMargin(0).setBorder(Border.createEmpty());
        ActionNode addAction = node.getAction(ActionCategories.LIST_ADD_ACTION);
        if (addAction != null) {
            FloatingActionButton fab = FloatingActionButton.createFAB(FontImage.MATERIAL_ADD);
            fab.addActionListener(e -> {
                addAction.fireEvent(getEntity(), this);
            });
            add(BorderLayout.CENTER, fab.bindFabToContainer(wrapper));
        } else {
            add(BorderLayout.CENTER, wrapper);
        }
        
        update();
    }

    @Override
    protected void initComponent() {
        super.initComponent();
        getEntity().addActionListener(listListener);
    }

    @Override
    protected void deinitialize() {
        getEntity().removeActionListener(listListener);
        super.deinitialize();
    }
    
    
    
    
    
    public void setListCellRenderer(EntityListCellRenderer renderer) {
        this.renderer = renderer;
    }
    
    public EntityListCellRenderer getListCellRenderer() {
        return this.renderer;
    }
    
    /**
     * Checks to see if the model has changed since last update, and requires update.
     * Update is required only if rows are added or removed.
     * @return 
     */
    private boolean requiresUpdate() {
        List<Entity> viewSet = new ArrayList<Entity>();
        EntityList<?> modelSet = getEntity();
        int modelSize = modelSet.size();
        int index = 0;
        for (Component child : wrapper) {
            if (child instanceof EntityView) {
                
                EntityView rowView = (EntityView)child;
                if (index >= modelSize && modelSet.get(index) != rowView) {
                    return true;
                }
                index++;
            }
        }
        return index != modelSize;
        
    }
    
    private EntityView getEntityView(Component cmp) {
        if (cmp instanceof EntityView) {
            return (EntityView)cmp;
        }
        if (cmp instanceof Container) {
            for (Component child : (Container)cmp) {
                EntityView out = getEntityView(child);
                if (out != null) {
                    return out;
                }
            }
        }
        return null;
    }
    
    
    @Override
    public void update() {
        if (firstUpdate) {
            firstUpdate = false;
        } else {
            return;
        }
        
        EntityList<?> entityList = getEntity();
        
        
        int index = 0;
        
        for (Entity e : entityList) {
            EntityView rowView = renderer.getListCellRendererComponent(this, e, index, selection.isSelected(index, 0), false);
            wrapper.add((Component)rowView);
            index++;     
        }
        
    }

    @Override
    public void commit() {
        
    }

    @Override
    public Node getViewNode() {
        return node;
    }
    
    public Container getScrollWrapper() {
        return wrapper;
    }
    
}
