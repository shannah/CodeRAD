package com.codename1.rad.ui.entityviews;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.attributes.UIID;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.ui.AbstractEntityView;
import com.codename1.rad.ui.Actions;
import com.codename1.rad.ui.ViewContext;
import com.codename1.rad.ui.menus.ActionSheet;
import com.codename1.rad.ui.menus.PopupActionsMenu;
import com.codename1.ui.CN;
import com.codename1.ui.Component;
import com.codename1.ui.ComponentSelector;
import com.codename1.ui.FontImage;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;

public class Buttons extends AbstractEntityView {
    private ActionNode.Category actionCategory;
    private int align = -1;

    private ComponentSelector.ComponentMapper buttonWrapper = cmp -> {
        switch(align) {
            case Component.LEFT: return BoxLayout.encloseX(cmp);
            case Component.RIGHT: return BoxLayout.encloseXRight(cmp);
            case Component.CENTER: return BoxLayout.encloseXCenter(cmp);
        }
        return cmp;
    };
    private ActionNode.Builder actionTemplate = ActionNode.builder();
    private ActionNode.Builder overflowActionTemplate = ActionNode.builder();
    private ActionNode.Builder overflowButtonAction = ActionNode.builder();
    
    

    private boolean requiresRefresh = true;
    private int limit = -1;
    private OverflowMenuStyle overflowMenuStyle = OverflowMenuStyle.ActionSheet;
    private boolean built = false;

    /**
     * Enum defining how a Buttons component should handle overflow actions.
     */
    public static enum OverflowMenuStyle {
        ActionSheet,
        PopupMenu,
        None
    }

    public void setAlign(int align) {
        this.align = align;
    }


    public int getAlign() {
        return align;
    }


    public Buttons(@Inject ViewContext context) {
        super(context);
        requiresRefresh = true;
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

    public ActionNode.Builder getActionTemplate() {
        return actionTemplate;
    }

    public ActionNode.Builder getOverflowActionTemplate() {
        return overflowActionTemplate;
    }

    public ActionNode.Builder getOverflowButtonAction() {
        return overflowButtonAction;
    }

   
    private void rebuild() {
        built = true;
        requiresRefresh = false;
        removeAll();
        this.actionCategory = actionCategory;
        Actions actions = getViewNode().getInheritedActions(this.actionCategory);
        Actions overflowActions = null;

        actions = actions.proxy(getViewNode());
        if (limit >= 0 && actions.size() >= limit) { // Note: >= because need to fit overflow button under limit
            overflowActions = new Actions();
            Actions tempActions = new Actions();
            int len = actions.size();
            int index = -1;
            UIID uiid = null;
            for (ActionNode action : actions) {

                index++;

                if (index < limit - (overflowMenuStyle == OverflowMenuStyle.None ? 0 : 1)) {
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
                ActionNode overflowAction = overflowButtonAction.icon(FontImage.MATERIAL_MORE_HORIZ).build();
                // Try to match the UIID of the other actions
                if (uiid != null) {
                    overflowAction.setAttributes(uiid);
                }

                overflowAction.setParent(getViewNode());
                final Actions fOverflowActions = overflowActions;
                overflowActions.copyAttributesIfNotExists(overflowActionTemplate.build());

                overflowAction.addActionListener(e -> {
                    e.consume();
                    showOverflowMenu(fOverflowActions);
                });
                if (overflowMenuStyle != OverflowMenuStyle.None) {
                    actions.add(overflowAction);
                }
            }


        }


        actions.copyAttributesIfNotExists(actionTemplate.build());

        actions.addToContainer(this, getEntity(), buttonWrapper);
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
