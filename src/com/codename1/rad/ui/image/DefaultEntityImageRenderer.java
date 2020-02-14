/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui.image;

import com.codename1.rad.ui.EntityView;
import com.codename1.rad.models.ContentType;
import com.codename1.rad.models.Property;

/**
 *
 * @author shannah
 */
public class DefaultEntityImageRenderer implements EntityImageRenderer {

    @Override
    public AsyncImage createImage(EntityView view, Property property, int rowIndex, boolean selected, boolean focused) {
        try {
            return (AsyncImage)ContentType.convert(property.getContentType(), property.getValue(view.getEntity()), AsyncImage.CONTENT_TYPE);
        } catch (Throwable t) {
            return null;
        }
    }
    
}
