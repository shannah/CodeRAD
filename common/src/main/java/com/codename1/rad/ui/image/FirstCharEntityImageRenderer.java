/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui.image;

import com.codename1.rad.ui.EntityView;
import ca.weblite.shared.components.ComponentImage;
import com.codename1.rad.models.Property;
import com.codename1.ui.CN;
import static com.codename1.ui.Component.CENTER;
import com.codename1.ui.Font;
import com.codename1.ui.Graphics;
import com.codename1.ui.Image;
import com.codename1.ui.plaf.Style;
import java.util.HashMap;
import java.util.Map;

/**
 * A renderer that renders the first letter of a given entity's property in a nice colored circle.
 * @author shannah
 */
public class FirstCharEntityImageRenderer implements EntityImageRenderer {
    private final int width;
    private final int height;
    private final int[] colors = new int[]{
        0xf44336, 0xe91e63, 0x9c27b0, 0x3f51b5, 0x9688, 0xff9800, 0x9e9e9e
    };
    private Font letterFont;
    private Map<Integer,Image> backgroundImageCache = new HashMap<>();
    
    public FirstCharEntityImageRenderer(float sizeMM) {
        width = CN.convertToPixels(sizeMM);
        height = width;
        letterFont = Font.createTrueTypeFont("native:MainThin", "native:MainThin");
        letterFont = letterFont.derive(height - height/ 3, Font.STYLE_PLAIN);
    }
    
    @Override
    public AsyncImage createImage(EntityView view, Property property, int rowIndex, boolean selected, boolean focused) {
        String str = view.getEntity().getEntityType().getText(property, view.getEntity());
        if (str == null || str.length() == 0) {
            str = " ";
        }
        return new WrappedImage(getLetter(str.charAt(0)));
    }
    
    private Image getLetter(char c) {
        c = Character.toUpperCase(c);
        String cstr = "" + c;
        com.codename1.ui.Label lbl = new com.codename1.ui.Label(cstr);
        Image bg = getLetterBackground(c);
        lbl.getStyle().setBgImage(bg);
        lbl.getStyle().setBgTransparency(0x0);
        lbl.getStyle().setBackgroundType(Style.BACKGROUND_IMAGE_ALIGNED_CENTER);
        lbl.getStyle().setAlignment(CENTER);
        lbl.getStyle().setFont(letterFont);
        lbl.getStyle().setFgColor(0xffffff);
        return new ComponentImage(lbl, bg.getWidth(), bg.getHeight());
        
    }
    
    private Image getLetterBackground(char c) {
        c = Character.toUpperCase(c);
        String cstr = "" + c;
        int color = colors[Math.max(0, c - 'A') % colors.length];
        Image i = backgroundImageCache.get(color);
        if(i != null) {
            return i;
        }
        int circleMaskWidth = width;
        int circleMaskHeight = height;
        Image img = Image.createImage(circleMaskWidth, circleMaskHeight, 0);
        Graphics g = img.getGraphics();
        g.setAntiAliased(true);
        g.setColor(color);
        g.fillArc(1, 1, circleMaskWidth - 2, circleMaskHeight - 2, 0, 360);
        backgroundImageCache.put(color, img);
        return img;
    }
    

    
}
