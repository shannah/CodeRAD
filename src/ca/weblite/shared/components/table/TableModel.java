/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.shared.components.table;

import com.codename1.rad.models.ContentType;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;

/**
 *
 * @author shannah
 */
public interface TableModel {
        
    public ContentType getCellContentType(int row, int column);
    public int getColumnCount();
    public String getColumnName(int column);
    public int getRowCount();
    public Object getValueAt(int rowIndex, int columnIndex);
    public boolean isCellEditable(int rowIndex, int columnIndex);
    public void setValueAt(Object value, int rowIndex, int columnIndex);
    public void addTableModelListener(ActionListener<TableModelEvent> l);
    public void removeTableModelListener(ActionListener<TableModelEvent> l);
    
    public static class TableModelEvent extends ActionEvent {

        /**
         * @return the column
         */
        public int getColumn() {
            return column;
        }

        /**
         * @param column the column to set
         */
        public void setColumn(int column) {
            this.column = column;
        }

        /**
         * @return the firstRow
         */
        public int getFirstRow() {
            return firstRow;
        }

        /**
         * @return the lastRow
         */
        public int getLastRow() {
            return lastRow;
        }

        /**
         * @return the type
         */
        public int getType() {
            return type;
        }
        public static final int INSERT=1;
        public static final int UPDATE=2;
        public static final int DELETE=3;
        private int column;
        private int firstRow;
        private int lastRow;
        private int type;
        
        public TableModelEvent(TableModel source, int column, int firstRow, int lastRow, int type) {
            super(source);
            this.column = column;
            this.firstRow = firstRow;
            this.lastRow = lastRow;
            this.type = type;
        }
        
    }

}
