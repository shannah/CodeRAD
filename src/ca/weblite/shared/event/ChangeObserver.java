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
import java.util.Arrays;
import java.util.List;

/**
 *
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
