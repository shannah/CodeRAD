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
 * An interface used by views that can bind to entities.
 * @author shannah
 */
public interface EntityView<T extends Entity> {
    public void bind();
    public void unbind();
    public void update();
    public void commit();
    public void setEntity(T entity);
    public T getEntity();
    public Node getViewNode();
}
