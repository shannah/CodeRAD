/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;


import com.codename1.ui.Form;
import com.codename1.rad.models.Entity;

/**
 * A form with an embedded {@link EntityEditor}.
 * @author shannah
 */
public class EntityForm extends Form {
    private EntityEditor editor;
    
    public EntityForm(Entity entity, UI uiDescriptor) {
        editor = new EntityEditor(entity, uiDescriptor, this);
    }
}
