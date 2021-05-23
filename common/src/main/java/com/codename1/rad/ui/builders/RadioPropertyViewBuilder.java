package com.codename1.rad.ui.builders;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.propertyviews.LabelPropertyView;
import com.codename1.rad.propertyviews.RadioPropertyView;
import com.codename1.rad.ui.ViewContext;
import com.codename1.ui.Label;
import com.codename1.ui.RadioButton;

import java.util.Map;

@RAD(tag={"radioPropertyView", "radRadio"})
public class RadioPropertyViewBuilder extends PropertyViewBuilder<RadioButton> {
    private RadioButton radioButton;
    public RadioPropertyViewBuilder(ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }

    public RadioPropertyViewBuilder radioButton(@Inject RadioButton radioButton) {
        this.radioButton = radioButton;
        return this;
    }

    @Override
    public RadioPropertyView build() {
        if (fieldNode != null) {
            throw new IllegalStateException("RadioPropertyViewBuilder requires tag to be set");
        }
        return new RadioPropertyView(radioButton == null ? new RadioButton() : radioButton, getEntity(), fieldNode);
    }

    @Override
    public RadioPropertyView getComponent() {
        return (RadioPropertyView)super.getComponent();
    }
}
