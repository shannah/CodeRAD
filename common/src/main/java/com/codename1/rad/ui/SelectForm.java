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
package com.codename1.rad.ui;

import com.codename1.rad.attributes.WidgetType;

import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.nodes.OptionsNode;
import com.codename1.ui.Container;
import com.codename1.ui.TextField;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.rad.models.Entity;

/**
 *
 * @author shannah
 */
public class SelectForm extends Container {
    private TextField searchField = new TextField();
    private Container optionsWrapper = new Container(BoxLayout.y());
    private FieldNode field;
    private Entity entity;
    
    public SelectForm(Entity entity, FieldNode field) {
        super(new BorderLayout());
        this.entity = entity;
        this.field = field;
        
        OptionsNode options = field.getOptions();
        PropertyView pv = field.getViewFactory().createPropertyView(entity, field);
        add(BorderLayout.NORTH, searchField);
        add(BorderLayout.CENTER, pv);
        
    }
    
}
