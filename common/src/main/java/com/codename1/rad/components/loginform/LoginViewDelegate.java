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

import com.codename1.rad.nodes.ActionNode.ActionNodeEvent;

/**
 * An interface that can be implemented to handle login in the {@link LoginView}.
 * 
 * @author shannah
 * @see LoginViewController#setLoginViewDelegate(LoginViewDelegate) 
 * @see LoginFormController#setLoginViewDelegate(LoginViewDelegate)
 * @since 2.0
 */
public interface LoginViewDelegate {

    /**
     * Triggered when the {@link LoginView#loginAction} action is triggered - which
     * is usually when the user presses the "Login" button.
     *
     * @param evt The event.
     */
    public void loginViewHandleLogin(ActionNodeEvent evt);

    /**
     * Triggered when the "Forgot password" button is pressed in the {@link LoginView}
     * @param evt
     */
    public void loginViewHandleForgotPassword(ActionNodeEvent evt);

    
}
