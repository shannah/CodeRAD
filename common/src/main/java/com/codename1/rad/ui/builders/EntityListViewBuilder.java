package com.codename1.rad.ui.builders;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.models.EntityList;
import com.codename1.rad.models.EntityListProvider;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.ListNode;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.ui.AbstractComponentBuilder;
import com.codename1.rad.ui.EntityListCellRenderer;
import com.codename1.rad.ui.EntityView;
import com.codename1.rad.ui.ViewContext;
import com.codename1.rad.ui.entityviews.EntityListView;

import java.util.Map;

@RAD(tag={"entityList", "entityListView"})
public class EntityListViewBuilder extends AbstractComponentBuilder<EntityListView> {
    private EntityListView.Builder builder = new EntityListView.Builder();
    public EntityListViewBuilder(@Inject ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }


    public EntityListViewBuilder scrollableY(boolean scrollableY) {
        builder.scrollableY(scrollableY);
        return this;
    }




    public EntityListViewBuilder listLayout(EntityListView.RowLayout layout) {
        builder.listLayout(layout);
        return this;
    }

    public EntityListViewBuilder columns(int columns) {
        builder.columns(columns);
        return this;
    }

    public EntityListViewBuilder landscapeColumns(int columns) {
        builder.landscapeColumns(columns);
        return this;
    }

    public EntityListViewBuilder animateRemovals(boolean animate) {
        builder.animateRemovals(animate);
        return this;
    }

    public EntityListViewBuilder animateInsertions(boolean animate) {
        builder.animateInsertions(animate);
        return this;
    }

    public EntityListViewBuilder refreshAction(ActionNode.Category category) {

        ActionNode n = getContext().getNode().getInheritedAction(category);
        if (n != null) {
            builder.refreshAction(n);
        }
        return this;

    }

    public EntityListViewBuilder loadMoreAction(ActionNode.Category category) {

        ActionNode n = getContext().getNode().getInheritedAction(category);
        if (n != null) {
            builder.loadMoreAction(n);
        }
        return this;
    }

    public EntityListViewBuilder addAction(ActionNode.Category category) {

        ActionNode n = getContext().getNode().getInheritedAction(category);
        if (n != null) {
            builder.addAction(n);
        }
        return this;
    }

    public EntityListViewBuilder selectAction(ActionNode.Category category) {
        ActionNode n = getContext().getNode().getInheritedAction(category);
        if (n != null) {
            builder.selectAction(n);
        }
        return this;
    }

    public EntityListViewBuilder removeAction(ActionNode.Category category) {

        ActionNode n = getContext().getNode().getInheritedAction(category);
        if (n != null) {
            builder.removeAction(n);
        }
        return this;
    }

    public EntityListViewBuilder renderer(@Inject EntityListCellRenderer renderer) {
        builder.renderer(renderer);
        return this;
    }

    public EntityListViewBuilder provider(@Inject EntityListProvider provider) {
        builder.provider(provider);
        return this;
    }

    public EntityListViewBuilder providerLookup(Class cls) {
        builder.providerLookup(cls);
        return this;
    }

    public EntityListViewBuilder model(EntityList model) {
        builder.model(model);
        return this;
    }

    public EntityListViewBuilder node(ListNode node) {
        builder.node(node);
        return this;
    }

    public EntityListViewBuilder parentNode(@Inject Node parentNode) {
        builder.parentNode(parentNode);
        return this;
    }




    @Override
    public EntityListView build() {
        return builder.build();
    }

    @Override
    public Object parseConstraint(String constraint) {
        return null;
    }
}
