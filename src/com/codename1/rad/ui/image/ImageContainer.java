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

import ca.weblite.shared.components.ComponentImage;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.Property;
import com.codename1.rad.schemas.ListRowItem;
import com.codename1.rad.schemas.Thing;
import com.codename1.rad.ui.UI;
import com.codename1.ui.CN;
import com.codename1.ui.Component;
import static com.codename1.ui.ComponentSelector.$;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.Graphics;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.URLImage;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.geom.GeneralPath;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.Layout;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.RoundBorder;
import com.codename1.ui.plaf.RoundRectBorder;

/**
 *
 * @author shannah
 */
public class ImageContainer extends Container {
    private String storageFile, filePath;
    private boolean useStorage, useFileSystem;
    private Entity entity;
    private Property property;
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
    
    private GeneralPath createShape(RoundRectBorder border, int shapeW, int shapeH) {
        GeneralPath gp = new GeneralPath();
        float radius = Display.getInstance().convertToPixels(border.getCornerRadius());
        float x = 0;
        float y = 0;
        float widthF = shapeW;
        float heightF = shapeH;
        
        if(border.isTopLeft()) {
            gp.moveTo(x + radius, y);
        } else {
            gp.moveTo(x, y);            
        }
        
        if(border.isTopRight()) {
            gp.lineTo(x + widthF - radius, y);            
            gp.quadTo(x + widthF, y, x + widthF, y + radius);
        } else {
            gp.lineTo(x + widthF, y);
        }
        
        
        if(border.isBottomRight()) {
            gp.lineTo(x + widthF, y + heightF - radius);
            gp.quadTo(x + widthF, y + heightF, x + widthF - radius, y + heightF);
        } else {
            gp.lineTo(x + widthF, y + heightF);
        }
        
        
        
        
        if(border.isBottomLeft()) {
            gp.lineTo(x + radius, y + heightF);
            gp.quadTo(x, y + heightF, x, y + heightF - radius);
        } else {
            gp.lineTo(x, y + heightF);
        }
        
        
        
        if(border.isTopLeft()) {
            gp.lineTo(x, y + radius);
            gp.quadTo(x, y, x + radius, y);
        } else {
            gp.lineTo(x, y);            
        }
        
        
        gp.closePath();  
        
        
        return gp;
    }
    
    private void fillShape(RoundBorder border, Graphics g, int color, int opacity, int width, int height) {
        g.setColor(color);
        g.setAlpha(opacity);
        if(!border.isRectangle() || width <= height) {
            
            int x = 0; 
            int y = 0;
            int size = width;
            if(width != height) {
                if(width > height) {
                    size = height;
                    x = (width - height) / 2;
                } else {
                    size = width;
                    y = (height - width) / 2;
                }
            }
            if(size < 5) {
                // probably won't be visible anyway so do nothing, otherwise it might throw an exception
                return;
            }
            
            g.fillArc(x, y, size, size, 0, 360);
            
        } else {
            GeneralPath gp = new GeneralPath();
            float sw = 0;
            gp.moveTo(height / 2.0, sw);
            if(border.isOnlyLeftRounded()) {
                gp.lineTo(width, sw);
                gp.lineTo(width , height-sw);                
            } else {
                gp.lineTo(width - (height / 2.0), sw);
                gp.arcTo(width - (height / 2.0), height / 2.0, width - (height / 2.0), height-sw, true);
            }
            if(border.isOnlyRightRounded()) {
                gp.lineTo(sw, height-sw);
                gp.lineTo(sw, sw);                
            } else {
                gp.lineTo(height / 2.0, height-sw);
                gp.arcTo(height / 2.0, height / 2.0, height / 2.0, sw, true);
            }
            gp.closePath();
            g.fillShape(gp);
                  
        }
    }
    
    public Object createImageMask(int width) {
        Border border = getStyle().getBorder();
        if (!(border instanceof RoundRectBorder || border instanceof RoundBorder)) {
            return null;
        }
        int height = (int)Math.round(width / aspectRatio);
        String cacheKey = "ImageContainer."+getUIID()+"#"+getName()+width+"x"+height;
        Object mask = UI.getCache().get(cacheKey);
        if (mask != null) {
            return mask;
        }
        
        Image maskImage = Image.createImage(width, height, 0xff000000);
        Graphics gr = maskImage.getGraphics();
        gr.setAntiAliased(true);
        //gr.drawImage(cim, 0, 0);
        gr.setColor(0xffffff);
        if (border instanceof RoundRectBorder) {
            RoundRectBorder rb = (RoundRectBorder)border;
            gr.fillShape(createShape(rb, width, height));
        } else if (border instanceof RoundBorder) {
            RoundBorder rb = (RoundBorder)border;
            fillShape(rb, gr, 0xffffff, 0xff, width, height);
        } else {
            return null;
        }
        
        mask = maskImage.createMask();
        UI.getCache().set(cacheKey, mask);

        return mask;
    }
    
    private URLImage.ImageAdapter createMaskAdapter(int width) {
        Object mask = createImageMask(width);
        if (mask == null) {
            return null;
        }
        return URLImage.createMaskAdapter(mask);
    }
    
    private Image createImage(int width) {
        if (filePath != null || useFileSystem) {
            return entity.createImageToFile(property, createPlaceholder(width), filePath, createMaskAdapter(width));
        } else {
            return entity.createImageToStorage(property, createPlaceholder(width), storageFile, createMaskAdapter(width));
        }
            
    }
    
    private EncodedImage createPlaceholder(int width) {
        int height = (int)Math.round(width / aspectRatio);
        Component cmp = new Component() {
            
        };
        cmp.setWidth(width);
        cmp.setHeight(height);
        return new ComponentImage(cmp, width, height).toEncodedImage();
    }

    public static ImageContainer createToStorage(Entity entity, Property property, String storageFile) {
        ImageContainer out = new ImageContainer();
        out.storageFile = storageFile;
        out.useStorage = true;
        out.property = property;
        out.entity = entity;
        
        return out;
    }
    

    
    public static ImageContainer createToStorage(Entity entity, Property property) {
        return createToStorage(entity, property, (String)null);
    }
    
    public static ImageContainer createToStorage(Entity entity) {
        return createToStorage(entity, entity.getEntityType().findProperty(Thing.image, Thing.thumbnailUrl, ListRowItem.icon));
    }    
    
    
    public static ImageContainer createToFileSystem(Entity entity, Property property, String filePath) {
        ImageContainer out = new ImageContainer();
        out.filePath = filePath;
        out.useFileSystem = true;
        out.property = property;
        out.entity = entity;
        
        return out;
    }
   
    
    public static ImageContainer createToFileSystem(Entity entity, Property property) {
        return createToFileSystem(entity, property, (String)null);
    }
    
    public static ImageContainer createToFileSystem(Entity entity) {
        return createToFileSystem(entity, entity.getEntityType().findProperty(Thing.image, Thing.thumbnailUrl, ListRowItem.icon));
    }


    
    public void setAspectRatio(double aspect) {
        this.aspectRatio = aspect;
    }
    
    
    
    
}
