/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.attributes.Condition;
import com.codename1.rad.attributes.ImageIcon;
import com.codename1.rad.attributes.MaterialIcon;
import com.codename1.rad.attributes.SelectedCondition;
import com.codename1.rad.attributes.UIID;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.models.Entity;
import com.codename1.ui.Button;
import com.codename1.ui.Component;
import static com.codename1.ui.Component.BOTTOM;
import static com.codename1.ui.Component.LEFT;
import static com.codename1.ui.Component.RIGHT;
import static com.codename1.ui.Component.TOP;
import com.codename1.rad.models.PropertyChangeEvent;
import com.codename1.rad.nodes.ActionNode.EnabledCondition;
import com.codename1.ui.CheckBox;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.events.ActionListener;
import java.util.Objects;

/**
 *
 * @author shannah
 */
public class DefaultActionViewFactory implements ActionViewFactory {
    
    
    private static void update(Button btn, Entity entity, ActionNode action) {
        boolean repaint = false;
        boolean revalidate = false;
        Condition cond = action.getCondition();
        if (cond != null) {
            boolean hidden = !cond.getValue().test(entity);
            if (hidden != btn.isHidden()) {
                btn.setHidden(hidden);
                btn.setVisible(!hidden);
                revalidate = true;
            }

        }
        EnabledCondition enabledCond = action.getEnabledCondition();
        if (enabledCond != null) {
            boolean enabled = enabledCond.getValue().test(entity);
            if (enabled != btn.isEnabled()) {
                btn.setEnabled(enabled);
                repaint = true;
            }
        }
        if (btn instanceof CheckBox) {
            SelectedCondition selectedCond = action.getSelectedCondition();
            if (selectedCond != null) {
                boolean selected = selectedCond.getValue().test(entity);
                if (selected != btn.isSelected()) {
                    ((CheckBox)btn).setSelected(selected);
                    repaint = true;
                    ActionNode newState = selected ? action.getSelected() : action.getUnselected();
                    ActionNode oldState = selected ? action.getUnselected() : action.getSelected();
                    if (oldState != newState) {
                        String currText = btn.getText();
                        String newText = newState.getLabelText();
                        if (!newState.isTextStyle()) {
                            newText = "";
                        }
                        
                        if (!Objects.equals(newText, btn.getText())) {
                            btn.setText(newText);
                        }
                    }
                }
            }
        }
        if (revalidate || repaint) {
            Form f = btn.getComponentForm();
            if (f != null) {
                if (revalidate) {
                    Component entityView = findEntityViewParent(btn);
                    if (entityView instanceof Container) {
                        ((Container)entityView).revalidateWithAnimationSafety();
                    } else {
                        entityView.repaint();
                    }
                } else {
                    btn.repaint();
                }
            }
        }
    }
    
    private static void initUI(Button btn, Entity entity, ActionNode action) {
        
        boolean text = action.isTextStyle();
        boolean includeIcon = action.isIconStyle();
        UIID uiid = action.getUIID();
        if (uiid != null) {
            btn.setUIID(uiid.getValue());
        }
        
        Button button = btn;
        
        if (action.getLabel() != null && text) {
            button.setText(action.getLabel().getValue());
        }
        
        if (action.getImageIcon() != null && includeIcon) {
            button.setIcon(action.getImageIcon().getValue());
            if (action.getSelected() != action && action.getSelected().getImageIcon() != action.getImageIcon()) {
                button.setRolloverIcon(action.getSelected().getImageIcon().getValue());
            }
            if (action.getPressed() != action && action.getPressed().getImageIcon() != action.getImageIcon()) {
                button.setPressedIcon(action.getPressed().getImageIcon().getValue());
            }
            if (action.getDisabled() != action && action.getDisabled().getImageIcon() != action.getImageIcon()) {
                button.setDisabledIcon(action.getDisabled().getImageIcon().getValue());
            }
        }
        if (action.getMaterialIcon() != null && includeIcon) {
            char unselectedIcon = action.getMaterialIcon().getValue();
            char selectedIcon = action.getSelected().getMaterialIcon().getValue();
            char pressedIcon = action.getPressed().getMaterialIcon().getValue();
            char selectedPressed = pressedIcon;
            if (action.getPressed() == action) {
                selectedPressed = selectedIcon;
            }
            char disabledIcon = action.getDisabled().getMaterialIcon().getValue();
            //button.setMaterialIcon(action.getMaterialIcon().getValue());
            FontImage.setMaterialIcon(btn, new char[]{unselectedIcon, selectedIcon, pressedIcon, selectedPressed, disabledIcon}, -1);
        }
        

        if (includeIcon && text) {
            ActionStyle style = action.getActionStyle();
            if (style != null) {
                switch (style) {
                    case IconTop:
                        button.setTextPosition(BOTTOM);
                        break;
                    case IconBottom:
                        button.setTextPosition(TOP);
                        break;
                    case IconLeft:
                        button.setTextPosition(RIGHT);
                        break;
                    case IconRight:
                        button.setTextPosition(LEFT);
                }
            }
        }

        
        

        button.addActionListener(evt->{
            action.fireEvent(entity, button);
        });
        
        update(button, entity, action);
    }
    
    private static Component findEntityViewParent(Component start) {
        if (start instanceof EntityView) {
            return start;
        }
        if (start != null) {
            return findEntityViewParent(start.getParent());
        }
        return null;
    }
    
    private static class ActionButton extends Button {
        Entity entity;
        ActionNode action;
        
        private final ActionListener<PropertyChangeEvent> pcl = evt->{
            update(ActionButton.this, entity, action);
        };
        
        ActionButton(Entity entity, ActionNode action) {
            this.entity = entity;
            this.action = action;
            initUI(this, entity, action);
            
        
        }

        @Override
        protected void initComponent() {
            super.initComponent();
            entity.addPropertyChangeListener(pcl);
        }

        @Override
        protected void deinitialize() {
            entity.removePropertyChangeListener(pcl);
            super.deinitialize();
        }
        
        
    }
    
    private static class ActionToggleButton extends CheckBox {
        Entity entity;
        ActionNode action;
        
        private final ActionListener<PropertyChangeEvent> pcl = evt->{
            update(ActionToggleButton.this, entity, action);
        };
        
        ActionToggleButton(Entity entity, ActionNode action) {
            this.entity = entity;
            this.action = action;
            this.setToggle(true);
            initUI(this, entity, action);
            
        
        }

        @Override
        protected void initComponent() {
            super.initComponent();
            entity.addPropertyChangeListener(pcl);
        }

        @Override
        protected void deinitialize() {
            entity.removePropertyChangeListener(pcl);
            super.deinitialize();
        }
        
        
    }
    

    @Override
    public Component createActionView(Entity entity, ActionNode action) {
        SelectedCondition selectedCond = action.getSelectedCondition();
        if (selectedCond != null) {
            return new ActionToggleButton(entity, action);
        } else {
            return new ActionButton(entity, action);
        }
        
    }
    
}
