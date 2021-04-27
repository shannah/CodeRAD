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

import com.codename1.rad.models.Tag;
import com.codename1.rad.nodes.ActionNode.Category;
import com.codename1.rad.schemas.Page;
import com.codename1.rad.ui.ViewProperty;
import com.codename1.rad.ui.ViewPropertyParameter;

/**
 *
 * @author shannah
 */
public interface LoginViewSchema {
    public static final Tag loginUsername = new Tag("loginUsername"),
            loginPassword = new Tag("loginPassword"),
            title = Page.title,
            subtitle = Page.subtitle,
            errorMessage = new Tag("errorMessage"),
            state = new Tag("state"),
            headerSlot = new Tag("LoginView.headerSlot"),
            footerSlot = new Tag("LoginView.footerSlot")
    ;
    
    
    public static final Category loginAction = new Category("loginAction"),
            forgotPasswordAction = new Category("forgotPasswordAction"),
            registerAction = new Category("registerAction"),
            topLeftActions = new Category("topLeftActions"),
            topRightActions = new Category("topRightActions");
    
    public static final ViewProperty<String> 
            textfieldUiid = ViewProperty.stringProperty(),
            titleUiid = ViewProperty.stringProperty()
            ;
    
    
    
}
