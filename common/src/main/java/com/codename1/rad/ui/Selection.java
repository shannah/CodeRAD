/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.compat.java.util.Objects;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.util.EventDispatcher;

/**
 * Encapsulates the selection of a list or table.
 * @author shannah
 */
public class Selection {
    int firstRow, lastRow, firstColumn, lastColumn;
    protected EventDispatcher listeners = new EventDispatcher();
    
    public Selection() {
        
    }
    
    public Selection(int firstRow, int lastRow, int firstColumn, int lastColumn) {
        this.firstRow = firstRow;
        this.lastRow = lastRow;
        this.firstColumn = firstColumn;
        this.lastColumn = lastColumn;
    }
    
    public int getFirstRow() {
        return firstRow;
    }
    
    public int getLastRow() {
        return lastRow;
    }
    
    public int getFirstColumn() {
        return firstColumn;
    }
    
    public int getLastColumn() {
        return lastColumn;
    }
    
    
    public boolean isSelected(int row, int column) {
        return row >= firstRow && row <= lastRow && column >= firstColumn && column <= lastColumn;
    }
    
    public static class SelectionEvent extends ActionEvent {
        private Selection selection;
        private int type;
        public static final int ADD=1;
        public static final int REMOVE=2;
        

        public SelectionEvent(Selection source, Selection selection, int type) {
            super(source);
            this.selection = selection;
            this.type = type;
        }
        
        public Selection getSelection() {
            return selection;
        }
        
        public int getType() {
            return type;
        }
    }
    
    public void addSelectionListener(ActionListener<SelectionEvent> l) {
        listeners.addListener(l);
    }
    
    public void removeSelectionListener(ActionListener<SelectionEvent> l) {
        listeners.removeListener(l);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Selection) {
            Selection s = (Selection)obj;
            return Objects.equals(s.firstColumn, firstColumn) &&
                    Objects.equals(s.lastColumn, lastColumn) &&
                    Objects.equals(s.firstRow, firstRow) &&
                    Objects.equals(s.lastRow, lastRow);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.firstRow;
        hash = 89 * hash + this.lastRow;
        hash = 89 * hash + this.firstColumn;
        hash = 89 * hash + this.lastColumn;
        return hash;
    }
    
    
    
}
