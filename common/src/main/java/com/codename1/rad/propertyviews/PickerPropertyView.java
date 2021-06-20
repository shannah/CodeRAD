/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.propertyviews;

import com.codename1.io.Util;
import com.codename1.rad.annotations.Inject;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.PropertyChangeEvent;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.ui.PropertyView;
import com.codename1.ui.ComboBox;
import com.codename1.ui.Display;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.spinner.Picker;

import java.util.Date;
import java.util.Objects;

/**
 * View for binding to {@link ComboBox} components.
 * @author shannah
 */
public class PickerPropertyView extends PropertyView<Picker> {



    private ActionListener<PropertyChangeEvent> pcl = pce->{
        update();
    };

    private ActionListener dcl = evt ->{
        commit();
    };


    public PickerPropertyView(@Inject Picker component, @Inject Entity entity, @Inject FieldNode field) {
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
        Object oldVal = getComponent().getValue();
        Object newVal = getProperty().getValue(getEntity().getEntity());
        if (!Objects.equals(oldVal, newVal)) {
            switch(getComponent().getType()) {
                case Display.PICKER_TYPE_DATE:
                case Display.PICKER_TYPE_DATE_AND_TIME:
                    getComponent().setDate((Date)newVal);
                    break;
                case Display.PICKER_TYPE_STRINGS:
                    getComponent().setSelectedString((String)newVal);
                    break;
                case Display.PICKER_TYPE_TIME:
                    getComponent().setTime(newVal == null ? 0 : (int)newVal);
                    break;
                case Display.PICKER_TYPE_DURATION:
                case Display.PICKER_TYPE_DURATION_HOURS:
                case Display.PICKER_TYPE_DURATION_MINUTES:

                    getComponent().setDuration(newVal == null ? 0L : (long)newVal);
                    break;
            }
        }
    }
    
    @Override
    public void commit() {



        getProperty().setValue(
                getEntity().getEntity(), 
                getComponent().getValue()
        );
    }
    
}
