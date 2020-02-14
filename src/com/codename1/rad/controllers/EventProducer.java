/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.controllers;

/**
 * An interface that can be implemented by a View so that ViewControllers can seamlessly
 * register to receive events from them.
 * @author shannah
 */
public interface EventProducer {
    public ActionSupport getActionSupport();
}
