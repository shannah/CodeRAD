package com.codename1.rad.ui.builders;

import ca.weblite.shared.components.FormContainer;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.ui.AbstractComponentBuilder;
import com.codename1.rad.ui.ViewContext;
import com.codename1.ui.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RAD(tag = "form")
public class FormContainerBuilder extends AbstractComponentBuilder<FormContainer> {
    private List<Component> queuedChildren = new ArrayList<Component>();
    

    public FormContainerBuilder(ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }



    public void addChild(Component component) {
        if (component instanceof FormContainer.Section || component instanceof FormContainer.Field) {
            queuedChildren.add(component);
        } else {
            throw new IllegalArgumentException("FormContainer can only have FormContainer.Section and FormContainer.Field components as children");
        }
    }

    @Override
    public FormContainer build() {
        FormContainer out = new FormContainer();
        for (Component child : queuedChildren) {
            if (child instanceof FormContainer.Field) {
                out.addField((FormContainer.Field)child);
            } else if (child instanceof FormContainer.Section){
                out.addSection((FormContainer.Section)child);
            } else {
                throw new IllegalStateException("Attempt to add child to FormContainer of type "+child.getClass()+".  Only FormContainer.Section and FormContainer.Field supported");
            }
        }
        return out;
    }

    @Override
    public Object parseConstraint(String constraint) {
        return null;
    }
}
