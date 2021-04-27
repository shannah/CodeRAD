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
import com.codename1.rad.controllers.FormController;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.ui.Toolbar;

/**
 * A FormController using {@link LoginView} as its view.  This wraps
 * {@link LoginViewController}, setting itself as the controller.
 * @author shannah
 */
public class LoginFormController extends FormController {
    
    private LoginViewController loginCtl;
    public LoginFormController(Controller parent) {
        super(parent);

    }

    @Override
    protected void onStartController() {
        super.onStartController();
        loginCtl = createLoginViewController();
        setView(loginCtl.getView());
        Toolbar tb = new Toolbar();
        getView().setToolbar(tb);
        tb.getTitleComponent().setHidden(true);
        tb.hideToolbar();

    }

    protected LoginViewController createLoginViewController() {
        LoginViewController ctl = new LoginViewController(this);

        return ctl;
    }


    public void setLoginViewDelegate(LoginViewDelegate delegate) {
        addLookup(LoginViewDelegate.class, delegate);
    }

    public LoginViewDelegate getLoginViewDelegate() {
        return lookup(LoginViewDelegate.class);
    }


}
