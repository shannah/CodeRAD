package com.codename1.rad.nodes;



public interface NodeBuilder<T extends Node> {

    public T getNode();
    public T build();
}
