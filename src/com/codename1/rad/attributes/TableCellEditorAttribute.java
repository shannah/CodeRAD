/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.attributes;

import ca.weblite.shared.components.table.TableCellEditor;
import com.codename1.rad.models.Attribute;

/**
 * Attribute used by {@link Table} to specify the cell editor for a table. 
 * This can be added to any Node, as interested views can obtain it using {@link com.codename1.rad.nodes.Node#findInheritedAttribute(java.lang.Class) }.
 * 
 * The primary target of this attribute is the {@link FieldNode} with the {@link WidgetType#TABLE} type (ie. {@link com.codename1.rad.ui.UI#table(com.codename1.rad.models.Attribute...) }.
 * @author shannah
 * @see com.codename1.rad.ui.UI#cellEditor(ca.weblite.shared.components.table.TableCellEditor) 
 */
public class TableCellEditorAttribute extends Attribute<TableCellEditor> {
    
    public TableCellEditorAttribute(TableCellEditor value) {
        super(value);
    }
    
}
