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
 * A model for the content of a {@link Table}.
 * @author shannah
 */
public interface TableModel {
    
    /**
     * Get the content type of a cell.
     * @param row The row.
     * @param column The column
     * @return The content type of the cell.
     */
    public ContentType getCellContentType(int row, int column);
    
    /**
     * Get the number of columns in the model.
     * @return The number of columns.
     */
    public int getColumnCount();
    
    /**
     * Get the name of the given column
     * @param column The column number
     * @return The column name.
     */
    public String getColumnName(int column);
    
    /**
     * Get the number of rows in the model.
     * @return The number of rows.
     */
    public int getRowCount();
    
    /**
     * Get the value of a particular cell.
     * @param rowIndex The row of the cell
     * @param columnIndex The column of the cell
     * @return The cell content.
     */
    public Object getValueAt(int rowIndex, int columnIndex);
    
    /**
     * Check if the cell is editable.
     * @param rowIndex The row
     * @param columnIndex The column
     * @return 
     */
    public boolean isCellEditable(int rowIndex, int columnIndex);
    
    /**
     * Set the value in a cell.
     * @param value The value to set.
     * @param rowIndex The row
     * @param columnIndex The column
     */
    public void setValueAt(Object value, int rowIndex, int columnIndex);
    
    /**
     * Add a listener to be notified of changes to the model.
     * @param l 
     */
    public void addTableModelListener(ActionListener<TableModelEvent> l);
    
    /**
     * Remove a listener.
     * @param l 
     */
    public void removeTableModelListener(ActionListener<TableModelEvent> l);
    
    /**
     * An event fired by a table model when the model changes.  Three event types {@link #INSERT}, {@link #UPDATE}, and {@link #DELETE}.
     */
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
