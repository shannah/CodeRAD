package com.codename1.rad.ui.builders;

import com.codename1.components.CheckBoxList;
import com.codename1.components.RadioButtonList;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.ui.ViewContext;
import com.codename1.ui.Component;
import com.codename1.ui.list.DefaultListModel;
import com.codename1.ui.list.ListModel;
import com.codename1.ui.list.MultipleSelectionListModel;

import java.util.Map;

@RAD(tag="radioButtonList")
public class RadioButtonListBuilder extends AbstractButtonListBuilder<RadioButtonList> {

    public RadioButtonListBuilder(ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }

    @Override
    public RadioButtonList build() {
        if (model == null) {
            throw new IllegalArgumentException("A model is required for CheckBoxList");
        }
        return new RadioButtonList(model) {
            @Override
            protected Component decorateComponent(Object modelItem, Component b) {
                decorateButton(b);
                return super.decorateComponent(modelItem, b);
            }
        };
    }



    @Override
    public Object parseConstraint(String constraint) {
        return null;
    }
}
