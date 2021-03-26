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
import com.codename1.rad.ui.UI;
import com.codename1.ui.Component;
import com.codename1.ui.Display;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.Graphics;
import com.codename1.ui.Image;
import com.codename1.ui.URLImage;
import com.codename1.ui.geom.GeneralPath;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.RoundBorder;
import com.codename1.ui.plaf.RoundRectBorder;
import com.codename1.ui.plaf.Style;

/**
 * Utility functions for dealing with images.  Includes, among other things, useful functions for generating
 * image masks (to add round corners to images).
 * @author shannah
 */
public class ImageUtil {
    public static EncodedImage createPlaceholder(int width, int height) {
        Component cmp = new Component() {
            
        };
        cmp.setWidth(width);
        cmp.setHeight(height);
        return new ComponentImage(cmp, width, height).toEncodedImage();
    }
    
    public static Object createRoundSquareImageMask(float cornerRadiusMM, int size) {
        return createRoundRectImageMask(cornerRadiusMM, size, size);
    }
    
    public static URLImage.ImageAdapter createRoundSquareMaskAdapter(float cornerRadius, int size) {
        Object mask = createRoundSquareImageMask(cornerRadius, size);
        if (mask == null) {
            return null;
        }
        return URLImage.createMaskAdapter(mask);
    }
    
    public static Object createRoundRectImageMask(float cornerRadiusMM, int width, int height) {
        RoundRectBorder border = RoundRectBorder.create().cornerRadius(cornerRadiusMM);
        return createImageMask(border, width, height);
    }
    
    public static URLImage.ImageAdapter createRoundRectMaskAdapter(float cornerRadius, int width, int height) {
        Object mask = createRoundRectImageMask(cornerRadius, width, height);
        if (mask == null) {
            return null;
        }
        return URLImage.createMaskAdapter(mask);
    }
    
    
    public static Object createImageMask(RoundRectBorder border, int width, int height) {
        Style style = new Style();
        style.setBorder(border);
        return createImageMask("RoundRectBorder", style, width, height);
    }
    
    public static URLImage.ImageAdapter createMaskAdapter(RoundRectBorder border, int width, int height) {
        Object mask = createImageMask(border, width, height);
        if (mask == null) {
            return null;
        }
        return URLImage.createMaskAdapter(mask);
    }
    
    public static Object createRoundImageMask(int size) {
        return createImageMask(RoundBorder.create(), size);
    }
    
     public static URLImage.ImageAdapter createRoundMaskAdapter(int size) {
        Object mask = createRoundImageMask(size);
        if (mask == null) {
            return null;
        }
        return URLImage.createMaskAdapter(mask);
    }
    
    public static Object createImageMask(RoundBorder border, int size) {
        Style style = new Style();
        style.setBorder(border);
        return createImageMask("RoundBorder", style, size, size);
    }
    
   
    
    public static URLImage.ImageAdapter createMaskAdapter(RoundBorder border, int size) {
        Object mask = createImageMask(border, size);
        if (mask == null) {
            return null;
        }
        return URLImage.createMaskAdapter(mask);
    }
    
    
    public static Object createImageMask(String id, Style style, int width, int height) {
        Border border = style.getBorder();
        //if (!(border instanceof RoundRectBorder || border instanceof RoundBorder)) {
        //    return null;
        //}
        
        String cacheKey = "ImageUtil.createImageMask."+id+"#"+width+"x"+height;
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
            gr.fillRect(0, 0, width, height);
        }
        
        mask = maskImage.createMask();
        UI.getCache().set(cacheKey, mask);

        return mask;
    }
    
    public static URLImage.ImageAdapter createMaskAdapter(String id, Style style, int width, int height) {
        Object mask = createImageMask(id, style, width, height);
        if (mask == null) {
            return null;
        }
        return URLImage.createMaskAdapter(mask);
    }
    
    private static GeneralPath createShape(RoundRectBorder border, int shapeW, int shapeH) {
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
    
    private static void fillShape(RoundBorder border, Graphics g, int color, int opacity, int width, int height) {
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
}
