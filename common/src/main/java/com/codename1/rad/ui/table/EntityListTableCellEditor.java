/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui.table;

import com.codename1.rad.nodes.FieldNode;
import ca.weblite.shared.components.table.AbstractTableCellEditor;
import ca.weblite.shared.components.table.Table;
import ca.weblite.shared.components.table.TableCellEditor;

import com.codename1.ui.Component;
import com.codename1.rad.ui.PropertyViewFactory;
import com.codename1.rad.ui.UI;
import com.codename1.rad.models.Entity;

/**
 * A cell editor for editing cell in a Table that uses {@link EntityListTableModel} as a model.
 * @author shannah
 */
public class EntityListTableCellEditor extends  AbstractTableCellEditor {
    
    private PropertyViewFactory viewFactory;
    private TableCellEditor parent;
    
    public EntityListTableCellEditor(TableCellEditor parent, PropertyViewFactory viewFactory) {
        this.parent = parent;
        this.viewFactory = viewFactory;
    }
    
    public EntityListTableCellEditor(TableCellEditor parent) {
        this(parent, UI.getDefaultPropertyViewFactory());
    }
    
    public EntityListTableCellEditor(PropertyViewFactory viewFactory) {
        this(UI.getDefaultTableCellEditor(), viewFactory);
    }
    
    public EntityListTableCellEditor() {
        this(UI.getDefaultTableCellEditor(), UI.getDefaultPropertyViewFactory());
    }

    
  

    @Override
    public Component getTableCellEditorComponent(Table table, Object value, boolean isSelected, int row, int column) {
        EntityListTableModel model = (EntityListTableModel)table.getModel();
        FieldNode field = model.getColumnField(column);
       
        Entity entity = model.getEntity(row);
        if (entity == null) {
            if (parent != null) {
                return parent.getTableCellEditorComponent(table, value, isSelected, row, column);
            } else {
                return new com.codename1.ui.Label();
            }
        }
        return viewFactory.createPropertyView(entity, field);
        
    }

    
}
