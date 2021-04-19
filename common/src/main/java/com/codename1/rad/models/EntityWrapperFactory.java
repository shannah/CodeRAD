package com.codename1.rad.models;

public interface EntityWrapperFactory {
    public <T> T createWrapperFor(Entity entity, Class<T> type);
}
