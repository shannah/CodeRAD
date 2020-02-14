/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui.table;

import com.codename1.rad.ui.DefaultPropertyViewFactory;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.nodes.PropertyNode;
import ca.weblite.shared.components.table.AbstractTableCellEditor;
import ca.weblite.shared.components.table.AbstractTableCellRenderer;
import ca.weblite.shared.components.table.DefaultTableCellEditor;
import ca.weblite.shared.components.table.DefaultTableCellRenderer;
import ca.weblite.shared.components.table.Table;
import ca.weblite.shared.components.table.TableCellEditor;
import ca.weblite.shared.components.table.TableCellRenderer;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.Property;
import com.codename1.rad.models.Property.Editable;
import com.codename1.rad.models.Property.Label;
import com.codename1.ui.Component;
import com.codename1.rad.ui.PropertyViewFactory;

/**
 *
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
        this(parent, new DefaultPropertyViewFactory());
    }
    
    public EntityListTableCellEditor(PropertyViewFactory viewFactory) {
        this(new DefaultTableCellEditor(), viewFactory);
    }
    
    public EntityListTableCellEditor() {
        this(new DefaultTableCellEditor(), new DefaultPropertyViewFactory());
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
