/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.shared.event;

import com.codename1.ui.Component;
import com.codename1.ui.TextArea;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.util.EventDispatcher;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for observing changes on TextAreas.  API lends itself to expanding to other types of of components also. It subscribes to receive action events from the textareas,
 * but only propagates the event to its listeners if the value of the text area has changed since the last event.
 * 
 * NOTE:  This class isn't used by CodeRAD for its property binding functionality.  It is still a useful class for monitoring plain old
 * text areas, though.
 * @author shannah
 */
public class ChangeObserver {
    List<Component> components = new ArrayList<Component>();
    EventDispatcher listeners = new EventDispatcher();
    
    public ChangeObserver(Component... components) {
        for (Component cmp : components) {
            add(cmp);
        }
    }
    
    public ChangeObserver add(Component cmp) {
        components.add(cmp);
        if (cmp instanceof TextArea) {
            TextArea ta = (TextArea)cmp;
            FilteredActionListener l = FilteredActionListener.addFilteredActionListener(ta, e->{
                listeners.fireActionEvent(e);
            });
        }
        return this;
    }
    
    public ChangeObserver addActionListener(ActionListener l) {
        listeners.addListener(l);
        return this;
    }
    
    public ChangeObserver removeActionListener(ActionListener l) {
        listeners.removeListener(l);
        return this;
    }
}
