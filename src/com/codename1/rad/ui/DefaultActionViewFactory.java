/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.attributes.UIID;
import com.codename1.rad.events.DefaultEventFactory;
import com.codename1.rad.events.EventContext;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.EventFactoryNode;
import com.codename1.rad.models.Entity;
import com.codename1.ui.Button;
import com.codename1.ui.Component;
import static com.codename1.ui.Component.BOTTOM;
import static com.codename1.ui.Component.LEFT;
import static com.codename1.ui.Component.RIGHT;
import static com.codename1.ui.Component.TOP;
import com.codename1.rad.controllers.ActionSupport;
import com.codename1.ui.events.ActionEvent;

/**
 *
 * @author shannah
 */
public class DefaultActionViewFactory implements ActionViewFactory {

    @Override
    public Component createActionView(Entity entity, ActionNode action) {
        
        boolean text = action.isTextStyle();
        boolean includeIcon = action.isIconStyle();
        Button button = new Button();
        if (action.getLabel() != null && text) {
            button.setText(action.getLabel().getValue());
        }
        if (action.getMaterialIcon() != null && includeIcon) {
            button.setMaterialIcon(action.getMaterialIcon().getValue());
        }
        if (action.getImageIcon() != null && includeIcon) {
            button.setIcon(action.getImageIcon().getValue());
        }
        if (action.getPressed() != action) {
            ActionNode pressed = action.getPressed();
            if (pressed.getImageIcon() != null && includeIcon) {
                button.setPressedIcon(pressed.getImageIcon().getValue());
            }
            
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
        
        UIID uiid = action.getUIID();
        if (uiid != null) {
            button.setUIID(uiid.getValue());
        }
        
        button.addActionListener(evt->{
            EventFactoryNode eventFactory = action.getEventFactory();
            if (eventFactory == null) {
                eventFactory = new EventFactoryNode(new DefaultEventFactory());
            }
            EventContext eventContext = new EventContext();
            eventContext.setEntity(entity);
            eventContext.setAction(action);
            eventContext.setEventSource(button); 

            ActionEvent actionEvent = eventFactory.getValue().createEvent(eventContext);
            
            ActionSupport.dispatchEvent(actionEvent);
        });
            
        return button;
        
    }
    
}
