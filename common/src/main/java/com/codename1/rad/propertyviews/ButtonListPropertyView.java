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
import com.codename1.rad.annotations.Inject;
import com.codename1.rad.attributes.Columns;
import com.codename1.rad.models.Attribute;

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

import java.util.*;

import com.codename1.rad.models.Entity;

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
        
        String uiid = field.getUIID(null);
        if (uiid != null) {
            bl.setUIID(uiid);
            bl.setCellUIID(uiid+"Cell");
        }
        return bl;
    }
    
    private final ActionListener<PropertyChangeEvent> pcl = evt -> {
        update();
    };
    
    private final SelectionListener sl = (oldIndex, newIndex) -> {
        commit();
    };
    

    public ButtonListPropertyView(@Inject ButtonList buttonList, @Inject Entity entity, @Inject FieldNode field) {
        super(decorateButtonList(field, buttonList), entity, field);
    }
    
    @Override
    protected void bindImpl() {
        getPropertySelector().addPropertyChangeListener(pcl);
        getComponent().getModel().addSelectionListener(sl);
    }

    @Override
    protected void unbindImpl() {
        
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
        super.update();
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
        Object val = p.getValue(e.getEntity());
        
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
                EntityList el = e.getEntity().getEntityList(p);
                
                for (Object obj : el) {
                    selectedObjects.add(obj);
                    if (obj instanceof Entity) {
                        String id = ((Entity)obj).getEntity().getText(Thing.identifier);
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
                            String rowId = rowEnt.getEntity().getText(Thing.identifier);
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
        Object val = p.getValue(e.getEntity());

        ListModel model = getComponent().getModel();
        int len = model.getSize();

        int selectedIndex = model.getSelectedIndex();
        if (p.getContentType().isEntity()) {
            // For entities, we'll allow matching on ID.
            Entity currSelection = e.getEntity().getEntity(p);
            String id = currSelection.getEntity().getText(Thing.identifier);
            if (id != null) {
                for (int i=0; i<len; i++) {
                    Object rowVal = model.getItemAt(i);
                    if (rowVal instanceof Entity) {
                        String rowId = ((Entity)rowVal).getEntity().getText(Thing.identifier);
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
                if (Objects.equals(model.getItemAt(i), e.getEntity().get(p))) {
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
                e.getEntity().getEntityList(p).clear();
                e.getEntity().setChanged(p, true);
            } else if (Collection.class.isAssignableFrom(p.getContentType().getRepresentationClass())){
                ((Collection)e.getEntity().get(p)).clear();
                e.getEntity().setChanged(p, true);
            } else {
                throw new IllegalStateException("Unsupported property content type for property");
            }
        } else {
            ListModel model = getComponent().getModel();
            if (model instanceof MultipleSelectionListModel) {
                MultipleSelectionListModel multiModel = (MultipleSelectionListModel)model;
                int[] selectedIndices = multiModel.getSelectedIndices();
                boolean changed = false;
                Set selectedObjects = new HashSet();
                for (int i=0; i<selectedIndices.length; i++) {
                    selectedObjects.add(multiModel.getItemAt(selectedIndices[i]));
                }
                Set oldSelectedObjects = new HashSet();
                if (Iterable.class.isAssignableFrom(p.getContentType().getRepresentationClass())) {
                    Iterable it = e.getEntity().getAs(p, Iterable.class);
                    if (it != null) {
                        for (Object i : it) {
                            oldSelectedObjects.add(i);
                        }
                    }
                }

                changed = !(oldSelectedObjects.containsAll(selectedObjects) && selectedObjects.containsAll(oldSelectedObjects));
                if (!changed) return;

                if (p.getContentType().isEntityList()) {
                    EntityList el = e.getEntity().getEntityList(p);
                    if (el == null) {
                        el = new EntityList();
                        e.getEntity().set(p, el);
                    }
                    for (Object o : selectedObjects) {
                        if (!(o instanceof Entity)) {
                            throw new IllegalStateException("Cannot add non-entity to entity list for property "+p);
                        }
                        if (!el.contains(o)) {
                            el.add((Entity)o);
                        }
                    }
                    List<Entity> toRemove = new ArrayList<>();
                    for (Object entity : el) {
                        if (!selectedObjects.contains(entity)) {
                            toRemove.add((Entity)entity);
                        }
                    }
                    if (!toRemove.isEmpty()) {
                        for (Entity entity : toRemove) {
                            el.remove(entity);
                        }
                    }
                    e.setChanged(p, true);
                } else if (Collection.class.isAssignableFrom(p.getContentType().getRepresentationClass())) {
                    Collection c = e.getAs(p, Collection.class);
                    if (c == null) {
                        if (p.getContentType().getRepresentationClass().isAssignableFrom(List.class)) {
                            c = new ArrayList();
                            e.getEntity().set(p, c);
                        } else if (p.getContentType().getRepresentationClass().isAssignableFrom(Set.class)) {
                            c = new HashSet();
                            e.getEntity().set(p, c);
                        } else {
                            throw new IllegalStateException("Cannot set item in collection of property "+p+" because the collection is null.");
                        }
                    }
                    for (Object o : selectedObjects) {
                        if (!c.contains(o)) {
                            c.add(o);
                        }
                    }
                    List toRemove = new ArrayList<>();
                    for (Object entity : c) {
                        if (!selectedObjects.contains(entity)) {
                            toRemove.add(entity);
                        }
                    }
                    if (!toRemove.isEmpty()) {
                        for (Object entity : toRemove) {
                            c.remove(entity);
                        }
                    }
                    e.setChanged(p, true);

                }
            } else {
                Object selectedObject = getComponent().getModel().getItemAt(selectedIndex);
                if (p.getContentType().isEntityList()) {
                    if (!(selectedObject instanceof Entity)) {
                        throw new IllegalStateException("Attempt to add non-entity "+selectedObject+" to property of type entity list");
                    }
                    EntityList el = e.getEntity().getEntityList(p);
                    if (el == null) {
                        el = new EntityList();
                        e.getEntity().set(p, el);
                    }

                    if (el.size() == 1 && el.contains(selectedObject)) {
                        return;
                    }
                    if (el.size() != 0) {
                        el.clear();
                    }
                    el.add((Entity)selectedObject);
                    e.setChanged(p, true);
                } else if (Collection.class.isAssignableFrom(p.getContentType().getRepresentationClass())) {
                    Collection c = (Collection)e.getEntity().getAs(p, Collection.class);
                    if (c == null) {
                        if (p.getContentType().getRepresentationClass().isAssignableFrom(List.class)) {
                            c = new ArrayList();
                            e.getEntity().set(p, c);
                        } else if (p.getContentType().getRepresentationClass().isAssignableFrom(Set.class)) {
                            c = new HashSet();
                            e.getEntity().set(p, c);
                        } else {
                            throw new IllegalStateException("Cannot set item in collection of property "+p+" because the collection is null.");
                        }
                    }
                    if (c.size() == 1 && c.contains(selectedObject)) {
                        return;
                    }
                    if (!c.isEmpty()) {
                        c.clear();
                    }
                    c.add(selectedObject);
                    e.setChanged(p, true);

                }
                e.getEntity().set(p, selectedObject);
            }
        }
    }
    
    private void commitSingleSelectionModel() {
        Entity e = getPropertySelector().getLeafEntity();
        Property p = getPropertySelector().getLeafProperty();
        int selectedIndex = getComponent().getModel().getSelectedIndex();
        if (selectedIndex < 0) {
            e.getEntity().set(p, null);
        } else {
            Object selectedObject = getComponent().getModel().getItemAt(selectedIndex);
            e.getEntity().set(p, selectedObject);
        }
        
    }
    
}
