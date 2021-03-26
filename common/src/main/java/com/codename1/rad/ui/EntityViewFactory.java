/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.nodes.Node;
import com.codename1.rad.nodes.ViewNode;
import com.codename1.rad.models.Entity;

/**
 * A factory for converting {@link ViewNode} objects into views.
 * @author shannah
 */
public interface EntityViewFactory  {
    public EntityView createView(Entity entity, ViewNode node);
}
