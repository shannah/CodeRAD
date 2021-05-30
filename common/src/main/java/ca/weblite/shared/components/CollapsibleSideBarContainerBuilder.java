package ca.weblite.shared.components;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.ui.AbstractComponentBuilder;
import com.codename1.rad.ui.ViewContext;
import com.codename1.rad.ui.builders.AbstractEntityViewBuilder;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Form;

import java.util.Map;
import java.util.Objects;
@RAD(tag={"sidebar", "collapsibleSidebarContainer", "collapsibleSidebar"})
public class CollapsibleSideBarContainerBuilder extends AbstractComponentBuilder<CollapsibleSideBarContainer> {

    private Component sidebar;

    public CollapsibleSideBarContainerBuilder(@Inject ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }



    

    public CollapsibleSideBarContainerBuilder sidebar(@Inject Component sidebar) {
        if (this.sidebar != null || Objects.equals(sidebar, getContext().getEntityView())) return this;
        this.sidebar = sidebar;
        return this;
    }

    @Override
    public CollapsibleSideBarContainer build() {
        CollapsibleSideBarContainer out = new CollapsibleSideBarContainer(new Container(), new Container());
        getContext().getController().getFormController().addViewDecorator(view -> {
            if (view instanceof Form) return view;
            if (sidebar == null) sidebar = new Container();
            return new CollapsibleSideBarContainer(sidebar, view);
        });
        doNotAddToParentContainer(out);
        return out;
    }

    @Override
    public Object parseConstraint(String constraint) {
        return null;
    }

    public static interface SidebarContent {

    }
}
