package com.codename1.rad.ui.builders;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.models.Entity;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.ui.AbstractComponentBuilder;
import com.codename1.rad.ui.AbstractEntityView;
import com.codename1.rad.ui.ViewContext;

import java.util.Map;

public abstract class AbstractEntityViewBuilder<T extends AbstractEntityView> extends AbstractComponentBuilder<T> {
    protected Node node;
    protected Entity entity;



    protected AbstractEntityViewBuilder(ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }

    public AbstractEntityViewBuilder<T> node(@Inject Node node) {
        this.node = node;
        return this;
    }

    public AbstractEntityViewBuilder<T> entity(@Inject Entity entity) {
        this.entity = entity;
        return this;
    }

    @Override
    public Object parseConstraint(String constraint) {
        return null;
    }
}
