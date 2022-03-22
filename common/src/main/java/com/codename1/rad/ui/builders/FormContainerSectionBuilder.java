package com.codename1.rad.ui.builders;

import ca.weblite.shared.components.FormContainer;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.ui.AbstractComponentBuilder;
import com.codename1.rad.ui.ViewContext;
import com.codename1.ui.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RAD(tag = "section")
public class FormContainerSectionBuilder extends AbstractComponentBuilder<FormContainer.Section> {
    private List<Component> queuedChildren = new ArrayList<Component>();
    private int columns=1;

    public FormContainerSectionBuilder(ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }



    public void addChild(Component component) {
        if (component instanceof FormContainer.Field) {
            queuedChildren.add(component);
        } else {
            throw new IllegalArgumentException("FormContainer can only have FormContainer.Section and FormContainer.Field components as children");
        }
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getColumns() {
        return columns;
    }

    @Override
    public FormContainer.Section build() {
        FormContainer.Section out = new FormContainer.Section();
        out.setColumns(columns);
        for (Component child : queuedChildren) {
            if (child instanceof FormContainer.Field) {
                out.addField((FormContainer.Field)child);
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
