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

import com.codename1.components.Switch;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.models.Property;
import com.codename1.rad.models.PropertyChangeEvent;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.ui.PropertyView;
import com.codename1.ui.events.ActionListener;
import com.codename1.rad.models.Entity;

/**
 *
 * @author shannah
 */
public class SwitchPropertyView extends PropertyView<Switch> {

    private ActionListener<PropertyChangeEvent> pcl = pce->{
        update();
    };
    
    private ActionListener al = ae -> {
        commit();
    };
    
    public SwitchPropertyView(@Inject Switch sw, @Inject Entity e, @Inject FieldNode field) {
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
        getComponent().removeChangeListener(al);
    }

    @Override
    public void update() {
        super.update();
        if (getPropertySelector().isFalsey() != getComponent().isOff()) {
            getComponent().setValue(!getPropertySelector().isFalsey());
        }
    }

    @Override
    public void commit() {
        if (getPropertySelector().isFalsey() != getComponent().isOff()) {
            
            Entity e = getPropertySelector().getLeafEntity();
            Property p = getPropertySelector().getLeafProperty();
            e.getEntity().setBoolean(p, getComponent().isOn());
            
        }
        
    }
    
}
