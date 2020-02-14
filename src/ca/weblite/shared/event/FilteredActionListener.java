/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.shared.event;

import com.codename1.ui.ComponentSelector;
import com.codename1.ui.TextArea;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import java.util.Objects;

/**
 * An ActionListener that defines a method {@link #valueChanged(com.codename1.ui.events.ActionEvent) }
 * that is only fired when the value is changed.
 * @author shannah
 */
public class FilteredActionListener implements ActionListener {
    private ActionListener internal;
    private static final String KEY_VALUE = "FilteredEventListener.currentValue";
    public static interface ValueGetter {
        public String getValue();
    }
    private FilteredActionListener() {
        
    }
    
    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getComponent() instanceof TextArea) {
            String val = ((TextArea)evt.getComponent()).getText();
            String currentValue = (String)evt.getComponent().getClientProperty(KEY_VALUE);
            
            if (!Objects.equals(val, currentValue)) {
                evt.getComponent().putClientProperty(KEY_VALUE, val);
                internal.actionPerformed(evt);
            }
        }
        
    }
    
    
    public static FilteredActionListener addFilteredActionListener(TextArea ta, ActionListener l) {
        FilteredActionListener fal = new FilteredActionListener();
        fal.internal = l;
        ta.addActionListener(fal);
        return fal;
    }
    
    
    
    
    
    
    
}
