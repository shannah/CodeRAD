/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.shared.components.table;

import com.codename1.ui.Component;

/**
 *
 * @author shannah
 */
public interface TableCellEditor {
    public Component getTableCellEditorComponent(Table table, Object value, boolean isSelected, int row, int column);
}
