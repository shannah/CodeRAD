/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui.table;

import com.codename1.rad.nodes.TableColumns;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.nodes.PropertyNode;
import ca.weblite.shared.components.table.TableModel;
import static ca.weblite.shared.components.table.TableModel.TableModelEvent.DELETE;
import static ca.weblite.shared.components.table.TableModel.TableModelEvent.INSERT;
import static ca.weblite.shared.components.table.TableModel.TableModelEvent.UPDATE;
import com.codename1.rad.models.ContentType;
import static com.codename1.rad.models.ContentType.Text;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.EntityList;
import com.codename1.rad.models.EntityList.EntityAddedEvent;
import com.codename1.rad.models.EntityList.EntityRemovedEvent;
import com.codename1.rad.models.EntityType;
import com.codename1.rad.models.Property;
import com.codename1.rad.models.Property.Label;
import com.codename1.rad.models.PropertyChangeEvent;

import com.codename1.ui.events.ActionListener;
import com.codename1.ui.util.EventDispatcher;
import java.util.HashMap;
import java.util.Map;

/**
 * A table model that allows using a {@link Table} to render and edit an EntityList.
 * @author shannah
 */
public class EntityListTableModel<T extends Entity> implements TableModel {
    private EventDispatcher listeners = new EventDispatcher();
    private EntityList<T> entities;
    private TableColumns columns;
    private Map<T,Integer> indexMap = new HashMap<>();
    private Map<Property,Integer> colMap = new HashMap<>();
    private EntityType entityType; 
    private void rebuildIndexMap() {
        indexMap.clear();
        int len = entities.size();
        for (int i=0; i<len; i++) {
            indexMap.put(entities.get(i), i);
        }
    }
    
    private void rebuildColMap() {
        colMap.clear();
        int len = columns.getColumnCount();
        for (int i=0; i<len; i++) {
            FieldNode def = columns.getColumn(i);
            Property p = def.getProperty(entityType);
            if (p != null) {
                colMap.put(p, i);
            }
        }
    }
    
    private ActionListener<PropertyChangeEvent> pcl = pce -> {
        if (listeners.hasListeners()) {
            Integer row = indexMap.get((T)pce.getSource());
            if (row == null) {
                return;
            }
            Integer col = colMap.get(pce.getProperty());
            if (col == null) {
                return;
            }
            listeners.fireActionEvent(
                    new TableModelEvent(
                            EntityListTableModel.this, 
                            col,
                            row,
                            row,
                            UPDATE
                    )
            );
        }
    };
    
    
    private ActionListener<EntityList.EntityListEvent> entityListener = evt -> {
        if (evt instanceof EntityAddedEvent) {
            EntityAddedEvent eae = (EntityAddedEvent)evt;
            if (eae.getIndex() == entities.size()-1) {
                // it was the last one
                indexMap.put((T)eae.getEntity(), eae.getIndex());
            } else {
                rebuildIndexMap();
            }
            eae.getEntity().addPropertyChangeListener(pcl);
            listeners.fireActionEvent(
                    new TableModelEvent(
                            EntityListTableModel.this,
                            -1, 
                            eae.getIndex(), 
                            eae.getIndex(), 
                            INSERT
                    )
            );
        } else if (evt instanceof EntityRemovedEvent) {
            EntityRemovedEvent eae = (EntityRemovedEvent)evt;
            eae.getEntity().removePropertyChangeListener(pcl);
            listeners.fireActionEvent(
                    new TableModelEvent(
                            EntityListTableModel.this,
                            -1, 
                            eae.getIndex(), 
                            eae.getIndex(), 
                            DELETE
                    )
            );
        }
    };
    
    
    public EntityListTableModel(EntityType rowType, EntityList<T> entities, TableColumns columnsNode) {
        this.entityType = rowType;
        this.entities = entities;
        this.columns = columnsNode;
        rebuildIndexMap();
        rebuildColMap();
        
    }
    @Override
    public ContentType getCellContentType(int row, int column) {
        Property prop = columns.getColumn(column).getProperty(entityType);
        if (prop != null) {
            return prop.getContentType();
        } else {
            return Text;
        }
    }

    @Override
    public int getColumnCount() {
        return columns.getColumnCount();
    }

    @Override
    public String getColumnName(int column) {
        
        Label label = columns.getColumn(column).getLabel(entityType);
        if (label == null) {
            return String.valueOf(column);
        }
        return label.getValue();
        
    }

    @Override
    public int getRowCount() {
        return entities.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Entity e = entities.get(rowIndex);
        FieldNode column = columns.getColumn(columnIndex);
        PropertyNode prop = column.getProperty();
        if (prop != null) {
            return prop.getValue().getValue(e);
        } else {
            return "";
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columns.getColumn(columnIndex).isEditable();
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Property prop = columns.getColumn(columnIndex).getProperty(entityType);
        Entity e = entities.get(rowIndex);
        if (prop != null) {
            prop.setValue(e, value);
        }
    }

    @Override
    public void addTableModelListener(ActionListener<TableModelEvent> l) {
        if (!listeners.hasListeners()) {
            entities.addActionListener(entityListener);
            addPropertyListeners();
        }
        listeners.addListener(l);
        
    }

    @Override
    public void removeTableModelListener(ActionListener<TableModelEvent> l) {
        listeners.removeListener(l);
        if (!listeners.hasListeners()) {
            entities.removeActionListener(entityListener);
            removePropertyListeners();
        }
    }
    
    
    private void addPropertyListeners() {
        for (T e : entities) {
            e.addPropertyChangeListener(pcl);
        }
    }
    
    private void removePropertyListeners() {
        for (T e : entities) {
            e.removePropertyChangeListener(pcl);
        }
    }
    
    public Property getColumnProperty(int column) {
        FieldNode colDef = columns.getColumn(column);
        return colDef.getProperty(entityType);
        
        
    }
    
    public T getEntity(int row) {
        return entities.get(row);
    }
    
    public FieldNode getColumnField(int column) {
        return columns.getColumn(column);
    }
    
    public EntityList<T> getEntityList() {
        return entities;
    }
}
