/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.shared.components.table;

import com.codename1.ui.Component;
import static com.codename1.ui.ComponentSelector.$;
import com.codename1.ui.Label;
import com.codename1.ui.table.TableLayout;

/**
 *
 * @author shannah
 */
public class DefaultTableCellRenderer implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(Table table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Label out = new Label();
        String strVal = value == null ? "" : String.valueOf(value);
        out.setText(strVal);
        if (isSelected) {
            $(out)
                    .setBgColor(0xff0000)
                    .setBgTransparency(0xff);
        }
        $(out).setMargin(0);
        return out;
        
    }
    
    public TableLayout.Constraint createCellConstraint(Table table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        TableLayout tl = (TableLayout)table.getLayout();
        TableLayout.Constraint cnst = tl.createConstraint(row, column);
        
        return cnst;
    }
    
}
