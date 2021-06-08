package com.codename1.rad.ui.beans;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.ui.AbstractEntityView;
import com.codename1.rad.ui.EntityView;
import com.codename1.rad.ui.ViewContext;
import com.codename1.ui.Component;

public class Title {
    private ViewContext context;
    public Title(@Inject ViewContext context) {
        this.context = context;
    }

    public void setText(String text) {
        context.getController().getFormController().setTitle(text);
    }

    public void setComponent(@Inject Component cmp) {
        context.getController().getFormController().setTitleComponent(cmp);
    }

    public void setHidden(boolean hidden) {
        if (hidden) {
            context.getController().getFormController().setAddTitleBar(false);
        } else {
            context.getController().getFormController().setAddTitleBar(true);
        }
    }

}
