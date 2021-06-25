/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.nodes;

import com.codename1.rad.controllers.Controller;
import com.codename1.rad.models.*;
import com.codename1.rad.ui.*;
import com.codename1.rad.attributes.ActionStyleAttribute;
import com.codename1.rad.attributes.Badge;
import com.codename1.rad.attributes.BadgeUIID;
import com.codename1.rad.events.EventContext;
import com.codename1.rad.attributes.Condition;
import com.codename1.rad.attributes.ImageIcon;
import com.codename1.rad.attributes.MaterialIcon;
import com.codename1.rad.attributes.SelectedCondition;
import com.codename1.rad.attributes.TextIcon;

import com.codename1.rad.models.Property.Description;
import com.codename1.rad.models.Property.Label;
import com.codename1.rad.models.Property.Name;
import com.codename1.ui.*;
import com.codename1.rad.controllers.ActionSupport;
import com.codename1.rad.controllers.ControllerEvent;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.util.EventDispatcher;
import com.codename1.util.SuccessCallback;

import java.util.Map;
import java.util.Objects;

import com.codename1.rad.ui.image.EntityImageRenderer;
import com.codename1.rad.ui.image.PropertyImageRenderer;

/**
 * A special {@link Node} that defines an action. When added to prescribed {@link Category}, an Action may manifest itself as a button,
 * or a menu item.  The exact visual form of an can be customized using an {@link ActionViewFactory} to the node hierarchy.  It is up to the
 * View to decide how to render actions that it finds in its node hierarchy, or whether to render them at all.  Generally a View will document
 * which action categories it supports so that controllers know to register actions in those categories.  For example, the {@link ProfileAvatarView} supports
 * the {@link com.codename1.rad.ui.entityviews.ProfileAvatarView#PROFILE_AVATAR_CLICKED_MENU} category.  Actions added to that category will be rendered
 * as menu items in a popup menu that is shown when the user clicks the avatar.  If the user, then clicks on the menu item for the action, it will fire 
 * an event which can be handled by the Controller.
 * 
 * == Example
 * 
 * .Excerpts from `ChatFormController`.  Defines a single action `send`, and adds it to the view.  Also handles the events when the `send` action is "fired".   See https://shannah.github.io/RADChatApp/getting-started-tutorial.html[this tutorial,target=top] for a more comprehensive treatment of this material.
[source,java]
----
public class ChatFormController extends FormController {
    // Define the "SEND" action for the chat room
    public static final ActionNode send = action( <1>
        enabledCondition(entity-> {
            return !entity.isEmpty(ChatRoom.inputBuffer);
        }),
        icon(FontImage.MATERIAL_SEND)
    );
    
    //... More action definitions
    
    public ChatFormController(Controller parent) {
        super(parent); <2>
        Form f = new Form("My First Chat Room", new BorderLayout());
        
        // Create a "view node" as a UI descriptor for the chat room.
        // This allows us to customize and extend the chat room.
        ViewNode viewNode = new ViewNode(
            actions(ChatRoomView.SEND_ACTION, send), <3>
            // ... more action definitions
        );
        
        // Add the viewNode as the 2nd parameter
        ChatRoomView view = new ChatRoomView(createViewModel(), viewNode, f); <4>
        f.add(CENTER, view);
        setView(f);
        
        // Handle the send action
        addActionListener(send, evt->{ <5>
            evt.consume();
            //.. code to handle the send action.
            
        });
    }
}
----
<1> Define an action.
<2> Call `super(parent)` to register the given controller as its parent controller, so that unhandled events will propagate to it.
<3> Assign `send` action to the `SEND_ACTION` category (requirement of the `ChatRoomView` component). The `ChatRoomView` will check this category for the presense of an action.  If none is found, it simply won't include a send button in the UI, nor will it fire "send" events.
<4> Create a new `ChatRoomView`, passing it a dummy view model, and the `viewNode` that includes our action.
<5> Register a handler for the "send" action.  Notice that we could have registered this handler in the parent controller instead (i.e. the ApplicationController) because unhandled events would propagate up.  In this case, it makes more sense as a part of the ChatFormController though.

 * @author shannah
 */
public class ActionNode extends Node implements Proxyable {


    public static class OverrideRule extends Attribute {
        private int maxNum;

        private boolean aggregate;

        public OverrideRule() {
            super(null);
        }

        public OverrideRule(int maxNum, boolean aggregate) {
            this();
            this.maxNum = maxNum;
            this.aggregate = aggregate;
        }


        /**
         * The maximum number of actions to fetch from parent
         * controllers.
         */
        public int getMaxNum() {
            return maxNum;
        }

        /**
         * Whether to aggregate actions from parents together
         * in a single set. If true, then this will load
         * actions from parents up to the maxNum.  If false,
         * then it will take only from a single parent controller
         * -- the top level one.
         */
        public boolean isAggregate() {
            return aggregate;
        }


    }
    
    /**
     * A callback the is to be called after an ActionNodeEvent has been triggered.  This is useful
     * for determining if the event was consumed.
     */
    public static interface AfterActionCallback extends SuccessCallback<ActionEvent> {
        
    }
    
    /**
     * A node wrapper for {@link AfterActionCallback}.
     */
    public static class AfterActionCallbackNode extends Node<AfterActionCallback> {
        public AfterActionCallbackNode(AfterActionCallback callback) {
            super(callback);
        }
    }
    
    public void addAfterActionCallback(AfterActionCallback callback) {
        setAttributes(new AfterActionCallbackNode(callback));
    }
    
    
    /**
     * Adds an action listener to the action node.
     */
    private static class ActionListenerNode extends Node<ActionListener> {
        private ActionListenerNode(ActionListener l) {
            super(l);
        }
    }
    
    /**
     * Adds an action listener to this action.
     * @param l 
     */
    public void addActionListener(ActionListener l) {
        setAttributes(new ActionListenerNode(l));
        
    }
    
   
   
    /**
     * Attribute for adding a test for the enabled condition of the action.
     */
    public static class EnabledCondition extends Attribute<EntityTest> {
        
        public EnabledCondition(EntityTest value) {
            super(value);
        }
        
    }
    
    /**
     * Creates a new ActionNode.
     * @param atts 
     */
    public ActionNode(Attribute... atts) {
        super(null, atts);
    }

    public void setActionTag(Tag tag) {
        setAttributes(tag);
    }

    public Tag getActionTag() {
        return (Tag)this.findAttribute(Tag.class);
    }

    public boolean containsActionTag(Tag tag) {
        if (tag == null) return false;
        return tag.equals(getActionTag());
    }
    
    
    /**
     * Gets the component decorators for this action.
     * @return 
     */
    public ComponentDecorators getComponentDecorators() {
        return new ComponentDecorators(getChildNodes(ComponentDecoratorNode.class));
    }
   
    /**
     * Decorates the given component using the registered component decorators.
     * @param cmp 
     */
    public void decorateComponent(Component cmp) {
        getComponentDecorators().decorate(cmp);
    }

    /**
     * Gets the event factory used for creating action events.
     * @return 
     */
    public EventFactoryNode getEventFactory() {
        
        return (EventFactoryNode)findInheritedAttribute(EventFactoryNode.class);
    }
    
    /**
     * Gets the registered view factory for the action.
     * @return 
     */
    public ActionViewFactory getViewFactory() {
        ActionViewFactoryNode n =  (ActionViewFactoryNode)findInheritedAttribute(ActionViewFactoryNode.class);
        if (n != null) {
            return n.getValue();
        } else {
            return UI.getDefaultActionViewFactory();
        }
    }
    
    @Override
    public Attribute findAttribute(Class type) {
        Attribute att = super.findAttribute(type);
        if (att != null) {
            return att;
        }

        Node parent = getParent();
        if (parent instanceof ActionNode) {
            return parent.findAttribute(type);
        }
        return null;
    }
    
    /**
     * Checks the action style.
     * @return 
     */
    public ActionStyle getActionStyle() {
        ActionStyleAttribute att = (ActionStyleAttribute)findInheritedAttribute(ActionStyleAttribute.class);
        if (att != null) {
            return att.getValue();
        }
        return null;
    }
    
    /**
     * Checks if this action uses the text style.  If using the text style, then this
     * action will display it's label as text.
     * @return 
     */
    public boolean isTextStyle() {
        ActionStyle style = getActionStyle();
        if (style == null) {
            return true;
        }
        switch (style) {
            case IconOnly:
                return false;
        }
        return true;
    }
    
    /**
     * Checks if this action uses the icon style. If using the icon style, then this action 
     * will display an icon.
     * @return 
     */
    public boolean isIconStyle() {
        ActionStyle style = getActionStyle();
        if (style == null) {
            return true;
        }
        switch (style) {
            case TextOnly:
                return false;
        }
        return true;
    }
    
    /**
     * Gets the badge attribute.
     * @return 
     */
    public Badge getBadge() {
        return (Badge)findAttribute(Badge.class);
    }
    
    /**
     * Gets the badge UIID attribute.
     * @return 
     */
    public BadgeUIID getBadgeUIID() {
        return (BadgeUIID)findAttribute(BadgeUIID.class);
    }
    
    
    /**
     * Gets the selected action node.
     * @return 
     */
    public ActionNode getSelected() {
        Selected sel = (Selected)findAttribute(Selected.class);
        if (sel == null) {
            return this;
        }
        return sel;
    }
    
    /**
     * Gets the unselected action node.
     * @return 
     */
    public ActionNode getUnselected() {
        Unselected sel = (Unselected)findAttribute(Unselected.class);
        if (sel == null) {
            return this;
        }
        return sel;
    }
    
    /**
     * Gets the pressed action node.
     * @return 
     */
    public ActionNode getPressed() {
        Pressed sel = (Pressed)findAttribute(Pressed.class);
        if (sel == null) {
            return this;
        }
        return sel;
    }
    
    /**
     * Gets the disabled action node.
     * @return 
     */
    public ActionNode getDisabled() {
        Disabled sel = (Disabled)findAttribute(Disabled.class);
        if (sel == null) {
            return this;
        }
        return sel;
    }
    
    /**
     * Gets the category of the action.
     * @return 
     */
    public Category getCategory() {
        Category out = (Category)findAttribute(Category.class);
        if (out != null) {
            return out;
        }
        if (getParent() != null) {
            Node parent = getParent();
            if (parent instanceof ActionNode) {
                return ((ActionNode)parent).getCategory();
            } else if (parent instanceof ActionsNode) {
                return ((ActionsNode)parent).getCategory();
            }
            
        }
        ActionNode proxyNode = (ActionNode)getProxying();
        if (proxyNode != null) {
            return proxyNode.getCategory();
        }
        return null;
        
    }
    
    /**
     * Gets the action label.
     * @return 
     */
    public Label getLabel() {
        return (Label)findAttribute(Label.class);
    }
    
    /**
     * Gets a description for the action.
     * @return 
     */
    public Description getDescription() {
        return (Description)findAttribute(Description.class);
    }
    
    /**
     * Gets the condition attribute.  This attribute can be used to show/hide the action
     * depending on a boolean test condition. 
     * @return 
     */
    public Condition getCondition() {
        return (Condition)findAttribute(Condition.class);
    }
    
    /**
     * Gets the selected condition attribute.
     * @return 
     */
    public SelectedCondition getSelectedCondition() {
        return (SelectedCondition)findAttribute(SelectedCondition.class);
    }
    
    /**
     * Gets the enabled condition attribute.
     * @return 
     */
    public EnabledCondition getEnabledCondition() {
        return (EnabledCondition)findAttribute(EnabledCondition.class);
    }
    
    /**
     * Checks if the action is enabled for the given entity context.
     * @param entity The entity to use as a context for testing the enabled state.
     * @return 
     */
    public boolean isEnabled(Entity entity) {
        EnabledCondition cond = getEnabledCondition();
        if (cond != null) {
            return cond.getValue().test(entity);
        }
        return true;
    
    }
    
    /**
     * Checks if the action is selected for the given entity context.
     * @param entity The entity to use as a context for testing the selected condition.
     * @return 
     */
    public boolean isSelected(Entity entity) {
        SelectedCondition cond = getSelectedCondition();
        if (cond != null) {
            return cond.getValue().test(entity);
        }
        return true;
    }
    
    /**
     * Gets the image icon for the action.
     * @return 
     */
    public ImageIcon getImageIcon() {
        return (ImageIcon)findAttribute(ImageIcon.class);
    }
    
    /**
     * Gets the material icon for the action.
     * @return 
     */
    public MaterialIcon getMaterialIcon() {
        return (MaterialIcon)findAttribute(MaterialIcon.class);
    }
    
    /**
     * Gets the text icon for the action.
     * @return 
     */
    public TextIcon getTextIcon() {
        return (TextIcon) findAttribute(TextIcon.class);
    }

    /**
     * Creates a proxy node for the current ActionNode.
     * @param parent The parent node for the proxy node.
     * @return The proxy node.
     */
    @Override
    public Node createProxy(Node parent) {
        ActionNode out = new ActionNode();
        out.setProxying(this);
        out.setParent(parent);
        return out;
    }

    @Override
    /**
     * Action Nodes can act as a proxy for other action nodes, so this returns true by default.
     */
    public boolean canProxy() {
        return true;
    }
    
    
    
    /**
     * A sub-node of an ActionNode that contains attributes for the action specific to
     * its selected state.
     */
    public static class Selected extends ActionNode {
        public Selected(Attribute... atts) {
            super(atts);
        }
    }
    
    /**
     * A sub-node of an ActionNode that contains attributes for the action specific to
     * its disabled state.
     */
    public static class Disabled extends ActionNode {
        public Disabled(Attribute... atts) {
            super(atts);
        }
    }
    
    /**
     * A sub-node of an ActionNode that contains attributes for the action specific to
     * its unselected state.
     */
    public static class Unselected extends ActionNode {
        public Unselected(Attribute... atts) {
            super(atts);
        }
    }
    
    /**
     * A sub-node of an ActionNode that contains attributes for the action specific 
     * to its pressed state.
     */
    public static class Pressed extends ActionNode {
        public Pressed(Attribute... atts) {
            super(atts);
        }
    }
    
    /**
     * A category is used as a marker in a View to allow groups of actions to be added to the view
     * in a single category.
     */
    public static class Category extends Attribute<Name> {
        
        public Category(Name value) {
            super(value);
        }
        
        public Category() {
            this(new Name(""));
        }
        
        public Category(String name) {
            this(new Name(name));
        }
        
    }
    
    /**
     * An action node event.  This is the type of event triggered by an action node.
     */
    public static class ActionNodeEvent extends ControllerEvent {
        
        /**
         * The event context.
         */
        private EventContext context;
        
        /**
         * Creates a new ActionNodeEvent with the given context.
         * @param context 
         */
        public ActionNodeEvent(EventContext context) {
            super(context.getEventSource());
            this.context = context;
            
        }
        
        /**
         * Gets the action that triggeed this event.
         * @return 
         */
        public ActionNode getAction() {
            return context.getAction();
        }
        
        /**
         * Gets the entity context where the action was triggered.
         * @return 
         */
        public Entity getEntity() {
            return context.getEntity();
        }
        
        /**
         * Gets the context of the event.
         * @return 
         */
        public EventContext getContext() {
            return context;
        }
    }
    
    
    public static ActionNodeEvent getActionNodeEvent(ActionEvent evt) {
        if (evt instanceof ActionNodeEvent) {
            return (ActionNodeEvent)evt;
        }
        return null;
    }

    public static boolean asActionNodeEvent(ActionEvent evt, SuccessCallback<ActionNodeEvent> callback) {
        ActionNodeEvent ane = getActionNodeEvent(evt);
        if (ane == null) return false;
        callback.onSucess(ane);
        return true;
    }
    
    public static ActionNodeEvent getActionNodeEvent(ActionEvent evt, ActionNode action) {
        ActionNodeEvent ane = getActionNodeEvent(evt);
        if (ane != null && ane.getAction().getCanonicalNode() == action.getCanonicalNode()) {
            return ane;
        }
        return null;
    }

    public static boolean asActionNodeEvent(ActionEvent evt, ActionNode action, SuccessCallback<ActionNodeEvent> callback) {
        ActionNodeEvent ane = getActionNodeEvent(evt, action);
        if (ane == null) return false;
        callback.onSucess(ane);
        return true;
    }
    
    public static ActionNode getActionNode(ActionEvent evt) {
        ActionNodeEvent ane = getActionNodeEvent(evt);
        if (ane != null) {
            return ane.getAction();
        }
        return null;
    }

    /**
     * If the provided event is an ActionNode, then it will be passed as an argument to the callback.  Otherwise the callback will
     * not be run.
     * @param evt The event
     * @param callback The callback.
     * @return 
     */
    public static boolean asActionNode(ActionEvent evt, SuccessCallback<ActionNode> callback) {
        ActionNode n = getActionNode(evt);
        if (n == null) return false;
        callback.onSucess(n);
        return true;
    }
    
    /**
     * Fires an event with the given event context.
     * @param context The event context.
     * @return 
     */
    public ActionEvent fireEvent(EventContext context) {
        EventFactoryNode eventFactory = this.getEventFactory();
        EventContext contextCopy = context.copyWithNewAction(this);
        ActionEvent actionEvent = eventFactory.getValue().createEvent(contextCopy);
        fireActionListeners(actionEvent);
        if (actionEvent.isConsumed()) {
            fireAfterActionCallback(actionEvent);
            return actionEvent;
        }
        ActionSupport.dispatchEvent(actionEvent);
        fireAfterActionCallback(actionEvent);
        return actionEvent;
    }
    
    /**
     * Fires registered callbacks to run after the action has been dispatched.
     * 
     * @param evt 
     */
    private void fireAfterActionCallback(ActionEvent evt) {
        for (Node n : getChildNodes(AfterActionCallbackNode.class)) {
            AfterActionCallbackNode aacn = (AfterActionCallbackNode)n;
            ((AfterActionCallback)aacn.getValue()).onSucess(evt);
        }
    }

    
    
    /**
     * Fires an event with the given entity and source for the context
     * @param entity
     * @param source
     * @return 
     */
    public ActionEvent fireEvent(Entity entity, Component source) {
        return fireEvent(entity, source, null);
    }
    
    /**
     * Fires an event with the given entity, source, and extra data as the context.
     * @param entity
     * @param source
     * @param extraData
     * @return 
     */
    public ActionEvent fireEvent(Entity entity, Component source, Map extraData) {
        EventFactoryNode eventFactory = this.getEventFactory();
        if (eventFactory == null) {
            eventFactory = new EventFactoryNode(UI.getDefaultEventFactory());
        }
        EventContext eventContext = new EventContext();
        eventContext.setEntity(entity);
        eventContext.setAction(this);
        eventContext.setEventSource(source); 
        if (extraData != null) {
            for (Object k : extraData.keySet()) {
                eventContext.putExtra(k, extraData.get(k));
            }
        }

        ActionEvent actionEvent = eventFactory.getValue().createEvent(eventContext);
        fireActionListeners(actionEvent);
        if (actionEvent.isConsumed()) {
            fireAfterActionCallback(actionEvent);
            return actionEvent;
        }
        ActionSupport.dispatchEvent(actionEvent);
        fireAfterActionCallback(actionEvent);
        return actionEvent;
    }
    
    
    private void fireActionListeners(ActionEvent actionEvent) {
        NodeList actionListeners = getChildNodes(ActionListenerNode.class);
        for (Node n : actionListeners) {
            ActionListenerNode aln = (ActionListenerNode)n;
            aln.getValue().actionPerformed(actionEvent);
            if (actionEvent.isConsumed()) {
                return;
            }
        }
    }
    
    /**
     * Converts the string to the empty string if it is null. Otherwise just returns the string.
     * @param str
     * @return 
     */
    private String str(String str) {
        return str == null ? "" : str;
    }
    
    /**
     * Gets the label text for this action.
     * @return 
     */
    public String getLabelText() {
        Label l = getLabel();
        if (l == null) {
            return "";
        }
        return l.getValue();
    }


    
    /**
     * Gets the label text for this action with the given entity used as the context.
     * @param context
     * @return 
     */
    public String getLabelText(Entity context) {
        Label l = getLabel();
        if (l == null) {
            return "";
        }
        return l.getValue(context.getEntity());
    }

    
    /**
     * Creates a view for the action using the current view factory.
     * @param entity
     * @return 
     */
    public Component createView(Entity entity) {
        return getViewFactory().createActionView(entity, this);
    }
   
    
    /**
     * Creates a command from the action using the given entity and source componentas a context.
     * @param entity
     * @param source
     * @return 
     */
    public Command createCommand(Entity entity, Component source) {
        ActionStyle style = getActionStyle();
        if (style == null) {
            style = ActionStyle.IconRight;
        }
        ActionListener l = ev->{
            fireEvent(entity, source);
        };
        switch (style) {
            case IconOnly:
                if (getMaterialIcon() != null) {
                    return Command.createMaterial("", getMaterialIcon().getValue(), l);
                } else if (getImageIcon() != null) {
                    return Command.create("", getImageIcon().getValue(), l);
                } else {
                    return new Command("") {
                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            l.actionPerformed(evt);
                        }
                        
                    };
                }
                
            case TextOnly:
                return Command.create(getLabelText(), null, l);
                
            default:
                if (getMaterialIcon() != null) {
                    return Command.createMaterial(getLabelText(), getMaterialIcon().getValue(), l);
                } else if (getImageIcon() != null) {
                    return Command.create(getLabelText(), getImageIcon().getValue(), l);
                } else {
                    return new Command(str(getLabelText())) {
                        public void actionPerformed(ActionEvent evt) {
                            l.actionPerformed(evt);
                        }
                    };
                }
        }


        
    }
    
    /**
     * A builder to create action nodes.
     */
    public static class Builder {
        private ActionNode action;
        private boolean overwrite = true;
        
        public Builder(ActionNode action) {
            this.action = action;
        }
        
        public Builder() {
            this(UI.action());
        }

        public Builder overwrite(boolean overwrite) {
            this.overwrite = overwrite;
            return this;
        }
        
        public Builder label(String label) {
            action.setAttributes(overwrite, UI.label(label));
            return this;
        }

        public void setLabel(String label) {label(label);}
        
        public Builder uiid(String uiid) {
            action.setAttributes(overwrite, UI.uiid(uiid));
            return this;
        }
        public void setUiid(String uiid) {uiid(uiid);}
        
        public Builder icon(String text) {
            action.setAttributes(overwrite, UI.icon(text));
            return this;
        }
        public void setIcon(String text){icon(text);}

        public Builder name(String text) {
            action.setAttributes(overwrite, new Property.Name(text));
            return this;
        }
        public void setName(String text){name(text);}
        
        public Builder icon(char materialIcon) {
            action.setAttributes(overwrite, UI.icon(materialIcon));
            return this;
        }
        public void setMaterialIcon(char materialIcon){icon(materialIcon);}
        
        public Builder icon(Image icon) {
            action.setAttributes(overwrite, UI.icon(icon));
            return this;
        }
        public void setImageIcon(Image icon){icon(icon);}
        
        public Builder icon(StringProvider provider) {
            action.setAttributes(overwrite, UI.icon(provider));
            return this;
        }
        public void setIconProvider(StringProvider provider) {icon(provider);}
        
        public Builder icon(String text, StringProvider provider) {
            action.setAttributes(overwrite, UI.icon(text, provider));
            return this;
        }


        
        public Builder iconRenderer(EntityImageRenderer renderer) {
            action.setAttributes(overwrite, UI.iconRenderer(renderer));
            return this;
        }

        public void setIconRenderer(EntityImageRenderer renderer){iconRenderer(renderer);}
        
        public Builder iconRenderer(PropertyImageRenderer renderer) {
            action.setAttributes(overwrite, UI.iconRenderer(renderer));
            return this;
        }
        
        public Builder iconUiid(String uiid) {
            action.setAttributes(overwrite, UI.iconUiid(uiid));
            
            return this;
        }

        public void setIconUiid(String uiid){ iconUiid(uiid);}
        
        public Builder selectedCondition(EntityTest test) {
            action.setAttributes(overwrite, UI.selectedCondition(test));
            return this;
        }

        public void setSelectedCondition(EntityTest test){selectedCondition(test);}
        
        public Builder enabledCondition(EntityTest test) {
            action.setAttributes(overwrite, UI.enabledCondition(test));
            return this;
        }

        public void setEnabledCondition(EntityTest test){enabledCondition(test);}
        public Builder addActionListener(ActionListener l) {
            action.addActionListener(l);
            return this;
        }

        public Builder tag(Tag tag) {
            action.setActionTag(tag);
            return this;
        }

        public void setActionTag(Tag tag) {tag(tag);}
        
        public Builder addAfterActionCallback(AfterActionCallback callback) {
            action.addAfterActionCallback(callback);
            return this;
        }

        public void setAfterActionCallback(AfterActionCallback callback){ addAfterActionCallback(callback);}
        
        public Builder condition(EntityTest test) {
            action.setAttributes(overwrite, UI.condition(test));
            return this;
        }

        public void setCondition(EntityTest test){condition(test);}

        public Builder badge(String badge) {
            action.setAttributes(UI.badge(badge));
            return this;
        }

        public void setBadge(String badge){ badge(badge);}

        public Builder badge(StringProvider stringProvider) {
            action.setAttributes(UI.badge(null, stringProvider));
            return this;
        }

        public void setBadgeProvider(StringProvider badgeProvider) {
            badge(badgeProvider);
        }

        public Builder badge(String badgeText, StringProvider provider) {
            action.setAttributes(UI.badge(badgeText, provider));
            return this;
        }

        public Builder badgeUIID(String uiid) {
            action.setAttributes(UI.badgeUiid(uiid));
            return this;
        }

        public void setBadgeUIID(String uiid) {
            badgeUIID(uiid);
        }

        public Builder actionStyle(ActionStyle style) {
            action.setAttributes(UI.actionStyle(style));
            return this;

        }

        public void setActionStyle(ActionStyle style) {
            actionStyle(style);
        }


        public Builder addToController(Controller controller, Category category, ActionListener<ActionNodeEvent> listener) {
            controller.addAction(category, build(), listener);
            return this;
        }
        
        public ActionNode build() {
            return action;
        }


    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static Builder mutator(ActionNode node) {
        return new Builder(node);
    }
    
}
