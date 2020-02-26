/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.attributes.TableCellEditorAttribute;
import com.codename1.rad.attributes.TableCellRendererAttribute;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.propertyviews.TextFieldPropertyView;
import com.codename1.rad.propertyviews.TextAreaPropertyView;
import com.codename1.rad.attributes.WidgetType;
import com.codename1.rad.nodes.TableColumns;
import com.codename1.rad.nodes.OptionsNode;
import com.codename1.rad.nodes.PropertyViewFactoryNode;
import com.codename1.rad.propertyviews.ComboBoxPropertyView;
import com.codename1.rad.propertyviews.LabelPropertyView;
import com.codename1.rad.propertyviews.TablePropertyView;
import com.codename1.rad.ui.table.EntityListTableCellEditor;
import com.codename1.rad.ui.table.EntityListTableCellRenderer;
import com.codename1.rad.ui.table.EntityListTableModel;
import ca.weblite.shared.components.table.DefaultTableCellEditor;
import ca.weblite.shared.components.table.DefaultTableCellRenderer;
import ca.weblite.shared.components.table.Table;
import ca.weblite.shared.components.table.TableCellEditor;
import ca.weblite.shared.components.table.TableCellRenderer;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.EntityList;
import com.codename1.rad.models.Property.Name;
import com.codename1.ui.ComboBox;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import java.util.HashMap;
import java.util.Map;

/**
 * Default factory used to convert a {@link FieldNode} into a {@link PropertyView}.
 * 
 * @author shannah
 */
public class DefaultPropertyViewFactory implements PropertyViewFactory {

    private Map<WidgetType,PropertyViewFactory> registry = new HashMap<>();
    
    {
        registry.put(WidgetType.TEXT, (entity, field)->{
            boolean editable = field.isEditable();
            if (editable) {
                return new TextFieldPropertyView(new TextField(), entity, field);
                
            } else {
                return new LabelPropertyView(new com.codename1.ui.Label(), entity, field);
            }
            
        });
        
        registry.put(WidgetType.TEXTAREA, (entity, field) ->{
            
            
            TextArea ta = new TextArea();
            ta.setRows(5);
            ta.setColumns(80);
            ta.setMaxSize(1024);
            ta.setEditable(field.isEditable());
            return new TextAreaPropertyView(ta, entity, field);
        });
        
        registry.put(WidgetType.COMBOBOX, (entity, field) -> {
            if (!field.isEditable()) {
                return new LabelPropertyView(new com.codename1.ui.Label(), entity, field);
            }
            ComboBox cb = new ComboBox();
            OptionsNode opts = field.getOptions(entity.getEntityType());
            if (opts != null) {
                cb.setModel(opts.getValue());
            }
            return new ComboBoxPropertyView(cb, entity, field);
            
            
            
        });
        
        registry.put(WidgetType.TABLE, (entity, field) -> {
            TableColumns columns = (TableColumns)field.findAttribute(TableColumns.class);
            if (columns == null) {
                throw new IllegalArgumentException("Cannot create a table for field "+field+" because it has not columns defined.  Add a ColumnsNode to the field's attributes.");
            }
            //EntityListTableModel tableModel = new EntityListTableModel
            EntityList entityList = (EntityList)entity.get(field.getProperty(entity.getEntityType()));
            if (entityList == null) {
                
            }
            EntityListTableModel tableModel = new EntityListTableModel(entityList.getRowType(), entityList, columns);
            
            TableCellEditorAttribute cellEditorAtt = (TableCellEditorAttribute)field.findInheritedAttribute(TableCellEditorAttribute.class);
            TableCellEditor cellEditor = null;
            if (cellEditorAtt == null) {
                PropertyViewFactory viewFactory = null;
                PropertyViewFactoryNode viewFactoryNode = (PropertyViewFactoryNode)field.findInheritedAttribute(PropertyViewFactoryNode.class);
                if (viewFactoryNode == null) {
                    viewFactory = UI.getDefaultPropertyViewFactory();
                } else {
                    viewFactory = viewFactoryNode.getValue();
                }
                cellEditor = new EntityListTableCellEditor(UI.getDefaultTableCellEditor(), viewFactory);
            } else {
                cellEditor = cellEditorAtt.getValue();
            }
            
            TableCellRenderer cellRenderer = null;
            TableCellRendererAttribute cellRendererAtt = (TableCellRendererAttribute)field.findInheritedAttribute(TableCellRendererAttribute.class);
            if (cellRendererAtt == null) {
                PropertyViewFactory viewFactory = null;
                PropertyViewFactoryNode viewFactoryNode = (PropertyViewFactoryNode)field.findInheritedAttribute(PropertyViewFactoryNode.class);
                if (viewFactoryNode == null) {
                    viewFactory = UI.getDefaultPropertyViewFactory();
                } else {
                    viewFactory = viewFactoryNode.getValue();
                }
                cellRenderer = new EntityListTableCellRenderer(UI.getDefaultTableCellRenderer(), viewFactory);
            }
            Table out = new Table(tableModel, cellRenderer, cellEditor);
            out.setEditable(field.isEditable());
            return new TablePropertyView(out, entity, field);  
        });
    }
    
    @Override
    public PropertyView createPropertyView(Entity entity, FieldNode field) {
        PropertyViewFactory typeFactory = registry.get(field.getWidgetType(entity.getEntityType()));
        if (typeFactory == null) {
            throw new IllegalArgumentException("Type "+field.getWidgetType()+" not supported");
        }
        return typeFactory.createPropertyView(entity, field);
    }
    
}
