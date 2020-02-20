/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui.entityviews;

import com.codename1.rad.models.Entity;
import com.codename1.rad.models.EntityList;
import com.codename1.rad.models.Property;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.ActionNode.Category;
import com.codename1.rad.nodes.ListNode;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.nodes.ViewNode;
import com.codename1.rad.schemas.Thing;
import com.codename1.rad.ui.AbstractEntityView;
import com.codename1.rad.ui.Actions;
import com.codename1.rad.ui.EntityListCellRenderer;
import com.codename1.rad.ui.EntityView;
import com.codename1.rad.ui.UI;
import com.codename1.ui.Button;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.layouts.BorderLayout;
import static com.codename1.ui.layouts.BorderLayout.EAST;
import static com.codename1.ui.layouts.BorderLayout.NORTH;
import static com.codename1.ui.layouts.BorderLayout.WEST;
import com.codename1.ui.layouts.GridLayout;
import java.util.Objects;

/**
 * A list view that can show a list of profiles.  Each row shows the profile avatar/icon,
 * name, and optionally a set of actions that will be rendered as buttons for the user to perform.  Row click 
 * events can be handled by registering an action in the {@link #ACCOUNT_LIST_ROW_SELECTED} category.  Row actions
 * can be assigned to the {@link #ACCOUNT_LIST_ROW_ACTIONS} category.
 * 
 * <h3>View Model Requirements</h3>
 * <p>This view will accept any {@link EntityList} where the row items are "profiles". For best results, each profile
 * should contain a name and icon, although it will fall back to suitable output if one or both of those fields are missing.</p>
 * <p>The icon is rendered using the {@link ProfileAvatarView} view, so see its docs for details on what information it needs.  A simple
 * solution, that will always work, is to make sure that the profile entities contain properties tagged with {@link Thing#name} and {@link Thing#thumbnailUrl}.</p>
 * <p>Since rows are rendered using {@link ProfileListRowView}, you can also see its docs for information about row view model requirements.</p>
 * 
 * <h3>Supported Actions</h3>
 * 
 * <p>The following actions are supported by this view:</p>
 * 
 * <ul>
 *  <li>{@link #ACCOUNT_LIST_ROW_ACTIONS}</li>
 *  <li>{@link #ACCOUNT_LIST_ROW_SELECTED}</li>
 * </ul>
 * 
 * @author shannah
 */
public class ProfileListView extends EntityListView {
    
    /**
     * Actions that can be performed on a single row of the list.  These will be rendered in the right
     * side of the row, as buttons.
     */
    public static final Category ACCOUNT_LIST_ROW_ACTIONS = new Category();
    
    /**
     * Category used to register an action to receive "click" events for a row.
     */
    public static final Category ACCOUNT_LIST_ROW_SELECTED = new Category();
    
    public ProfileListView(EntityList list, ListNode node, float avatarSizeMM) {
        super(list, decorateNode(node, avatarSizeMM));
    }
    
    private static ListNode decorateNode(ListNode n, float avatarSizeMM) {
        
        n.setAttributes(UI.cellRenderer(new ProfileListRowCellRenderer(avatarSizeMM)));
        return n;
    }

    /**
     * Row view for rendering a single row of the {@link ProfileListView}.
     * <h3>View Model Requirements</h3>
    * <p>This view will accept an entity that is a "profile".  Only needs to contain a name and icon, 
    * although it will fall back to suitable output if one or both of those fields are missing.</p>
    * <p>The icon is rendered using the {@link ProfileAvatarView} view, so see its docs for details on what information it needs.  A simple
    * solution, that will always work, is to make sure that the profile entities contain properties tagged with {@link Thing#name} and {@link Thing#thumbnailUrl}.</p>
    * 
    * <h3>Supported Actions</h3>
    * 
    * <p>The following actions are supported by this view:</p>
    * 
    * <ul>
    *  <li>{@link #ACCOUNT_LIST_ROW_ACTIONS}</li>
    *  <li>{@link #ACCOUNT_LIST_ROW_SELECTED}</li>
    * </ul>
     */
    public static class ProfileListRowView extends AbstractEntityView {
        
        /**
         * @see ProfileListView#ACCOUNT_LIST_ROW_ACTIONS
         * 
         */
        public static final Category ACCOUNT_LIST_ROW_ACTIONS = ProfileListView.ACCOUNT_LIST_ROW_ACTIONS;
        
        /**
         * @see ProfileListView#ACCOUNT_LIST_ROW_SELECTED
         */
        public static final Category ACCOUNT_LIST_ROW_SELECTED = ProfileListView.ACCOUNT_LIST_ROW_SELECTED;
        private ViewNode node;
        
        private Property nameProp;
        private com.codename1.ui.Label nameLabel;
        private Button leadButton = new Button();
        
        /**
         * Creates a row for the given profile entity.
         * @param profile The view model.  A profile entity.  Should implement the {@link Thing#name} and {@link ProfileAvatarView#icon} (or {@link Thing#thumbnailUrl}
         * tags.  This uses a {@link ProfileAvatarView} to render the icon, so see the view model requirements for that view for details.  In general, all you need is {@link Thing#name} and {@link ProfileAvatarView#icon} or {@link Thing#thumbnailUrl}.
         * @param node The view node.
         * @param avatarSizeMM The size to render the avatar in mm.
         */
        public ProfileListRowView(Entity profile, ViewNode node, float avatarSizeMM) {
            super(profile);
            setGrabsPointerEvents(true);
            setFocusable(true);
            this.node = node;
            
            setLayout(new BorderLayout());
            ProfileAvatarView avatar = new ProfileAvatarView(profile, avatarSizeMM);
            add(WEST, avatar);
            nameProp = profile.getEntityType().findProperty(Thing.name);
            nameLabel = new com.codename1.ui.Label();
            add(CENTER, nameLabel);
            
            Actions rowActions = node.getInheritedActions(ACCOUNT_LIST_ROW_ACTIONS);
            if (!rowActions.isEmpty()) {
                Container actionsCnt = new Container(new GridLayout(1, rowActions.size()));
                
                rowActions.addToContainer(actionsCnt, profile);
                for (Component child : actionsCnt) {
                    child.setBlockLead(true);
                }
                
                add(EAST, actionsCnt);
            }
            
            ActionNode action = node.getInheritedAction(ACCOUNT_LIST_ROW_SELECTED);
            if (action != null) {
                leadButton.addActionListener(evt->{
                    action.fireEvent(profile, this);
                });
            }
            leadButton.setVisible(false);
            leadButton.setHidden(true);
            setLeadComponent(leadButton);
            add(NORTH, leadButton);
            update();
            
        }
        
        @Override
        public void update() {
            String name = "";
            if (nameProp != null) {
                name = getEntity().getText(nameProp);
            }
            if (!Objects.equals(name, nameLabel.getText())) {
                nameLabel.setText(name);
            }
                    
        }

        @Override
        public void commit() {
            
        }

        @Override
        public Node getViewNode() {
            return node;
        }
        
    }
    
    /**
     * A list cell renderer for rendering rows of a list with {@link ProfileListRowView}.  This is used by {@link ProfileListView}
     * to render its rows.
     */
    public static class ProfileListRowCellRenderer implements EntityListCellRenderer {

        private float avatarSizeMM = 5f;
        
        /**
         * Creates a renderer for a row.
         * @param avatarSizeMM 
         */
        public ProfileListRowCellRenderer(float avatarSizeMM) {
            this.avatarSizeMM = avatarSizeMM;
        }
        
        @Override
        public EntityView getListCellRendererComponent(EntityListView list, Entity value, int index, boolean isSelected, boolean isFocused) {
            ViewNode node = new ViewNode();
            node.setParent(list.getViewNode());
            return new ProfileListRowView(value, node, avatarSizeMM);
        }
        
    }
    
}
