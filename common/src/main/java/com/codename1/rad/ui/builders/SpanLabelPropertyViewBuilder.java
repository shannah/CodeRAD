package com.codename1.rad.ui.builders;

import com.codename1.components.SpanLabel;
import com.codename1.rad.annotations.Inject;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.propertyviews.LabelPropertyView;
import com.codename1.rad.propertyviews.SpanLabelPropertyView;
import com.codename1.rad.schemas.Thing;
import com.codename1.rad.ui.ViewContext;

import java.util.Map;

@RAD(tag={"spanLabelPropertyView", "radSpanLabel"})
public class SpanLabelPropertyViewBuilder extends PropertyViewBuilder<SpanLabel> {
    private SpanLabel spanLabel;
    public SpanLabelPropertyViewBuilder(ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }

    public SpanLabelPropertyViewBuilder spanLabel(@Inject SpanLabel label) {
        this.spanLabel = label;
        return this;
    }

    @Override
    public SpanLabelPropertyView build() {
        if (fieldNode == null) {
            tag(Thing.description);
        }
        return new SpanLabelPropertyView(spanLabel == null ? new SpanLabel() : spanLabel, getEntity(), fieldNode);
    }

    @Override
    public SpanLabelPropertyView getComponent() {
        return (SpanLabelPropertyView)super.getComponent();
    }
}
