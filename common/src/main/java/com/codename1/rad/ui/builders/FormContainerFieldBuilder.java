package com.codename1.rad.ui.builders;

import ca.weblite.shared.components.FormContainer;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.ui.AbstractComponentBuilder;
import com.codename1.rad.ui.ViewContext;
import com.codename1.ui.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RAD(tag = "field")
public class FormContainerFieldBuilder extends AbstractComponentBuilder<FormContainer.Field> {
    private Component value;
    public FormContainerFieldBuilder(ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }



    public void addChild(Component component) {
        if (this.value != null) {
            throw new IllegalStateException("Attempt to add two values to field: "+component+" and "+value);
        }
        this.value = component;
    }


    @Override
    public FormContainer.Field build() {
        FormContainer.Field out = new FormContainer.Field();

        if (value != null) {
            out.setValue(value);
        } else {
            throw new IllegalStateException("No value provided for field");
        }
        return out;
    }

    @Override
    public Object parseConstraint(String constraint) {
        return null;
    }
}
