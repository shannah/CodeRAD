/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.shared.components;

import com.codename1.ui.Component;
import com.codename1.ui.Graphics;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.plaf.Border;

/**
 *
 * @author shannah
 */
public class ComponentImage extends Image {

    Component cmp;
    int w;
    int h;

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

    @Override
    public boolean isAnimation() {
        return false;
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
        cmp.paintComponent(g, true);
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
    public boolean animate() {
        return false;
    }
};
