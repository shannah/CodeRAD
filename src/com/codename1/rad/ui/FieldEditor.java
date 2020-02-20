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
import com.codename1.ui.Container;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;

/**
 *
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
        Property.Description description = field.getDescription(entity.getEntityType());
        Container above = new Container(new FlowLayout());
        Container wrap = new Container(new BorderLayout());
        if (lbl != null) {
            above.add(new SpanLabel(lbl.getValue()));
            if (description != null) {
                above.add(new HelpButton(description.getValue()));
            }
        } else {
            if (description != null) {
                wrap.add(BorderLayout.EAST, new HelpButton(description.getValue()));
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
