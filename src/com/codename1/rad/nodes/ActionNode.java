/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.nodes;

import com.codename1.rad.ui.ActionStyle;
import com.codename1.rad.ui.ActionViewFactory;
import com.codename1.rad.attributes.ActionStyleAttribute;
import com.codename1.rad.events.EventContext;
import com.codename1.rad.attributes.Condition;
import com.codename1.rad.attributes.ImageIcon;
import com.codename1.rad.attributes.MaterialIcon;
import com.codename1.rad.attributes.SelectedCondition;
import com.codename1.rad.events.DefaultEventFactory;
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
import com.codename1.rad.models.Property.Test;
import com.codename1.rad.ui.DefaultActionViewFactory;
import com.codename1.rad.ui.UI;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.ActionSource;
import java.util.Map;

/**
 *
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
        return (Category)findAttribute(Category.class);
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
    
    public void decorate(com.codename1.ui.Label label) {
        ActionStyle style = getActionStyle();
        if (style == null) {
            style = ActionStyle.IconRight;
        }
        boolean icon = style != ActionStyle.TextOnly;
        boolean text = style != ActionStyle.IconOnly;
        if (text) {
            label.setText(getLabelText());
        } else {
            label.setText("");
        }
        if (icon) {
            if (getMaterialIcon() != null) {
                label.setMaterialIcon(getMaterialIcon().getValue());
            } else if (getImageIcon() != null) {
                label.setIcon(getImageIcon().getValue());
            }
        }
 
    }
    
    
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
