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

import com.codename1.components.ButtonList;
import com.codename1.rad.attributes.Columns;
import com.codename1.rad.models.Attribute;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.EntityList;
import com.codename1.rad.models.Property;
import com.codename1.rad.models.PropertyChangeEvent;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.schemas.Thing;
import com.codename1.rad.ui.PropertyView;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.SelectionListener;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.list.DefaultListModel;
import com.codename1.ui.list.ListModel;
import com.codename1.ui.list.MultipleSelectionListModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author shannah
 */
public class ButtonListPropertyView extends PropertyView<ButtonList> {
    
    public static enum ButtonListLayout {
        Y,
        X,
        Flow,
        Grid
    };
    
    
    
    public static class ButtonListLayoutAttribute extends Attribute<ButtonListLayout> {
        public ButtonListLayoutAttribute(ButtonListLayout l) {
            super(l);
        }
    }
    
    private static ButtonList decorateButtonList(FieldNode field, ButtonList bl) {
        
        ButtonListLayoutAttribute att = (ButtonListLayoutAttribute)field.findInheritedAttribute(ButtonListLayoutAttribute.class);
        if (att != null) {
            switch (att.getValue()) {
                case Flow:
                    bl.setLayout(new FlowLayout());
                    break;
                case Y:
                    bl.setLayout(BoxLayout.y());
                    break;
                case X:
                    bl.setLayout(BoxLayout.x());
                    break;
                case Grid:
                    Columns cols = (Columns)field.findAttribute(Columns.class);
                    int numCols = 2;
                    if (cols != null) {
                        numCols = cols.getValue();
                    }
                    bl.setLayout(new GridLayout(numCols));
                    break;
                    
            }
        }
        return bl;
    }
    
    private final ActionListener<PropertyChangeEvent> pcl = evt -> {
        update();
    };
    
    private final SelectionListener sl = (oldIndex, newIndex) -> {
        commit();
    };
    

    public ButtonListPropertyView(ButtonList buttonList, Entity entity, FieldNode field) {
        super(decorateButtonList(field, buttonList), entity, field);
    }
    
    @Override
    public void bind() {
        getPropertySelector().addPropertyChangeListener(pcl);
        getComponent().getModel().addSelectionListener(sl);
    }

    @Override
    public void unbind() {
        
        getComponent().getModel().removeSelectionListener(sl);
        getPropertySelector().removePropertyChangeListener(pcl);
    }

    private boolean isMultiSelectionListModel() {
        ListModel model = getComponent().getModel();
        if (!(model instanceof MultipleSelectionListModel)) {
            return false;
        }
        MultipleSelectionListModel multiModel = (MultipleSelectionListModel)model;
        if (multiModel instanceof DefaultListModel) {
            DefaultListModel dlm = (DefaultListModel)multiModel;
            return dlm.isMultiSelectionMode();
        }
        return true;
    }
    
    @Override
    public void update() {
        if (getPropertySelector().isEmpty()) {
            if (getComponent().getModel().getSelectedIndex() != -1) {
                getComponent().getModel().setSelectedIndex(-1);
            }
            return;
        }
        
        if (isMultiSelectionListModel()) {
            updateMultiSelectionModel();
        } else {
            updateSingleSelectionModel();
        }
        
        
    }
    
    private void updateMultiSelectionModel() {
        MultipleSelectionListModel model = getComponent().getMultiListModel();
        Entity e = getPropertySelector().getLeafEntity();
        Property p = getPropertySelector().getLeafProperty();
        Object val = p.getValue(e);
        
        int len = model.getSize();

        
        if (getPropertySelector().isEmpty()) {
            if (model.getSelectedIndices().length > 0) {
                model.setSelectedIndices(new int[0]);
            }
            
        } else {
            List<Integer> selectedIndices = new ArrayList<>();
            if (p.getContentType().isEntityList()) {
                // Property contains an entity list
                List selectedObjects = new ArrayList();
                List<String> selectedIds = new ArrayList<>();
                boolean useIds = true;
                EntityList el = e.getEntityList(p);
                
                for (Object obj : el) {
                    selectedObjects.add(obj);
                    if (obj instanceof Entity) {
                        String id = ((Entity)obj).getText(Thing.identifier);
                        if (id == null) {
                            useIds = false;
                            break;
                        }
                        
                        selectedIds.add(id);
                    } else {
                        useIds = false;
                    }
                }
                
                if (useIds) {
                    // We will useIds to match rows in options list with rows in entity list
                    for (int i=0; i<len; i++) {
                        Object rowVal = model.getItemAt(i);
                        if (rowVal instanceof Entity) {
                            Entity rowEnt = (Entity)rowVal;
                            String rowId = rowEnt.getText(Thing.identifier);
                            if (rowId == null) {
                                throw new IllegalStateException("Attempt to use identifiers for matching items in ButtonListPropertyView, but row item "+rowEnt+" has no identifier.  Property: "+p+" in entity "+e);
                            }
                            if (selectedIds.contains(rowId)) {
                                selectedIndices.add(i);
                            }
                        } else {
                            throw new IllegalStateException("Options for field should all be entities. Property "+p+" entity "+e);
                        }
                    }
                } else {
                    // Not using IDS.  We will use direct matching.
                    for (int i=0; i<len; i++) {
                        if (selectedObjects.contains(model.getItemAt(i))) {
                            selectedIndices.add(i);
                        }
                    }
                }
            } else if (Collection.class.isAssignableFrom(p.getContentType().getRepresentationClass())){
                // It's a collection
                for (int i=0; i<len; i++) {
                    if (((Collection)val).contains(model.getItemAt(i))) {
                        selectedIndices.add(i);
                    }
                }       
            } else {
                throw new IllegalStateException("Property "+p+" must contain either EntityList or Collection in order to be editable by ButtonListPropertyView with a multi-selection options model.");
            }
            java.util.Collections.sort(selectedIndices, (i1, i2)-> {
                return i1-i2;
            });
            List<Integer> existingSelectedIndices = new ArrayList<>();
            for (int index : model.getSelectedIndices()) {
                existingSelectedIndices.add(index);
            }
            java.util.Collections.sort(existingSelectedIndices, (i1, i2)->{
                return i1-i2;
            });

            if (!Objects.deepEquals(selectedIndices, existingSelectedIndices)) {
                int size0 = selectedIndices.size();
                int[] selectedIndicesArr = new int[size0];
                for (int i=0; i<size0; i++) {
                    selectedIndicesArr[i] = selectedIndices.get(i);
                }
                model.setSelectedIndices(selectedIndicesArr);
            }
                
                
            
        }
    }
    
    private void updateSingleSelectionModel() {
        Entity e = getPropertySelector().getLeafEntity();
        Property p = getPropertySelector().getLeafProperty();
        Object val = p.getValue(e);

        ListModel model = getComponent().getModel();
        int len = model.getSize();

        int selectedIndex = model.getSelectedIndex();
        if (p.getContentType().isEntity()) {
            // For entities, we'll allow matching on ID.
            Entity currSelection = e.getEntity(p);
            String id = currSelection.getText(Thing.identifier);
            if (id != null) {
                for (int i=0; i<len; i++) {
                    Object rowVal = model.getItemAt(i);
                    if (rowVal instanceof Entity) {
                        String rowId = ((Entity)rowVal).getText(Thing.identifier);
                        if (Objects.equals(rowId, id)) {
                            if (i != selectedIndex) {
                                model.setSelectedIndex(i);
                            }
                            break;
                        }
                    } else {
                        if (Objects.equals(id, String.valueOf(rowVal))) {
                            if (i != selectedIndex) {
                                model.setSelectedIndex(i);
                            }
                            break;
                        }
                    }
                }
            } else {
                for (int i=0; i<len; i++) {
                    if (Objects.equals(e, model.getItemAt(i))) {
                        if (i != selectedIndex) {
                            model.setSelectedIndex(i);
                        }
                        break;
                    }

                }
            }

        } else {
            for (int i=0; i<len; i++) {
                if (Objects.equals(model.getItemAt(i), e.get(p))) {
                    if (selectedIndex != i) {
                        model.setSelectedIndex(i);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void commit() {
        if (isMultiSelectionListModel()) {
            commitMultiSelectionModel();
        } else {
            commitSingleSelectionModel();
        }
    }
    
    private void commitMultiSelectionModel() {
        Entity e = getPropertySelector().getLeafEntity();
        Property p = getPropertySelector().getLeafProperty();
        int selectedIndex = getComponent().getModel().getSelectedIndex();
        if (selectedIndex < 0) {
            if (p.getContentType().isEntityList()) {
                e.getEntityList(p).clear();
                e.setChanged(p, true);
            } else if (Collection.class.isAssignableFrom(p.getContentType().getRepresentationClass())){
                ((Collection)e.get(p)).clear();
                e.setChanged(p, true);
            } else {
                throw new IllegalStateException("Unsupported property content type for property");
            }
        } else {
            Object selectedObject = getComponent().getModel().getItemAt(selectedIndex);
            e.set(p, selectedObject);
        }
    }
    
    private void commitSingleSelectionModel() {
        Entity e = getPropertySelector().getLeafEntity();
        Property p = getPropertySelector().getLeafProperty();
        int selectedIndex = getComponent().getModel().getSelectedIndex();
        if (selectedIndex < 0) {
            e.set(p, null);
        } else {
            Object selectedObject = getComponent().getModel().getItemAt(selectedIndex);
            e.set(p, selectedObject);
        }
        
    }
    
}
