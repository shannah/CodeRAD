package com.codename1.rad.models;

public interface Bindable {
    public void addBindListener(Runnable  onBind);
    public void addUnbindListener(Runnable onUnbind);
}
