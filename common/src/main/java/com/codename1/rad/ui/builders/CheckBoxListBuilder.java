package com.codename1.rad.ui.builders;

import com.codename1.components.CheckBoxList;
import com.codename1.rad.ui.ViewContext;
import com.codename1.ui.list.DefaultListModel;
import com.codename1.ui.list.ListModel;
import com.codename1.ui.list.MultipleSelectionListModel;
import com.codename1.rad.annotations.RAD;

import java.util.Map;

@RAD(tag="checkBoxList")
public class CheckBoxListBuilder extends AbstractButtonListBuilder<CheckBoxList> {

    public CheckBoxListBuilder(ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }

    @Override
    public CheckBoxList build() {
        if (model == null) {
            throw new IllegalArgumentException("A model is required for CheckBoxList");
        }
        if (!(model instanceof MultipleSelectionListModel)) {
            throw new IllegalArgumentException("CheckBoxList required multiple selection list model");
        }
        return new CheckBoxList((MultipleSelectionListModel)model);
    }

    @Override
    public void setModel(ListModel model) {
        if (model instanceof DefaultListModel) {
            ((DefaultListModel)model).setMultiSelectionMode(true);
        }
        super.setModel(model);
    }

    @Override
    public Object parseConstraint(String constraint) {
        return null;
    }
}
