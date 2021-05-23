package com.codename1.rad.ui;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.controllers.Controller;
import com.codename1.rad.controllers.ViewController;
import com.codename1.rad.models.Entity;
import com.codename1.rad.nodes.Node;


public class ViewContext<T extends Entity> {
    private T entity;
    private ViewController controller;
    private Node node;
    private EntityView<T> entityView;

    public ViewContext(@Inject ViewController controller, @Inject T entity, @Inject Node node) {
        this.entity = entity;
        this.controller = controller;
        this.node = node;
    }

    public ViewContext(@Inject ViewController controller, @Inject T entity) {
        this.controller = controller;
        this.node = controller.getViewNode();
        this.entity = entity;

    }

    public ViewController getController() {
        return controller;
    }

    public void setController(ViewController controller) {
        this.controller = controller;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public void setEntityView(EntityView<T> entityView) {
        this.entityView = entityView;

    }

    public EntityView<T> getEntityView() {
        return this.entityView;
    }


}
