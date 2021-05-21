package com.codename1.rad.ui.builders;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.propertyviews.HTMLComponentPropertyView;
import com.codename1.rad.propertyviews.LabelPropertyView;
import com.codename1.rad.schemas.Thing;
import com.codename1.rad.ui.ViewContext;
import com.codename1.ui.Label;
import com.codename1.ui.html.HTMLComponent;

import java.util.Map;

@RAD(tag={"htmlComponentPropertyView", "radHtmlComponent"})
public class HTMLComponentPropertyViewBuilder extends PropertyViewBuilder<HTMLComponent> {
    private HTMLComponent htmlComponent;
    public HTMLComponentPropertyViewBuilder(ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }

    public HTMLComponentPropertyViewBuilder htmlComponent(@Inject HTMLComponent htmlComponent) {
        this.htmlComponent = htmlComponent;
        return this;
    }

    @Override
    public HTMLComponentPropertyView build() {
        if (fieldNode == null) {
            tag(Thing.description);
        }
        return new HTMLComponentPropertyView(htmlComponent == null ? new HTMLComponent() : htmlComponent, getContext().getEntity(), fieldNode);
    }

    @Override
    public HTMLComponentPropertyView getComponent() {
        return (HTMLComponentPropertyView)super.getComponent();
    }
}
