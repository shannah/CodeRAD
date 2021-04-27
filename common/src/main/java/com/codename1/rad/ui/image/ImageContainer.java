/*
 * Copyright 2020 shannah.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codename1.rad.ui.image;


import com.codename1.rad.models.Property;
import com.codename1.rad.models.PropertySelector;
import com.codename1.rad.schemas.ListRowItem;
import com.codename1.rad.schemas.Thing;
import static com.codename1.ui.ComponentSelector.$;
import com.codename1.ui.Container;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import static com.codename1.ui.URLImage.createMaskAdapter;

import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.Layout;
import com.codename1.ui.plaf.Border;
import com.codename1.rad.models.Entity;

/**
 * A that manages the loading of URLImages, application of mask (rect or roundrect), and proper sizing
 * according to the current layout manager.
 * @author shannah
 */
public class ImageContainer extends Container {
    private String storageFile, filePath;
    private boolean useStorage, useFileSystem;
    private PropertySelector property;
    private double aspectRatio = 16.0/9.0;
    private Image image;
    
    
    private Label imageLabel = new Label();
    
    
    private ImageContainer() {
        super(new BorderLayout());
        setLayout(new FillLayout());
        $(this).selectAllStyles().setPadding(0);
        
        $(imageLabel).selectAllStyles().setPadding(0).setMargin(0).setBorder(Border.createEmpty()).setBgTransparency(0x0);
        add(imageLabel);
    }

    private class FillLayout extends Layout {

        @Override
        public void layoutContainer(Container parent) {
            if (parent.getLayoutWidth() > 0 && (image == null || image.getWidth() != parent.getLayoutWidth())) {
                image = createImage(parent.getLayoutWidth());
                imageLabel.setIcon(image);
                setShouldLayout(true);
            }
            imageLabel.setX(0);
            imageLabel.setY(0);
            imageLabel.setWidth(getWidth());
            imageLabel.setHeight(getHeight());
        }

        @Override
        public Dimension getPreferredSize(Container parent) {
            if (image == null) {
                if (getWidth() > 0) {
                    return new Dimension(getWidth(), (int)Math.round(getWidth()/aspectRatio));
                } else {
                    return new Dimension(0, 0);
                }
            } else {
                return new Dimension(image.getWidth(), image.getHeight());
            }
        }
        
    }
    
    public Object createImageMask(int width) {
        int height = (int)Math.round(width / aspectRatio);
        return ImageUtil.createImageMask(getUIID(), getStyle(), width, height);
        
    }
    
    
    
    private Image createImage(int width) {
        if (filePath != null || useFileSystem) {
            return property.createImageToFile(createPlaceholder(width), filePath, createMaskAdapter(createImageMask(width)));
        } else {
            return property.createImageToStorage(createPlaceholder(width), storageFile, createMaskAdapter(createImageMask(width)));
        }
            
    }
    
    private EncodedImage createPlaceholder(int width) {
        int height = (int)Math.round(width / aspectRatio);
        return ImageUtil.createPlaceholder(width, height);
    }

    public static ImageContainer createToStorage(Entity entity, Property property, String storageFile) {
        ImageContainer out = new ImageContainer();
        out.storageFile = storageFile;
        out.useStorage = true;
        out.property = new PropertySelector(entity, property);
        
        
        return out;
    }
    
    public static ImageContainer createToStorage(PropertySelector property, String storageFile) {
        ImageContainer out = new ImageContainer();
        out.storageFile = storageFile;
        out.useStorage = true;
        out.property = property;
        
        
        return out;
    }
    

    
    public static ImageContainer createToStorage(Entity entity, Property property) {
        return createToStorage(entity, property, (String)null);
    }
    
    public static ImageContainer createToStorage(PropertySelector property) {
        return createToStorage(property, (String)null);
    }
    
    public static ImageContainer createToStorage(Entity entity) {
        return createToStorage(entity, entity.getEntity().getEntityType().findProperty(Thing.image, Thing.thumbnailUrl, ListRowItem.icon));
    }    
    
    
    
    public static ImageContainer createToFileSystem(Entity entity, Property property, String filePath) {
        ImageContainer out = new ImageContainer();
        out.filePath = filePath;
        out.useFileSystem = true;
        out.property = new PropertySelector(entity, property);
        
        
        return out;
    }
    
    public static ImageContainer createToFileSystem(PropertySelector property, String filePath) {
        ImageContainer out = new ImageContainer();
        out.filePath = filePath;
        out.useFileSystem = true;
        out.property = property;
        
        
        return out;
    }
   
    
    public static ImageContainer createToFileSystem(Entity entity, Property property) {
        return createToFileSystem(entity, property, (String)null);
    }
    
    public static ImageContainer createToFileSystem(PropertySelector property) {
        return createToFileSystem(property, (String)null);
    }
    
    public static ImageContainer createToFileSystem(Entity entity) {
        return createToFileSystem(entity, entity.getEntity().getEntityType().findProperty(Thing.image, Thing.thumbnailUrl, ListRowItem.icon));
    }


    
    public void setAspectRatio(double aspect) {
        this.aspectRatio = aspect;
    }
    
    public void invalidateImage() {
        image = null;
    }
    
    
}
