package ca.weblite.shared.components;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.controllers.ActionSupport;
import com.codename1.rad.controllers.AppSectionController;
import com.codename1.rad.controllers.FormController;
import com.codename1.rad.ui.AbstractComponentBuilder;
import com.codename1.rad.ui.ComponentBuilder;
import com.codename1.rad.ui.ViewContext;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.plaf.Style;

import java.util.Map;

import static com.codename1.ui.ComponentSelector.$;

@RAD(tag={"collapsibleHeader", "collapsibleHeaderContainer"})
public class CollapsibleHeaderContainerBuilder extends AbstractComponentBuilder<CollapsibleHeaderContainer> {

    private Component titleBar;
    private String scrollableComponent;

    public CollapsibleHeaderContainerBuilder(@Inject ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }

    public CollapsibleHeaderContainerBuilder titleBar(@Inject Component titleBar) {
        if (titleBar.equals(getContext().getEntityView())) return this;
        this.titleBar = (Component)titleBar;
        return this;
    }

    /**
     * Optional selector for the scrollable component that the collapsible header should "track".  If not set
     * then it just uses the body component.
     * @param selector
     * @return
     */
    public CollapsibleHeaderContainerBuilder scrollableComponent(String selector) {
        this.scrollableComponent = selector;
        return this;
    }



    @Override
    public CollapsibleHeaderContainer build() {

        CollapsibleHeaderContainer out = new CollapsibleHeaderContainer();

        getContext().getController().addViewDecorator(view -> {
            if (view instanceof Form) {
                return view;
            }

            Component body = view;
            if (!(body instanceof Container)) {
                body = BorderLayout.center(body);
                body.stripMarginAndPadding();
            }

            Component scrollable = (Component)getContext().getEntityView();
            if (scrollableComponent != null) {
                scrollable = $(scrollableComponent, body).asComponent();
                if (!(scrollable instanceof Container)) {
                    scrollable = BorderLayout.center(scrollable);
                    scrollable.stripMarginAndPadding();
                }
            }
            if (titleBar == null) {
                Container titleBar = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER_ABSOLUTE));
                titleBar.setSafeArea(true);
                titleBar.setUIID("TitleArea");

                if (getContext().getController().getFormController().hasBackCommand()) {
                    Button back = new Button();
                    FontImage.setIcon(back, FontImage.MATERIAL_ARROW_BACK_IOS, -1);
                    titleBar.add(BorderLayout.WEST, back);
                    back.addActionListener(evt->{
                        evt.consume();
                        ActionSupport.dispatchEvent(new FormController.FormBackEvent(back));
                    });

                }

                AppSectionController sectionCtl = getContext().getController().getSectionController();
                if (sectionCtl != null) {
                    Button done = new Button("Done");
                    done.addActionListener(evt->{
                        evt.consume();
                        ActionSupport.dispatchEvent(new AppSectionController.ExitSectionEvent(done));
                    });
                    titleBar.add(BorderLayout.EAST, done);
                }

                Label titleLbl = new Label();
                titleLbl.setUIID("Title");
                if (getContext().getController().getFormController().getTitle() != null) {
                    titleLbl.setText(getContext().getController().getFormController().getTitle());
                }
                titleBar.add(BorderLayout.CENTER, titleLbl);
                this.titleBar = titleBar;
            }
            if (!(titleBar instanceof Container)) {
                titleBar = BorderLayout.center(titleBar);
                titleBar.stripMarginAndPadding();
            }
            if (body.getParent() != null) {
                throw new IllegalStateException("Error creating collapsible header.  Body container is already added to another parent.  Body container was "+body+". Parent is "+body.getParent());
            }
            if (titleBar.getParent() != null) {
                throw new IllegalStateException("Error creating collapsible header.  titleBar container is already added to another parent.  titleBar container was "+titleBar+". Parent is "+titleBar.getParent());
            }


            out.init(
                    (Container)titleBar,
                    (Container)body,
                    (Container)scrollable
            );

            

            return out;


        });
        getContext().getController().getFormController().setAddTitleBar(false);
        doNotAddToParentContainer(out);
        return out;
    }

    @Override
    public Object parseConstraint(String constraint) {
        return null;
    }




}
