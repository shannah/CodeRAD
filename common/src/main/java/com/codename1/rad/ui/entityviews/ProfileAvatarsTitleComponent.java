/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui.entityviews;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.controllers.ControllerEvent;
import com.codename1.rad.controllers.ViewController;
import com.codename1.rad.layouts.FanLayout;
import com.codename1.rad.models.EntityList;

import com.codename1.rad.models.Property;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.ActionNode.ActionNodeEvent;
import com.codename1.rad.nodes.ActionNode.Category;
import com.codename1.rad.nodes.ListNode;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.nodes.ViewNode;
import com.codename1.rad.schemas.Thing;
import com.codename1.rad.ui.AbstractEntityView;
import com.codename1.rad.ui.Actions;
import com.codename1.rad.ui.EntityView;
import com.codename1.rad.ui.UI;
import com.codename1.rad.ui.menus.PopupActionsMenu;
import static com.codename1.ui.CN.SOUTH;
import com.codename1.ui.Component;
import static com.codename1.ui.ComponentSelector.$;
import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.Sheet;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.plaf.Border;
import java.util.ArrayList;
import com.codename1.rad.models.Entity;

/**
 * A component with profile avatars of one or more profiles.  This component is appropriate to use as a
 * form's Title component in forms that involve interaction between people, such as in a chat
 * form.
 * 
 * .A ProfileAvatarsTitleComponent rendering two entities that have {@link Thing#thumbnailUrl} properties.
 * image::https://shannah.github.io/RADChatApp/images/Image-210220-020916.612.png[ProvileAvatarsTitleComponent]
 * 
 * === View Model Requirements
 * 
 * For the view model, this view expects an EntityList where each entity in the list contains an "icon",
 * and a "name".  The individual avatars are rendered as {@link ProfileAvatarView}, so you can refer to its
 * documentation for details the view model requirements for each individual profile.
 * 
 * === Supported Actions
 * 
 * The following actions are supported on this view:
 * 
 *  . {@link #PROFILE_AVATAR_TITLE_COMPONENT_CLICKED}
 *  . {@link #PROFILE_AVATAR_TITLE_COMPONENT_LONG_PRESS}
 *  . {@link #PROFILE_AVATAR_CLICKED}
 *  . {@link #PROFILE_AVATAR_LONG_PRESS}
 *  . {@link #PROFILE_AVATAR_TITLE_COMPONENT_CLICKED_MENU}
 *  . {@link #PROFILE_AVATAR_TITLE_COMPONENT_LONG_PRESS_MENU}
 *  . {@link #PROFILE_AVATAR_CLICKED_MENU}
 *  . {@link #PROFILE_AVATAR_LONG_PRESS_MENU}
 * 
 * 
 * 
 * @author shannah
 */
public class ProfileAvatarsTitleComponent extends AbstractEntityView<EntityList<?>> {
    
    /**
     * Action registered with individual profile avatar views to so that we can intercept
     * click events.
     */
    private static final ActionNode interceptAvatarClicked = UI.action();
    
    /**
     * Action registered with individual profile avatar views to so that we can intercept
     * longpress events.
     */
    private static final ActionNode interceptAvatarLongPress = UI.action();
    
    /**
     * Action fired on click event.
     */
    public static final Category PROFILE_AVATAR_TITLE_COMPONENT_CLICKED = new Category();
    
    /**
     * Action fired on long press.
     */
    public static final Category PROFILE_AVATAR_TITLE_COMPONENT_LONG_PRESS = new Category();
    
    /**
     * Action fired when individual profile avatar is clicked.
     * @see ProfileAvatarView#PROFILE_AVATAR_CLICKED
     */
    public static final Category PROFILE_AVATAR_CLICKED = ProfileAvatarView.PROFILE_AVATAR_CLICKED;
    
    /**
     * Action fired when individual profile avatar is long pressed.
     * @see ProfileAvatarView#PROFILE_AVATAR_LONG_PRESS
     */
    public static final Category PROFILE_AVATAR_LONG_PRESS = ProfileAvatarView.PROFILE_AVATAR_LONG_PRESS;
    
    /**
     * Actions displayed in a popup menu when individual avatar is clicked.
     * @see ProfileAvatarView#PROFILE_AVATAR_CLICKED_MENU
     */
    public static final Category PROFILE_AVATAR_CLICKED_MENU = ProfileAvatarView.PROFILE_AVATAR_CLICKED_MENU;
    
    /**
     * Actions displayed in popup menu when individual avatar is longpressed.
     * @see ProfileAvatarView#PROFILE_AVATAR_LONG_PRESS_MENU
     */
    public static final Category PROFILE_AVATAR_LONG_PRESS_MENU = ProfileAvatarView.PROFILE_AVATAR_LONG_PRESS_MENU;
    
    /**
     * Actions displayed in popup menu when this component is clicked
     */
    public static final Category PROFILE_AVATAR_TITLE_COMPONENT_CLICKED_MENU = new Category();
    
    /**
     * Actions displayed in popup menu when this component is longpressed.
     */
    public static final Category PROFILE_AVATAR_TITLE_COMPONENT_LONG_PRESS_MENU = new Category();
    
    /**
     * Wrapper containing all of the avatars.
     */
    private Container wrapper;
    private ViewNode node;
    private float avatarSizeMM = 5f;
    
    /**
     * A controller for the wrapper containing all of the avatar views so that we can
     * intercept the events before they propagate up.  We need to set the parent
     * on bind so that some events will propagate up to the top.
     */
    private final ViewController avatarWrapperViewController = new ViewController(null) {

        public ViewNode getViewNode() {
            return ProfileAvatarsTitleComponent.this.node;
        }
        
        private void handleClick(ControllerEvent evt) {
            handleEvent(evt, interceptAvatarClicked, PROFILE_AVATAR_TITLE_COMPONENT_CLICKED, PROFILE_AVATAR_TITLE_COMPONENT_CLICKED_MENU, PROFILE_AVATAR_CLICKED, PROFILE_AVATAR_CLICKED_MENU);
        }
        
        private void handleLongPress(ControllerEvent evt) {
            handleEvent(evt, interceptAvatarLongPress, PROFILE_AVATAR_TITLE_COMPONENT_LONG_PRESS,  PROFILE_AVATAR_TITLE_COMPONENT_LONG_PRESS_MENU, PROFILE_AVATAR_LONG_PRESS, PROFILE_AVATAR_LONG_PRESS_MENU);
        }
        
        private void handleEvent(ControllerEvent evt, ActionNode interceptEvent, Category titleActionCategory, Category titleMenuCategory, Category avatarActionCategory, Category avatarMenuCatetory) {
            ActionNodeEvent ane;
            if ((ane =ActionNode.getActionNodeEvent(evt, interceptEvent)) != null) {
                // There's only one avatar, so we'll first try to propagate this event
                
                ane.consume();
                
                // If application doesn't consume the ACCOUNT_AVATAR_CLICKED
                // we'll sent it the avatar title component clicked event.
                ActionNode action = getViewNode().getInheritedAction(titleActionCategory);
                if (action != null && action.isEnabled(getEntity())) {
                    ActionEvent e2 = action.fireEvent(getEntity(), ProfileAvatarsTitleComponent.this);
                    if (e2.isConsumed()) {

                        // The application consumed the avatar click event so we don't proceed
                        // to the title component clicked.
                        return;
                    }
                }
                
                // See if there are any actions registered in the title menu
                Actions menu = getViewNode().getInheritedActions(titleMenuCategory).getEnabled(ane.getEntity());
                if (!menu.isEmpty()) {

                    PopupActionsMenu m = new PopupActionsMenu(menu, getEntity(), ProfileAvatarsTitleComponent.this);
                    m.setCommandsLayout(new GridLayout(1, menu.size()));
                    m.showPopupDialog(ProfileAvatarsTitleComponent.this);
                    return;
                }
                
                if (getEntity().size() == 1) {
                    // There was only one avatar in tht title component
                    // Let's process the clicked action first for this avatar
                    
                    action = getViewNode().getInheritedAction(avatarActionCategory);
                    if (action != null && action.isEnabled(getEntity())) {
                        
                        ActionEvent e2 = action.fireEvent(ane.getContext());
                        if (e2.isConsumed()) {
                            
                            // The application consumed the avatar click event so we don't proceed
                            // to the title component clicked.
                            return;
                        }
                    }
                    
                    
                    
                    // See if there are any actions registered in the avatar menu.
                    menu = getViewNode().getInheritedActions(avatarMenuCatetory).getEnabled(ane.getEntity());
                    if (!menu.isEmpty()) {
                        
                        new PopupActionsMenu(menu, ane.getEntity(), ane.getContext().getEventSource()).showPopupDialog(ane.getContext().getEventSource());
                        return;
                    }

                } else if (getEntity().size() > 1) {
                    //  There is more than one avatar, so we'll display a menu
                   // Let's find out if it is worth displaying the menu at all.  Need to check if any of the
                   // avatars have click actions or click menus registered.
                   EntityList filteredProfiles = new EntityList();
                   ViewNode viewNode = ProfileAvatarsTitleComponent.this.node;
                   Actions clickMenu = getViewNode().getInheritedActions(avatarMenuCatetory);
                   ActionNode clickAction = getViewNode().getInheritedAction(avatarActionCategory);
                   for (Entity profile : getEntity()) {
                       
                       Actions filteredClickMenu = clickMenu.getEnabled(profile);
                       if ((clickAction != null && clickAction.isEnabled(profile)) || !filteredClickMenu.isEmpty()) {
                           filteredProfiles.add(profile);
                       }
                   }
                   
                   if (filteredProfiles.size() == 0) {
                       return;
                   }
                   ListNode accountListNode = new ListNode(
                           
                   );
                   if (clickAction != null) {
                       accountListNode.setAttributes(UI.actions(ProfileListView.ACCOUNT_LIST_ROW_SELECTED, clickAction));
                   }
                   if (!clickMenu.isEmpty()) {
                       accountListNode.setAttributes(UI.actions(ProfileListView.ACCOUNT_LIST_ROW_ACTIONS, clickMenu));
                   }
                   accountListNode.setParent(getViewNode());
                   ProfileListView accountList = new ProfileListView(filteredProfiles, accountListNode, avatarSizeMM);
                   
                   Sheet sheet = new Sheet(Sheet.findContainingSheet(wrapper), "Select Profile");
                   sheet.getContentPane().setLayout(new BorderLayout());
                   sheet.getContentPane().add(BorderLayout.CENTER, accountList);
                   sheet.show();
                   
                   
                }
                return;
            }
        }
        
        @Override
        public void actionPerformed(ControllerEvent evt) {
            handleClick(evt);
            handleLongPress(evt);
            super.actionPerformed(evt);
        }
        
    };
    
    /**
     * Creates individual avatar component.
     * @param row
     * @return 
     */
    private ProfileAvatarView createAvatar(Entity row) {
         
            // We will intercept the individual avatar actions by inserting our own actions here.
            // We will then handle the clicks and long presses ourselves, because 
            // we may need to expand the list of avatars and allow the user to 
            // choose an avatar from the list.
            ViewNode childNode = new ViewNode(
                    UI.actions(ProfileAvatarView.PROFILE_AVATAR_CLICKED, interceptAvatarClicked),
                    UI.actions(ProfileAvatarView.PROFILE_AVATAR_LONG_PRESS, interceptAvatarLongPress)
            );
            childNode.setParent(node);
            ProfileAvatarView v = new ProfileAvatarView(row, childNode, avatarSizeMM);
            
            return v;
    }
    
    /**
     * Listener to add/remove avatars when profiles are added or removed from the view model.
     */
    private final ActionListener<EntityList.EntityListEvent> listListener = evt->{
        if (evt instanceof EntityList.EntityListInvalidatedEvent) {
            wrapper.removeAll();
            int len = getEntity().size();
            for (int i = len-1; i>=0; i--) {
            //for (Entity child : getEntity()) {
                Entity child = getEntity().get(i);
                ProfileAvatarView v = createAvatar(child);
                wrapper.add(v);
            }
            Form f = getComponentForm();
            if (f != null) {
                revalidateLater();
            }
            return;
        }
        if (evt instanceof EntityList.EntityAddedEvent) {
            ProfileAvatarView v = createAvatar((((EntityList.EntityAddedEvent)evt)).getEntity());
            wrapper.addComponent(0, v);
            v.setX(wrapper.getWidth());
            v.setY(0);
            v.setWidth(v.getPreferredW());
            v.setHeight(wrapper.getHeight() - wrapper.getStyle().getVerticalPadding());
            wrapper.animateHierarchy(300);
            
        } else if (evt instanceof EntityList.EntityRemovedEvent) {
            ArrayList<Component> toRemove = new ArrayList<>();
            int len = wrapper.getComponentCount();
            for (int i=0; i<len; i++) {
                Component cmp = wrapper.getComponentAt(i);
                if (cmp instanceof EntityView) {
                    EntityView ev = (EntityView)cmp;
                    if (ev.getEntity() == ((EntityList.EntityRemovedEvent) evt).getEntity()) {
                        toRemove.add(cmp);
                        break;
                    }
                        
                }
            }
            if (!toRemove.isEmpty()) {
                for (Component cmp : toRemove) {
                    cmp.remove();
                }
                
                wrapper.animateHierarchy(300);
            }
            
        }
    };
    
    
    
    

    
    
    /**
     * Creates a title component.
     * @param entity An EntityList containg profiles.  See {@link ProfileAvatarView} for information on properties
     * and tags that should be present for avatars.
     * @param node The view node for this title component.  
     * @param avatarSizeMM The size in millimeters of the avatars.
     */
    public ProfileAvatarsTitleComponent(@Inject EntityList entity, @Inject ViewNode node, float avatarSizeMM) {
        super(entity);
        this.node = node;
        this.avatarSizeMM = avatarSizeMM;
        initUI();
        
        
    }
    
    private void initUI() {
        
        
        setLayout(new BorderLayout());
        wrapper = new Container(new FanLayout(FanLayout.X_AXIS));
        avatarWrapperViewController.setView(wrapper);
        $(wrapper).selectAllStyles().setPadding(0).setMargin(0).setBorder(Border.createEmpty());
        int len = getEntity().size();
        for (int i = len-1; i>=0; i--) {
        //for (Entity child : getEntity()) {
            Entity child = getEntity().get(i);
            ProfileAvatarView v = createAvatar(child);
            wrapper.add(v);
        }
        add(CENTER, wrapper);
        StringBuilder text = new StringBuilder();
        Property nameProp = getEntity().getRowType().findProperty(Thing.name);
        if (getEntity().size() > 0) {
            
            if (nameProp != null) {
                text.append(getEntity().get(0).getEntity().getText(nameProp));
            }
        }
        if (getEntity().size() == 2) {
            if (nameProp != null) {
                text.append(" & ").append(getEntity().get(1).getEntity().getText(nameProp));
            }
        }
        if (getEntity().size() > 2) {
            if (text.length() == 0) {
                text.append(getEntity().size()).append(" People");
            } else {
                text.append(" and ").append(getEntity().size()-1).append(" others");
            }
        }
        
        Label lbl = new Label(text.toString());
        lbl.setUIID("AccountAvatarsTitleComponentText");
        add(SOUTH, FlowLayout.encloseCenter(lbl));
        
        
        
    }

    @Override
    protected void bindImpl() {
        
        avatarWrapperViewController.setParent(ViewController.getViewController(this));
        ((EntityList)getEntity()).addActionListener(listListener);
        
        
    }

    @Override
    protected void unbindImpl() {
        avatarWrapperViewController.setParent(null);
        ((EntityList)getEntity()).removeActionListener(listListener);

    }

    
    
    @Override
    public void update() {
        
        
    }

    @Override
    public void commit() {
        
    }

    @Override
    public Node getViewNode() {
        return node;
    }
    
}
