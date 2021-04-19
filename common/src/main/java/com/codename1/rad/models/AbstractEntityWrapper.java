package com.codename1.rad.models;

public abstract class AbstractEntityWrapper implements EntityWrapper {
    protected final Entity entity;

    protected AbstractEntityWrapper(Entity entity) {
        this.entity = entity;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }
}
