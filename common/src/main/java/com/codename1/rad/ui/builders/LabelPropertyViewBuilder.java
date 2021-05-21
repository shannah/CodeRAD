package com.codename1.rad.ui.builders;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.propertyviews.LabelPropertyView;
import com.codename1.rad.propertyviews.TextFieldPropertyView;
import com.codename1.rad.schemas.Thing;
import com.codename1.rad.ui.ViewContext;
import com.codename1.ui.Label;
import com.codename1.ui.TextField;

import java.util.Map;

@RAD(tag={"labelPropertyView", "radLabel"})
public class LabelPropertyViewBuilder extends PropertyViewBuilder<Label> {
    private Label label;
    public LabelPropertyViewBuilder(ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }

    public LabelPropertyViewBuilder label(@Inject Label label) {
        this.label = label;
        return this;
    }

    @Override
    public LabelPropertyView build() {
        if (fieldNode == null) {
            tag(Thing.name);
        }
        return new LabelPropertyView(label == null ? new Label() : label, getContext().getEntity(), fieldNode);
    }

    @Override
    public LabelPropertyView getComponent() {
        return (LabelPropertyView)super.getComponent();
    }
}
