/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.shared.components.table;

import com.codename1.rad.ui.Selection;
import com.codename1.rad.ui.ComplexSelection;
import com.codename1.rad.ui.Selection.SelectionEvent;
import ca.weblite.shared.components.table.TableModel.TableModelEvent;
import static ca.weblite.shared.components.table.TableModel.TableModelEvent.DELETE;
import static ca.weblite.shared.components.table.TableModel.TableModelEvent.INSERT;
import static ca.weblite.shared.components.table.TableModel.TableModelEvent.UPDATE;
import com.codename1.rad.ui.UI;
import com.codename1.ui.CN;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.FocusListener;
import com.codename1.ui.table.TableLayout;
import com.codename1.ui.util.EventDispatcher;


/**
 * A Table UI component similar to swing's JTable.  Supports row selection, column selection, cell selection, and editing.
 * @author shannah
 */
public class Table<T> extends Container {

    /**
     * @return the selectionMode
     */
    public SelectionMode getSelectionMode() {
        return selectionMode;
    }

    /**
     * @param selectionMode the selectionMode to set
     */
    public void setSelectionMode(SelectionMode selectionMode) {
        this.selectionMode = selectionMode;
    }

    /**
     * @return the selectionEnabled
     */
    public boolean isSelectionEnabled() {
        return selectionEnabled;
    }

    /**
     * @param selectionEnabled the selectionEnabled to set
     */
    public void setSelectionEnabled(boolean selectionEnabled) {
        this.selectionEnabled = selectionEnabled;
    }
    private EventDispatcher listeners = new EventDispatcher();
    private TableModel model;
    private TableCellRenderer renderer = UI.getDefaultTableCellRenderer();
    private TableCellEditor editor = UI.getDefaultTableCellEditor();
    private final ComplexSelection selection = new ComplexSelection();
    private int focusedRow, focusedCol;
    
    private SelectionMode selectionMode = SelectionMode.RowOnly;
    private boolean selectionEnabled;
    private boolean enableSelectionOnLongPress;
    private boolean editable;
    protected Component editorComp;
    protected int editingRow = -1;
    protected int editingColumn = -1;
    
    
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    
    public class TableEvent extends ActionEvent {
        
        public TableEvent() {
            super(Table.this);
        }
        
    }
    
    private class Tuple {
        int row, column;
        Tuple(int row, int column) {
            this.row = row;
            this.column = column;
        }
    }
    
    private Tuple findNextEditableCell(int row, int column) {
        for (int i=row; i<model.getRowCount(); i++) {
            for (int j=0; j<model.getColumnCount(); j++) {
                if (i == row && j <= column) {
                    j = column+1;
                    if (j >= model.getColumnCount()) {
                        continue;
                    }
                }
                
                if (model.isCellEditable(i, j)) {
                    return new Tuple(i, j);
                }
            
            }
            
        }
        return null;
    }
    
    private Tuple findPrevEditableCell(int row, int column) {
        for (int i=row; i>=0; i--) {
            for (int j=model.getColumnCount()-1; j>=0; j--) {
                if (i == row && j >= column) {
                    j = column -1;
                    if (j < 0) {
                        continue;
                    }
                }
                if (model.isCellEditable(i, j)) {
                    return new Tuple(i, j);
                }
            }
        }
        return null;
    }
    
    private class FocusTarget extends Component {
        private int row, column;
        FocusTarget(int row, int column) {
            this.row = row;
            this.column = column;
            setFocusable(true);
            setEnabled(true);

        }

        @Override
        public void startEditingAsync() {
            editCellAt(row, column);
        }
        
        

    }
    
    public void editCellAt(int row, int column) {
        if (!editable) {
            throw new IllegalStateException("Cannot edit cell because table is not editable.");
        }
        if (row < 0 || row >= model.getRowCount()) {
            throw new IndexOutOfBoundsException("Attempt to edit cell in row "+row+" but row does not exist.");
        }
        if (column < 0 || column >= model.getColumnCount()) {
            throw new IndexOutOfBoundsException("Attempt to edit cell in column "+ column+" but column does not exist.");
        }
        if (editingRow == row && editingColumn == column) {
            if (!editorComp.isEditing()) {
                editorComp.startEditingAsync();
            }
            return;
        }
        if (isEditing()) {
            editorComp.stopEditing(()->{
                int updateColumn = editingColumn;
                int updateRow = editingRow;
                editingColumn = -1;
                editingRow = -1;
                editorComp = null;
                update(updateRow, updateRow, updateColumn);
                editCellAt(row, column);
            });
            return;
        }
        editorComp = editor.getTableCellEditorComponent(
                this, 
                model.getValueAt(row, column), 
                selection.isSelected(row, column), 
                row, 
                column
        );
        editingRow = row;
        editingColumn = column;
        TableLayout tl = (TableLayout)getLayout();
        Component existing = tl.getComponentAt(row, column);
        editorComp.setPreferredH(existing.getHeight());
        editorComp.setPreferredW(existing.getWidth());
        this.replace(existing, editorComp, null);
        revalidateWithAnimationSafety();
        Tuple nextPos = findNextEditableCell(row, column);
        if (nextPos != null) {
            editorComp.setNextFocusRight(new FocusTarget(nextPos.row, nextPos.column));
        }
        Tuple prevPos = findPrevEditableCell(row, column);
        if (prevPos != null) {
            editorComp.setNextFocusLeft(new FocusTarget(prevPos.row, prevPos.column));
        }
        editorComp.startEditingAsync();
        
    }

    @Override
    public void startEditingAsync() {
        if (!editable) {
            return;
        }
        if (editorComp != null && !editorComp.isEditing()) {
            editorComp.startEditingAsync();
            return;
        }
        
        if (focusedCol <= 0  || focusedCol >= model.getColumnCount()) {
            focusedCol = 0;
        }
        
        if (focusedRow <= 0 || focusedRow >= model.getRowCount()) {
            focusedRow = 0;
        }
        editCellAt(focusedRow, focusedCol);
        
    }
    
    public TableModel getModel() {
        return model;
    }

    @Override
    public void stopEditing(Runnable onFinish) {
        
        if (editorComp != null) {
            System.out.println("Stopping");
            int row = editingRow;
            int col = editingColumn;
            Component editor = editorComp;
            editorComp = null;
            editingRow = -1;
            editingColumn = -1;
            editor.stopEditing(()->{
                System.out.println("Stopped");
                update(row, row, col);
                if (onFinish != null) {
                    onFinish.run();
                }
            });
        } else {
            super.stopEditing(onFinish);
        }
    }

    
    
    
    public boolean isEditing() {
        return editable && editorComp != null;
    }

    @Override
    public boolean isEditable() {
        return editable;
    }
    
    
    
    
    
    public class SelectionModeChangedEvent extends TableEvent {

        /**
         * @return the selectionEnabled
         */
        public boolean isSelectionEnabled() {
            return selectionEnabled;
        }

        /**
         * @return the selectionMode
         */
        public SelectionMode getSelectionMode() {
            return selectionMode;
        }
        private boolean selectionEnabled;
        private SelectionMode selectionMode;
        
        public SelectionModeChangedEvent(SelectionMode selectionMode, boolean selectionEnabled) {
            this.selectionEnabled = selectionEnabled;
            this.selectionMode = selectionMode;
        }
    }

    private ActionListener<TableModelEvent> modelListener = evt -> {
        //TableLayout tl = (TableLayout)getLayout();
        int firstRow = evt.getFirstRow();
        int lastRow = evt.getLastRow();
        int column = evt.getColumn();
        switch (evt.getType()) {
            case INSERT:
            case DELETE:
                build();
                break;
            case UPDATE:
                update(firstRow, lastRow, column);
                break;
                
                
        }

    };
    
    private ActionListener<SelectionEvent> selectionListener = evt -> {
        Selection sel = evt.getSelection();
        int firstColumn = sel.getFirstColumn();
        int lastColumn = sel.getLastColumn();
        int firstRow = sel.getFirstRow();
        int lastRow = sel.getLastRow();
        
        if (firstColumn < 0 || lastColumn < 0 || firstRow < 0 || lastRow < 0) {
            return;
        }
        
        for (int j = firstColumn; j <= lastColumn; j++) {
            update(firstRow, lastRow, j);
        }
    };
    
    
    
    private ActionListener formKeyListener = evt -> {
        System.out.println("Key event received "+evt.getKeyEvent());
        if (model.getColumnCount() < 1 || model.getRowCount() < 1) {
            return;
        }
        if (!selectionEnabled) {
            return;
        }
        if (isEditing()) {
            return;
        }
        
        switch (evt.getKeyEvent()) {
            
            case -91:
                // up arrow
                
                if (selection.getFirstRow() > 0) {
                    selection.replace(new Selection(selection.getFirstRow()-1, selection.getFirstRow()-1, 0, model.getColumnCount()-1));
                } else {
                    selection.replace(new Selection(0, 0, 0, model.getColumnCount()-1));
                }
                break;
            case -92:
                // down arrow
                
                if (selection.getLastRow() > -1 && selection.getLastRow() < model.getColumnCount()-1) {
                    selection.replace(new Selection(selection.getLastRow()+1, selection.getLastRow()+1, 0, model.getColumnCount()-1));
                } else {
                    selection.replace(new Selection(0, 0, 0, model.getColumnCount()-1));
                }
        }
        
    };
    
    private boolean pointerDown;
    private int pointerX, pointerY;
    private int clickCount=0;
    private long lastPointerRelease;
    
    
    private ActionListener formPointerListener = evt -> {
        
        boolean inTable = Table.this.contains(evt.getX(), evt.getY());
        if (evt.getEventType() == ActionEvent.Type.PointerReleased) {
            
            Component pressedCmp = getComponentForm().getComponentAt(evt.getX(), evt.getY());
            if (editorComp != null) {
                
                boolean continueEditing = false;
                
                if (pressedCmp != null && pressedCmp.isOwnedBy(editorComp)) {
                    continueEditing = true;
                }
                if (!continueEditing && editorComp.contains(evt.getX(), evt.getY())) {
                    continueEditing = true;
                }
                if (!continueEditing) {
                    stopEditing(()->{});
                } 
                
            }
        }
        
        if (pointerDown && evt.getEventType() == ActionEvent.Type.PointerReleased) {
            pointerDown = false;
            
            if (inTable) {
                if (!isSelectionEnabled()) {
                    if (enableSelectionOnLongPress) {
                        if (evt.isLongEvent()) {
                            setSelectionEnabled(true);
                            listeners.fireActionEvent(new SelectionModeChangedEvent(getSelectionMode(), isSelectionEnabled()));
                            return;
                        }
                    }
                }
            }
            
            int threshold = CN.convertToPixels(2);
            long now = System.currentTimeMillis();
            if (lastPointerRelease + 200 > now && Math.abs(pointerX - evt.getX()) < threshold && Math.abs(pointerY - evt.getY()) < threshold) {
                clickCount++;
            } else {
                clickCount = 1;
            }
            lastPointerRelease = now;
            
            
            if (inTable && isEditable() && clickCount == 2) {
                Component cellCmp = Table.this.getComponentAt(evt.getX(), evt.getY());
                boolean startEditing = true;
                if (cellCmp == null || !Table.this.contains(cellCmp)) {
                    startEditing = false;
                }
                while (startEditing && cellCmp.getParent() != Table.this) {
                    cellCmp = cellCmp.getParent();
                    if (cellCmp == null) {
                        startEditing = false;
                    }
                }
                //System.out.println("startEditing = ")
                if (startEditing && cellCmp != null) {
                    Object ocnst = getLayout().getComponentConstraint(cellCmp);
                    if (ocnst instanceof TableLayout.Constraint) {
                        TableLayout.Constraint cnst = (TableLayout.Constraint)ocnst;
                        editCellAt(cnst.getRow(), cnst.getColumn());
                        return;
                    }
                    
                }
            }
            
            return;
        }
        
        
        
        if (evt.getEventType() == ActionEvent.Type.PointerPressed && inTable) {
            
            pointerDown = true;
            pointerX = evt.getX();
            pointerY = evt.getY();
            
            
            
            
            
            
            if (isSelectionEnabled()) {
                
                Component cellCmp = Table.this.getComponentAt(evt.getX(), evt.getY());

                if (cellCmp == null || !Table.this.contains(cellCmp)) {
                    return;
                }
                while (cellCmp.getParent() != Table.this) {
                    cellCmp = cellCmp.getParent();
                    if (cellCmp == null) {
                        return;
                    }
                }
                TableLayout.Constraint cnst = (TableLayout.Constraint)Table.this.getLayout().getComponentConstraint(cellCmp);
                int row = cnst.getRow();
                int column = cnst.getColumn();
                boolean cellSelected = selection.isSelected(row, column);
                if (Display.getInstance().isShiftKeyDown()) {
                    
                    
                    switch (getSelectionMode()) {
                        case RowOnly: {
                            if (!cellSelected) {
                                // The row isnt' currently selected
                                int firstColumn = 0;
                                int lastColumn = model.getColumnCount()-1;
                                int firstRow = row;
                                int lastRow = row;
                                int existingFirstRow = selection.getFirstRow();
                                if (existingFirstRow < firstRow && existingFirstRow > -1) {
                                    firstRow = existingFirstRow;
                                }
                                int existingLastRow = selection.getLastRow();
                                if (existingLastRow > -1 && existingLastRow > lastRow) {
                                    lastRow = existingLastRow;
                                }
                                Selection newSelection = new Selection(firstRow, lastRow, firstColumn, lastColumn);
                                selection.replace(newSelection);
                                
                            } else {
                                // Cell is already selected so this will deselect this row and everything after
                                int firstColumn = 0;
                                int lastColumn = model.getColumnCount()-1;
                                
                                int firstRow = selection.getFirstRow();
                                int lastRow = row-1;
                                if (lastRow < firstRow) {
                                    selection.clear();
                                } else {
                                    selection.replace(new Selection(firstRow, lastRow, firstColumn, lastColumn));
                                }
                                
                            }
                            break;
                            
                        }
                        
                        case ColumnOnly: {
                            if (!cellSelected) {
                                // The row isnt' currently selected
                                int firstRow = 0;
                                int lastRow = model.getRowCount()-1;
                                int firstColumn = column;
                                int lastColumn = column;
                                int existingFirstColumn = selection.getFirstColumn();
                                if (existingFirstColumn < firstColumn && existingFirstColumn > -1) {
                                    firstColumn = existingFirstColumn;
                                }
                                int existingLastColumn = selection.getLastColumn();
                                if (existingLastColumn > -1 && existingLastColumn > lastColumn) {
                                    lastColumn = existingLastColumn;
                                }
                                Selection newSelection = new Selection(firstRow, lastRow, firstColumn, lastColumn);
                                selection.replace(newSelection);
                                
                            } else {
                                // Cell is already selected so this will deselect this row and everything after
                                int firstRow = 0;
                                int lastRow = model.getRowCount()-1;
                                
                                int firstColumn = selection.getFirstColumn();
                                int lastColumn = column-1;
                                if (lastColumn < firstColumn) {
                                    selection.clear();
                                } else {
                                    selection.replace(new Selection(firstRow, lastRow, firstColumn, lastColumn));
                                }
                                
                            }
                            break;
                        }
                        
                        case SingleCell : {
                            if (!cellSelected) {
                                selection.replace(new Selection(row, row, column, column));
                            } else {
                                selection.clear();
                            }
                            break;
                        }
                        
                        case CellRange : {
                            if (!cellSelected) {
                                int firstRow = row;
                                int lastRow = row;
                                int firstColumn = column;
                                int lastColumn = column;
                                int existingFirstRow = selection.getFirstRow();
                                if (existingFirstRow > -1 && existingFirstRow < firstRow) {
                                    firstRow = existingFirstRow;
                                }
                                int existingFirstColumn = selection.getFirstColumn();
                                if (existingFirstColumn > -1 && existingFirstColumn < firstColumn) {
                                    firstColumn = existingFirstColumn;
                                }
                                
                                selection.replace(new Selection(firstRow, lastRow, firstColumn, lastColumn));
                            } else {
                                int firstRow = selection.getFirstRow();
                                int lastRow = row - 1;
                                if (lastRow < firstRow) {
                                    selection.clear();
                                    return;
                                }
                                
                                int firstColumn = selection.getFirstColumn();
                                int lastColumn = column - 1;
                                if (lastColumn < firstColumn) {
                                    selection.clear();
                                    return;
                                }
                                
                                selection.replace(new Selection(firstRow, lastRow, firstColumn, lastColumn));
                                
                            }
                            break;
                        }
                    }
                } else if (Display.getInstance().isMetaKeyDown() || Display.getInstance().isControlKeyDown()) {
                    switch (getSelectionMode()) {
                    
                        case RowOnly: {
                            if (!cellSelected) {
                                selection.add(new Selection(row, row, 0, model.getColumnCount()-1));
                            } else {
                                Selection activeSelection = null;
                                for (Selection s : selection) {
                                    if (s.isSelected(row, column)) {
                                        activeSelection = s;
                                        break;
                                    }
                                }
                                if (activeSelection != null) {
                                    selection.remove(activeSelection);
                                    if (activeSelection.getFirstRow() < row) {
                                        selection.add(new Selection(activeSelection.getFirstRow(), row-1, 0, model.getColumnCount()-1));
                                    }
                                    if (activeSelection.getLastRow() > row) {
                                        selection.add(new Selection(row+1, activeSelection.getLastRow(), 0, model.getColumnCount()-1));
                                    }
                                }
                            }
                            break;
                        }
                        
                        case ColumnOnly : {
                            if (!cellSelected) {
                                selection.add(new Selection(0, model.getRowCount()-1, column, column));
                            } else {
                                Selection activeSelection = null;
                                for (Selection s : selection) {
                                    if (s.isSelected(row, column)) {
                                        activeSelection = s;
                                        break;
                                    }
                                }
                                if (activeSelection != null) {
                                    selection.remove(activeSelection);
                                    if (activeSelection.getFirstColumn() < column) {
                                        selection.add(new Selection(0, model.getRowCount()-1, activeSelection.getFirstColumn(), column-1));
                                    }
                                    if (activeSelection.getLastColumn() > column) {
                                        selection.add(new Selection(0, model.getRowCount()-1, column+1, activeSelection.getLastColumn()));
                                    }
                                } else {
                                    throw new IllegalStateException("Cannot find active selection, even though it says that the cell was selected.");
                                }
                            }
                            break;
                        }
                        
                        case SingleCell : {
                            if (!cellSelected) {
                                selection.clear();
                            } else {
                                selection.replace(new Selection(row, row, column, column));
                            }
                            break;
                        }
                        
                        case CellRange : {
                            if (cellSelected) {
                                selection.remove(new Selection(row, row, column, column));
                            } else {
                                selection.add(new Selection(row, row, column, column));
                            }
                            break;
                        }
                    }
                } else {
                    switch (getSelectionMode()) {
                        case RowOnly: {
                            if (!cellSelected) {
                                selection.replace(new Selection(row, row, 0, model.getColumnCount()-1));
                            }
                            break;
                        }
                        case ColumnOnly: {
                            if (!cellSelected) {
                                selection.replace(new Selection(0, model.getRowCount()-1, column, column));
                            }
                            break;
                        }
                        case SingleCell:
                        case CellRange: {
                            if (!cellSelected) {
                                selection.replace(new Selection(row, row, column, column));
                            }
                            break;
                        }
                        
                    }
                }
            }
        }
    };

    private Form form;
    @Override
    protected void initComponent() {
        super.initComponent();
        form = getComponentForm();
        if (form != null) {
            form.addPointerPressedListener(formPointerListener);
            form.addPointerReleasedListener(formPointerListener);
            form.addKeyListener(-91, formKeyListener);
            form.addKeyListener(-92, formKeyListener);
        }
            
    }

    @Override
    protected void deinitialize() {
        if (form != null) {
            pointerDown = false;
            form.removePointerPressedListener(formPointerListener);
            form.removePointerReleasedListener(formPointerListener);
            form.removeKeyListener(-91, formKeyListener);
            form.removeKeyListener(-92, formKeyListener);
        }
        super.deinitialize();
    }
    
    
    
    
    

    public void setModel(TableModel model) {
        if (this.model != null) {
            this.model.removeTableModelListener(modelListener);
        }
        this.model = model;
        if (this.model != null) {
            this.model.addTableModelListener(modelListener);
        }
    }
    
    public boolean isFocused(int row, int col) {
        return row == focusedRow && col == focusedCol;
    }
    
    public Table(TableModel model) {
        setModel(model);
        selection.addSelectionListener(selectionListener);
        build();
    }
    
    public Table(TableModel model, TableCellRenderer renderer, TableCellEditor editor) {
        setModel(model);
        this.renderer = renderer;
        this.editor = editor;
        selection.addSelectionListener(selectionListener);
        build();
    }
    
    public void setTableCellRenderer(TableCellRenderer renderer) {
        if (renderer != this.renderer) {
            this.renderer = renderer;
        build();
        }
        
    }
    
    public void setTableCellEditor(TableCellEditor editor) {
        if (editor != this.editor) {
            this.editor = editor;
            build();
        }
        
    }
    
    private void update(int firstRow, int lastRow, int column) {
        TableLayout tl = (TableLayout)getLayout();
        
        for (int i=firstRow; i<=lastRow; i++) {
            if (editingRow == i && editingColumn == column) {
                // If editing, we don't want to disturb the cell
                continue;
            }
            Component cmp = tl.getComponentAt(i, column);
            cmp.remove();
            TableLayout.Constraint cellConstraint = renderer.createCellConstraint(this, model.getValueAt(i, column), selection.isSelected(i, column), isFocused(i, column), i, column);
            cmp = createTableCell(model.getValueAt(i, column), i, column);
            add(cellConstraint, cmp);
        }
        revalidateWithAnimationSafety();
    }
    
    private Component createTableCell(Object value, int row, int col) {
        return renderer.getTableCellRendererComponent(this, value, selection.isSelected(row, col), isFocused(row, col), row, col);
    }
    
    private void build() {
        removeAll();
        TableLayout tl = createLayout();
        setLayout(tl);
        int rows = model.getRowCount();
        int cols = model.getColumnCount();
        for (int i=0; i<rows; i++) {
            for (int j=0; j<cols; j++) {
                Object value = model.getValueAt(i, j);
                TableLayout.Constraint cellConstraint = renderer.createCellConstraint(
                        this, 
                        value, 
                        selection.isSelected(i, j),
                        isFocused(i, j), 
                        i,
                        j
                );
                
                add(cellConstraint, createTableCell(value, i, j));
            }
        }
        revalidateWithAnimationSafety();
    }
    
    
    protected TableLayout createLayout() {
        TableLayout out = new TableLayout(model.getRowCount(), model.getColumnCount());
        out.setGrowHorizontally(true);
        return out;
    }
    
}
