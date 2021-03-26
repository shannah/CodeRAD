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
package com.codename1.rad.controllers;

import com.codename1.ui.Component;

/**
 * A controller for a section of an app.  The primary purpose of this is to group a set of 
 * forms together into a section which can include common functionality.  
 * @author shannah
 */
public class AppSectionController extends Controller {
    public AppSectionController(Controller parent) {
        super(parent);
    }
    
    /**
     * An event that can be dispatched from anywhere inside an AppSection to exit the section.  
     * This will be converted into a FormBackEvent and propagated up the stack from the section.
     */
    public static class ExitSectionEvent extends ControllerEvent {
        
        public ExitSectionEvent(Component source) {
            super(source);
        }
    }

    @Override
    public void actionPerformed(ControllerEvent evt) {
        if (evt instanceof ExitSectionEvent) {
            evt.consume();
            dispatchEvent(new FormController.FormBackEvent(evt.getSource()));
        }
        super.actionPerformed(evt);
    }
    
    
}
