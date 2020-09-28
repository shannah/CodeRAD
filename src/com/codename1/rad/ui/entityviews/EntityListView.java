/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui.entityviews;

import ca.weblite.shared.components.CollapsibleHeaderContainer.ScrollableContainer;
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
import com.codename1.ui.layouts.Layout;
import com.codename1.ui.plaf.Border;
import java.util.ArrayList;
import java.util.List;
import com.codename1.rad.models.ContentType;
import com.codename1.ui.layouts.GridLayout;

/**
 * A view that renders an {@link EntityList} visually.  This will bind to the list's events so that rows will animate in and 
 * out appropriately when they are added to the model.  The list can be customized with a {@link EntityListCellRenderer}.
 * 
 * @see ListCellRendererAttribute
 * @see RowTemplateNode
 * 
 * @author shannah
 */
public class EntityListView<T extends EntityList> extends AbstractEntityView<T> implements ScrollableContainer {
    
    /**
     * An enum to specify the layout used for the rows.
     */
    public static enum RowLayout {
        /**
         * Layout the list in a BoxLayout.Y layout (i.e. vertical rows)
         */
        Y,
        
        /**
         * Layout the list in a grid.  Use the {@link #COLUMNS} and {@link #LANDSCAPE_COLUMNS} view properties
         * to configure the number of columns in the grid.
         */
        Grid
    }
    
    /**
     * View property to configure whether the list is scrollable vertically.
     */
    public static final ViewProperty<Boolean> SCROLLABLE_Y = ViewProperty.booleanProperty();
    
    /**
     * View property to configure whether the list is scrollable horizontally.
     */
    public static final ViewProperty<Boolean> SCROLLABLE_X = ViewProperty.booleanProperty();
    
    /**
     * View property to configure whether inserting new rows should be animated.
     */
    public static final ViewProperty<Boolean> ANIMATE_INSERTIONS = ViewProperty.booleanProperty();
    
    /**
     * View property to configure whether removing rows should be animated.
     */
    public static final ViewProperty<Boolean> ANIMATE_REMOVALS = ViewProperty.booleanProperty();
    
    /**
     * View property to specify the layout of the list.  See {@link RowLayout}.
     */
    public static final ViewProperty<RowLayout> LAYOUT = new ViewProperty<RowLayout>(ContentType.createObjectType(RowLayout.class));
    
    /**
     * View property to specify the number of columns to use for the {@link RowLayout#Grid} layout.
     */
    public static final ViewProperty<Integer> COLUMNS = ViewProperty.intProperty();
    
    /**
     * View property to specify the number of columns to use in landscape mode for {@link RowLayout#Grid}
     */
    public static final ViewProperty<Integer> LANDSCAPE_COLUMNS = ViewProperty.intProperty();
    
    
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
    private boolean animateInsertions=true;
    private boolean animateRemovals=true;
    
    /**
     * Sets whether to animate insertions into the list.  This can also be configured using the {@link #ANIMATE_INSERTIONS}
     * view property.
     * @param anim 
     */
    public void setAnimateInsertions(boolean anim) {
        animateInsertions = anim;
    }
    
    /**
     * Checks if animation of row insertions is enabled.
     * @return 
     */
    public boolean isAnimateInsertions() {
        return animateInsertions;
    }
    
    /**
     * Sets whether to animate removes from the list.  This can also be configured using the {@link #ANIMATE_REMOVALS} view property.
     */
    public void setAnimateRemovals(boolean anim) {
        animateRemovals = anim;
    }
    
    /**
     * Checks if animate removals is enabled.
     * @return 
     */
    public boolean isAnimateRemovals() {
        return animateRemovals;
    }
    
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
                if (animateInsertions) {
                    cmp.setX(0);
                    cmp.setY(wrapper.getHeight() + wrapper.getScrollY());
                    cmp.setWidth(getWidth());
                    cmp.setHeight(cmp.getPreferredH());

                    ComponentAnimation anim = wrapper.createAnimateHierarchy(300);
                    anim.addOnCompleteCall(()->{
                        wrapper.scrollComponentToVisible(cmp);
                    });
                    getComponentForm().getAnimationManager().addAnimation(anim);
                } else {
                    getComponentForm().revalidateWithAnimationSafety();
                }
                
                
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
                    if (animateRemovals) {
                        wrapper.animateHierarchy(300);
                    } else {
                        wrapper.revalidateWithAnimationSafety();
                    }
                }
            }
 
        }
    };
    

    /**
     * Creates a list view for the given Entity list
     * @param list The view model to render.
     * @param node Node providing configuration and actions for the view.
     */
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
        Boolean animateInsertionsBool = (Boolean)node.getViewParameter(ANIMATE_INSERTIONS, ViewPropertyParameter.createValueParam(ANIMATE_INSERTIONS, true)).getValue(list);
        if (animateInsertionsBool != null) {
            animateInsertions = animateInsertionsBool.booleanValue();
        }
        Boolean animateRemovalsBool = (Boolean)node.getViewParameter(ANIMATE_REMOVALS, ViewPropertyParameter.createValueParam(ANIMATE_REMOVALS, true)).getValue(list);
        if (animateRemovalsBool != null) {
            animateRemovals= animateRemovalsBool.booleanValue();
        }
        Boolean scrollableY = (Boolean)node.getViewParameter(SCROLLABLE_Y, ViewPropertyParameter.createValueParam(SCROLLABLE_Y, false)).getValue(list);
        if (scrollableY != null) {
            wrapper.setScrollableY(scrollableY);
            wrapper.setGrabsPointerEvents(true);
        }
        
        RowLayout rowLayout = (RowLayout)node.getViewParameterValue(LAYOUT);
        if (rowLayout != null && rowLayout == RowLayout.Grid) {
            Integer columns = (Integer)node.getViewParameterValue(COLUMNS);
            GridLayout gridLayout;
            if (columns == null) {
               gridLayout = GridLayout.autoFit();
            } else {
                Integer landscapeColumns = (Integer)node.getViewParameterValue(LANDSCAPE_COLUMNS);
                if (landscapeColumns == null) {
                    gridLayout = new GridLayout(columns);
                } else {
                    gridLayout = new GridLayout(1, columns, 1, landscapeColumns);
                }
            }
            wrapper.setLayout(gridLayout);
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
    
    /**
     * Gets the container that is scrollable, for scrolling through rows.
     * @return 
     */
    public Container getScrollWrapper() {
        return wrapper;
    }

    /**
     * A wrapper for {@link #getScrollWrapper() } to conform to the {@link ScrollableContainer} interface.
     * @return 
     */
    @Override
    public Container getVerticalScroller() {
        return getScrollWrapper();
    }
    
    
    /**
     * Sets the layout used to render rows of the list.
     * @param l The layout used to render the list.
     */
    public void setListLayout(Layout l) {
        wrapper.setLayout(l);
    }
    
    /**
     * Gets the layout used to render the rows of the list.
     * @return 
     */
    public Layout getListLayout() {
        return wrapper.getLayout();
    }

    
}
