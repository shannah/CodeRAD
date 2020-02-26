/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.shared.components.table;

import com.codename1.ui.Component;
import com.codename1.ui.table.TableLayout;

/**
 * An abstract base class for the {@link TableCellRenderer}.
 * @author shannah
 */
public abstract class AbstractTableCellRenderer implements TableCellRenderer {



    @Override
    public TableLayout.Constraint createCellConstraint(Table table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        TableLayout tl = (TableLayout)table.getLayout();
        TableLayout.Constraint cnst = tl.createConstraint(row, column);
        
        return cnst;
    }
    
}
