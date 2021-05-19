package com.codename1.rad.ui;

import com.codename1.rad.annotations.Inject;
import com.codename1.ui.*;

public abstract class EntityViewFragment {
    private EntityView context;
    private Component component;
    
    
    
    protected EntityViewFragment(@Inject EntityView context) {
        this.context = context;
    }
    
    public EntityView getContext() {
        return context;
    }

    
    public Component getComponent() {
        if (component == null) {
            component = createComponent();
        }
        return component;
    }
    
    
    protected abstract Component createComponent();




}
