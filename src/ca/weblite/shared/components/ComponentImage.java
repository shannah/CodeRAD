/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.shared.components;

import com.codename1.ui.Component;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.Graphics;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.plaf.Border;

/**
 *
 * @author shannah
 */
public class ComponentImage extends Image {

    private Component cmp;
    private int w;
    private int h;
    private boolean pulsingAnimation;
    private double minPulsingAlpha = 0.2, maxPulsingAlpha = 1.0;
    private double pulsingStepSize = 1;
    private double pulsingCurrStep = 0;
    private boolean animation;

    public ComponentImage(Component cmp, int w, int h) {
        super(null);
        this.cmp = cmp;
        this.w = w;
        this.h = h;
    }

    ComponentImage(Component cmp) {
        super(null);
        this.cmp = cmp;
        w = cmp.getWidth();
        h = cmp.getHeight();
    }

    ComponentImage(int w, int h) {
        super(null);
        cmp = new Label();
        this.w = w;
        this.h = h;
    }
    
    public void enablePulsingAnimation(double currStep, double stepSize, double minAlpha, double maxAlpha) {
        minAlpha = Math.min(1, Math.max(0, minAlpha));
        maxAlpha = Math.min(1, Math.max(0, maxAlpha));
        this.pulsingAnimation = true;
        this.pulsingCurrStep = currStep;
        this.pulsingStepSize = stepSize;
        this.minPulsingAlpha = minAlpha;
        this.maxPulsingAlpha = maxAlpha;
    }
    
    public void disablePulsingAnimation() {
        pulsingAnimation = false;
        
    }
    
    public boolean isPulsingAnimationEnabled() {
        return pulsingAnimation;
    }

    @Override
    public int getWidth() {
        return w;
    }

    @Override
    public int getHeight() {
        return h;
    }

    @Override
    public void scale(int width, int height) {
        w = width;
        h = height;
    }

    @Override
    public Image fill(int width, int height) {
        ComponentImage out = new ComponentImage(cmp);
        out.w = width;
        out.h = height;
        return out;
    }

    @Override
    public Image applyMask(Object mask) {
        return new ComponentImage(cmp, w, h);
    }

    public void setAnimation(boolean anim) {
        this.animation = anim;
    }
    
    @Override
    public boolean isAnimation() {
        return animation || pulsingAnimation;
    }

    @Override
    public boolean requiresDrawImage() {
        return true;
    }

    @Override
    protected void drawImage(Graphics g, Object nativeGraphics, int x, int y) {
        
        int tx = g.getTranslateX();
        int ty = g.getTranslateY();
        //g.translate(-tx, -ty);
        int oldX = cmp.getX();
        int oldY = cmp.getY();
        int oldW = cmp.getWidth();
        int oldH = cmp.getHeight();
        cmp.setX(x);
        cmp.setY(y);
        cmp.setWidth(w);
        cmp.setHeight(h);
        int col = g.getColor();
        g.setColor(0x00ff00);
        //g.drawRect(x, y, w, h);
        g.setColor(col);
        //Label l = (Label)cmp;
        
        //cmp.getStyle().setAlignment(Component.CENTER);
        //cmp.getStyle().setBorder(Border.createLineBorder(1));
        //cmp.getStyle().setBgColor(0xff0000);
        //cmp.getStyle().setFgColor(0x0000ff);
        //cmp.getStyle().setBgTransparency(255);
        //cmp.getStyle().setOpacity(255);
        //cmp.paintBackgrounds(g);
        //cmp.paint(g);
        boolean antialias = g.isAntiAliased();
        g.setAntiAliased(true);
        
        int alpha = g.getAlpha();
        if (pulsingAnimation) {
            double sinVal = (Math.sin(pulsingCurrStep) + 1)/2;
            sinVal = minPulsingAlpha + (maxPulsingAlpha - minPulsingAlpha) * sinVal;
            g.setAlpha((int)Math.round(sinVal * alpha));
        }
        
        cmp.paintComponent(g, true);
        if (pulsingAnimation) {
            g.setAlpha(alpha);
        }
        g.setAntiAliased(antialias);
        cmp.setX(oldX);
        cmp.setY(oldY);
        cmp.setWidth(oldW);
        cmp.setHeight(oldH);
        //g.translate(tx, ty);
    }

    @Override
    protected void drawImage(Graphics g, Object nativeGraphics, int x, int y, int w, int h) {
        int oldW = this.w;
        int oldH = this.h;
        drawImage(g, nativeGraphics, x, y);
        this.w = oldW;
        this.h = oldH;
    }

    @Override
    public Image scaled(int width, int height) {
        return new ComponentImage(cmp, width, height);
    }
    
    

    @Override
    public boolean animate() {
        if (pulsingAnimation) {
            pulsingCurrStep += pulsingStepSize;
            if (pulsingCurrStep >= Math.PI * 2) {
                pulsingCurrStep -= Math.PI * 2;
            }
        }
        cmp.animate();
        return pulsingAnimation || animation;
    }
    
    public EncodedImage toEncodedImage() {
        return new EncodedWrapper();
    }
    
    
    public class EncodedWrapper extends EncodedImage {

        EncodedWrapper() {
            super(ComponentImage.this.getWidth(), ComponentImage.this.getHeight());
        }

        @Override
        public EncodedImage scaledEncoded(int width, int height) {
            return new ComponentImage(cmp, width, height).toEncodedImage();
            
        }

        @Override
        public Image scaled(int width, int height) {
            return new ComponentImage(cmp, width, height).toEncodedImage();
        }
        
        
        
        @Override
        protected Image getInternal() {
            return ComponentImage.this;
        }
        
    }
   
    
};
