package com.codename1.rad.ui.builders;

import com.codename1.components.ButtonList;
import com.codename1.rad.ui.AbstractComponentBuilder;
import com.codename1.rad.ui.ViewContext;
import com.codename1.ui.list.ListModel;
import com.codename1.ui.list.MultipleSelectionListModel;

import java.util.Map;

public abstract class AbstractButtonListBuilder<T extends ButtonList> extends AbstractComponentBuilder<T> {
    protected ListModel model;
    protected AbstractButtonListBuilder(ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }

    public void setModel(ListModel model) {
        this.model = model;
    }

    public void setMultiSelectModel(MultipleSelectionListModel model) {
        this.model = model;
    }




}
