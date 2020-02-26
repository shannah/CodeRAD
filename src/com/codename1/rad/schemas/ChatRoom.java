/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.schemas;

import com.codename1.rad.models.Tag;

/**
 * A schema with tags to identify properties you woud expect in a ChatRoom.
 * @author shannah
 */
public interface ChatRoom extends Thing {
    public static final Tag messages = new Tag();
    public static final Tag participants = new Tag();
    
    /**
     * A property that stores the content of the current input buffer (i.e. the text
     * field where the user enters text messages).
     */
    public static final Tag inputBuffer = new Tag();
}
