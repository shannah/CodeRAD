package com.codename1.rad.ui.beans;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.ui.AbstractEntityView;
import com.codename1.rad.ui.EntityView;
import com.codename1.rad.ui.ViewContext;

public class Title {
    private ViewContext context;
    public Title(@Inject ViewContext context) {
        this.context = context;
    }

    public void setText(String text) {
        context.getController().getFormController().setTitle(text);
    }

}
