/**
 *  This package contains foundation components that are used by views in CodeRAD.  
 *  
 *  The components in this package are not, themselves, CodeRAD views - i.e. they don't bind to a view model.  They are used *by* CodeRAD components, and can also be used on their own.
 */
package ca.weblite.shared.components;


/**
 *  A container that can provide some "overflow" content when the user swipes left.  This is similar to a {@link SwipeContainer}, except that the overflow
 *  area will automatically scroll out of view again when the user releases their finger.
 *  @author shannah
 */
public class OverflowContainer extends Container {

	public OverflowContainer(Component mainContents, Component overflowContent) {
	}

	@java.lang.Override
	public int getLayoutHeight() {
	}

	@java.lang.Override
	public int getLayoutWidth() {
	}

	@java.lang.Override
	protected Dimension calcScrollSize() {
	}

	@java.lang.Override
	public boolean isScrollableX() {
	}

	@java.lang.Override
	protected boolean constrainWidthWhenScrollable() {
	}

	@java.lang.Override
	public void layoutContainer() {
	}

	public static class OverflowGroup {


		public OverflowGroup() {
		}

		public void add(OverflowContainer cnt) {
		}

		public void remove(OverflowContainer cnt) {
		}

		public void clear() {
		}

		public static OverflowContainer.OverflowGroup findGroup(OverflowContainer cnt) {
		}

		public Component findGroupContainer(Component searchStart) {
		}

		public static OverflowContainer.OverflowGroup createGroup(Container cnt) {
		}

		public static void removeGroup(Container cnt) {
		}
	}
}
