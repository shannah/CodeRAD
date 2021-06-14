package com.codename1.rad.models;

public class EntitySupport {
    public static void notifyObservers(Entity... entities) {
        for (Entity e : entities) {
            e.getEntity().notifyObservers();
        }
    }

    public static void setChanged(Entity... entities) {
        for (Entity e : entities) {
            e.getEntity().setChangedInternal();
        }
    }
}
