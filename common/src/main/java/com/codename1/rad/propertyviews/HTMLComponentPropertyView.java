/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.propertyviews;

import com.codename1.l10n.ParseException;
import com.codename1.rad.annotations.Inject;
import com.codename1.rad.ui.PropertyView;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.models.ContentType;

import com.codename1.rad.models.PropertyChangeEvent;
import com.codename1.rad.models.TextFormatterAttribute;
import com.codename1.ui.TextArea;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.DataChangedListener;
import com.codename1.ui.html.HTMLComponent;
import java.util.Objects;
import com.codename1.rad.models.Entity;

/**
 * A view for binding to {@link TextArea} components.
 * @author shannah
 */
public class HTMLComponentPropertyView extends PropertyView<HTMLComponent> {
    
    

    private ActionListener<PropertyChangeEvent> pcl = pce->{
        update();
    };
    
    
    
    public HTMLComponentPropertyView(@Inject HTMLComponent component, @Inject Entity entity, @Inject FieldNode field) {
        super(component, entity, field);
    }

    @Override
    protected void bindImpl() {
        getEntity().getEntity().addPropertyChangeListener(getProperty(), pcl);
    }

    @Override
    protected void unbindImpl() {
        getEntity().getEntity().removePropertyChangeListener(getProperty(), pcl);
    }
    
    private String oldVal;
    @Override
    public void update() {
        super.update();
        //String oldVal = getComponent().get
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
            getComponent().setBodyText(newVal);
            oldVal = newVal;
        }
    }
    
    public void commit() {
        
    }
    
}
