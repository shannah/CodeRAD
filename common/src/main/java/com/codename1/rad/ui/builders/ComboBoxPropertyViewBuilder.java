package com.codename1.rad.ui.builders;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.propertyviews.ComboBoxPropertyView;
import com.codename1.rad.ui.PropertyView;
import com.codename1.rad.ui.ViewContext;
import com.codename1.ui.CheckBox;
import com.codename1.ui.ComboBox;

import java.util.Map;

@RAD(tag={"comboBoxPropertyView", "radComboBox"})
public class ComboBoxPropertyViewBuilder extends PropertyViewBuilder<ComboBox> {

    private ComboBox comboBox;

    public ComboBoxPropertyViewBuilder(@Inject ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }

    public void setComboBox(@Inject ComboBox comboBox) {
        this.comboBox = comboBox;
    }


    @Override
    public ComboBoxPropertyView build() {
        if (fieldNode == null) {
            throw new IllegalStateException("CheckBoxPropertyView requires tag to be set");
        }
        return new ComboBoxPropertyView(comboBox == null ? new ComboBox() : comboBox, getEntity(), fieldNode);
    }

    @Override
    public ComboBoxPropertyView getComponent() {
        return (ComboBoxPropertyView)super.getComponent();
    }
}
