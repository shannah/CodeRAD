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

import com.codename1.rad.controllers.Controller;
import com.codename1.rad.controllers.ViewController;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.ActionNode.ActionNodeEvent;
import com.codename1.rad.nodes.ViewNode;
import com.codename1.rad.ui.UI;

import static com.codename1.rad.util.NonNull.with;


/**
 * A ViewController for the {@link LoginView}.  Use the {@link #setLoginViewDelegate(LoginViewDelegate)} to register
 * a delegate to handle common actions.
 * @author shannah
 * @since 2.0
 */
public class LoginViewController extends ViewController {
    

    
    public LoginViewController(Controller parent) {
        super(parent);

    }

    /**
     * This is run just before {@link #startController()}.  It can be overridden
     * by subclasses to modify the actions further.
     */
    protected void initControllerActions() {

        // We use extendAction here because we want to use the loginAction defined
        // in any parent Controller as the base of our action.
        // That will allow that parent controller to handle the action, if we choose
        // not to handle it here.

        // The loginAction is triggered when the user presses the Login button
        extendAction(LoginView.loginAction, action -> {
            ActionNode.mutator(action)
                    .overwrite(false)
                    .label("Log in")
                    .overwrite(true)
                    .name("LoginViewController.loginAction");
        });

        // The forgotPasswordAction is triggered when the user presses the
        // forgotPassword button.
        extendAction(LoginView.forgotPasswordAction, action -> {
            ActionNode.mutator(action)
                .overwrite(false)
                .label("Forgot password?")
                .overwrite(true)
                .name("LoginViewController.forgotPasswordAction");
        });

    }

    @Override
    protected void onStopController() {
        super.onStopController();
    }

    @Override
    protected void onStartController() {
        super.onStartController();
        setView(LoginView.builder(getViewNode())
                .model(createViewModel())
                .build());


        withLookup(LoginViewDelegate.class, delegate -> {
            with(getAction(LoginView.loginAction), action -> {
                addActionListener(action, evt -> {
                    delegate.loginViewHandleLogin(evt);
                });
            });
            with(getAction(LoginView.forgotPasswordAction), action -> {
                addActionListener(action, evt -> {
                    delegate.loginViewHandleForgotPassword(evt);
                });
            });
        });


    }

    public LoginViewModel getViewModel() {
        return (LoginViewModel)super.getViewModel();
    }

    
    @Override
    protected ViewNode createViewNode() {
        ViewNode n = super.createViewNode();
        /*
        n.setAttributes(
                getSingleActionsNode(LoginView.loginAction),
                getSingleActionsNode(LoginView.forgotPasswordAction)
        );
        */
        return n;
    }


    /**
     * Creates the view model to use for the loginview.  Subclasses can override
     * this method to provide an alternate {@link LoginViewModel} implementation.
     * @return
     */
    protected LoginViewModel createViewModel() {
        return new LoginViewModelImpl();
    }


    /**
     * Sets the delegate to handle events from the {@link LoginView}.
     * @param delegate The delegate to register.
     */
    public void setLoginViewDelegate(LoginViewDelegate delegate) {
        addLookup(LoginViewDelegate.class, delegate);
    }

    /**
     * Gets the registered LoginViewDelegate.
     * @return
     */
    public LoginViewDelegate getLoginViewDelegate() {
        return lookup(LoginViewDelegate.class);
    }
        
        
    
}
