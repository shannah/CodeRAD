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
import com.codename1.rad.attributes.PropertySelectorAttribute;
import com.codename1.rad.attributes.UIID;
import com.codename1.rad.attributes.WidgetType;
import com.codename1.rad.controllers.FieldEditorFormController;
import com.codename1.rad.controllers.ViewController;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.Property;
import com.codename1.rad.models.Property.Description;
import com.codename1.rad.models.PropertySelector;
import com.codename1.rad.models.Tag;
import com.codename1.rad.models.Tags;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.nodes.SectionNode;
import com.codename1.rad.nodes.ViewNode;
import com.codename1.rad.propertyviews.LabelPropertyView;
import com.codename1.ui.Button;
import com.codename1.ui.Component;
import static com.codename1.ui.ComponentSelector.$;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.Label;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;

/**
 *
 * @author shannah
 */
public class SettingsForm extends AbstractEntityView implements WidgetTypes {
    private ViewNode node;
    public SettingsForm(Entity entity, ViewNode node) {
        super(entity);
        this.node = node;
        node.setAttributesIfNotExists(UI.viewFactory(new SettingsFormActionViewFactory()));
        String uiidPrefix = node.getUIIDPrefix("");
        String uiid = node.getUIID("SettingsForm");
        
        setUIID(uiidPrefix + uiid);
        $(this).addTags("SettingsForm");
        setLayout(BoxLayout.y());
        setScrollableY(true);
        NodeList l = node.getChildNodes(SectionNode.class);
        for (Node n : l) {
            SectionNode sn = (SectionNode)n;
            add(new SettingsFormSection(entity, sn));
        }
        
    }

    @Override
    public void update() {
        
    }

    @Override
    public void commit() {
        
    }

    @Override
    public Node getViewNode() {
        return node;
    }
    
    public static class SettingsFormSection extends Container {
        public SettingsFormSection(Entity entity, SectionNode section) {
            super(BoxLayout.y());
            String uiidPrefix = section.getUIIDPrefix("");
            String uiid = section.getUIID("SettingsFormSection");
            
            setUIID(uiidPrefix + uiid);
            $(this).addTags("SettingsFormSection", "left-edge");
            com.codename1.rad.models.Property.Label l = section.getLabel();
            if (l != null) {
                Label lbl = new Label(l.getValue(entity), uiidPrefix+uiid+"Label");
                $(lbl).addTags("left-inset");
                add(lbl);
            }
            for (Node n : section.getChildNodes(ActionNode.class)) {
                ActionNode an = (ActionNode)n;
                if (!an.isEnabled(entity)) {
                    continue;
                }
                add(an.createView(entity));
            }
        }
    }
    
    public static class SettingsFormActionView extends Container {
        
        public SettingsFormActionView(Entity entity, ActionNode action) {
            super(new BorderLayout());
            Container cnt = this;
            String uiidPrefix = action.getUIIDPrefix("");
            String uiid = action.getUIID("SettingsFormAction");
            cnt.setUIID(uiidPrefix+uiid);
            $(cnt).addTags("SettingsFormAction", "left-edge");
            String labelText = action.getLabelText(entity);
            
            FieldNode fieldNode = (FieldNode)action.findAttribute(FieldNode.class);
            if (fieldNode == null) {
                fieldNode = new FieldNode();
                fieldNode.setParent(action);
                
            } 
            
            WidgetType wtype = fieldNode.getWidgetType(entity.getEntityType());
            
            
            PropertySelector psel = fieldNode.getPropertySelector(entity);
            
            String value = "";
            Component valueView = null;
            boolean expandable = true;
            if (psel != null ) {
                
                if (wtype == RADIO || wtype == CHECKBOX || wtype == SWITCH) {
                    valueView = fieldNode.getViewFactory().createPropertyView(entity, fieldNode);
                    expandable = valueView == null;
                    if (valueView == null) {
                        valueView = new Label("", uiidPrefix+uiid+"Value");
                    }
                } else {

                    value = psel.getText("");
                    Label valueLabel = new Label(value, uiidPrefix+uiid+"Value");
                    valueView = new LabelPropertyView(valueLabel, entity, fieldNode);
                }
            } else {
                PropertySelectorAttribute pselAtt = (PropertySelectorAttribute)fieldNode.findInheritedAttribute(PropertySelectorAttribute.class);
                if (pselAtt != null) {
                    valueView = new Label(pselAtt.getValue(entity).getText(""), uiidPrefix+uiid+"Value");
                }
            }
            
            SpanLabel description = null;
            
            Description fieldDesc = fieldNode.getDescription(entity.getEntityType());
            Description actionDesc = action.getDescription();
            if (!expandable && actionDesc == null && fieldDesc != null) {
                actionDesc = fieldDesc;
            }
            
            if (actionDesc != null) {
                description = new SpanLabel(actionDesc.getValue());
                $(description).addTags("left-inset");
                description.setUIID(uiidPrefix+uiid+"Description");
                description.setTextUIID(uiidPrefix+uiid+"DescriptionText");
                Container wrap = new Container(BoxLayout.y());
                wrap.stripMarginAndPadding();
                
                cnt = new Container(new BorderLayout());
                cnt.stripMarginAndPadding();
                
                wrap.add(cnt);
                wrap.add(description);
                
                add(BorderLayout.CENTER, wrap);
                
            }
            Label lbl = new Label(labelText, uiidPrefix+uiid+"Label");
            $(lbl).addTags("left-inset");
            cnt.add(BorderLayout.CENTER, lbl);
            if (expandable) {
                Button button = new Button("", uiidPrefix+uiid+"ArrowButton");
                FontImage.setIcon(button, FontImage.MATERIAL_ARROW_FORWARD_IOS, -1);

                



                cnt.add(BorderLayout.EAST, BorderLayout.centerEastWest(valueView, button, null).stripMarginAndPadding());
                cnt.setLeadComponent(button);
                
                FieldNode fFieldNode = fieldNode;
                button.addActionListener(evt->{
                    evt.consume();
                    ActionEvent ae = action.fireEvent(entity, button);
                    if (ae.isConsumed()) {
                        return;
                    }
                    FieldNode proxy = (FieldNode)fFieldNode.createProxy(fFieldNode.getParent());
                    if (wtype == RADIO_LIST || wtype == CHECKBOX_LIST || wtype == SWITCH_LIST) {
                        proxy.setAttributesIfNotExists(new UIID("SettingsFormButtonList"));
                    }
                    if (wtype != null) {
                        FieldEditorFormController ctl = new FieldEditorFormController(
                                ViewController.getViewController(button), 
                                entity, 
                                proxy
                        );
                        ctl.getView().show();
                    }
                });
            } else {
                cnt.add(BorderLayout.EAST, valueView);
            }
            
            
        }
    }
    
    
    
    public static class SettingsFormActionViewFactory implements ActionViewFactory {

        @Override
        public Component createActionView(Entity entity, ActionNode action) {
            return new SettingsFormActionView(entity, action);
        }
        
    }
    
}
