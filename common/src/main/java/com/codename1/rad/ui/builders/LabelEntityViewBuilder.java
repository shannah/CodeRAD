package com.codename1.rad.ui.builders;

import com.codename1.rad.models.Attribute;
import com.codename1.rad.models.Entity;
import com.codename1.rad.nodes.ViewNode;
import com.codename1.rad.ui.EntityEditor;
import com.codename1.rad.ui.UI;
import com.codename1.rad.ui.ViewContext;
import com.codename1.rad.ui.entityviews.LabelEntityView;
import com.codename1.rad.annotations.*;
import com.codename1.ui.CN;
import com.codename1.ui.Component;
import com.codename1.ui.Label;
import com.codename1.ui.plaf.Style;

import java.util.Map;

@RAD(tag="labelEntityView")
public class LabelEntityViewBuilder extends AbstractEntityViewBuilder<LabelEntityView> {
    public LabelEntityViewBuilder(@Inject ViewContext context, String tagName, Map attributes) {
        super(context, tagName, attributes);
    }

    private int iconWidth, iconHeight;


    public void setIconWidth(int width) {
        this.iconWidth = width;
    }

    public void setIconHeight(int height) {
        this.iconHeight = height;
    }

    @Override
    public LabelEntityView build() {
        iconWidth = (iconWidth <= 0) ? CN.convertToPixels(1.5f, Style.UNIT_TYPE_REM) : iconWidth;
        iconHeight = (iconHeight <= 0) ? CN.convertToPixels(1.5f, Style.UNIT_TYPE_REM) : iconHeight;

        return new LabelEntityView(getContext().getController().createViewContext(Entity.class, entity), new Label(), iconWidth, iconHeight);
    }


    @Override
    public Object parseConstraint(String constraint) {
        return null;
    }
}
