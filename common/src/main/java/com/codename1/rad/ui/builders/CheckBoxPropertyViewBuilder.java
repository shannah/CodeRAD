package com.codename1.rad.ui.builders;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.propertyviews.CheckBoxPropertyView;
import com.codename1.rad.propertyviews.LabelPropertyView;
import com.codename1.rad.schemas.Thing;
import com.codename1.rad.ui.ViewContext;
import com.codename1.ui.CheckBox;
import com.codename1.ui.Label;

import java.util.Map;

@RAD(tag={"checkboxPropertyView", "radCheckBox"})
public class CheckBoxPropertyViewBuilder extends PropertyViewBuilder<CheckBox> {
    private CheckBox checkBox;
    public CheckBoxPropertyViewBuilder(ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }

    public CheckBoxPropertyViewBuilder checkBox(@Inject CheckBox checkBox) {
        this.checkBox = checkBox;
        return this;
    }

    @Override
    public CheckBoxPropertyView build() {
        if (fieldNode == null) {
            throw new IllegalStateException("CheckBoxPropertyView requires tag to be set");
        }
        return new CheckBoxPropertyView(checkBox == null ? new CheckBox() : checkBox, getEntity(), fieldNode);
    }

    @Override
    public CheckBoxPropertyView getComponent() {
        return (CheckBoxPropertyView)super.getComponent();
    }
}
