package com.codename1.rad.ui.beans;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.controllers.FormController;
import com.codename1.rad.ui.AbstractEntityView;
import com.codename1.rad.ui.EntityView;
import com.codename1.rad.ui.ViewContext;
import com.codename1.rad.util.NonNull;
import com.codename1.ui.Component;

public class Title {
    private ViewContext context;
    public Title(@Inject ViewContext context) {
        this.context = context;
    }

    public void setText(String text) {
        NonNull.with(context.getController().getFormController(1), formController -> {
            formController.setTitle(text);
        });
    }

    public void setComponent(@Inject Component cmp) {

        NonNull.with(context.getController().getFormController(1), formController-> {
            formController.setTitleComponent(cmp);
        });

    }

    public void setHidden(boolean hidden) {
        NonNull.with(context.getController().getFormController(1), formController -> {
            if (hidden) {
                formController.setAddTitleBar(false);
            } else {
                formController.setAddTitleBar(true);
            }
        });

    }

}
