/**
 *  This package contains a Table component similar to swing's JTable and associated renderer and editor classes.
 *  
 *  
 */
package ca.weblite.shared.components.table;


/**
 *  A default {@link TableCellRenderer} implementation that renders cell as a {@link Label}.
 *  @author shannah
 */
public class DefaultTableCellRenderer implements TableCellRenderer {

	public DefaultTableCellRenderer() {
	}

	@java.lang.Override
	public Component getTableCellRendererComponent(Table table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	}

	public TableLayout.Constraint createCellConstraint(Table table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	}
}
