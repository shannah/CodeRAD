/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.controllers;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.events.FillSlotEvent;

import com.codename1.rad.models.Attribute;
import com.codename1.rad.models.Tag;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.ActionNode.ActionNodeEvent;
import com.codename1.rad.nodes.ActionsNode;
import com.codename1.rad.nodes.ViewNode;
import com.codename1.rad.ui.Actions;
import com.codename1.rad.ui.EntityView;
import com.codename1.rad.ui.Slot;
import com.codename1.rad.ui.UI;
import com.codename1.ui.Component;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.util.EventDispatcher;
import com.codename1.util.SuccessCallback;

import java.util.*;

import com.codename1.rad.models.Entity;

/**
 * A base class for all Controller classes.
 * 
 * Each application should implement a single {@link ApplicationController} class which will be used as the app's main lifecycle class.  Forms should all have associated {@link FormController} classes.  
 * In some cases, for more complex views, you may also want to have a dedicated {@link ViewController} for the view also.  
 * 
 * == Controller Hierarchy
 * 
 * Similar to {@link com.codename1.ui.Component}, controllers have a hierarchy that is used for managing user navigation and event dispatch. 
 * A Controller may have a "parent" controller.  All events received by a Controller will propagate up to its parent controller if it isn't 
 * consumed.  Additionally, {@link FormController} views its "parent" controller as the previous form for navigation purposes.  E.g. If a {@link FormController}
 * has a parent controller that is also a {@link FormController}, then it will automatically add a "back" event to its form so that the user will return
 * to the "parent" controller's form when the user selects "back".
 * 
 * == Actions and Events
 * 
 * The primary mechanism for receiving notification about user actions is via Actions.  The controller defines the action, and the passes it to the view, associating it with
 * {@link ActionNode.Category}.  If the {@link EntityView} supports that {@link ActionNode.Category} it will fire an {@link ActionNode.ActionNodeEvent} event which the controller can process.
 * 
 * For example, the {@link com.codename1.rad.ui.entityviews.ProfileAvatarView} view supports the {@link com.codename1.rad.ui.entityviews.ProfileAvatarView#PROFILE_AVATAR_CLICKED} category
 * so a controller can register an action with that view as follows:
 * 
 * [source,java]
 * ----
 * public class MyViewController extends ViewController {
 *     public static final ActionNode showDetails = UI.action(icon(FontImage.MATERIAL_INFO)); <1>
 *     public MyViewController(Controller parent, Entity profile) {
 *         super(parent);
 *         setLayout(new BorderLayout());
 *         ProfileAvatarView view = new ProfileAvatarView(profile, new ViewNode(
 *             actions(ProfileAvatarView.PROFILE_AVATAR_CLICKED, showDetails) <2>
 *         ), 10);
 *         addActionListener(showDetails, evt->{ <3>
 *             evt.consume();
 * 
 *             new ProfileDetailsController(this, profile).getView().show(); <4>
 *         });
 *     }
 *        
 * }
 * ----
 * <1> We define the action.  It doesn't need be `static` or `public`.  It just needs to be addressable within the Controller.  I make it public and static so that
 * it is also easily accessible by other controllers that may want to listen for that action.
 * <2> We add the `showDetails` action to the ViewNode of the `ProfileAvatarView`'s `PROFILE_AVATAR_CLICKED category so that it knows to fire that action when users click on the avatar.
 * <3> We add a listener for the `showDetails` action so that we can handle the event where a user clicks on the avatar.
 * <4> This is a made up class `ProfileDetailsController`, but we assume it is a subclass of {@link FormController}, and it shows the details for a profile.
 * 
 * 
 * @author shannah
 */
public class Controller implements ActionListener<ControllerEvent> {
    private ViewNode node;
    private Controller parent;
    private Map<Class,Object> lookups;
    private Map<ActionNode.Category,Actions> actionMap;
    private boolean started;
    
    private static class ActionHandler {
        private ActionNode action;
        private ActionListener<ActionNodeEvent> handler;
        private ActionListener<ControllerEvent> wrapperListener;
        
    }
    
    private List<ActionHandler> actionHandlers = new ArrayList<>();
    
    
    
    private EventDispatcher listeners = new EventDispatcher();
    {
        listeners.addListener(this);
    }
    
    /**
     * Creates a controller with a given parent controller.
     * @param parent The parent controller of this controller.  
     */
    public Controller(@Inject Controller parent) {
        this.parent = parent;

    }
    
    /**
     * Adds a controller listener.  Controller listeners are notified of Controller
     * events that are dispatched using {@link #dispatchEvent(ControllerEvent) }.
     * 
     * This is the means by which information propagates up the controller hierarchy from views and 
     * sub-controllers.
     * @param l 
     */
    public void addEventListener(ActionListener<ControllerEvent> l) {
        listeners.addListener(l);
    }
    
    public void removeEventListener(ActionListener<ControllerEvent> l) {
        listeners.removeListener(l);
    }
    
    private ActionHandler findActionHandler(ActionNode action, ActionListener<ActionNodeEvent> l) {
        ActionHandler h = null;
        action = (ActionNode)action.getCanonicalNode();
        for (ActionHandler candidate : actionHandlers) {
            if (candidate.action == action && candidate.handler == l) {
                return candidate;
            }
        }
        return null;
    }
    
    /**
     * Adds a listener to respond to events fired by a given action.
     * @param action The action to subscribe to.
     * @param l The listener.
     */
    public void addActionListener(ActionNode action, ActionListener<ActionNodeEvent> l) {
        ActionHandler h = new ActionHandler();
        h.action = (ActionNode)action.getCanonicalNode();
        h.handler = l;
        h.wrapperListener = evt -> {
            if (ActionNode.getActionNodeEvent(evt, action) != null) {
                l.actionPerformed((ActionNodeEvent)evt);
            }
        };
        actionHandlers.add(h);
        addEventListener(h.wrapperListener);
        
    }


    
    public void removeActionListener(ActionNode action, ActionListener<ActionNodeEvent> l) {
        ActionHandler h = findActionHandler(action, l);
        if (h != null) {
            listeners.removeListener(h.wrapperListener);
            actionHandlers.remove(h);
        }
    }
    
    /**
     * Dispatches an event first to listeners of this controller, and then, if not consumed yet,
     * to listeners of the parent controller.  The event will propagate up the controller hierarchy
     * until it is either consumed, or until it reaches the top of the hierarchy (i.e. parent == null).
     * @param evt The event to be dispatched.
     */
    protected void dispatchEvent(ControllerEvent evt) {
        listeners.fireActionEvent(evt);
        if (!evt.isConsumed() && parent != null) {
            parent.dispatchEvent(evt);
        }
    }
    
   
    /**
     * Gets the parent controller for this controller.  All controllers except for the ApplicationController
     * should have a parent.  This controller hierarchy is used to keep a navigation history also.  For example,
     * the parent controller of a FormController is the "previous" form.  Hence the back command of a FormController
     * will go "back" to the parent controller's form.
     * @return 
     */
    public Controller getParent() {
        return parent;
    }
    
    /**
     * Should be overridden by subclasses to handle ControllerEvent s.  This is the
     * cornerstone of how information is passed "up" the controller hierarchy, from the view.  The view
     * or sub-controller, dispatches an event.  The event propagates up the controller hierarchy, until
     * a controller consumes the event.
     * @param evt 
     */
    @Override
    public void actionPerformed(ControllerEvent evt) {

    }
    
    /**
     * Gets the FormController for the current controller context. This will walk up the 
     * controller hierarchy (i.e. {@link #getParent()} until it finds an instance of {@link FormController}.
     * If none is found, it returns null.
     * @return The FormController, or null if none found.
     */
    public FormController getFormController() {
        if (this instanceof FormController) {
            return (FormController)this;
        }
        if (parent != null) {
            return parent.getFormController();
        }
        return null;
    }

    public ViewController getViewController() {
        if (this instanceof ViewController) {
            return (ViewController) this;
        }
        if (parent != null) {
            return parent.getViewController();
        }
        return null;
    }
    
    /**
     * Gets the section controller for the current controller context.  This will walk up the 
     * controller hierarchy until it finds an instance of {@link AppSectionController}.
     * 
     * @return The AppSectionController, or null if none found.
     */
    public AppSectionController getSectionController() {
        if (this instanceof AppSectionController) {
            return (AppSectionController)this;
        }
        if (parent != null) {
            return parent.getSectionController();
        }
        return null;
        
    }
    
    /**
     * Gets the ApplicationController for the current controller context.  This will walk up
     * the controller hierarchy (i.e. {@link #getParent()} until it finds an instance of {@link ApplicationController}.
     * @return The ApplicationController or null if none found.
     */
    public ApplicationController getApplicationController() {
        if (this instanceof ApplicationController) {
            return (ApplicationController)this;
        }
        if (parent != null) {
            return parent.getApplicationController();
        }
        return null;
    }
    
    /**
     * Sets the parent controller for this controller.
     * @param parent 
     */
    public void setParent(Controller parent) {
        this.parent = parent;
    }
    
    /**
     * Creates the view node that should be used as the node for the controller's view. This method should 
     * be overridden by subclasses to define the default view node, but this method shouldn't be called directly.  Rather
     * the {@link #getViewNode()} method should be called so that the view node is only created once.  {@link #getViewNode() }
     * will also set the parent node to the view node of the parent controller to more easily benefit from inherited attributes
     * in the UI descriptor hierarchy.
     * @return A ViewNode
     */
    protected ViewNode createViewNode() {

        startControllerInternal();
        ViewNode n = new ViewNode();
        if (actionMap != null) {
            for (ActionNode.Category c : actionMap.keySet()) {
                n.setAttributes(UI.actions(c, actionMap.get(c)));
            }
        }
        return n;
    }
    
    /**
     * Gets the {@link ViewNode} that should be used as the view model for views of this controller. Subclasses should override {@link #createViewNode() }
     * to define the view node for the controller.  This method will defer to that for the initial view node creation, and then just return
     * that view node on subsequeuent calls.
     * 
     * NOTE: This will automatically set the parent node of the view node to the view node of the parent controller.
     * @return 
     */
    public ViewNode getViewNode() {
        startControllerInternal();
        if (node == null) {
            node = createViewNode();
            ViewNode parentNode = null;
            if (parent != null) {
                parentNode = parent.getViewNode();
            }
            node.setParent(parentNode);
        }
        return node;
    }
    
    /**
     * Obtains an object of the given type that has been previously registered by this controller (or a parent controller) via the {@link #addLookup(java.lang.Object) }
     * method.  This facilitates the creation of shared objects that can be accessed by controllers and all of their descendants. For example, 
     * the ApplicationController might register a webservice client via addLookup() so that all controllers in the application can obtain
     * a reference to this client via a simple lookup.
     * 
     * 
     * @param <T> The type object to look up.
     * @param type The type of object to look up.
     * @return The object, or null if none was registered.
     */
    public <T> T lookup(Class<T> type) {

        if (lookups != null) {
            T out = (T)lookups.get(type);
            if (out != null) {
                return out;
            }
        }
        if (type.isAssignableFrom(getClass())) {
            return (T)this;
        }
        if (parent != null) {
            return parent.lookup(type);
        }
        return null;
    }

    /**
     * Obtains a lookup as an entity.  This is a a convenient wrapper around {@link #lookup(Class)} that can
     * be used in cases where you expect to receive an Entity.
     * @param type
     * @return
     * @since 2.0
     */
    public Entity lookupEntity(Class type) {
        return (Entity)lookup(type);
    }
    
    /**
     * Registers an object so that it can be retrieved using {@link #lookup(java.lang.Class) }.
     * @param obj The object to add to the lookups.
     */
    public void addLookup(Object obj) {

        if (obj == null) return;
        if (lookups == null) {
            lookups = new HashMap<>();
        }
        lookups.put(obj.getClass(), obj);
    }

    /**
     * Adds a lookup with a specific clas as a key.
     * @param type The class to use as the key of the lookup.
     * @param object The value of the lookup.
     * @param <T> The type of object.
     * @since 2.0
     */
    public <T> void addLookup(Class<T> type, T object) {
        startControllerInternal();
        if (object == null) return;
        if (lookups == null) {
            lookups = new HashMap<>();
        }
        lookups.put(type, object);
    }

    /**
     * Looks up an object starting at the parent controller.
     * @param type The type of object to lookup.
     * @param <T> The type of object to lookup.
     * @return A matching object or null.
     * @since 2.0
     */
    public <T> T parentLookup(Class<T> type) {
        if (parent == null) {
            return null;
        }
        return parent.lookup(type);
    }

    /**
     * Looks up an entity starting at the parent controller.
     * @param type The type of the entity to look for.
     * @return A matching Entity or null.
     * @since 2.0
     */
    public Entity parentLookupEntity(Class type) {
        if (parent == null) return null;
        return parent.lookupEntity(type);
    }

    /**
     * Calls {@link #withLookup(Class, SuccessCallback)} on the parent controller.
     * @param type The type of object we're looking for.
     * @param callback Callback executed only if lookup finds something.
     * @param <T> The type of object to look for.
     * @return True if lookup finds something.
     * @since 2.0
     */
    public <T> boolean withParentLookup(Class<T> type, SuccessCallback<T> callback) {

        T o = parentLookup(type);
        if (o == null) return false;
        callback.onSucess(o);
        return true;
    }

    /**
     * Calls {@link #lookupEntity(Class)} on the Parent controller.
     * @param type The typeof entity to look for.  This may also be an Interface.
     * @param callback Callback executed only if a matching entity is found.
     * @return True if entity is found.
     * @since 2.0
     */
    public boolean withParentLookupEntity(Class type, SuccessCallback<Entity> callback) {
        Entity o = parentLookupEntity(type);
        if (o == null) return false;
        callback.onSucess(o);
        return true;

    }

    /**
     * Does lookup for object of given type.  If found, it passes it to the given callback.
     * @param type The type of object to lookup.
     * @param callback Callback executed only if the lookup is found.
     * @param <T> The type of object to look for.
     * @return True if the lookup is found.
     * @since 2.0
     */
    public <T> boolean withLookup(Class<T> type, SuccessCallback<T> callback) {
        T o = lookup(type);
        if (o == null) return false;
        callback.onSucess(o);
        return true;
    }

    /**
     * Looks up an entity of a given class, and executes the provided callback with it if the
     * entity is found.
     * @param type A type of entity.  This may be a Class or an Interface.
     * @param callback  Call back that is only called if the lookup finds a match.
     * @return Boolean returns true if the lookup was found.
     * @since 2.0
     */
    public boolean withLookupEntity(Class type, SuccessCallback<Entity> callback) {
        Entity o = lookupEntity(type);
        if (o == null) return false;
        callback.onSucess(o);
        return true;

    }

    /**
     * Adds a listener to be notified when the Slot placeholder with the given ID is requesting to be filled.
     * @param slotId The ID of the slot that you wish to fill with this handler.
     * @param l Listener that is called when a slot with the given ID is requesting to be filled.  To set content in the slot
     *          you can obtain a reference to the slot via {@link FillSlotEvent#getSlot()} and call {@link Slot#setContent(Component)}
     *          on it.  *Make sure you call {@link FillSlotEvent#consume()} if you set the content to prevent the event
     *          from bubbling up the controller hierarchy and being overridden by another controller.*
     * @since 2.0
     */
    public void fillSlot(Tag slotId, ActionListener<FillSlotEvent> l) {

        addEventListener(evt -> {
            FillSlotEvent fse = evt.as(FillSlotEvent.class);
            if (fse != null && fse.getSlot().getId() == slotId) {
                l.actionPerformed(fse);
            }
        });
    }


    

    /**
     * A lifecycle method that should be used to cleanup variables and listeners that were setup in
     * the {@link #onStartController()} method.  Lookups, actions, and the view node are automatically
     * cleared when the controller is stopped, so you don't have to worry about cleaning up things like that.
     *
     * However, you should clean up any variables that you initialized in {@link #onStartController()}.
     *
     * When live-content reloading is enabled in the Codename One simulator, it will trigger {@link #refresh()}
     * which stops and starts the controller hierarchy.
     *
     * @since 2.0
     * @see #onStartController()
     * @see #refresh()
     */
    protected void onStopController() {

    }

    /**
     * A lifecycle method that shoudl be used to setup the controller.  Use this instead of
     * the constructor as this will allow you to work nicely with live content reloading.  When
     * live-content reloading is enabled in the Codename One simulator, it will trigger {@link #refresh()}
     * which stops and starts the controller hierarchy.
     *
     * When this method is run, you can assume that the parent controller is already in the "started" state.
     *
     */
    protected void onStartController() {

    }

    /**
     * Starts the given controller.  This will trigger the {@link #onStartController()} method, which
     * should be used for setup instead of the constructor.
     *
     * @since 2.0
     * @see #onStartController()
     * @see #onStopController()
     */
    public final void startController() {
        startControllerInternal();
    }


    /**
     * Static method that starts a given controller, and returns the controller back
     * for chaining.
     * @param controller The controller to start.
     * @param <T>
     * @return The controller.
     * @since 2.0
     */
    public static <T extends Controller> T start(T controller) {
        controller.startControllerInternal();
        return controller;
    }


    /**
     * Package-private method used to start the controller.  This calls {@link #onStartController()}
     * which can be implemented by subclasses to create the view, and set up action listeners.
     *
     * The start/stop lifecycle is used to support live-content reloading when resources or class definitions
     * are changed.  The Codename One simulator will trigger a {@link #refresh()} when live content reloading
     * is enabled.
     * @since 2.0
     *
     */
    void startControllerInternal() {
        if (started || starting || stopping) {
            return;
        }
        starting = true;
        if (parent != null) {
            parent.startControllerInternal();
        }


        initControllerActionsInternal();
        onStartController();
        started = true;
        starting = false;
    }

    /**
     * Flag to indicate that the controller is in the process of starting or stopping.  Calls to {@link #startControllerInternal()}
     * or {@link #stopControllerInternal()} will short circuit if called while starting or stopping.
     */
    private boolean starting, stopping;

    /**
     * Package-private method used to stop the controller.  This calls {@link #onStopController()}
     * which can be implemented by subclasses to cleanup resources when the controller is stopped.
     *
     * This clears out lookups, actions, node, etc...  After stopping this controller, it will propagate
     * up the controller hierarchy, stopping the parents also.
     *
     * The start/stop lifecycle is used to support live-content reloading when resources or class definitions
     * are changed.  The Codename One simulator will trigger a {@link #refresh()} when live content reloading
     * is enabled.
     *
     */
    void stopControllerInternal() {

        if (!started || stopping || starting) {
            return;
        }
        stopping = true;
        onStopController();
        lookups = null;
        actionMap = null;
        node = null;
        actionHandlers.clear();
        actionsInitialized = false;
        List toRemove = new ArrayList();
        for (Object l : listeners.getListenerCollection()) {
            toRemove.add(l);

        }
        for (Object o : toRemove) {
            listeners.removeListener(o);
        }
        listeners.addListener(this);


        if (parent != null) {
            parent.stopControllerInternal();
        }

        started = false;
        stopping = false;
    }

    /**
     * Checks if the controller is started.  Controllers follow multiple lifecycles.  This "started" flag
     * pertains to the "start/stop" lifecycle.  When the app is "reloaded" due to resource changes, then
     * the {@link #onStopController()} method is triggered, which cleans up all of the controller's resources.
     * Then the {@link #startController()}} method is triggered, which creates them again.  This is used
     * to allow live reloading of changes.
     *
     * @return True if the controller is in "started" state.
     * @see #startController()
     * @see #onStartController()
     * @see #onStopController()
     */
    public boolean isStarted() {
        return started;
    }


    /**
     * Refreshes the controller.  This calls {@link #onStopController()} and {@link #startController()}
     * and is useful for reloading the application when changes are made to the classes or resources.
     */
    public void refresh() {
        stopControllerInternal();
        startControllerInternal();
    }


    /**
     * Extends an existing action from one of the parent controllers, and registers it as an action
     * on this controller.
     * @param category The category to register the action to.
     * @param overwriteAttributes Whether to overwrite existing attributes of the action.  If false, attributes
     *                            provided will be ignored when extending actions that already have those attributes
     *                            defined.
     * @param attributes Attributes to add to the action.
     * @return The action that was added.
     * @since 2.0
     */
    public ActionNode extendAction(ActionNode.Category category, boolean overwriteAttributes, Attribute... attributes) {
        ActionNode action = getInheritedAction(category);
        if (action == null) {
            action = UI.action(attributes);
        }
        else {
            action = (ActionNode)action.createProxy(action.getParent());
            action.setAttributes(overwriteAttributes, attributes);
        }
        addActions(category, action);
        return action;
    }

    /**
     * Extends an action from a parent controller, and registers it as an action on this controller.
     * @param category The category to register the action to.
     * @param callback Callback which is executed immediately on the action, which is used to configure
     *                 the action.
     * @return The action node that was added/extended.
     * @see #extendAction(ActionNode.Category, boolean, Attribute[])
     */
    public ActionNode extendAction(ActionNode.Category category, SuccessCallback<ActionNode> callback) {
        ActionNode extendedAction = extendAction(category, false);
        callback.onSucess(extendedAction);
        return extendedAction;
    }


    /**
     * Adds a set of actions to this controller, registered to a given category.
     * @param category The category to register the actions under.
     * @param actions The actions to register.
     * @see #extendAction(ActionNode.Category, SuccessCallback) To extend an existing action
     * from a parent controller, and register the extended action on this controller.
     * @since 2.0
     */
    public void addActions(ActionNode.Category category, ActionNode... actions) {
        if (actionMap == null) {
            actionMap = new HashMap();
        }
        Actions acts = null;
        if (!actionMap.containsKey(category)) {
            acts = new Actions();
            actionMap.put(category, acts);
        } else {
            acts = actionMap.get(category);
        }

        acts.add(actions);

    }

    public void addActions(ActionNode.Category category, Actions actions) {
        addActions(category, actions.toArray());
    }

    public void addAction(ActionNode.Category category, ActionNode action, ActionListener<ActionNodeEvent> l) {
        addActions(category, action);
        addActionListener(action, l);
    }

    public void addAction(ActionNode.Category category, ActionListener<ActionNodeEvent>l) {
        addAction(category, ActionNode.builder().build(), l);
    }


    /**
     * Gets the first action matching the given category that is registered on this controller
     * or one of the controller's parents.  If none is found, it will return null.
     * @param category The category of action to return.
     * @return A matching action or null if none is found.
     * @see #getAction(ActionNode.Category) to retrieve an action from this controller only.
     * @see #getInheritedActions(ActionNode.Category, boolean) To retrieve multiple actions from the controller
     * @since 2.0
     * hierarchy.
     */

    public ActionNode getInheritedAction(ActionNode.Category category) {
        ActionNode out = getAction(category);
        if (out != null) return out;
        if (parent != null) {
            return parent.getInheritedAction(category);
        }
        return null;
    }

    /**
     * Gets actions registered in the specified category on this controller or parents.
     * @param category The category of action to retrieve.
     * @param aggregate If true, then this will return a set of all actions in this category, registered in
     *                  this controller, and all of its parents - combined together.  If false, then this will
     *                  return the set of actions in this category in the first controller that contains at least
     *                  one action in this category, starting with this container, and walking up the
     *                  controller hierarchy until it finds one with at least one action in this category.
     * @return A set of matching actions.
     * @see #getActions(ActionNode.Category) To retrieve only actions registered on this controller.
     * @see #getInheritedAction(ActionNode.Category) To retrieve only a single action.
     * @since 2.0
     */
    public Actions getInheritedActions(ActionNode.Category category, boolean aggregate) {
        Actions out = getActions(category);
        if (out != null) {
            if (aggregate && parent != null) {
                out.add(parent.getInheritedActions(category, aggregate));
            }
            return out;
        }
        if (parent != null) {
            return parent.getInheritedActions(category, aggregate);
        }
        return new Actions();
    }


    /**
     * Gets an action in the specified category that is registered on this controller.
     * @param category The category of action to look for.
     * @return A matching ActionNode or null if this controller has no action registered in that category.
     * @see {@link #getActions(ActionNode.Category)} to get multiple actions in the category.
     * @see {@link #getInheritedAction(ActionNode.Category)} To check parent controllers for actions as well.
     * @since 2.0
     */
    public ActionNode getAction(ActionNode.Category category) {
        if (actionMap == null) {
            return null;
        }
        Actions categoryActions = actionMap.get(category);
        if (categoryActions == null || categoryActions.isEmpty()) return null;
        return categoryActions.iterator().next();
    }

    /**
     * Gets actions registered in this Controller in the specified category.  This does *not*
     * check the parent for actions.  If there are no actions registered on *this* controller
     * then it will return an empty {@link Actions}.
     *
     *
     *
     * @param category The category of action to retrieve.
     * @return An {@link Actions} object with all of the actions that are registered in this controller
     * for that category.
     *
     * @see {@link #getInheritedActions(ActionNode.Category, boolean)} To retrieve all actions in this controller
     * and its parent(s).
     * @see {@link #getAction(ActionNode.Category)} to retrieve only a single action in the category.
     * @since 2.0
     */
    public Actions getActions(ActionNode.Category category) {
        if (actionMap == null) {
            return new Actions();
        }
        Actions out = actionMap.get(category);
        if (out == null) return new Actions();
        return new Actions(out);
    }

    /**
     * Gets an actions node for the given category such that it can be added directly to
     * a {@link ViewNode}.  This method is usually called inside {@link #createViewNode()}
     * for adding actions to the view node.
     * @param category
     * @param aggregate
     * @return
     * @since 2.0
     */
    public ActionsNode getActionsNode(ActionNode.Category category, boolean aggregate) {
        return UI.actions(category, getInheritedActions(category, aggregate));
    }

    /**
     * Gets a single {@link ActionNode} for the given category.  This is a convenience method
     * that you can use in implementations of {@link #createViewNode()} to easily get an {@link ActionsNode}
     * that is ready to add to the view node.
     *
     * This uses {@link #getInheritedAction(ActionNode.Category)} to find the action.
     *
     * @param category The category of action we wish to retrieve.
     * @return An {@link ActionsNode} with zero or one action registered with the given category.
     *
     * @see #getInheritedAction(ActionNode.Category)
     * @since 2.0
     */
    public ActionsNode getSingleActionsNode(ActionNode.Category category) {
        ActionNode action = getInheritedAction(category);
        if (action == null) {
            return UI.actions(category);
        } else {
            return UI.actions(category, action);
        }
    }

    // Flag to indicate whether actions have been initialized.  Used to ensure
    // that initControllerActionsInternal() is only called once.
    private boolean actionsInitialized;

    /**
     * Initializes actions for the controller.  Uses {@link #actionsInitialized} flag
     * to ensure that this is only run once.
     *
     * Calls {@link #initControllerActions()} which can be overridden by subclasses.
     */
    void initControllerActionsInternal() {
        if (actionsInitialized) {
            return;
        }
        actionsInitialized = true;
        initControllerActions();
    }

    /**
     * This can be used to initialize actions that the controller uses.  It is called
     * just before {@link #startController()}, and it can be overridden by subclasses
     * to provide further modifications to actions that have been initialized.
     *
     * Generally this would include calls to {@link #addActions(ActionNode.Category, ActionNode...)} and
     * {@link #extendAction(ActionNode.Category, SuccessCallback)}.
     * @since 2.0
     */
    protected void initControllerActions() {

    }



    public static class CreateObjectRequest<T> extends ControllerEvent {
        private final Class<T> objectType;
        private T object;
        private Object[] params;

        public CreateObjectRequest(Object source, Class<T> objectType, Object[] params) {
            super(source);
            this.objectType = objectType;
            this.params = params;
        }

        public void setObject(T object) {
            this.object = object;
            this.consume();
        }

        public T getObject() {
            return object;
        }

        public Object[] getParams() {
            return params;
        }

        public Class<T> getObjectType() {
            return objectType;
        }
    }


    public static interface ObjectFactory {
        public <T> T createObject(CreateObjectRequest<T> request);
    }


    private static class ObjectFactoryWrapper<T> implements ActionListener<ControllerEvent> {
        private Class<T> objectType;
        private ObjectFactory factory;

        public ObjectFactoryWrapper(Class<T> objectType, ObjectFactory factory) {
            this.objectType = objectType;
            this.factory = factory;
        }

        @Override
        public void actionPerformed(ControllerEvent _evt) {
            if (_evt.getClass() == CreateObjectRequest.class) {
                CreateObjectRequest<T> evt = (CreateObjectRequest<T>)_evt;
                if (evt.getObjectType() == objectType) {
                    T object = factory.createObject(evt);
                    if (object != null) {
                        evt.setObject(object);
                    }
                }
            }

        }




    }


    /**
     * Registers a factory to create objects of the given type.
     * @param type
     * @param factory
     * @param <T>
     */
    public <T> void addObjectFactory(Class<T> type, ObjectFactory factory) {
        addEventListener(new ObjectFactoryWrapper<T>(type, factory));
    }

    /**
     * This will attempt to create an object of the given type using registered factories.  It will pass
     * the event up the controller hierarchy.
     * @param type The type of object to create
     * @param <T>
     * @return A newly created object of the given type, or null if no factories were registered.
     */
    public <T> T createObjectWithFactory(Class<T> type, Object[] params) {
        CreateObjectRequest<T> request = new CreateObjectRequest<>(this, type, params);
        dispatchEvent(request);
        if (request.isConsumed()) {
            return request.getObject();
        }
        return null;
    }

}
