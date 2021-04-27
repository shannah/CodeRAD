/*
 * The MIT License
 *
 * Copyright 2021 shannah.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.codename1.rad.components.loginform;

import com.codename1.io.Log;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.ActionNode.ActionNodeEvent;
import com.codename1.rad.nodes.ViewNode;
import com.codename1.rad.propertyviews.LabelPropertyView;
import com.codename1.rad.propertyviews.SpanLabelPropertyView;
import com.codename1.rad.propertyviews.TextFieldPropertyView;
import com.codename1.rad.ui.*;

import static com.codename1.rad.util.NonNull.empty;
import static com.codename1.rad.util.NonNull.with;
import com.codename1.ui.CN;
import com.codename1.ui.Container;
import com.codename1.ui.Label;
import com.codename1.ui.TextArea;
import com.codename1.ui.UIFragment;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.GridLayout;
import java.io.IOException;

/**
 *
 * @author shannah
 */
public class LoginView extends AbstractEntityView implements LoginViewSchema {
    TextFieldPropertyView usernameView,
            passwordView;
    LabelPropertyView titleView;
    SpanLabelPropertyView errorMessageView;
    
    public LoginView(LoginViewModel model, ViewNode node) {
        super(model, node);
        model.setState(LoginViewModel.State.Idle);
        UIBuilder ui = new UIBuilder(model, node);
        usernameView = ui.textField(loginUsername);
        with(usernameView.getComponent(), cmp -> {
            cmp.setHint("Phone, email or username");
            cmp.setUIID((String)getParam(textfieldUiid, "TextField"));
        });
        passwordView = ui.textField(loginPassword);
        with(passwordView.getComponent(), cmp -> {
            cmp.setConstraint(TextArea.PASSWORD);
            cmp.setHint("Password");
            cmp.setUIID((String)getParam(textfieldUiid, "TextField"));
            
        });
        titleView = ui.label(title);
        with(titleView.getComponent(), cmp -> {
            cmp.setUIID((String)getParam(titleUiid, "PageTitle"));      
        });
        
        errorMessageView = ui.spanLabel(errorMessage);
        
        setLayout(new BorderLayout());
        
        UIFragment fragment = getTemplate();
        
        
        //Container center = new Container(BoxLayout.y());
        //center.add(titleView).add(usernameView).add(passwordView);
        fragment.set("title", titleView);
        fragment.set("errormessage", errorMessageView);
        fragment.set("loginusername", usernameView);
        fragment.set("loginpassword", passwordView);
        
        
        Container south = new Container(new BorderLayout());
        boolean forgotPasswordActionFound = with(node.getInheritedAction(forgotPasswordAction), forgot -> {
            //south.add(BorderLayout.WEST, forgot.createView(model));
            fragment.set("forgotpasswordaction", forgot.createView(model));
        });
        if (!forgotPasswordActionFound) {
            fragment.set("forgotpasswordaction", new Label());
        }
        boolean loginActionFound = with(node.getInheritedAction(loginAction), action -> {
            System.out.println("Setting up login action in LoginView");
            // We are making changes to the action so we need to be responsible
            // and not change the original action - so we create a proxy.
            action = (ActionNode)action.createProxy(node);
            
            // Make it so that the login button is only enabled if the view state is Idle
            // and the login username and password are both non-empty.
            // NOTE:  This may have to be made more elaborate if alternate login
            // methods are added that don't require username and password.
            ActionNode.mutator(action).enabledCondition(e -> {
                System.out.println("here");
                return model.getState() == LoginViewModel.State.Idle && 
                        !empty(model.getLoginUsername()) && !empty(model.getLoginPassword());
            });
            
            // Add a listener that is run when the action event is triggered before it is 
            // propagated to controllers to prevent it from firing if already authenticated.

            action.addActionListener(evt -> {
                System.out.println("In LoginView. login action listener");
                if (model.getState() == LoginViewModel.State.Authenticating) {
                    // Authentication in progress
                    evt.consume();
                }
            });


            
            // Add callback to run after the login event is dispatched so we can find out
            // if any async login handling is going on.  If any listeners have added an async
            // resource to the event, that means that it is processing so we will set
            // the state to Authenticating - and then set it back to idle when the
            // async task completes.

            action.addAfterActionCallback(actionEvent -> {
                with(actionEvent, ActionNodeEvent.class, ane -> {
                    with(ane.getAsyncResource(), task -> {
                        
                        model.setState(LoginViewModel.State.Authenticating);
                        task.onResult((res, err) -> {
                            model.setState(LoginViewModel.State.Idle);
                        });
                        
                    });
                    
                });
                
            });

            // Add the action button to the bottom right (following twitter layout).
            //south.add(BorderLayout.EAST, action.createView(model));
            fragment.set("loginaction", action.createView(model));
        });
        if (!loginActionFound) {
            fragment.set("loginaction", new Label());
        }
        
        Actions actions = node.getInheritedActions(topLeftActions);
        
        fragment.set("topleftactions", actions.createHorizontalGrid(model));
        
        actions = node.getInheritedActions(topRightActions);
        
        fragment.set("toprightactions", actions.createHorizontalGrid(model));
        
        Slot hslot = new Slot(headerSlot, this);
        fragment.set("headercontent", hslot);

        Slot fslot = new Slot(footerSlot, this);
        fragment.set("footercontent", fslot);

        setSafeAreaRoot(true);
        with(fragment.findById("top-actions"), Container.class, cnt -> {
            cnt.setSafeArea(true);

        });
        with(fragment.findById("bottom-actions"), Container.class, cnt -> {
            cnt.setSafeArea(true);

        });

        add(BorderLayout.CENTER, fragment.getView());
    
       
        
    }

    @Override
    public void update() {
        
    }

    @Override
    public void commit() {
        
    }
    
    /**
     * A builder for creating a {@link LoginView}.
     */
    public static class Builder {
        //private String textfieldUiid, titleUiid;
        //private ActionNode loginAction, forgotPasswordAction, registerAction;
        private ViewNode node;
        private LoginViewModel model;
        
        public Builder(ViewNode node) {
            if (node ==null) node = new ViewNode();
            this.node = node;
        }
        
        public Builder textfieldUiid(String uiid) {
            node.setAttributes(UI.param(textfieldUiid, uiid));
            return this;
        }
        
        public Builder titleUiid(String uiid) {
            node.setAttributes(UI.param(titleUiid, uiid));
            return this;
        }
        
        public Builder loginAction(ActionNode login) {
            node.setAttributes(UI.actions(loginAction, login));
            return this;
        }
        
        public Builder forgotPasswordAction(ActionNode action) {
            node.setAttributes(UI.actions(forgotPasswordAction, action));
            return this;
        }
        
        public Builder registerAction(ActionNode action) {
            node.setAttributes(UI.actions(registerAction, action));
            return this;
        }
        
        public Builder model(LoginViewModel model) {
            this.model = model;
            return this;
        }
        
        public ViewNode buildNode() {
            return node;
        }
        
        
        
        public LoginView build() {
            return new LoginView(model == null ? new LoginViewModelImpl() : model, node);
        }
    }
    
    public static Builder builder(ViewNode node) {
        return new Builder(node);
    }
    
    
    public static Builder builder() {
        return new Builder(null);
    }
    
    private static UIFragment template;
    protected UIFragment getTemplate() {
        if (template == null) {
            template = UIFragment.parseXML(CN.getResourceAsStream("/com_codename1_rad_components_loginform_LoginView.xml"));
        }
        return template;
    }


    public void purgeTemplateCache() {
        template = null;
    }
    
}
