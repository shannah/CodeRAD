/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.propertyviews;

import com.codename1.rad.ui.PropertyView;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.models.ContentType;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.Property;
import com.codename1.rad.models.Property.Label;
import com.codename1.rad.models.PropertyChangeEvent;
import com.codename1.ui.TextField;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.DataChangedListener;
import java.util.Objects;

/**
 *
 * @author shannah
 */
public class SpanLabelPropertyView extends PropertyView<com.codename1.components.SpanLabel> {
    
    

    private ActionListener<PropertyChangeEvent> pcl = pce->{
        update();
    };
    
    private DataChangedListener dcl = (type, index) ->{
        commit();
    };
    
    
    public SpanLabelPropertyView(com.codename1.components.SpanLabel component, Entity entity, FieldNode field) {
        super(component, entity, field);
    }

    @Override
    public void bind() {
        getEntity().addPropertyChangeListener(getProperty(), pcl);
        
    }

    @Override
    public void unbind() {
        
        getEntity().removePropertyChangeListener(getProperty(), pcl);
    }
    
    @Override
    public void update() {
        String oldVal = getComponent().getText();
        String newVal = ContentType.convert(
                getProperty().getContentType(), 
                getProperty().getValue(getEntity()),
                ContentType.Text
        );
        if (!Objects.equals(oldVal, newVal)) {
            getComponent().setText(newVal);
        }
    }
    
    @Override
    public void commit() {
        getProperty().setValue(
                getEntity(), 
                ContentType.convert(
                        ContentType.Text, 
                        getComponent().getText(), 
                        getProperty().getContentType()
                )
        );
    }
    
}
