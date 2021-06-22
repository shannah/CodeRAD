package com.codename1.rad.ui.builders;

import com.codename1.rad.models.Bindable;
import com.codename1.rad.models.PropertyChangeEvent;
import com.codename1.rad.models.PropertySelector;
import com.codename1.ui.Component;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.FocusListener;

public class ComponentBinder {
    public static void bindFocus(Bindable bindable, PropertySelector property, Component cmp) {
        cmp.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(Component cmp) {
                if (property.isFalsey()) {
                    property.setBoolean(true);
                }
            }

            @Override
            public void focusLost(Component cmp) {
                if (!property.isFalsey()) {
                    property.setBoolean(false);
                }
            }
        });
        ActionListener<PropertyChangeEvent> pcl = evt->{
            boolean isFocused = !property.isFalsey();
            if (isFocused != cmp.hasFocus()) {
                cmp.requestFocus();
            }
        };
       bindable.addBindListener(()->{
           property.addPropertyChangeListener(pcl);
       });
       bindable.addUnbindListener(()->{
           property.removePropertyChangeListener(pcl);
       });


    }




}
