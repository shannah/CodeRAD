/**
 *  This package contains some event utility classes that aren't part of CodeRAD proper, but may be useful to some apps.
 */
package ca.weblite.shared.event;


/**
 *  A utility class for observing changes on TextAreas.  API lends itself to expanding to other types of of components also. It subscribes to receive action events from the textareas,
 *  but only propagates the event to its listeners if the value of the text area has changed since the last event.
 *  
 *  NOTE:  This class isn't used by CodeRAD for its property binding functionality.  It is still a useful class for monitoring plain old
 *  text areas, though.
 *  @author shannah
 */
public class ChangeObserver {

	public ChangeObserver(Component[] components) {
	}

	public ChangeObserver add(Component cmp) {
	}

	public ChangeObserver addActionListener(ActionListener l) {
	}

	public ChangeObserver removeActionListener(ActionListener l) {
	}
}
