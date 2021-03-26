/**
 *  This package contains foundation components that are used by views in CodeRAD.  
 *  
 *  The components in this package are not, themselves, CodeRAD views - i.e. they don't bind to a view model.  They are used *by* CodeRAD components, and can also be used on their own.
 */
package ca.weblite.shared.components;


/**
 *  A Pane that includes a side bar and main content, which the sidebar would "overlap".
 *  @author shannah
 */
public class CollapsibleSideBarContainer extends Container {

	public CollapsibleSideBarContainer(Component left, Component center) {
	}

	@java.lang.Override
	protected void initComponent() {
	}

	@java.lang.Override
	protected void deinitialize() {
	}

	@java.lang.Override
	public void paint(Graphics g) {
	}

	@java.lang.Override
	protected void paintGlass(Graphics g) {
	}

	/**
	 *  Wraps a component in a container that will calculate an appropriate preferred size for a side menu.
	 *  @param cmp
	 *  @return 
	 */
	public static Component wrapSideMenu(Component cmp) {
	}

	public void showSideBar() {
	}

	public void hideSideBar() {
	}

	public void install(Form form) {
	}

	public static class SideBarEvent {


		public SideBarEvent(Component source) {
		}
	}

	public static class ShowSideBarEvent {


		public ShowSideBarEvent(Component source) {
		}
	}

	public static class HideSideBarEvent {


		public HideSideBarEvent(Component source) {
		}
	}
}
