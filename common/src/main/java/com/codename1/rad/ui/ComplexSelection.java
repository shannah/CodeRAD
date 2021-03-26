/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import static com.codename1.rad.ui.Selection.SelectionEvent.ADD;
import static com.codename1.rad.ui.Selection.SelectionEvent.REMOVE;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * A complex selection in a {@link Table} or List.  A complex selection is one that consists of 
 * one or more sub-selections.
 * @author shannah
 */
public class ComplexSelection extends Selection implements Iterable<Selection> {
    private List<Selection> selections = new ArrayList<>();

    @Override
    public int getFirstRow() {
        int out = -1;
        for (Selection sel : selections) {
            if (out == -1) {
                out = sel.getFirstRow();
            } else {
                int fr = sel.getFirstRow();
                if (fr >= 0) {
                    out = Math.min(out, fr);
                }
            }
        }
        return out;
    }

    @Override
    public int getLastRow() {
        int out = -1;
        for (Selection sel : selections) {
            if (out == -1) {
                out = sel.getLastRow();
            } else {
                int lr = sel.getLastRow();
                if (lr >= 0) {
                    out = Math.max(out, lr);
                }
            }
        }
        return out;
    }
    
    @Override
    public int getFirstColumn() {
        int out = -1;
        for (Selection sel : selections) {
            if (out == -1) {
                out = sel.getFirstColumn();
            } else {
                int fr = sel.getFirstColumn();
                if (fr >= 0) {
                    out = Math.min(out, fr);
                }
            }
        }
        return out;
    }

    @Override
    public int getLastColumn() {
        int out = -1;
        for (Selection sel : selections) {
            if (out == -1) {
                out = sel.getLastColumn();
            } else {
                int lr = sel.getLastColumn();
                if (lr >= 0) {
                    out = Math.max(out, lr);
                }
            }
        }
        return out;
    }
    
    
    public void add(Selection sel) {
        if (!selections.contains(sel)) {
            selections.add(sel);
            listeners.fireActionEvent(new SelectionEvent(this, sel, ADD));
        }
    }
    public void remove(Selection sel) {
        
        if (selections.remove(sel)) {
            listeners.fireActionEvent(new SelectionEvent(this, sel, REMOVE));
        }
    }
    
    public void replace(Selection sel) {
        clear();
        add(sel);
    }
    
    public void clear() {
        List<Selection> toRemove = new ArrayList<>();
        for (Selection s : this) {
            toRemove.add(s);
        }
        for (Selection s : toRemove) {
            remove(s);
        }
    }

    @Override
    public Iterator<Selection> iterator() {
        return selections.iterator();
    }

    @Override
    public boolean isSelected(int row, int column) {
        for (Selection sel : this) {
            if (sel.isSelected(row, column)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof ComplexSelection) {
            ComplexSelection cs = (ComplexSelection)obj;
            if (cs.selections.size() != selections.size()) {
                return false;
            }
            int len = selections.size();
            for (int i=0; i<len; i++) {
                if (!Objects.equals(selections.get(i), cs.selections.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.selections);
        return hash;
    }
    
    
    
}
