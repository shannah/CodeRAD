/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.schemas;

import com.codename1.rad.models.Tag;

/**
 * Schema with tags to identify properties in a list row item.
 * @author shannah
 */
public interface ListRowItem extends Thing {
    public static final Tag line1 = new Tag("line1");
    public static final Tag line2 = new Tag("line2");
    public static final Tag icon = new Tag("icon");
}
