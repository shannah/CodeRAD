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
package com.codename1.rad.ui;

import com.codename1.rad.nodes.ComponentDecoratorNode;
import com.codename1.rad.nodes.Node;
import com.codename1.ui.Component;

/**
 *
 * @author shannah
 */
public class ComponentDecorators extends NodeList {

    public ComponentDecorators(NodeList nodes) {
        add(nodes);
    }
    
    @Override
    public void add(Node... nodes) {
        for (Node n : nodes) {
            if (!(n instanceof ComponentDecoratorNode)) {
                throw new IllegalArgumentException("ComponentDecorators can only accept ComponentDecoratorNode type");
            }
        }
        super.add(nodes);
    }
    
    public void decorate(Component cmp) {
        for (Node n : this) {
            if (!(n instanceof ComponentDecoratorNode)) {
                continue;
            }
            ComponentDecoratorNode cdn = (ComponentDecoratorNode)n;
            cdn.getValue().decorate(cmp);
        }
    }

    
    
}
