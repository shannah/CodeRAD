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

/**
 * An interface for decorating PropertyViews. If you add this (via {@link com.codename1.rad.attributes.PropertyViewDecoratorAttribute} or
 * {@link com.codename1.rad.nodes.PropertyViewDecoratorNode}) to a {@link FieldNode}, its {@link #decorate(com.codename1.rad.ui.PropertyView) }
 * method will be executed on the {@link PropertyView} just after it is created by the {@link DefaultPropertyViewFactory}.
 * @author shannah
 * 
 * @see com.codename1.rad.attributes.PropertyViewDecoratorAttribute
 * @see com.codename1.rad.nodes.PropertyViewDecoratorNode
 * 
 */
public interface PropertyViewDecorator {
    /**
     * Decorates a property view.
     * @param propertyView The property view to decorate.
     * @return The decorated property view.  Generally the same object that was received as an argument.
     */
    public PropertyView decorate(PropertyView propertyView);
}
