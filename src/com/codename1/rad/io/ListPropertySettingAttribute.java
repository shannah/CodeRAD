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
package com.codename1.rad.io;

import com.codename1.rad.models.Attribute;

/**
 * An attribute that can be attached to a node for setting the {@link ListPropertySetting} to use
 * when parsing a list property.  E.g. For setting whether list is replaced, updated, or appended to.
 */
public class ListPropertySettingAttribute extends Attribute<XMLElementParser.ListPropertySetting> {

    public ListPropertySettingAttribute(XMLElementParser.ListPropertySetting setting) {
        super(setting);
    }
    
}
