/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui.image;

import com.codename1.rad.ui.EntityView;
import com.codename1.rad.models.Property;

/**
 * Creates an image for a given entity.
 * @author shannah
 */
public interface EntityImageRenderer {
    public AsyncImage createImage(EntityView view, Property property, int rowIndex, boolean selected, boolean focused);
}
