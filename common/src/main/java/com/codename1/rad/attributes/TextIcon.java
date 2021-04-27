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
package com.codename1.rad.attributes;

import com.codename1.rad.models.Attribute;

import com.codename1.rad.models.StringProvider;
import com.codename1.rad.models.Entity;

/**
 *
 * @author shannah
 */
public class TextIcon extends Attribute<String> {
    private StringProvider provider;
    
    public TextIcon(String text) {
        this(text, e->{
            return text;
        });
    }
    
    public TextIcon(String text, StringProvider provider) {
        super(text);
        this.provider = provider;
    }
    
    public String getValue(Entity context) {
        if (provider != null) {
            return provider.getString(context);
        } else {
            return getValue();
        }
    }
}
