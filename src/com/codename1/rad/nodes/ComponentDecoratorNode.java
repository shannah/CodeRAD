/*
 * Copyright 2020 shannah.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codename1.rad.nodes;

import com.codename1.rad.ui.ComponentDecorator;

/**
 *
 * @author shannah
 */
public class ComponentDecoratorNode extends Node<ComponentDecorator> implements Proxyable  {
    public ComponentDecoratorNode(ComponentDecorator decorator) {
        super(decorator);
    }
    
    @Override
    public Node createProxy(Node parent) {
        ComponentDecoratorNode out = new ComponentDecoratorNode(null);
        out.setProxying(this);
        out.setParent(parent);
        return out;
    }

    @Override
    public boolean canProxy() {
        return true;
    }

    
}
