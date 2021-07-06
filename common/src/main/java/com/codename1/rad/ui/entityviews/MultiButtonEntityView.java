/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui.entityviews;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.ui.ActionCategories;
import com.codename1.rad.ui.EntityView;
import com.codename1.rad.ui.ViewProperty;
import com.codename1.rad.ui.ViewPropertyParameter;
import com.codename1.rad.attributes.IconRendererAttribute;
import com.codename1.rad.attributes.UIID;
import com.codename1.rad.events.EventContext;
import com.codename1.rad.ui.image.AsyncImage;
import com.codename1.rad.ui.image.DefaultEntityImageRenderer;
import com.codename1.rad.ui.image.EntityImageRenderer;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.ActionNode.Category;
import com.codename1.rad.nodes.EventFactoryNode;
import com.codename1.rad.nodes.ViewNode;
import com.codename1.rad.models.Attribute;
import com.codename1.rad.models.ContentType;

import com.codename1.rad.models.EntityType;
import com.codename1.rad.models.Property;
import com.codename1.rad.models.PropertyChangeEvent;
import com.codename1.rad.models.Tag;
import com.codename1.rad.schemas.ListRowItem;
import com.codename1.rad.schemas.Thing;
import com.codename1.compat.java.util.Objects;
import com.codename1.components.MultiButton;
import com.codename1.rad.controllers.ActionSupport;
import com.codename1.ui.events.ActionEvent;

import com.codename1.ui.events.ActionListener;
import com.codename1.rad.models.Entity;

/**
 * A view that renders an {@link Entity} as a MultiButton.  
 * 
 * @deprecated With CodeRAD 2.0 bindings and views, it is now just as easy to use a MultiButton directly
 * and bind properties to model properties as required.
 * @author shannah
 */
public class MultiButtonEntityView<T extends Entity> extends MultiButton implements EntityView<T> {
    public static final Tag line1 = ListRowItem.line1;
    public static final Tag line2 = ListRowItem.line2;
    public static final Tag line3 = new Tag();
    public static final Tag line4 = new Tag();
    public static final Tag icon = ListRowItem.icon;
    
    
    
    private T entity;
    private ViewNode viewNode;
    private boolean iconDirty = true, line1PropDirty = true, line2PropDirty = true, line3PropDirty = true, line4PropDirty = true;
    private Property iconProp, line1Prop, line2Prop, line3Prop, line4Prop;
    private ActionNode action;
    
    
    private ActionListener<PropertyChangeEvent> pcl = pce -> {
        Property changedProp = pce.getProperty();
        if (changedProp == iconProp) {
            iconDirty = true;
        }
        if (changedProp == line1Prop) {
            line1PropDirty = true;
        }
        if (changedProp == line2Prop) {
            line2PropDirty = true;
        }
        if (changedProp == line3Prop) {
            line3PropDirty = true;
        }
        if (changedProp == line4Prop) {
            line4PropDirty = true;
        }
        if (iconDirty || line1PropDirty || line2PropDirty || line3PropDirty || line4PropDirty) {
            update();
        }
    };
    
    public MultiButtonEntityView(@Inject T entity, @Inject ViewNode viewNode) {
        this.entity = entity;
        this.viewNode = viewNode;
        line1Prop = prop(LINE1, line1Default);
        if (line1Prop == null) {
            String line1Val = value(LINE1, line1Default);
            if (line1Val != null) {
                setTextLine1(line1Val);
            }
        }
        line2Prop = prop(LINE2, line2Default);
        if (line2Prop == null) {
            String line2Val = value(LINE2, line2Default);
            if (line2Val != null) {
                setTextLine2(line2Val);
            }
        }
        line3Prop = prop(LINE3, line3Default);
        if (line3Prop == null) {
            String line3Val = value(LINE3, line3Default);
            if (line3Val != null) {
                setTextLine3(line3Val);
            }
        }
        line4Prop = prop(LINE4, line4Default);
        if (line4Prop == null) {
            String line4Val = value(LINE4, line4Default);
            if (line4Val != null) {
                setTextLine4(line4Val);
            }
        }
        iconProp = prop(ICON, iconDefault);
        //System.out.println("Icon prop is "+iconProp+" default is "+iconDefault);
        
        if (iconProp == null) {
            AsyncImage iconVal = value(ICON, iconDefault);
            if (iconVal != null) {
                iconVal.ready(im->{
                    setIcon(im);
                    revalidateLater();
                });
            }
        }
        
        String line1UIID = (String)viewNode.getViewParameterValue(LINE1_UIID);
        if (line1UIID != null) {
            setUIIDLine1(line1UIID);
        }
        String line2UIID = (String)viewNode.getViewParameterValue(LINE2_UIID);
        if (line2UIID != null) {
            setUIIDLine2(line2UIID);
        }
        String line3UIID = (String)viewNode.getViewParameterValue(LINE3_UIID);
        if (line3UIID != null) {
            setUIIDLine3(line3UIID);
        }
        String line4UIID = (String)viewNode.getViewParameterValue(LINE4_UIID);
        if (line4UIID != null) {
            setUIIDLine4(line4UIID);
        }
        
        String uiid = (String)viewNode.getViewParameterValue(UIID);
        if (uiid != null) {
            setUIID(uiid);
        }
        addActionListener(e->{
            EventFactoryNode eventFactory = (EventFactoryNode)viewNode.findInheritedAttribute(EventFactoryNode.class);
            if (eventFactory == null) {
                return;
            }
            EventContext eventContext = new EventContext();
            eventContext.setEntity(entity);
            Category category = value(ACTION_CATEGORY, actionCategoryDefault);
            ActionNode action = this.action == null ? viewNode.getInheritedAction(category) : this.action;
            if (action == null) {
                return;
            }
            eventContext.setAction(action);
            eventContext.setEventSource(this); 

            ActionEvent actionEvent = eventFactory.getValue().createEvent(eventContext);
            
            ActionSupport.dispatchEvent(actionEvent);
        });
        
        update();
    }
    
    public void setAction(ActionNode action) {
        this.action = action;
    }
    
    private <V> V value(ViewProperty<V> prop, ViewPropertyParameter<V> defaultParam) {
        return (V)viewNode.getViewParameter(prop, defaultParam).getValue(entity);
    }
    
    private Property prop(ViewProperty prop, ViewPropertyParameter defaultParam) {
        return viewNode.getViewParameter(prop, defaultParam).findProperty(entity);
    }

    @Override
    protected void initComponent() {
        super.initComponent();
        bind();
    }

    @Override
    protected void deinitialize() {
        unbind();
        super.deinitialize(); 
    }
    
    
    
    
    @Override
    public void bind() {
        entity.getEntity().addPropertyChangeListener(pcl);
    }

    @Override
    public void unbind() {
        entity.getEntity().removePropertyChangeListener(pcl);
    }

    @Override
    public void update() {
        EntityType type = getEntity().getEntity().getEntityType();
        boolean changed = false;
        if (line1PropDirty) {
            line1PropDirty = false;
            line1Prop = line1Prop == null ? type.findProperty(line1, Thing.name) : line1Prop;
            if (line1Prop != null) {
                String text = type.getText(line1Prop, entity.getEntity());
                if (!Objects.equals(text, getTextLine1())) {
                    setTextLine1(text);
                    changed = true;
                }
            }
        }
        if (line2PropDirty) {
            line2PropDirty = false;
            line2Prop = line2Prop == null ? type.findProperty(line2, Thing.description) : line2Prop;
            if (line2Prop != null) {
                String text = type.getText(line2Prop, entity.getEntity());
                if (!Objects.equals(text, getTextLine2())) {
                    setTextLine2(text);
                    changed = true;
                }
            }
        }
        if (line3PropDirty) {
            line3PropDirty = false;
            line3Prop = line3Prop == null ? type.findProperty(line3) : line3Prop;
            if (line3Prop != null) {
                String text = type.getText(line3Prop, entity.getEntity());
                if (!Objects.equals(text, getTextLine3())) {
                    setTextLine3(text);
                    changed = true;
                }
            }
        }
        if (line4PropDirty) {
            line4PropDirty = false;
            line4Prop = line4Prop == null ? type.findProperty(line4) : line4Prop;
            if (line4Prop != null) {
                String text = type.getText(line4Prop, entity.getEntity());
                if (!Objects.equals(text, getTextLine4())) {
                    setTextLine4(text);
                    changed = true;
                }
            }
        }
        
        if (iconDirty) {
            iconDirty = false;
            Property iconProp = this.iconProp != null ? this.iconProp : type.findProperty(icon);
            this.iconProp = iconProp;
            if (iconProp != null) {
                Object iconData = iconProp.getValue(getEntity().getEntity());
                if (iconData != null) {
                    IconRendererAttribute iconRendererAtt = (IconRendererAttribute)viewNode.findInheritedAttribute(IconRendererAttribute.class);
                    EntityImageRenderer iconRenderer = iconRendererAtt == null ? new DefaultEntityImageRenderer() : iconRendererAtt.getValue();
                    iconRenderer.createImage(this, iconProp, 0, false, false).ready(im->{
                        setIcon(im);
                        revalidateLater();
                    });
                }
            }
        }
        if (changed) {
            revalidateLater();
        }
        
                
    }

    @Override
    public void commit() {
        
    }

    @Override
    public void setEntity(T entity) {
        this.entity = entity;
    }

    @Override
    public T getEntity() {
        return entity;
    }

    @Override
    public ViewNode getViewNode() {
        return viewNode;
    }
    
    /**
     * View property to bind {@link MultiButton#setTextLine1(java.lang.String) }
     */
    public static final ViewProperty<String> LINE1 = ViewProperty.stringProperty();
    
    /**
     * Default parameter value for {@link #LINE1}.  This will look for properties tagged  {@link #line1},
     * or {@link Thing#name}, in that order.
     */
    private static final ViewPropertyParameter<String> line1Default = ViewPropertyParameter.createBindingParam(LINE1, line1, Thing.name);
    
    /**
     * View property to bind {@link MultiButton#setTextLine2(java.lang.String) }.
     */
    public static final ViewProperty<String> LINE2 = ViewProperty.stringProperty();
    
    /**
     * Default parameter for {@link #LINE2}.  This will look for properties tagged {@link #line2}, or {@link Thing#description} 
     * in that order.
     */
    private static final ViewPropertyParameter<String> line2Default = ViewPropertyParameter.createBindingParam(LINE2, line2, Thing.description);
    
    /**
     * View property to bind {@link MultiButton#setTextLine3(java.lang.String) }
     */
    public static final ViewProperty<String> LINE3 = ViewProperty.stringProperty();
    
    /**
     * Default parameter for {@link #LINE3}.  This will look for properties tagged {@link #line3}.
     * 
     */
    private static final ViewPropertyParameter<String> line3Default = ViewPropertyParameter.createBindingParam(LINE3, line3);
    
    /**
     * View property to bind {@link MultiButton#setTextLine4(java.lang.String) }
     */
    public static final ViewProperty<String> LINE4 = ViewProperty.stringProperty();
    
    /**
     * Default parameter for {@link #LINE4}.  This will look for properties tagged {@link #line4}.
     * 
     */
    private static final ViewPropertyParameter<String> line4Default = ViewPropertyParameter.createBindingParam(LINE4, line4);
    
    /**
     * View property to bind {@link MultiButton#setIcon(com.codename1.ui.Image) }.
     */
    public static final ViewProperty<AsyncImage> ICON = new ViewProperty<AsyncImage>(AsyncImage.CONTENT_TYPE);
    
    /**
     * Default parameter for {@link #ICON}.  This will look for properties tagged {@link #icon}.
     */
    private static final ViewPropertyParameter<AsyncImage> iconDefault = ViewPropertyParameter.createBindingParam(ICON, icon);

    
    public static final ViewProperty<Category> ACTION_CATEGORY = new ViewProperty<Category>(ContentType.createObjectType(Category.class));
    private static final ViewPropertyParameter<Category> actionCategoryDefault = ViewPropertyParameter.createValueParam(ACTION_CATEGORY, ActionCategories.BUTTON_ACTION);
    public static final ViewProperty<String> LINE1_UIID = ViewProperty.stringProperty();
    public static final ViewProperty<String> LINE2_UIID = ViewProperty.stringProperty();
    public static final ViewProperty<String> LINE3_UIID = ViewProperty.stringProperty();
    public static final ViewProperty<String> LINE4_UIID = ViewProperty.stringProperty();
    public static final ViewProperty<String> UIID = ViewProperty.stringProperty();
}
