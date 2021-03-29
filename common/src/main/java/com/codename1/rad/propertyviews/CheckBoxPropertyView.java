/*
 * Copyright 2020 shannah.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codename1.rad.propertyviews;

import com.codename1.rad.models.Entity;
import com.codename1.rad.models.Property;
import com.codename1.rad.models.PropertyChangeEvent;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.ui.PropertyView;
import com.codename1.ui.CheckBox;
import com.codename1.ui.events.ActionListener;

/**
 *
 * @author shannah
 */
public class CheckBoxPropertyView extends PropertyView<CheckBox> {

    private ActionListener<PropertyChangeEvent> pcl = pce->{
        update();
    };
    
    private ActionListener al = ae -> {
        commit();
    };
    
    public CheckBoxPropertyView(CheckBox sw, Entity e, FieldNode field) {
        super(sw, e, field);
    }
    
    @Override
    protected void bindImpl() {
        getPropertySelector().addPropertyChangeListener(pcl);
        getComponent().addChangeListener(al);
    }

    @Override
    protected void unbindImpl() {
        getPropertySelector().removePropertyChangeListener(pcl);
        getComponent().removeChangeListeners(al);
    }

    @Override
    public void update() {
        super.update();
        if (getPropertySelector().isFalsey() == getComponent().isSelected()) {
            getComponent().setSelected(!getPropertySelector().isFalsey());
        }
    }

    @Override
    public void commit() {
        if (getPropertySelector().isFalsey() == getComponent().isSelected()) {
            
            Entity e = getPropertySelector().getLeafEntity();
            Property p = getPropertySelector().getLeafProperty();
            e.setBoolean(p, getComponent().isSelected());
            
        }
        
    }
}
