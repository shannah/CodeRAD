/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.nodes;

/**
 *
 * @author shannah
 */
public interface Proxyable<T> {
    public Node<T> createProxy(Node parent);
}
