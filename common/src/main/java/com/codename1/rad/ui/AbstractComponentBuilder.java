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
package com.codename1.rad.ui;

import com.codename1.ui.Component;
import com.codename1.ui.Container;

import java.util.Map;

/**
 *
 * @author shannah
 */
public abstract class AbstractComponentBuilder<T extends Component> implements ComponentBuilder<T> {
    private final ViewContext context;
    private T component;
    private String tagName;
    private Map<String,String> attributes;
    private Container parentContainer;
    protected AbstractComponentBuilder(ViewContext context, String tagName, Map<String,String> attributes) {
        this.tagName = tagName;
        this.context = context;
        this.attributes = attributes;
    }

    @Override
    public void setParentContainer(Container cnt, Object arg) {
        this.parentContainer = cnt;
    }

    public ViewContext getContext() {
        return context;
    }
    
    public T getComponent() {
        if (component == null) {
            component = build();
        }
        return component;
    }



    public String getTagName() {
        return tagName;
    }

    public Map<String,String> getAttributes() {
        return attributes;
    }

    public String getAttribute(String name) {
        return attributes.get(name);
    }

    public boolean hasAttribute(String name) {
        return attributes.containsKey(name);
    }

    /**
     * Marks the given component so that it won't be added to the parent container.
     * @param cmp
     */
    public static void doNotAddToParentContainer(Component cmp) {
        cmp.putClientProperty("RAD_NO_ADD", true);
    }

    public Container getParentContainer() {
        return parentContainer;
    }

}
