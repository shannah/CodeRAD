package com.codename1.rad.ui.builders;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.ui.AbstractComponentBuilder;
import com.codename1.rad.ui.EntityView;
import com.codename1.ui.Component;

import java.util.Map;

/**
 * A component builder that takes a prebuilt component in its constructor, and thus acts more like a component
 * decorator.  This is used by the annotation processor for building components that don't have explicit builders
 * defined.
 */
public class SimpleComponentDecorator<T extends Component> extends AbstractComponentBuilder<T> {
    private T component;
    public SimpleComponentDecorator(@Inject T component, @Inject EntityView context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
        this.component = component;
    }

    @Override
    public T build() {
        return component;
    }

    @Override
    public Object parseConstraint(String constraint) {
        return null;
    }
}
