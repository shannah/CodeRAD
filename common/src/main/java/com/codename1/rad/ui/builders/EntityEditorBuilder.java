package com.codename1.rad.ui.builders;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.models.Attribute;
import com.codename1.rad.models.AttributeSet;
import com.codename1.rad.ui.*;
import com.codename1.ui.Component;

import java.util.ArrayList;
import java.util.Map;

@RAD(tag="entityEditor")
public class EntityEditorBuilder extends AbstractComponentBuilder {

    private ArrayList<Attribute> atts = new ArrayList<Attribute>();

    public EntityEditorBuilder(@Inject ViewContext context, String tagName, Map attributes) {
        super(context, tagName, attributes);
    }

    @Override
    public Component build() {
        return new EntityEditor(getContext().getEntity(), new UI() {
            {
                form(atts.toArray(new Attribute[atts.size()]));
            }
        });
    }

    public EntityEditorBuilder addAttribute(Attribute attribute) {
        this.atts.add(attribute);

        return this;
    }

    @Override
    public Object parseConstraint(String constraint) {
        return null;
    }
}
