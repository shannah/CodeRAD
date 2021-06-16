package com.codename1.rad.ui.builders;

import com.codename1.components.SplitPane;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.ui.AbstractComponentBuilder;
import com.codename1.rad.ui.ViewContext;
import com.codename1.ui.Component;
import com.codename1.ui.Container;

import java.util.Map;

@RAD(tag="splitPane")
public class SplitPaneBuilder extends AbstractComponentBuilder<SplitPane> {

    public static enum Orientation {
        Vertical,
        Horizontal
    }

    private Component left, right;

    private Orientation orientation = Orientation.Horizontal;
    private String minInset = "0", maxInset="100%", preferredInset="33%";


    public SplitPaneBuilder(ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }

    @Override
    public SplitPane build() {

        int orientationInt;
        switch (orientation) {
            case Horizontal: orientationInt = SplitPane.HORIZONTAL_SPLIT; break;
            case Vertical: orientationInt = SplitPane.VERTICAL_SPLIT; break;
            default: orientationInt =SplitPane.HORIZONTAL_SPLIT;

        }
        if (left == null) left = new Container();
        if (right == null) right = new Container();
        return new SplitPane(orientationInt, left, right, minInset, preferredInset, maxInset);
    }

    public void setMinInset(String minInset) {
        this.minInset = minInset;
    }

    public void setMaxInset(String maxInset) {
        this.maxInset = maxInset;
    }

    public void setPreferredInset(String preferredInset) {
        this.preferredInset = preferredInset;
    }

    public void addChild(Component cmp) {
        if (left == null) left = cmp;
        else if (right == null) right = cmp;
        else {
            throw new IllegalArgumentException("SplitPane can only contain two components");
        }
    }

    @Override
    public Object parseConstraint(String constraint) {
        return null;
    }
}
