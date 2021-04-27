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
package com.codename1.rad.controllers;


import com.codename1.rad.models.Property.Label;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.ui.EntityEditor;
import com.codename1.rad.ui.EntityEditor.DescriptionStyle;
import com.codename1.rad.ui.EntityEditor.DescriptionStyleAttribute;
import com.codename1.rad.ui.EntityEditor.LabelStyle;
import com.codename1.rad.ui.EntityEditor.LabelStyleAttribute;
import com.codename1.rad.ui.UI;
import com.codename1.ui.Button;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.rad.models.Entity;

/**
 *
 * @author shannah
 */
public class FieldEditorFormController extends FormController {
    private FieldNode field;
    private Entity entity;
    
    public FieldEditorFormController(Controller parent, Entity entity, FieldNode fld) {
        super(parent);
        this.entity = entity;
        fld = (FieldNode) fld.createProxy(fld.getParent());
        this.field = fld;
    }
    protected void onStartController() {
        super.onStartController();
        Form f = new Form(new BorderLayout());
        f.getToolbar().hideToolbar();
        Container titleBar = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER_ABSOLUTE));
        titleBar.setSafeArea(true);
        titleBar.setUIID("TitleArea");
        
        if (hasBackCommand()) {
            Button back = new Button();
            FontImage.setIcon(back, FontImage.MATERIAL_ARROW_BACK_IOS, -1);
            titleBar.add(BorderLayout.WEST, back);
            back.addActionListener(evt->{
                evt.consume();
                ActionSupport.dispatchEvent(new FormController.FormBackEvent(back));
            });
            
        }
        
        AppSectionController sectionCtl = getSectionController();
        if (sectionCtl != null) {
            Button done = new Button("Done");
            done.addActionListener(evt->{
                evt.consume();
                ActionSupport.dispatchEvent(new AppSectionController.ExitSectionEvent(done));
            });
            titleBar.add(BorderLayout.EAST, done);
        }
        
        Label l = field.getLabel(entity.getEntity().getEntityType());
        if (l != null) {
            titleBar.add(BorderLayout.CENTER, new com.codename1.ui.Label(l.getValue(entity.getEntity()), "Title"));
        } 
        f.add(BorderLayout.NORTH, titleBar);
        
        field.setAttributes(
                new LabelStyleAttribute(LabelStyle.None),
                new DescriptionStyleAttribute(DescriptionStyle.SpanLabel)
                
        );
        
        UI ui = new UI() {{
            form(
                columns(1),
                field,
                editable(true)
            );
        }};
        EntityEditor editor = new EntityEditor(entity, ui);
        editor.setScrollableY(true);
        f.add(BorderLayout.CENTER, editor);
        setView(f);
        
    }
    
}
