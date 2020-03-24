/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.attributes.WidgetType;
import ca.weblite.shared.components.HelpButton;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.Property;
import com.codename1.components.SpanLabel;
import com.codename1.rad.ui.EntityEditor.DescriptionStyle;
import com.codename1.rad.ui.EntityEditor.LabelStyle;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;

/**
 * An editor for a {@link FieldNode}.  Used internally by {@link EntityEditor} for rendering its fields.
 * @author shannah
 */
public class FieldEditor extends Container {
    private Entity entity;
    private FieldNode field;
    private PropertyView component;
    private boolean built;

    public FieldEditor(Entity entity, FieldNode field) {
        this.entity = entity;
        this.field = field;
    }

    @Override
    protected void initComponent() {
        build();
        super.initComponent();
    }

    
    void build() {
        if (built) {
            return;
        }
        built = true;
        
        setLayout(BoxLayout.y());
        Property.Label lbl = field.getLabel(entity.getEntityType());
        LabelStyle labelStyle = field.getLabelStyle(LabelStyle.Default);
        
        Property.Description description = field.getDescription(entity.getEntityType());
        DescriptionStyle descriptionStyle = field.getDescriptionStyle(DescriptionStyle.HelpButton);
        Container above = new Container(new FlowLayout());
        above.stripMarginAndPadding();
        
        Container wrap = new Container(new BorderLayout());
        wrap.stripMarginAndPadding();
        if (lbl != null && labelStyle != LabelStyle.None) {
            above.add(new SpanLabel(lbl.getValue()));
            if (description != null) {
                switch (descriptionStyle) {
                    case HelpButton:
                        above.add(new HelpButton(description.getValue()));
                        break;
                    case SpanLabel:
                        SpanLabel descriptionSpanLabel = new SpanLabel(description.getValue(), "FieldDescriptionText");
                        above.add(descriptionSpanLabel);
                        break;
                        
                }
            }
        } else {
            if (description != null) {
                Component descCmp = null;
                switch (descriptionStyle) {
                    case HelpButton:
                        descCmp = new HelpButton(description.getValue());
                        wrap.add(BorderLayout.EAST, descCmp);
                        break;
                    case SpanLabel:
                        descCmp = new SpanLabel(description.getValue(), "FieldDescriptionText");
                        above.add(descCmp);
                        break;
                        
                }
                if (descCmp != null) {
                    
                }
            }
        }
        
        WidgetType widgetType = field.getWidgetType(entity.getEntityType());
        if (widgetType != null) {
            
            component = field.getViewFactory().createPropertyView(entity, field);
            wrap.add(BorderLayout.CENTER, component);
        } else {
            throw new IllegalStateException("Field "+field+" has not widget type");
        }
        
        
        Container topBar = new Container(BoxLayout.y());
        NodeUtilFunctions.buildTopActionsBar(field, topBar, entity);
        if (above.getComponentCount() > 0) {
            if (topBar.getComponentCount() > 0) {
                above = BorderLayout.centerEastWest(above, topBar, null);
            }
            add(above);
        } else {
            if (topBar.getComponentCount() > 0) {
                add(topBar);
            }
        }
        add(wrap);
        NodeUtilFunctions.buildBottomActionsBar(field, this, entity);
        
    }
}
