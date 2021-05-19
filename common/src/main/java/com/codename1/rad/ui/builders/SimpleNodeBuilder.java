package com.codename1.rad.ui.builders;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.nodes.AbstractNodeBuilder;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.nodes.NodeBuilder;
import com.codename1.rad.ui.EntityView;

import java.util.Map;

public class SimpleNodeBuilder<T extends Node> extends AbstractNodeBuilder<T> {
    private T node;

    protected SimpleNodeBuilder(@Inject T node, @Inject EntityView context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
        this.node = node;
    }


    public T getNode() {
        return node;
    }

    @Override
    public T build() {
        return node;
    }
}
