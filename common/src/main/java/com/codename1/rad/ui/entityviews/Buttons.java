package com.codename1.rad.ui.entityviews;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.attributes.UIID;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.schemas.Action;
import com.codename1.rad.ui.AbstractEntityView;
import com.codename1.rad.ui.Actions;
import com.codename1.rad.ui.UI;
import com.codename1.rad.ui.ViewContext;
import com.codename1.rad.ui.menus.ActionSheet;
import com.codename1.rad.ui.menus.PopupActionsMenu;
import com.codename1.ui.CN;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.Layout;

import java.util.Objects;

public class Buttons extends AbstractEntityView {
    private ActionNode.Category actionCategory;
    private String buttonUiid = "Button";

    private boolean requiresRefresh;
    private int limit = -1;
    private OverflowMenuStyle overflowMenuStyle = OverflowMenuStyle.ActionSheet;
    private boolean built = false;

    public static enum OverflowMenuStyle {
        ActionSheet,
        PopupMenu,
        None
    }

    public Buttons(@Inject ViewContext context) {
        super(context);

    }

    @Override
    public void update() {
        if (requiresRefresh) {
            rebuild();
        }

    }

    @Override
    public void commit() {

    }


    private void rebuild() {
        built = true;
        requiresRefresh = false;
        removeAll();
        this.actionCategory = actionCategory;
        Actions actions = getViewNode().getInheritedActions(this.actionCategory);
        Actions overflowActions = null;

        actions = actions.proxy(getViewNode());
        if (overflowMenuStyle != OverflowMenuStyle.None && limit >= 0 && actions.size() >= limit) { // Note: >= because need to fit overflow button under limit
            overflowActions = new Actions();
            Actions tempActions = new Actions();
            int len = actions.size();
            int index = -1;
            UIID uiid = null;
            for (ActionNode action : actions) {

                index++;

                if (index < limit - 1) {
                    tempActions.add(action);
                } else {
                    overflowActions.add(action);
                }

                if (uiid == null) {
                    uiid = action.getUIID();
                }
            }
            actions = tempActions;
            if (overflowActions.size() > 0) {
                ActionNode overflowAction = new ActionNode();
                // Try to match the UIID of the other actions
                if (uiid != null) {
                    overflowAction.setAttributes(uiid);
                }
                overflowAction.setParent(getViewNode());
                final Actions fOverflowActions = overflowActions;

                overflowAction.addActionListener(e -> {
                    e.consume();
                    showOverflowMenu(fOverflowActions);
                });
                actions.add(overflowAction);
            }


        }

        actions.setAttributesIfNotSet(UI.uiid(buttonUiid));
        actions.addToContainer(this, getEntity());
        revalidateWithAnimationSafety();
    }

    public void setActionCategory(ActionNode.Category actionCategory) {
        if (this.actionCategory != actionCategory) {
            this.actionCategory = actionCategory;
            requiresRefresh = true;

        }
    }

    public ActionNode.Category getActionCategory() {
        return actionCategory;
    }

    public void setButtonUiid(String uiid) {
        if (!Objects.equals(uiid, buttonUiid)) {
            this.buttonUiid = uiid;
            requiresRefresh = true;
        }

    }

    public String getButtonUiid() {
        return buttonUiid;
    }

    public void setOverflowMenuStyle(OverflowMenuStyle style) {
        if (overflowMenuStyle != style) {
            overflowMenuStyle = style;
            requiresRefresh = true;
        }
    }

    public OverflowMenuStyle getOverflowMenuStyle() {
        return overflowMenuStyle;
    }

    public void setLimit(int limit) {
        if (limit != this.limit) {
            this.limit = limit;
            requiresRefresh = true;
        }
    }

    public int getLimit() {
        return limit;
    }


    private void showOverflowMenu(Actions overflowActions) {
        switch (overflowMenuStyle) {
            case ActionSheet: {
                ActionSheet sheet = new ActionSheet(null, getEntity(), overflowActions);
                if (CN.isTablet() || !CN.isPortrait()) {
                    sheet.setPosition(BorderLayout.CENTER);
                } else {
                    sheet.setPosition(BorderLayout.SOUTH);
                }
                sheet.show();
                break;
            }

            case PopupMenu: {
                PopupActionsMenu menu = new PopupActionsMenu(overflowActions, getEntity(), getComponentAt(getComponentCount()-1));
                menu.showPopupDialog(getComponentAt(getComponentCount()-1));
                break;
            }
        }

    }

    @Override
    protected void initComponent() {
        if (!built) rebuild();
        super.initComponent();
    }
}
