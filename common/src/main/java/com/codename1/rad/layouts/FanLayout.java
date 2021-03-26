/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.layouts;

import com.codename1.ui.CN;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.Layout;
import com.codename1.ui.plaf.Style;

/**
 * Like BoxLayout, except if there isn't enough space to layout the children, it will overlap them,
 * fanning them out.
 * @author shannah
 */
public class FanLayout extends Layout {
    public static final int X_AXIS = BoxLayout.X_AXIS;
    
    // We'll uncomment this when we implement Y_AXIS orientation
    //public static final int Y_AXIS = BoxLayout.Y_AXIS;
    private int minFanIncrement = CN.convertToPixels(1.5f);
    
    private int orientation = X_AXIS;
    
    public FanLayout(int orientation) {
        this.orientation = orientation;
        if (orientation != X_AXIS) {
            // Haven't implemented Y_AXIS yet
            throw new IllegalArgumentException("Unsupported orientation "+orientation);
        }
    }
    

    @Override
    public void layoutContainer(Container parent) {
        
        int w = parent.getLayoutWidth();
        int h = parent.getLayoutHeight();
        Style s = parent.getStyle();
        int x = s.getPaddingLeftNoRTL();
        int y = s.getPaddingTop();
        int leftX = x;
        int topY = y;
        int rightX = w - s.getPaddingRightNoRTL() - parent.getSideGap();
        int bottomY = h - s.getPaddingBottom() - parent.getSideGap();
        int innerH = bottomY - topY;
        int innerW = rightX - leftX;
        Component lastChild = null;
        if (orientation == X_AXIS) {
            int visibleComponentCount = 0;
            for (Component child : parent) {
                if (child.isHidden()) {
                    continue;
                }
                Style cs = child.getStyle();
                x += cs.getMarginLeftNoRTL();
                child.setX(rightX - x - child.getPreferredW());
                child.setY(y + cs.getMarginTop());
                child.setWidth(child.getPreferredW());
                child.setHeight(innerH - cs.getVerticalMargins());
                x += child.getWidth();
                x += cs.getMarginRightNoRTL();
                lastChild = child;
                visibleComponentCount++;
            }
            if (visibleComponentCount < 1) {
                // Nothing to layout
                return;
            }
            
            if (x > rightX) {
                int fanIncrement;
                
                if (lastChild.getWidth() > innerW) {
                    // Last child is too big.It will need to be clipped.
                    // How big do we make that last child then?  It should depend
                    // on the total number of children.
                    int spaceRequiredForOtherChildren = minFanIncrement * (visibleComponentCount - 1);
                    fanIncrement = minFanIncrement;
                    x = leftX + 3 * fanIncrement;
                    
                    if (x >= rightX - 10) {
                        x = leftX;
                    }
                    lastChild.setX(rightX - x - lastChild.getWidth());
                    x = Math.max(leftX, x - fanIncrement);
                    
                } else {
                    
                    x = Math.max(leftX, rightX - lastChild.getWidth());
                    fanIncrement = (int)Math.max(0, (x - leftX) / (float)(visibleComponentCount-1));
                    fanIncrement = Math.max(fanIncrement, minFanIncrement);
                    lastChild.setX(rightX - x - lastChild.getWidth());
                    x = Math.max(leftX, x - fanIncrement);
                    
                }
                
                boolean foundLastChild = false;
                for (int index = parent.getComponentCount()-1; index >= 0; index--) {
                    
                    Component child = parent.getComponentAt(index);
                    if (child == lastChild) {
                        foundLastChild = true;
                        continue;
                    }
                    if (!foundLastChild) {
                        continue;
                    }
                    
                    if (child.isHidden()) {
                        continue;
                    }
                    child.setX(rightX - x - lastChild.getWidth());
                    x = Math.max(leftX, x - fanIncrement);
                }
            } else {
                for (int index = parent.getComponentCount()-1; index >= 0; index--) {
                    Component child = parent.getComponentAt(index);
                    if (child.isHidden()) {
                        continue;
                    }
                    child.setX(child.getX() - (rightX - x)/2);
                }
            }
        }
    }

    @Override
    public boolean isOverlapSupported() {
        return true;
    }

    
    
    @Override
    public Dimension getPreferredSize(Container parent) {
        Style s = parent.getStyle();
        int w = 0;
        int h = 0;
        if (orientation == X_AXIS) {
            for (Component child : parent) {
                Style cs = child.getStyle();
                w += child.getPreferredW() + cs.getHorizontalMargins();
                h = Math.max(h, child.getPreferredH() + cs.getVerticalMargins());
            }
        }
        
        return new Dimension(w + s.getHorizontalPadding() + parent.getSideGap(), h + s.getVerticalPadding() + parent.getBottomGap());
    }
    
}
