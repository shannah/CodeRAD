/*
 * Copyright 2020 Codename One.
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
package com.codename1.rad.ui.menus;


import com.codename1.rad.annotations.Inject;
import com.codename1.rad.ui.Actions;
import com.codename1.ui.Button;
import static com.codename1.ui.ComponentSelector.$;
import com.codename1.ui.Sheet;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.rad.models.Entity;

/**
 *
 * @author shannah
 */
public class ActionSheet extends Sheet {
    
    public ActionSheet(Sheet parent, @Inject Entity entity, @Inject Actions actions) {
        super(parent, "");
        
        
        getContentPane().setLayout(BoxLayout.y());
        actions.addToContainer(getContentPane(), entity);
        $("*", getContentPane()).filter(c->{return (c instanceof Button);}).addActionListener(evt->{
            back();
        });
        
    }
}
