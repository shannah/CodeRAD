package com.codename1.rad.ui.builders;

import com.codename1.components.ButtonList;
import com.codename1.rad.annotations.Inject;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.propertyviews.ButtonListPropertyView;
import com.codename1.rad.propertyviews.LabelPropertyView;
import com.codename1.rad.schemas.Thing;
import com.codename1.rad.ui.ViewContext;
import com.codename1.ui.Label;
import com.codename1.ui.list.DefaultListModel;

import java.util.Map;

@RAD(tag={"buttonListPropertyView", "radButtonList"})
public class ButtonListPropertyViewBuilder extends PropertyViewBuilder<ButtonList> {
    private ButtonList buttonList;
    public ButtonListPropertyViewBuilder(ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }

    public ButtonListPropertyViewBuilder buttonList(@Inject ButtonList buttonList) {
        this.buttonList = buttonList;
        return this;
    }

    @Override
    public ButtonListPropertyView build() {
        if (fieldNode == null) {
            throw new IllegalStateException("ButtonListPropertyView requires tag to be set");
        }
        if (buttonList == null) {
            throw new IllegalStateException("ButtonListPropertyView requires buttonList to be set");
        }
        return new ButtonListPropertyView(buttonList, getEntity(), fieldNode);
    }

    @Override
    public ButtonListPropertyView getComponent() {
        return (ButtonListPropertyView)super.getComponent();
    }

    public static <T> DefaultListModel<T> createSingleSelectionListModel(Class<T> type, T... items) {
        DefaultListModel<T> out = new DefaultListModel<T>(items);
        out.setMultiSelectionMode(false);
        return out;
    }

    public static <T> DefaultListModel<T> createMultipleSelectionListModel(Class<T> type, T... items) {
        DefaultListModel<T> out =  new DefaultListModel<T>(items);
        out.setMultiSelectionMode(true);
        return out;
    }


}
