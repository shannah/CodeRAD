/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.shared.components.table;

import com.codename1.ui.Component;
import com.codename1.ui.table.TableLayout;

/**
 * An interface used to create components which display data in a cell of a {@link Table}.
 * @author shannah
 */
public interface TableCellRenderer {
    
    /**
     * Create a component to display the content of a particular cell of the table.
     * @param table The table.
     * @param value The cell value to display.
     * @param isSelected Whether the cell is currently selected.
     * @param hasFocus Whether the cell currently has focus.
     * @param row The row number (0-based).
     * @param column The column number (0-based)
     * @return The component to display the content.
     */
    public Component getTableCellRendererComponent(Table table, Object value, boolean isSelected, boolean hasFocus, int row, int column);
    public TableLayout.Constraint createCellConstraint(Table table, Object value, boolean isSelected, boolean hasFocus, int row, int column);
    
}

