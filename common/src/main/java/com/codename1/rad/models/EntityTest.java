/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

import com.codename1.rad.attributes.Condition;
import com.codename1.rad.attributes.SelectedCondition;
import com.codename1.rad.models.Entity;
import com.codename1.rad.nodes.ActionNode;

/**
 * Encapsulates a boolean test performed against an {@link Entity}.  This is primarily used by {@link SelectedCondition}, {@link ActionNode.EnabledCondition}, and {@link Condition} as attributes of
 * {@link ActionNode} to allow actions to test their state when an entity is updated.
 * @author shannah
 * 
 * @see com.codename1.rad.ui.UI#condition(com.codename1.rad.models.EntityTest) 
 * @see com.codename1.rad.ui.UI#selectedCondition(com.codename1.rad.models.EntityTest) 
 * @see com.codename1.rad.ui.UI#enabledCondition(com.codename1.rad.models.EntityTest) 
 */
public interface EntityTest {
    public boolean test(Entity entity);
}
