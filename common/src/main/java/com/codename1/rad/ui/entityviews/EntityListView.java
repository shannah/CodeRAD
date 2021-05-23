/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui.entityviews;

import ca.weblite.shared.components.CollapsibleHeaderContainer.ScrollableContainer;
import com.codename1.components.InfiniteScrollAdapter;
import com.codename1.rad.annotations.Inject;
import com.codename1.rad.attributes.ListCellRendererAttribute;
import com.codename1.rad.controllers.ActionSupport;
import com.codename1.rad.controllers.ControllerEvent;
import com.codename1.rad.events.EventContext;
import com.codename1.rad.models.EntityListProvider;
import com.codename1.rad.nodes.RowTemplateNode;
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
import com.codename1.rad.models.EntityList;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.codename1.rad.models.ContentType;
import com.codename1.ui.Form;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.rad.models.Entity;

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

    private EntityListProvider.Request nextProviderRequest;




    /**
     * Creates a list view for the given Entity list
     * @param list The view model to render.
     * @param node Node providing configuration and actions for the view.
     */
    public EntityListView(@Inject T list, @Inject ListNode node) {
        super(list, node == null ? new ListNode() : node);
        node = getViewNode();
        setUIID("EntityListView");
        setName("EntityListView");
        this.getStyle().stripMarginAndPadding();

        renderer = getViewNode().getListCellRenderer();
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

        ActionNode refreshAction = node.getAction(ActionCategories.LIST_REFRESH_ACTION);
        ActionNode loadMoreAction = node.getAction(ActionCategories.LIST_LOAD_MORE_ACTION);


        // If there is a refresh action, or there is a provider, then
        // We'll add pullToRefresh
        if (refreshAction != null ) {
            wrapper.setScrollableY(true);
            wrapper.setGrabsPointerEvents(true);
            wrapper.addPullToRefresh(()->{
                refresh();
            });
        }

        // If there is a loadMore action or a provider, then
        // we'l add infinite scroll.
        if (loadMoreAction != null) {
            wrapper.setScrollableY(true);
            wrapper.setGrabsPointerEvents(true);
            InfiniteScrollAdapter.createInfiniteScroll(wrapper, ()->{
                loadMore();
            }, getEntity().size() == 0);
        }



        update();
    }



    public void refresh() {
        ActionNode refreshAction = getViewNode().getAction(ActionCategories.LIST_REFRESH_ACTION);
        ActionNode loadMoreAction = getViewNode().getAction(ActionCategories.LIST_LOAD_MORE_ACTION);

        Entity requestData = null;
        if (refreshAction != null) {
            Map extraData = new HashMap();
            EventContext.addExtra(extraData, EntityListProvider.RequestType.REFRESH);
            ControllerEvent evt = ActionSupport.as(refreshAction.fireEvent(getEntity(), this, extraData), ControllerEvent.class);
            if (evt != null && evt.isConsumed()) {
                EntityListProvider.Request request = evt.getAsyncResource(EntityListProvider.Request.class);
                if (request != null) {
                    // The provider is fulfilling the request asynchronously.
                    request.onResult((res, err) -> {
                        if (err != null) {
                            return;
                        }
                        nextProviderRequest = request.getNextRequest();
                        EntityList modelList = getEntity();
                        modelList.startTransaction();
                        modelList.clear();
                        for (Object o : res) {
                            modelList.add((Entity) o);
                        }
                        boolean localAnimateInsertions = animateInsertions;
                        animateInsertions = false;
                        boolean localAnimateRemovals = animateRemovals;
                        animateRemovals = false;
                        modelList.commitTransaction();
                        animateInsertions = localAnimateInsertions;
                        animateRemovals = localAnimateRemovals;
                        if (loadMoreAction != null) {
                            InfiniteScrollAdapter.addMoreComponents(wrapper, new Component[0], request.hasMore());
                        }
                        Form f = getComponentForm();
                        if (f != null) {
                            getComponentForm().revalidateWithAnimationSafety();
                        }

                    });
                }
            }

        }




    }

    public void loadMore() {
        Entity requestData = null;
        ActionNode refreshAction = getViewNode().getAction(ActionCategories.LIST_REFRESH_ACTION);
        ActionNode loadMoreAction = getViewNode().getAction(ActionCategories.LIST_LOAD_MORE_ACTION);

        if (loadMoreAction != null) {
            Map extraData = new HashMap();
            if (nextProviderRequest != null) {
                EventContext.addExtra(extraData, nextProviderRequest);

            }
            EventContext.addExtra(extraData, EntityListProvider.RequestType.LOAD_MORE);

            ControllerEvent evt = ActionSupport.as(loadMoreAction.fireEvent(getEntity(), this, extraData), ControllerEvent.class);

            if (evt != null && evt.isConsumed()) {
                EntityListProvider.Request req = evt.getAsyncResource(EntityListProvider.Request.class);

                nextProviderRequest = null;
                if (req != null) {
                    req.onResult((res, err) -> {
                        if (err != null) {
                            // We just swallow errors.
                            // The provider can return an error to indicate that it's done but no data.
                            // But it is up to the provider to propagate errors up the UI or log if necessary.
                            return;
                        }
                        nextProviderRequest = req.getNextRequest();
                        EntityList modelSet = getEntity();
                        if (res.size() > 0) {
                            modelSet.startTransaction();
                            for (Entity en : (Iterable<Entity>)res) {
                                modelSet.add(en);
                            }
                            boolean localAnimateInsertions = animateInsertions;
                            animateInsertions = false;
                            boolean localAnimateRemovals = animateRemovals;
                            animateRemovals = false;
                            modelSet.commitTransaction();
                            animateInsertions = localAnimateInsertions;
                            animateRemovals = localAnimateRemovals;
                            InfiniteScrollAdapter.addMoreComponents(wrapper, new Component[0], req.hasMore());
                            Form f = getComponentForm();
                            if (f != null) {
                                getComponentForm().revalidateWithAnimationSafety();
                            }

                        }
                    });
                }

            }


        }

    }



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
    
    /**
     * Handles when entities are added
     * @param evt The event
     * @param commit Whether to animate/revalidate UI right now.
     * @return 
     */
    private Component handleEntityAdded(EntityList.EntityAddedEvent evt, boolean commit) {
        EntityList.EntityAddedEvent eae = (EntityList.EntityAddedEvent)evt;
        Entity e = eae.getEntity();
        int index = evt.getIndex();
        EntityView rowView = renderer.getListCellRendererComponent(this, e, index, selection.isSelected(index, 0), false);
        Component cmp = (Component)rowView;

        
        if (index >= 0 && index < wrapper.getComponentCount()) {
            wrapper.addComponent(index, cmp);
        } else {
            wrapper.add(cmp);
        }
        if (getComponentForm() != null) {
            if (animateInsertions) {
                cmp.setX(0);
                cmp.setY(wrapper.getHeight() + wrapper.getScrollY());
                cmp.setWidth(getWidth());
                cmp.setHeight(cmp.getPreferredH());
                if (commit) {
                    ComponentAnimation anim = wrapper.createAnimateHierarchy(300);
                    anim.addOnCompleteCall(()->{
                        wrapper.scrollComponentToVisible(cmp);
                    });
                    getComponentForm().getAnimationManager().addAnimation(anim);
                }
            } else {
                if (commit) {
                    getComponentForm().revalidateLater();
                }
            }


        }
        return cmp;
    }
    
    public EntityView getRowViewForEntity(Entity e) {
        for (Component child : wrapper) {
            if (child instanceof EntityView) {
                EntityView ev = (EntityView)child;
                if (ev.getEntity() == e) {
                    return ev;
                }
            }
        }
        return null;
    }
    
    /**
     * Handles EntityRemoveEvent - when an entity is removed from the list.
     * @param ere The event
     * @param commit Whether to animate/revalidate right now.
     */
    private void handleEntityRemoved(EntityList.EntityRemovedEvent ere, boolean commit) {
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
                    if (commit) {
                        wrapper.animateHierarchy(300);
                    }
                } else {
                    if (commit) {
                        wrapper.revalidateLater();
                    }
                }
            }
        }
    }
    
    /**
     * Transaction events package up multiple adds and removes into
     * a bulk change so we handle it differently - we defer revalidation
     * and animation to the end of the transaction, rather than after
     * each individual add/remove.
     * @param te 
     */
    private void handleTransactionEvent(EntityList.TransactionEvent te) {
        Component lastAdd = null;
        boolean shouldAnimate = false;
        
        // First handle removes
        for (EntityList.EntityEvent subEvent : te) {
            if (subEvent instanceof EntityList.EntityRemovedEvent) {
                handleEntityRemoved((EntityList.EntityRemovedEvent) subEvent, false);
                if (animateRemovals) {
                    shouldAnimate = true;
                }
            }
        }
        
        
        // Now handle "Adds"
        for (EntityList.EntityEvent subEvent : te) {
            if (subEvent instanceof EntityList.EntityAddedEvent) {
                lastAdd = handleEntityAdded((EntityList.EntityAddedEvent)subEvent, false);
                if (animateInsertions) {
                    shouldAnimate = true;
                }
            } 
        }
        if (getComponentForm() != null) {
            if (shouldAnimate) {
                final Component fLastAdd = lastAdd;
                ComponentAnimation anim = wrapper.createAnimateHierarchy(300);
                anim.addOnCompleteCall(()->{
                    if (fLastAdd != null && wrapper.contains(fLastAdd)) {
                        wrapper.scrollComponentToVisible(fLastAdd);
                    }
                });
                getComponentForm().getAnimationManager().addAnimation(anim);
                
            } else {
                wrapper.revalidateLater();
            }
        }
        
    }
    
    private ActionListener<EntityList.EntityListEvent> listListener = evt-> {
        EntityList.EntityListInvalidatedEvent ie = evt.as(EntityList.EntityListInvalidatedEvent.class);
        if (ie != null) {
            // Received invalidated event.  We need to rebuild the view from scratch
            // to sync with the model.
            if (wrapper != null) {
                wrapper.removeAll();
            }
            firstUpdate = true;
            update();
            Form f = getComponentForm();
            if (f != null) {
                // Important: We seem to need revalidate() here rather than revalidateLater()
                // because if an animation is in progress, the component may just disappear for a 
                // moment.
                revalidate();
            }
            return;
        }
        
        EntityList.TransactionEvent te = evt.as(EntityList.TransactionEvent.class);
        if (te == null && evt.getTransaction() == null) {
            if (evt instanceof EntityList.EntityAddedEvent) {
                if (firstUpdate) {
                    update();
                    return;
                }
                handleEntityAdded((EntityList.EntityAddedEvent)evt, true);

            } else if (evt instanceof EntityList.EntityRemovedEvent) {
                if (firstUpdate) {
                    update();
                    return;
                }
                handleEntityRemoved((EntityList.EntityRemovedEvent)evt, true);

            }
        } else {
            if (te != null) {
                if (!te.isComplete() || te.isEmpty()) {
                    return;
                }
                if (firstUpdate) {
                    update();
                    return;
                }
                handleTransactionEvent((EntityList.TransactionEvent)evt);
                
            }
        }
    };
    
    public EntityListView(@Inject T list) {
        this(list, null);
    }



    @Override
    public void bindImpl() {
        getEntity().addActionListener(listListener);
    }

    @Override
    public void unbindImpl() {
        getEntity().removeActionListener(listListener);
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
    public ListNode getViewNode() {
        return (ListNode)super.getViewNode();
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

    @Override
    public void setScrollableY(boolean scrollableY) {
        wrapper.setScrollableY(scrollableY);
        wrapper.setGrabsPointerEvents(scrollableY);
    }



    public static class Builder {
        private Boolean scrollableY;
        private RowLayout listLayout;
        private Integer columns;
        private Integer landscapeColumns;
        private Boolean animateRemovals;
        private Boolean animateInsertions;
        private ActionNode refreshAction;
        private ActionNode loadMoreAction;
        private ActionNode addAction;
        private ActionNode selectAction;
        private ActionNode removeAction;
        private EntityListCellRenderer renderer;
        private EntityList model;
        private ListNode node;
        private Node parentNode;

        public Builder scrollableY(boolean scrollableY) {
            this.scrollableY = scrollableY;
            return this;
        }

        public Builder listLayout(RowLayout layout) {
            this.listLayout = layout;
            return this;
        }

        public Builder columns(int columns) {
            this.columns = columns;
            return this;
        }

        public Builder landscapeColumns(int cols) {
            this.landscapeColumns = cols;
            return this;
        }

        public Builder animateRemovals(boolean animate) {
            this.animateRemovals = animate;
            return this;
        }

        public Builder animateInsertions(boolean animate) {
            this.animateInsertions = animate;
            return this;

        }

        public Builder refreshAction(ActionNode refreshAction) {
            this.refreshAction = refreshAction;
            return this;
        }

        public Builder loadMoreAction(ActionNode loadMoreAction) {
            this.loadMoreAction = loadMoreAction;
            return this;
        }

        public Builder addAction(ActionNode addAction) {
            this.addAction = addAction;
            return this;
        }

        public Builder selectAction(ActionNode selectAction) {
            this.selectAction = selectAction;
            return this;
        }

        public Builder removeAction(ActionNode removeAction) {
            this.removeAction = removeAction;
            return this;
        }

        public Builder renderer(EntityListCellRenderer renderer) {
            this.renderer = renderer;
            return this;
        }


        public Builder model(EntityList model) {
            this.model = model;
            return this;
        }

        public Builder node(ListNode node) {
            this.node = node;
            return this;
        }

        public Builder parentNode(Node parentNode) {
            this.parentNode = parentNode;
            return this;
        }



        public ListNode buildNode() {
            if (node == null) {
                node = new ListNode();
                if (parentNode != null) {
                    node.setParent(parentNode);
                }
            }
            node.setAttributes(
                    scrollableY == null ? null : UI.param(SCROLLABLE_Y, scrollableY),
                    animateInsertions == null ? null : UI.param(ANIMATE_INSERTIONS, animateInsertions),
                    animateRemovals == null ? null : UI.param(ANIMATE_REMOVALS, animateRemovals),
                    listLayout == null ? null : UI.param(LAYOUT, listLayout),
                    columns == null ? null : UI.param(COLUMNS, columns),
                    landscapeColumns == null ? null : UI.param(LANDSCAPE_COLUMNS, landscapeColumns),
                    refreshAction == null ? null : UI.actions(ActionCategories.LIST_REFRESH_ACTION, refreshAction),
                    loadMoreAction == null ? null : UI.actions(ActionCategories.LIST_LOAD_MORE_ACTION, loadMoreAction),
                    addAction == null ? null : UI.actions(ActionCategories.LIST_ADD_ACTION, addAction),
                    removeAction == null ? null : UI.actions(ActionCategories.LIST_REMOVE_ACTION, removeAction),
                    selectAction == null ? null : UI.actions(ActionCategories.LIST_SELECT_ACTION, selectAction),
                    renderer == null ? null : UI.cellRenderer(renderer)
                    );

            return node;
        }

        public EntityListView build() {
            if (model == null) {
                model = new EntityList();
            }
            return new EntityListView(model, buildNode());
        }


    }


}
