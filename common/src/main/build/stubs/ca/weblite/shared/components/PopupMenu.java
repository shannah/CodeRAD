/**
 *  This package contains foundation components that are used by views in CodeRAD.  
 *  
 *  The components in this package are not, themselves, CodeRAD views - i.e. they don't bind to a view model.  They are used *by* CodeRAD components, and can also be used on their own.
 */
package ca.weblite.shared.components;


/**
 *  A popup-menu with commands that the user can click on.
 *  @author shannah
 */
public class PopupMenu extends InteractionDialog {

	public PopupMenu() {
	}

	/**
	 *  @return the commandLabel
	 */
	public String getCommandLabel() {
	}

	/**
	 *  @param commandLabel the commandLabel to set
	 */
	public void setCommandLabel(String commandLabel) {
	}

	/**
	 *  @return the materialIcon
	 */
	public char getMaterialIcon() {
	}

	/**
	 *  @param materialIcon the materialIcon to set
	 */
	public void setMaterialIcon(char materialIcon) {
	}

	public void setCommandsLayout(Layout layout) {
	}

	public Command getCommand() {
	}

	@java.lang.Override
	public void showPopupDialog(Component c) {
	}

	public PopupMenu addCommand(Component component) {
	}

	public PopupMenu addCommand(Command command) {
	}

	public void removeAllCommands() {
	}
}
