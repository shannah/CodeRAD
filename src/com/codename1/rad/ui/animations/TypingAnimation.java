/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui.animations;

import ca.weblite.shared.components.ComponentImage;
import static com.codename1.ui.ComponentSelector.$;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.plaf.Border;

/**
 * An animation that indicates that the other user is currently typing a message.  It is three dots, pulsing.
 * @author shannah
 */
public class TypingAnimation extends Container {
    
    private class AnimatingLabel extends Label {

        AnimatingLabel() {
            
        }
        
        AnimatingLabel(Image icon) {
            super(icon);
            //setPreferredW(3*icon.getWidth()/4);
            //setPreferredH(3*icon.getHeight()/4);
            
        }
        
        
        
        @Override
        public boolean animate() {
            Image icon = getIcon();
            if (icon != null) {
                icon.animate();
            }
            return true;
        }
        
    }
    
    private final Label[] dots = new Label[]{
        new AnimatingLabel(),
        new AnimatingLabel(),
        new AnimatingLabel()
    };

    public TypingAnimation() {
        this("TypingAnimation", "TypingAnimationDot");
    }
    
    public TypingAnimation(String uiid, String dotUiid) {
        setUIID(uiid);
        setLayout(new GridLayout(1, dots.length));
        int i=0;
        for (Label dot : dots) {
            dot.setUIID(dotUiid);
            dot.setMaterialIcon(FontImage.MATERIAL_FIBER_MANUAL_RECORD);
            
            dot.setWidth(dot.getPreferredW());
            dot.setHeight(dot.getPreferredH());
            
            ComponentImage img = new ComponentImage(dot, dot.getWidth(), dot.getHeight());
            img.enablePulsingAnimation(i * (Math.PI / dots.length), -0.075, 0.2, 1.0);
            AnimatingLabel lbl = new AnimatingLabel(img);
            $(lbl).setPadding(0).setMargin(0).setBorder(Border.createEmpty()).setBgTransparency(0x0);
            add(lbl);
            i++;
        }
    }

    @Override
    public boolean animate() {
        
        $("*", this).each(c->{
            c.animate();
        });
        return true;
    }

    private Form form;
    
    @Override
    protected void initComponent() {
        super.initComponent();
        form = getComponentForm();
        if (form != null) {
            form.registerAnimated(this);
        }
    }

    @Override
    protected void deinitialize() {
        if (form != null) {
            form.deregisterAnimated(this);
        }
        super.deinitialize();
    }
    
    
    public ComponentImage toImage() {
        this.setWidth(getPreferredW());
        this.setHeight(getPreferredH());
        layoutContainer();
        ComponentImage out = new ComponentImage(this, this.getWidth(), this.getHeight());
        out.setAnimation(true);
        //out.enablePulsingAnimation(0, 0.2, 1.0, 1.0);
        return out;
    }
    
    
    
    
   
    
}
