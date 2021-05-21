package com.codename1.rad.ui.builders;

import com.codename1.components.Switch;
import com.codename1.rad.annotations.Inject;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.propertyviews.LabelPropertyView;
import com.codename1.rad.propertyviews.SwitchPropertyView;
import com.codename1.rad.ui.ViewContext;
import com.codename1.ui.Label;

import java.util.Map;

@RAD(tag={"switchPropertyView", "radSwitch"})
public class SwitchPropertyViewBuilder extends PropertyViewBuilder<Switch> {
    private Switch _switch;
    public SwitchPropertyViewBuilder(ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }

    public SwitchPropertyViewBuilder setSwitch(@Inject Switch _switch) {
        this._switch = _switch;
        return this;
    }

    @Override
    public SwitchPropertyView build() {
        if (fieldNode == null) {
            throw new IllegalStateException("SwitchPropertyViewBuilder requires a tag to be set");
        }
        return new SwitchPropertyView(_switch == null ? new Switch() : _switch, getContext().getEntity(), fieldNode);
    }

    @Override
    public SwitchPropertyView getComponent() {
        return (SwitchPropertyView)super.getComponent();
    }
}
