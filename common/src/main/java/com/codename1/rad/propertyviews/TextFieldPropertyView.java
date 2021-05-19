/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.propertyviews;

import com.codename1.l10n.ParseException;
import com.codename1.rad.annotations.Inject;
import com.codename1.rad.attributes.HintUIID;
import com.codename1.rad.attributes.UIID;
import com.codename1.rad.ui.PropertyView;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.models.ContentType;

import com.codename1.rad.models.Property;
import com.codename1.rad.models.PropertyChangeEvent;
import com.codename1.rad.models.TextFormatterAttribute;
import com.codename1.ui.Label;
import com.codename1.ui.TextField;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.DataChangedListener;
import java.util.Objects;
import com.codename1.rad.models.Entity;

/**
 * A view for binding to TextField components.
 * @author shannah
 */
public class TextFieldPropertyView extends PropertyView<TextField> {
    
    

    private ActionListener<PropertyChangeEvent> pcl = pce->{
        update();
    };
    
    private DataChangedListener dcl = (type, index) ->{
        commit();
    };
    
    
    public TextFieldPropertyView(@Inject TextField component, @Inject Entity entity, @Inject FieldNode field) {
        super(component, entity, field);
    }
    
    

    @Override
    protected void bindImpl() {
        getPropertySelector().addPropertyChangeListener(pcl);
        getComponent().addDataChangedListener(dcl);
    }

    @Override
    protected void unbindImpl() {
        getComponent().removeDataChangedListener(dcl);
        getPropertySelector().removePropertyChangeListener(pcl);
    }
    
    @Override
    public void update() {
        super.update();
        String oldVal = getComponent().getText();
        String newVal = ContentType.convert(
                getProperty().getContentType(), 
                getProperty().getValue(getEntity().getEntity()),
                ContentType.Text
        );
        TextFormatterAttribute formatter = (TextFormatterAttribute)getField().findAttribute(TextFormatterAttribute.class);
        if (formatter != null) {
            newVal = formatter.getValue().format(newVal);
        }
        if (!Objects.equals(oldVal, newVal)) {
            getComponent().setText(newVal);
        }
        
        HintUIID hintUiid = (HintUIID)getField().findAttribute(HintUIID.class);
        if (hintUiid != null) {
            Label hintLabel = getComponent().getHintLabel();
            if (hintLabel != null) {
                String oldUiid = hintLabel.getUIID();
                String newUiidStr = hintUiid.getValue(getEntity());
                
                if (newUiidStr != null && !Objects.equals(oldUiid, newUiidStr)) {
                    hintLabel.setUIID(newUiidStr);
                }
            }
            
        }
        
        
    }
    
    @Override
    public void commit() {
        Entity leafEntity = getPropertySelector().getLeafEntity();
        Property leafProperty = getPropertySelector().getLeafProperty();
        String text = getComponent().getText();
        TextFormatterAttribute formatter = (TextFormatterAttribute)getField().findAttribute(TextFormatterAttribute.class);
        if (formatter != null) {
            if (!formatter.getValue().supportsParse()) {
                throw new RuntimeException("Formatter does not support parse committing text '"+text+"'.");
            }
            try {
                text = formatter.getValue().parse(text);
            } catch (ParseException ex) {
                throw new RuntimeException("Failed to parse text '"+text+"' for property.");
            }
        }
        leafProperty.setValue(
                leafEntity.getEntity(), 
                ContentType.convert(
                        ContentType.Text, 
                        text, 
                        getProperty().getContentType()
                )
        );
    }
    
}
