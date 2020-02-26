/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.shared.components.table;

/**
 * Selection mode for a table.
 * @author shannah
 */
public enum SelectionMode {
    
    /**
     * Allow only row selection.
     */
    RowOnly,
    
    /**
     * Allow only column selection.
     */
    ColumnOnly,
    
    /**
     * Allow single cell selection.
     */
    SingleCell,
    
    /**
     * Allow cell range selection.
     */
    CellRange
}
