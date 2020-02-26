/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.schemas;

import com.codename1.rad.models.Tag;

/**
 * A schema with tags to identify common properties you would expect for a Chat message.
 * @author shannah
 */
public interface ChatMessage extends Comment {
    
    public static final Tag isOwnMessage = new Tag();
    public static final Tag isFavorite = new Tag();
    public static final Tag typingInProgress = new Tag();
    public static final Tag attachment = new Tag();
    public static final Tag attachmentPlaceholderImage = new Tag();
}
