package com.codename1.rad.ui.builders;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.propertyviews.TextAreaPropertyView;
import com.codename1.rad.propertyviews.TextFieldPropertyView;
import com.codename1.rad.schemas.Comment;
import com.codename1.rad.ui.ViewContext;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;

import java.util.Map;

@RAD(tag={"textAreaPropertyView", "radTextArea"})
public class TextAreaPropertyViewBuilder extends PropertyViewBuilder<TextArea> {
    private TextArea textArea;
    public TextAreaPropertyViewBuilder(ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }

    public TextAreaPropertyViewBuilder textArea(@Inject TextArea textArea) {
        this.textArea = textArea;
        return this;
    }

    @Override
    public TextAreaPropertyView build() {
        if (fieldNode == null) {
            tag(Comment.text);
        }
        return new TextAreaPropertyView(textArea == null ? new TextArea() : textArea, getContext().getEntity(), fieldNode);
    }

    @Override
    public TextAreaPropertyView getComponent() {
        return (TextAreaPropertyView)super.getComponent();
    }
}
