package com.codename1.rad.ui.builders;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.controllers.ControllerEvent;
import com.codename1.rad.models.EntityList;
import com.codename1.rad.models.EntityListProvider;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.ListNode;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.ui.*;
import com.codename1.rad.ui.entityviews.EntityListView;
import com.codename1.ui.events.ActionListener;

import java.util.Map;

import static com.codename1.rad.util.NonNull.with;

@RAD(tag={"entityList", "entityListView"})
public class EntityListViewBuilder extends AbstractComponentBuilder<EntityListView> {
    private EntityListView.Builder builder = new EntityListView.Builder();
    public EntityListViewBuilder(@Inject ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
        builder.parentNode(context.getNode());
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
        ActionNode refreshAction = UI.action();
        ActionNode loadMoreAction = UI.action();
        getContext().getController().addActionListener(refreshAction, provider);
        getContext().getController().addActionListener(loadMoreAction, provider);
        builder.refreshAction(refreshAction).loadMoreAction(loadMoreAction);
        return this;
    }

    public EntityListViewBuilder provider(Class cls) {
        ActionNode refreshAction = UI.action();
        ActionNode loadMoreAction = UI.action();
        ActionListener<ActionNode.ActionNodeEvent> l = evt -> {
            with(getContext().getController().lookup(cls), EntityListProvider.class, provider -> {
                provider.actionPerformed(evt);
            });
        };
        getContext().getController().addActionListener(loadMoreAction, l);
        getContext().getController().addActionListener(refreshAction, l);
        builder
                .refreshAction(refreshAction)
                .loadMoreAction(loadMoreAction)
        ;


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
