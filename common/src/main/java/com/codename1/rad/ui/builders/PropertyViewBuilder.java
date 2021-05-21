package com.codename1.rad.ui.builders;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.models.Tag;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.nodes.FormNode;
import com.codename1.rad.nodes.ViewNode;
import com.codename1.rad.ui.*;
import com.codename1.ui.Component;

import java.util.Map;

public abstract class PropertyViewBuilder<E extends Component> extends AbstractComponentBuilder<PropertyView<E>> {
    protected UIBuilder uiBuilder;
    protected FieldNode fieldNode;

    protected PropertyViewBuilder(ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
        uiBuilder = new UIBuilder(context.getEntity(), (ViewNode)context.getNode());


    }

    public PropertyViewBuilder<E> tag(Tag tag) {
        if (fieldNode == null) fieldNode = new FieldNode();
        fieldNode.setAttributes(UI.tags(tag));
        return this;
    }




    @Override
    public Object parseConstraint(String constraint) {
        return null;
    }
}
