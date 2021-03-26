/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.ext.ui;

import com.codename1.ui.CN;
import com.codename1.ui.Font;
import com.codename1.ui.FontImage;
import static com.codename1.ui.FontImage.createMaterial;
import com.codename1.ui.Image;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;

/**
 * A limited implementation of the FontAwesome font.  Currently only includes a few brand logos.
 * @author shannah
 */
public class FontAwesome {
    private static Font brands;
    public static final char APPLE = 0xf179;
    public static final char JAVA = 0xf4e4;
    public static final char WINDOWS = 0xf17a;
    public static final char LINUX = 0xf17c;
    public static Font getBrandsFont() {
        if (brands == null) {
            brands = UIManager.getInstance().getComponentStyle("FontAwesomeBrands").getFont();
        }
        return brands;
        
    }
    
    public static FontImage createBrand(char icon, Style s) {
        Font f = getBrandsFont().derive(s.getFont().getHeight(), Font.STYLE_PLAIN);
        return FontImage.create("" + icon, s, f);
    }
    
    public static FontImage createBrand(char icon, String style, float size) {
        Style s = UIManager.getInstance().getComponentStyle(style);
        return createBrand(icon, s, size);
    }
    
    public static FontImage createBrand(char icon, Style s, float size) {
        Font f = getBrandsFont().derive(CN.convertToPixels(size), Font.STYLE_PLAIN);
        return FontImage.create("" + icon, s, f);
    }
}
