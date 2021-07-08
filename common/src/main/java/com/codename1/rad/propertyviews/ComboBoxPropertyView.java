/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.propertyviews;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.ui.PropertyView;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.models.ContentType;

import com.codename1.rad.models.Property;
import com.codename1.rad.models.PropertyChangeEvent;
import com.codename1.ui.CN;
import com.codename1.ui.ComboBox;
import com.codename1.ui.TextField;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.DataChangedListener;
import java.util.Objects;
import com.codename1.rad.models.Entity;
import com.codename1.ui.events.SelectionListener;

/**
 * View for binding to {@link ComboBox} components.
 * @author shannah
 */
public class ComboBoxPropertyView extends PropertyView<ComboBox> {
    
    

    private ActionListener<PropertyChangeEvent> pcl = pce->{
        update();
    };
    
    private ActionListener dcl = evt ->{
        commit();
    };
    
    
    public ComboBoxPropertyView(@Inject ComboBox component, @Inject Entity entity, @Inject FieldNode field) {
        super(component, entity, field);
    }

    @Override
    protected void bindImpl() {
        getEntity().getEntity().addPropertyChangeListener(getProperty(), pcl);
        getComponent().addActionListener(dcl);
    }

    @Override
    protected void unbindImpl() {
        getComponent().removeActionListener(dcl);
        getEntity().getEntity().removePropertyChangeListener(getProperty(), pcl);
    }
    
    @Override
    public void update() {
        super.update();
        if (getComponent().isShowingPopupDialog()) {
            // If we are just returning from showing the popup dialog
            // an update would overwrite whatever changes the user made in
            // the dialog so we short circuit in this case only.
            return;
        }
        Object oldVal = getComponent().getSelectedItem();
        Object newVal = getPropertySelector().get(null);
        if (!Objects.equals(oldVal, newVal)) {
            getComponent().setSelectedItem(newVal);

        }
    }
    
    @Override
    public void commit() {
        getPropertySelector().set(getComponent().getSelectedItem());

    }
    
}
