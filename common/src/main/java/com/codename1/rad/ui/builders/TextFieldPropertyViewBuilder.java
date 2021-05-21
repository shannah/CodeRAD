package com.codename1.rad.ui.builders;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.propertyviews.TextFieldPropertyView;
import com.codename1.rad.schemas.Comment;
import com.codename1.rad.schemas.Thing;
import com.codename1.rad.ui.PropertyView;
import com.codename1.rad.ui.ViewContext;
import com.codename1.ui.TextField;

import java.util.Map;
@RAD(tag={"textFieldPropertyView", "radTextField"})
public class TextFieldPropertyViewBuilder extends PropertyViewBuilder<TextField> {
    private TextField textField;

    public TextFieldPropertyViewBuilder(ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }

    public TextFieldPropertyViewBuilder textField(@Inject TextField textField) {
        this.textField = textField;
        return this;
    }

    @Override
    public TextFieldPropertyView build() {
        if (fieldNode == null) {
            tag(Thing.name);
        }
        return new TextFieldPropertyView(textField != null ? textField : new TextField(), getContext().getEntity(), fieldNode);
    }

    @Override
    public TextFieldPropertyView getComponent() {
        return (TextFieldPropertyView)super.getComponent();
    }
}
