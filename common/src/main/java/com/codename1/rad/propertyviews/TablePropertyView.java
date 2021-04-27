/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.propertyviews;

import com.codename1.rad.ui.PropertyView;
import com.codename1.rad.nodes.TableColumns;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.ui.table.EntityListTableModel;
import ca.weblite.shared.components.table.Table;
import ca.weblite.shared.components.table.Table.TableEvent;

import com.codename1.rad.models.EntityList;
import com.codename1.rad.models.PropertyChangeEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.rad.models.Entity;

/**
 * A view for binding to {@link Table} components. 
 * @author shannah
 */
public class TablePropertyView extends PropertyView<Table> {

    private ActionListener<TableEvent> tableListener = evt -> {
        
    };
    
    private ActionListener<PropertyChangeEvent> pcl = pce -> {
        
    };
    
    public TablePropertyView(Table component, Entity entity, FieldNode field) {
        super(component, entity, field);
    }
    
    private EntityList getPropertyAsEntityList() {
        Object propertyVal = getEntity().getEntity().get(getProperty());
        if (!(propertyVal instanceof EntityList)) {
            throw new IllegalStateException("TablePropertyView only supports EntityList properties");
        }
        EntityList ePropertyVal = (EntityList)propertyVal;
        return ePropertyVal;
    }

    @Override
    protected void bindImpl() {
        
    }

    @Override
    protected void unbindImpl() {
        
    }

    @Override
    public void update() {
        super.update();
        EntityListTableModel model = (EntityListTableModel)getComponent().getModel();
        EntityList list = model.getEntityList();
        
        EntityList ePropertyVal = getPropertyAsEntityList();
        if (ePropertyVal == list) {
            return;
        }
        
        TableColumns columns = (TableColumns)getField().findAttribute(TableColumns.class);
        if (columns == null) {
            throw new IllegalStateException("Cannot create TablePropertyView for field "+getField()+" because the field does not define any columns.  Add a ColumnsNode attribute to the field.");
        }
        
        EntityListTableModel newModel = new EntityListTableModel(list.getRowType(), ePropertyVal, columns);
        getComponent().setModel(newModel);
        
    }

    @Override
    public void commit() {
        EntityListTableModel model = (EntityListTableModel)getComponent().getModel();
        EntityList list = model.getEntityList();
       
        EntityList ePropertyVal = getPropertyAsEntityList();
        if (ePropertyVal == list) {
            return;
        }
        getEntity().getEntity().set(getProperty(), list);
    }
    
}
