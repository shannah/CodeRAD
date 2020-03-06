/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.nodes;

import com.codename1.rad.ui.ActionStyle;
import com.codename1.rad.ui.ActionViewFactory;
import com.codename1.rad.attributes.ActionStyleAttribute;
import com.codename1.rad.attributes.Badge;
import com.codename1.rad.attributes.BadgeUIID;
import com.codename1.rad.events.EventContext;
import com.codename1.rad.attributes.Condition;
import com.codename1.rad.attributes.ImageIcon;
import com.codename1.rad.attributes.MaterialIcon;
import com.codename1.rad.attributes.SelectedCondition;
import com.codename1.rad.attributes.TextIcon;
import com.codename1.rad.models.Attribute;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.Property.Description;
import com.codename1.rad.models.Property.Label;
import com.codename1.rad.models.Property.Name;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.rad.controllers.ActionSupport;
import com.codename1.rad.controllers.ControllerEvent;
import com.codename1.rad.models.EntityTest;
import com.codename1.rad.ui.ComponentDecorators;
import com.codename1.rad.ui.UI;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import java.util.Map;

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
 * .Excerpts from `ChatFormController`.  Defines a single action `send`, and adds it to the view.  Also handles the events when the `send` action is "fired".   See https://shannah.github.io/RADChatRoom/getting-started-tutorial.html[this tutorial,target=top] for a more comprehensive treatment of this material.
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
    
    
   
    
    public static class EnabledCondition extends Attribute<EntityTest> {
        
        public EnabledCondition(EntityTest value) {
            super(value);
        }
        
    }
    
    public ActionNode(Attribute... atts) {
        super(null, atts);
    }
    
    
    public ComponentDecorators getComponentDecorators() {
        return new ComponentDecorators(getChildNodes(ComponentDecoratorNode.class));
    }
   
    public void decorateComponent(Component cmp) {
        getComponentDecorators().decorate(cmp);
    }

    public EventFactoryNode getEventFactory() {
        
        return (EventFactoryNode)findInheritedAttribute(EventFactoryNode.class);
    }
    
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
    
    public ActionStyle getActionStyle() {
        ActionStyleAttribute att = (ActionStyleAttribute)findInheritedAttribute(ActionStyleAttribute.class);
        if (att != null) {
            return att.getValue();
        }
        return null;
    }
    
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
    
    public Badge getBadge() {
        return (Badge)findAttribute(Badge.class);
    }
    
    public BadgeUIID getBadgeUIID() {
        return (BadgeUIID)findAttribute(BadgeUIID.class);
    }
    
    
    public ActionNode getSelected() {
        Selected sel = (Selected)findAttribute(Selected.class);
        if (sel == null) {
            return this;
        }
        return sel;
    }
    
    public ActionNode getUnselected() {
        Unselected sel = (Unselected)findAttribute(Unselected.class);
        if (sel == null) {
            return this;
        }
        return sel;
    }
    
    public ActionNode getPressed() {
        Pressed sel = (Pressed)findAttribute(Pressed.class);
        if (sel == null) {
            return this;
        }
        return sel;
    }
    
    public ActionNode getDisabled() {
        Disabled sel = (Disabled)findAttribute(Disabled.class);
        if (sel == null) {
            return this;
        }
        return sel;
    }
    
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
        return null;
        
    }
    
    public Label getLabel() {
        return (Label)findAttribute(Label.class);
    }
    
    public Description getDescription() {
        return (Description)findAttribute(Description.class);
    }
    
    public Condition getCondition() {
        return (Condition)findAttribute(Condition.class);
    }
    
    public SelectedCondition getSelectedCondition() {
        return (SelectedCondition)findAttribute(SelectedCondition.class);
    }
    
    public EnabledCondition getEnabledCondition() {
        return (EnabledCondition)findAttribute(EnabledCondition.class);
    }
    
    public boolean isEnabled(Entity entity) {
        EnabledCondition cond = getEnabledCondition();
        if (cond != null) {
            return cond.getValue().test(entity);
        }
        return true;
    
    }
    
    public boolean isSelected(Entity entity) {
        SelectedCondition cond = getSelectedCondition();
        if (cond != null) {
            return cond.getValue().test(entity);
        }
        return true;
    }
    
    public ImageIcon getImageIcon() {
        return (ImageIcon)findAttribute(ImageIcon.class);
    }
    
    public MaterialIcon getMaterialIcon() {
        return (MaterialIcon)findAttribute(MaterialIcon.class);
    }
    
    public TextIcon getTextIcon() {
        return (TextIcon) findAttribute(TextIcon.class);
    }

    @Override
    public Node createProxy(Node parent) {
        ActionNode out = new ActionNode();
        out.setProxying(this);
        out.setParent(parent);
        return out;
    }

    @Override
    public boolean canProxy() {
        return true;
    }
    
    
    
    
    public static class Selected extends ActionNode {
        public Selected(Attribute... atts) {
            super(atts);
        }
    }
    
    public static class Disabled extends ActionNode {
        public Disabled(Attribute... atts) {
            super(atts);
        }
    }
    
    public static class Unselected extends ActionNode {
        public Unselected(Attribute... atts) {
            super(atts);
        }
    }
    
    public static class Pressed extends ActionNode {
        public Pressed(Attribute... atts) {
            super(atts);
        }
    }
    
    public static class Category extends Attribute<Name> {
        
        public Category(Name value) {
            super(value);
        }
        
        public Category() {
            this(new Name(""));
        }
        
    }
    
    public static class ActionNodeEvent extends ControllerEvent {
        private EventContext context;
        
        
        public ActionNodeEvent(EventContext context) {
            super(context.getEventSource());
            this.context = context;
            
        }
        
        public ActionNode getAction() {
            return context.getAction();
        }
        
        public Entity getEntity() {
            return context.getEntity();
        }
        
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
    
    public static ActionNodeEvent getActionNodeEvent(ActionEvent evt, ActionNode action) {
        ActionNodeEvent ane = getActionNodeEvent(evt);
        if (ane != null && ane.getAction().getCanonicalNode() == action.getCanonicalNode()) {
            return ane;
        }
        return null;
    }
    
    public static ActionNode getActionNode(ActionEvent evt) {
        ActionNodeEvent ane = getActionNodeEvent(evt);
        if (ane != null) {
            return ane.getAction();
        }
        return null;
    }
    
    public ActionEvent fireEvent(EventContext context) {
        EventFactoryNode eventFactory = this.getEventFactory();
        EventContext contextCopy = context.copyWithNewAction(this);
        ActionEvent actionEvent = eventFactory.getValue().createEvent(contextCopy);

        ActionSupport.dispatchEvent(actionEvent);
        return actionEvent;
    }
    
    
    
    public ActionEvent fireEvent(Entity entity, Component source) {
        return fireEvent(entity, source, null);
    }
    
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

        ActionSupport.dispatchEvent(actionEvent);
        return actionEvent;
    }
    
    private String str(String str) {
        return str == null ? "" : str;
    }
    
    public String getLabelText() {
        Label l = getLabel();
        if (l == null) {
            return "";
        }
        return l.getValue();
    }
    
    public String getLabelText(Entity context) {
        Label l = getLabel();
        if (l == null) {
            return "";
        }
        return l.getValue(context);
    }
    /*
    public void decorate(com.codename1.ui.Label label, Entity context) {
        ActionStyle style = getActionStyle();
        if (style == null) {
            style = ActionStyle.IconRight;
        }
        boolean icon = style != ActionStyle.TextOnly;
        boolean text = style != ActionStyle.IconOnly;
        if (text) {
            label.setText(getLabelText(context));
        } else {
            label.setText("");
        }
        if (icon) {
            if (getMaterialIcon() != null) {
                label.setMaterialIcon(getMaterialIcon().getValue());
            } else if (getImageIcon() != null) {
                label.setIcon(getImageIcon().getValue());
            } else if (getTextIcon() != null) {
                label.setIcon(getTextIcon().getValue(context));
            }
        }
 
    }
*/
    
    
    public Component createView(Entity entity) {
        return getViewFactory().createActionView(entity, this);
    }
   
    
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
    
}
