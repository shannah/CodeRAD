package com.codename1.rad.ui.builders;

import com.codename1.components.FloatingActionButton;
import com.codename1.rad.annotations.Inject;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.ui.AbstractComponentBuilder;
import com.codename1.rad.ui.ComponentBuilder;
import com.codename1.rad.ui.EntityView;
import com.codename1.rad.ui.ViewContext;
import com.codename1.ui.Component;
import com.codename1.ui.ComponentSelector;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;

import java.util.Map;

import static com.codename1.ui.ComponentSelector.$;

/**
 * Builder for the {@link FloatingActionButton}.
 *
 * === Usage
 *
 * Add the `<fab/>` tag into your view.
 */
@RAD(tag={"fab", "floatingActionButton"})
public class FloatingActionButtonBuilder extends AbstractComponentBuilder<FloatingActionButton> {
    private char icon;
    private String uiid;

    /**
     * Selector identifying target component to bind to.
     */
    private String target;
    

    public FloatingActionButtonBuilder(@Inject ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }

    /**
     * Sets the UIID of the button.
     * @param uiid
     * @return
     */
    public FloatingActionButtonBuilder uiid(String uiid) {
        this.uiid = uiid;
        return this;
    }

    public FloatingActionButtonBuilder icon(char icon) {
        this.icon = icon;
        return this;
    }

    /**
     * Sets a selector to specify the "target" component for this floating action button.  The target component
     * is the one that the button will hover above, and anchor itself to.  If no target is specified, then the fab
     * will target the "parent" container in the component hierarchy.
     * @param target Selector that identifies target component.  See {@link ComponentSelector}
     * @return
     */
    public FloatingActionButtonBuilder target(String target) {
        this.target = target;
        return this;
    }


    private boolean done;

    @Override
    public FloatingActionButton build() {
        if (icon == 0) icon = FontImage.MATERIAL_ADD;
        FloatingActionButton out;
        if (uiid == null) {
            out = FloatingActionButton.createFAB(icon);
        } else {
            out = FloatingActionButton.createFAB(icon, uiid);
        }
        // We don't want the fab added to the direct parent.
        doNotAddToParentContainer(out);

        EntityView ev = getContext().getEntityView();
        if (ev != null) {
            // Register a decorator that will be executed when the ViewController calls setView()
            // This is when we will wrap the target element
            getContext().getController().addViewDecorator(cmp -> {
                if (done) return cmp;
                done = true;

                Component parentContainer = getParentContainer();
                if (target != null) {
                    Component targetComponent = $(target, (Container)getContext().getEntityView()).asComponent();
                    if (targetComponent != null) {
                        parentContainer = targetComponent;
                    }

                }
                Container dummy = new Container();
                Container parentParent = parentContainer.getParent();
                parentParent.replace(parentContainer, dummy, null);
                Container wrapped = out.bindFabToContainer(parentContainer);
                parentParent.replace(dummy, wrapped, null);
                return cmp;
            });

        }
        return out;

    }


    @Override
    public Object parseConstraint(String constraint) {
        return null;
    }
}
