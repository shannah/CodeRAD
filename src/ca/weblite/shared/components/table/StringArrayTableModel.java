/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.shared.components.table;

import static ca.weblite.shared.components.table.TableModel.TableModelEvent.DELETE;
import static ca.weblite.shared.components.table.TableModel.TableModelEvent.INSERT;
import static ca.weblite.shared.components.table.TableModel.TableModelEvent.UPDATE;
import com.codename1.rad.models.ContentType;
import static com.codename1.rad.models.ContentType.Text;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.util.EventDispatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author shannah
 */
public class StringArrayTableModel implements TableModel {
    private EventDispatcher listeners = new EventDispatcher();
    private String[] columnNames;
    private List<String[]> rows = new ArrayList<>();
    
    
    public StringArrayTableModel(int columns, String... colnames) {
        columnNames = new String[columns];
        int len = colnames.length;
        for (int i=0; i<columns; i++) {
            if (i < len) {
                columnNames[i] = colnames[i];
            } else {
                columnNames[i] = String.valueOf(i);
            }
        }
        
        
        // the remaining data is row data
        for (int i = columns; i < len; i+= columns) {
            String[] row = new String[columns];
            int rowLen = columns;
            if (i + columns > len) {
                rowLen = len - i;
            }
            for (int j = 0; j < columns; j++) {
                if (j < rowLen) {
                    row[j] = colnames[i + j];
                } else {
                    row[j] = "";
                }
            }
            rows.add(row);
        }
        
    }

    @Override
    public ContentType getCellContentType(int row, int column) {
        return Text;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return rows.get(rowIndex)[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Object existing = getValueAt(rowIndex, columnIndex);
        if (!Objects.equals(existing, value)) {
            rows.get(rowIndex)[columnIndex] = String.valueOf(value);
            listeners.fireActionEvent(new TableModelEvent(this, columnIndex, rowIndex, rowIndex, UPDATE));
        }
    }

    @Override
    public void addTableModelListener(ActionListener<TableModelEvent> l) {
        listeners.addListener(l);
    }

    @Override
    public void removeTableModelListener(ActionListener<TableModelEvent> l) {
        listeners.removeListener(l);
    }
    
    public void insertRow(int rowNum, String... values) {
        String[] row = new String[getColumnCount()];
        int len = values.length;
        int colcount = getColumnCount();
        for (int i=0; i<colcount; i++) {
            if (i < len) {
                row[i] = values[i];
            } else {
                row[i] = "";
            }
        }
        rows.add(rowNum, values);
        listeners.fireActionEvent(new TableModelEvent(this, -1, rowNum, rowNum, INSERT));
    }
    
    public void addRow(String... values) {
        String[] row = new String[getColumnCount()];
        int len = values.length;
        int colcount = getColumnCount();
        for (int i=0; i<colcount; i++) {
            if (i < len) {
                row[i] = values[i];
            } else {
                row[i] = "";
            }
        }
        rows.add(row);
        listeners.fireActionEvent(new TableModelEvent(this, -1, rows.size()-1, rows.size()-1, INSERT));
        
    }
    
    public void deleteRow(int row) {
        rows.remove(row);
        listeners.fireActionEvent(new TableModelEvent(this, -1, row, row, DELETE));
    }
    
}
