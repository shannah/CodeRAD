/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.propertyviews;

import com.codename1.rad.ui.PropertyView;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.models.ContentType;
import com.codename1.rad.models.DateFormatterAttribute;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.Property;
import com.codename1.rad.models.Property.Label;
import com.codename1.rad.models.PropertyChangeEvent;
import com.codename1.rad.models.PropertySelector;
import com.codename1.rad.models.Tag;
import com.codename1.rad.models.TextFormatterAttribute;
import com.codename1.rad.text.TextFormatter;
import com.codename1.rad.ui.UI;
import com.codename1.rad.ui.image.PropertyImageRenderer;
import com.codename1.rad.ui.image.RoundImageRenderer;
import com.codename1.rad.ui.image.RoundRectImageRenderer;
import com.codename1.ui.Image;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.DataChangedListener;
import java.util.Date;
import java.util.Objects;

/**
 * View for binding to {@link com.codename1.ui.Label} components.
 * @author shannah
 */
public class LabelPropertyView extends PropertyView<com.codename1.ui.Label> {
    
    private FieldNode iconField;
    private PropertySelector iconPropertySelector;
    private boolean iconOnly;

    private ActionListener<PropertyChangeEvent> pcl = pce->{
        update();
    };
    
    private DataChangedListener dcl = (type, index) ->{
        commit();
    };
    
    
    
    public LabelPropertyView(com.codename1.ui.Label component, Entity entity, FieldNode textField) {
        this(component, entity, textField, null);
    }
    
    public LabelPropertyView(com.codename1.ui.Label component, Entity entity, FieldNode textField, FieldNode iconField) {
        super(component, entity, textField);
        this.iconField = iconField;
        update();
    }
    
    public LabelPropertyView(com.codename1.ui.Label component, Entity entity, Tag... tags) {
        this(component, entity, new FieldNode(UI.tags(tags)));
    }
    
    public LabelPropertyView(com.codename1.ui.Label component, Entity entity, Property prop) {
        this(component, entity, new FieldNode(UI.property(prop)));
    }

    @Override
    protected void bindImpl() {
        getPropertySelector().addPropertyChangeListener(pcl);
        if (getIconPropertySelector() != null) {
            iconPropertySelector.addPropertyChangeListener(pcl);
        }
        //getEntity().addPropertyChangeListener(getPropertySelector(), pcl);
        
    }

    @Override
    protected void unbindImpl() {
        getPropertySelector().removePropertyChangeListener(pcl);
        if (getIconPropertySelector() != null) {
            getIconPropertySelector().removePropertyChangeListener(pcl);
        }
        //getEntity().removePropertyChangeListener(getProperty(), pcl);
    }
    
    
    private String lastIcon;
    
    private String _getText() {
        Property prop = getPropertySelector().getLeafProperty();
        if (prop == null) {
            return "";
        }
        
        if (prop.getContentType().getRepresentationClass() == Date.class) {
            DateFormatterAttribute formatter = getField().getDateFormatter();
            if (formatter != null) {
                Date val = getPropertySelector().getDate(null);
                if (val != null) {
                    return formatter.getValue().format(val);
                }
            }
        }
                
        
        return getPropertySelector().getText("");
    }
    
    protected String getText() {
        String out = _getText();
        TextFormatterAttribute formatter = (TextFormatterAttribute)getField().findInheritedAttribute(TextFormatterAttribute.class);
        if (formatter != null) {
            return formatter.getValue().format(out);
        }
        return out;
    }
    
    @Override
    public void update() {
        String oldVal = getComponent().getText();
        
        String newVal = iconOnly ? "" : getText();
        
        if (!Objects.equals(oldVal, newVal)) {
            getComponent().setText(newVal);
        }
        
        if (iconField != null) {
            PropertyImageRenderer iconRenderer = iconField.getIconRenderer();
            if (iconRenderer != null) {
                if (!getIconPropertySelector().getText("").equals(lastIcon)) {
                    lastIcon = getIconPropertySelector().getText("");
                    Image icon = iconRenderer.createImage(iconPropertySelector);
                    getComponent().setIcon(icon);
                }
            }
        }
    }
    
    @Override
    public void commit() {
        
    }
    
    public PropertySelector getIconPropertySelector() {
        if (iconPropertySelector == null) {
            if (iconField != null) {
                iconPropertySelector = iconField.getPropertySelector(getEntity());
            }
        }
        return iconPropertySelector;
    }
    
    public void setIconOnly(boolean iconOnly) {
        this.iconOnly = iconOnly;
    }
    
    public static LabelPropertyView createIconLabel(com.codename1.ui.Label cmp, Entity entity, FieldNode iconField) {
        LabelPropertyView v = new LabelPropertyView(cmp, entity, iconField, iconField);
        v.setIconOnly(true);
        v.update();
        return v;
    }
    
    public static LabelPropertyView createIconLabel(Entity entity, FieldNode iconField) {
        
        return createIconLabel(new com.codename1.ui.Label(), entity, iconField);
    }
    
    public static LabelPropertyView createIconLabel(Entity entity, PropertySelector iconProperty) {
        return createIconLabel(entity, new FieldNode(UI.property(iconProperty)));
    }
    
    public static LabelPropertyView createRoundIconLabel(com.codename1.ui.Label label, Entity entity, PropertySelector iconProperty, int size) {
        return createIconLabel(label, entity, new FieldNode(UI.property(iconProperty), UI.iconRenderer(new RoundImageRenderer(size))));
    }
    
    public static LabelPropertyView createRoundRectIconLabel(com.codename1.ui.Label label, Entity entity, PropertySelector iconProperty, int width, int height, float cornerRadiusMM) {
        return createIconLabel(label, entity, new FieldNode(UI.property(iconProperty), UI.iconRenderer(new RoundRectImageRenderer(width, height, cornerRadiusMM))));
    }
    
    
    
    
    
}
