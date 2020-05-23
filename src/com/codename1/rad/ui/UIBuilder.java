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

import com.codename1.components.SpanLabel;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.Property.Label;
import com.codename1.rad.models.Tag;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.nodes.ViewNode;
import com.codename1.rad.propertyviews.HTMLComponentPropertyView;
import com.codename1.rad.propertyviews.LabelPropertyView;
import com.codename1.rad.propertyviews.SpanLabelPropertyView;
import com.codename1.rad.propertyviews.TextAreaPropertyView;
import com.codename1.rad.propertyviews.TextFieldPropertyView;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.html.HTMLComponent;

/**
 *
 * @author shannah
 */
public class UIBuilder {
    private Entity entity;
    private ViewNode parentNode;
    
    public UIBuilder(Entity entity, ViewNode parentNode) {
        this.entity = entity;
        this.parentNode = parentNode;
    }
    
    public LabelPropertyView label(Tag... tags) {
        FieldNode fn = new FieldNode(UI.tags(tags));
        fn.setParent(parentNode);
        return new LabelPropertyView(new com.codename1.ui.Label(), entity, fn);
    }
    
    
    public SpanLabelPropertyView spanLabel(Tag... tags) {
        FieldNode fn = new FieldNode(UI.tags(tags));
        fn.setParent(parentNode);
        return new SpanLabelPropertyView(new SpanLabel(), entity, fn);
    }
    
    public TextFieldPropertyView textField(Tag... tags) {
        FieldNode fn = new FieldNode(UI.tags(tags));
        fn.setParent(parentNode);
        return new TextFieldPropertyView(new TextField(), entity, fn);
    }
    
    public TextAreaPropertyView textArea(Tag... tags) {
        FieldNode fn = new FieldNode(UI.tags(tags));
        fn.setParent(parentNode);
        return new TextAreaPropertyView(new TextArea(), entity, fn);
    }
    
    public HTMLComponentPropertyView htmlComponent(Tag... tags) {
        FieldNode fn = new FieldNode(UI.tags(tags));
        fn.setParent(parentNode);
        return new HTMLComponentPropertyView(new HTMLComponent(), entity, fn);
    }
}
