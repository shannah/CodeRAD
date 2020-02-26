/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.shared.components.table;

import com.codename1.rad.models.ContentType;
import static com.codename1.rad.models.ContentType.Text;
import com.codename1.ui.Component;
import com.codename1.ui.TextField;

/**
 * A default {@link TableCellEditor} which renders cell content in a {@link TextField}.
 * @author shannah
 */
public class DefaultTableCellEditor implements TableCellEditor {

    @Override
    public Component getTableCellEditorComponent(Table table, Object value, boolean isSelected, int row, int column) {
        
        
        
        TextField tf = new TextField();
        TableModel model = table.getModel();
        ContentType contentType = model.getCellContentType(row, column);
        
        String strVal = value == null ? "" : String.valueOf(value);
        tf.setText(strVal);
        tf.addDataChangedListener((type, index)->{
            table.getModel().setValueAt(ContentType.convert(Text, tf.getText(), contentType), row, column);
        });
        
        return tf;
        
    }
    
}
