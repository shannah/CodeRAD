package com.codename1.rad.ui.builders;

import com.codename1.components.FloatingActionButton;
import com.codename1.rad.annotations.Inject;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.ui.AbstractComponentBuilder;
import com.codename1.rad.ui.ComponentBuilder;
import com.codename1.rad.ui.EntityView;
import com.codename1.rad.ui.ViewContext;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;

import java.util.Map;

@RAD(tag={"fab", "floatingActionButton"})
public class FloatingActionButtonBuilder extends AbstractComponentBuilder<FloatingActionButton> {
    private char icon;
    private String uiid;
    private boolean isBound;
    

    public FloatingActionButtonBuilder(@Inject ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }

    public FloatingActionButtonBuilder uiid(String uiid) {
        this.uiid = uiid;
        return this;
    }

    public FloatingActionButtonBuilder icon(char icon) {
        this.icon = icon;
        return this;
    }



    @Override
    public FloatingActionButton build() {
        if (icon == 0) icon = FontImage.MATERIAL_ADD;
        FloatingActionButton out;
        if (uiid == null) {
            out = FloatingActionButton.createFAB(icon);
        } else {
            out = FloatingActionButton.createFAB(icon, uiid);
        }
        out.putClientProperty("RAD_NO_ADD", true); // marker to prevent ViewProcessor from adding it to the hierarchy

        EntityView ev = getContext().getEntityView();
        if (ev != null) {
            getContext().getController().addViewDecorator(cmp -> {
                if (cmp == ev) {
                    return out.bindFabToContainer(cmp);
                }
                return cmp;
            });

        }
        return out;

    }


    @Override
    public Object parseConstraint(String constraint) {
        return null;
    }
}
