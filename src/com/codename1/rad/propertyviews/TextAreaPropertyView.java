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
import com.codename1.rad.models.PropertyChangeEvent;
import com.codename1.ui.TextArea;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.DataChangedListener;
import java.util.Objects;

/**
 * A view for binding to {@link TextArea} components.
 * @author shannah
 */
public class TextAreaPropertyView extends PropertyView<TextArea> {
    
    

    private ActionListener<PropertyChangeEvent> pcl = pce->{
        update();
    };
    
    private DataChangedListener dcl = (type, index) ->{
        commit();
    };
    
    
    public TextAreaPropertyView(TextArea component, Entity entity, FieldNode field) {
        super(component, entity, field);
    }

    @Override
    public void bind() {
        getEntity().addPropertyChangeListener(getProperty(), pcl);
        getComponent().addDataChangedListener(dcl);
    }

    @Override
    public void unbind() {
        getComponent().removeDataChangedListener(dcl);
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
