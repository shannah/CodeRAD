/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.schemas;

import com.codename1.rad.models.Tag;

/**
 * From https://schema.org/Comment
 * @author shannah
 */
public interface Comment extends CreativeWork {
    public static final Tag downvoteCount = new Tag("downvoteCount"),
            parentItem = new Tag("parentItem"),
            upvoteCount = new Tag("upvoteCount");
            
}
