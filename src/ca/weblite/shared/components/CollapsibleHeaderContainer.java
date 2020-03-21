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
package ca.weblite.shared.components;

import com.codename1.ui.CN;
import com.codename1.ui.Component;
import com.codename1.ui.ComponentSelector;
import static com.codename1.ui.ComponentSelector.$;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Graphics;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.geom.Rectangle;
import com.codename1.ui.layouts.Layout;
import com.codename1.ui.plaf.Style;

/**
 * A pane that includes a title bar that will hide when scrolling down the page, and reveal when scrolling back up 
 * the page.
 * 
 * == Component Alignment
 * 
 * This container includes a post-layout pass which can align child components' left edges.  This is handy if you 
 * need to line up a component in the title bar with components in the body.  This can't be easily achieved inside
 * a layout manager because The title bar is in a separate branch of the component hierarchy than the body.  Simply
 * setting the same left margin or padding on elements may be insufficient due to safe areas adjusting the padding
 * on some devices.
 * 
 * If you want to align the left edge of components, you need to use the {@link ComponentSelector#addTags(java.lang.String...) } method
 * to add the "left-inset" tag to components that should be left aligned, and the "left-edge" tag to the container that will 
 * bleed to the left edge of the screen.  The "left-edge" container is used as the relative "zero" point for calculating
 * the necessary padding to line them up.  
 * 
 * @author shannah
 */
public class CollapsibleHeaderContainer extends Container {
    private int slidePos;
    private Container titleBar;
    private Container body;
    private Container verticalScroller;
    private Rectangle safeArea = new Rectangle();
    
    // UIIDs to use for full and partial collapse.  In partial collapse mode
    // even when the title is collapsed, the body still includes its own header.
    // In such cases we usually want to omit the bottom border because we want
    // the body's header to be contiguous with the title component.
    private String fullCollapseUIID;
    private String partialCollapseUIID;
    public static enum CollapseMode {
        FullCollapse,
        PartialCollapse
    }
    
    private CollapseMode collapseMode = CollapseMode.FullCollapse;
    
    //private Container verticalScroller;
    
    private class TWTTitleBarPaneLayout extends Layout {
        private boolean firstLayout = true;
        @Override
        public void layoutContainer(Container parent) {
            int paddingTop = Display.getInstance().getDisplaySafeArea(safeArea).getY();
            if (titleBar.getStyle().getPaddingTop() != paddingTop) {
                titleBar.getStyle().setPaddingUnitTop(Style.UNIT_TYPE_PIXELS);
                titleBar.getStyle().setPaddingTop(paddingTop);
                //titleBar.setShouldCalcPreferredSize(true);
            }
            if (firstLayout) {
                slidePos = titleBar.getPreferredH();
                firstLayout = false;
            }
            
            titleBar.setX(0);
            titleBar.setY(slidePos - titleBar.getPreferredH());
            titleBar.setWidth(parent.getLayoutWidth());
            titleBar.setHeight(titleBar.getPreferredH());
            
            body.setX(0);
            if (collapseMode == CollapseMode.PartialCollapse) {
                body.setY(Math.max(paddingTop, titleBar.getY() + titleBar.getHeight()));
            } else {
                body.setY(titleBar.getY() + titleBar.getHeight());
            }
            body.setHeight(parent.getHeight() - body.getY());
            body.setWidth(parent.getWidth());
        }

        @Override
        public Dimension getPreferredSize(Container arg0) {
            return new Dimension(CN.getDisplayWidth(), CN.getDisplayHeight());
        }
        
    }
    
    public void setPartialCollapseUIID(String uiid) {
        this.partialCollapseUIID = uiid;
    }
    
    public void setFullCollapseUIID(String uiid) {
        this.fullCollapseUIID = uiid;
    }
    
    public void setCollapseMode(CollapseMode mode) {
        if (mode != this.collapseMode) {
            this.collapseMode = mode;
            switch (mode) {
                case FullCollapse:
                    if (partialCollapseUIID == null) {
                        partialCollapseUIID = titleBar.getUIID();
                    }
                    if (fullCollapseUIID != null) {
                        titleBar.setUIID(fullCollapseUIID);
                    }
                    break;
                case PartialCollapse:
                    if (fullCollapseUIID == null) {
                        fullCollapseUIID = titleBar.getUIID();
                    }
                    if (partialCollapseUIID != null) {
                        titleBar.setUIID(partialCollapseUIID);
                    }
                    
            }
        }
        
    }
    
    private boolean pressed;
    private boolean dragging;
    private int startDragX, startDragY, startSlidePos;
    
    private ActionListener formPressListener = new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            Container currentScroller = verticalScroller;
            if (currentScroller instanceof ScrollableContainer) {
                currentScroller = ((ScrollableContainer)currentScroller).getVerticalScroller();
            }
            if (!currentScroller.isScrollableY()) {
                return;
            }
            Form f = CN.getCurrentForm();
            if (f != null) {
                Component cmp = f.getComponentAt(evt.getX(), evt.getY());
                if (!pressed && cmp != currentScroller && !currentScroller.contains(cmp)) {
                    return;
                }
            }
            if (evt.getEventType() == ActionEvent.Type.PointerPressed) {
                pressed = true;
                startDragX = evt.getX();
                startDragY = evt.getY();
                startSlidePos = slidePos;
            } else if (ActionEvent.Type.PointerDrag == evt.getEventType()) {
                if (!pressed) {
                    return;
                }
                int diffY = evt.getY() - startDragY;
                if (diffY > 0) {
                    // dragging down
                    if (slidePos < titleBar.getPreferredH() && currentScroller.getScrollY() <= 0) {
                        startDragY = evt.getY();
                        slidePos = Math.max(0, Math.min(slidePos + diffY, titleBar.getPreferredH()));
                        evt.consume();
                        revalidateWithAnimationSafety();
                    }
                } else {
                    // dragging up
                    if (slidePos > 0) {
                        startDragY = evt.getY();
                        slidePos = Math.max(0, Math.min(slidePos + diffY, titleBar.getPreferredH()));
                        evt.consume();
                        revalidateWithAnimationSafety();
                    }
                }
            } else if (ActionEvent.Type.PointerReleased == evt.getEventType()) {
                if (!pressed) {
                    return;
                }
                pressed = false;
                if (slidePos > 0) {
                    slidePos = titleBar.getPreferredH();
                    animateLayout(200);
                    return;
                }
                
                float speed = Display.getInstance().getDragSpeed(true);
                
                if (slidePos == 0 && speed < -1f) {
                    slidePos = titleBar.getPreferredH();
                    animateLayout(200);
                    return;
                } else if (slidePos > 0 && speed > 1f) {
                    slidePos = 0;
                    animateLayout(200);
                }
                
                
            }

        }
    };
    
    public CollapsibleHeaderContainer(Container titleBar, Container body, Container verticalScroller) {
        $(this).addTags("CollapsibleHeaderContainer");
        int paddingTop = Display.getInstance().getDisplaySafeArea(safeArea).getY() + CN.convertToPixels(1f);
        if (titleBar.getStyle().getPaddingTop() != paddingTop) {
            titleBar.getStyle().setPaddingUnitTop(Style.UNIT_TYPE_PIXELS);
            titleBar.getStyle().setPaddingTop(paddingTop);
            //titleBar.setShouldCalcPreferredSize(true);
        }
        slidePos = titleBar.getPreferredH();
        this.titleBar = titleBar;
        this.body = body;
        this.verticalScroller = verticalScroller;
        setLayout(new TWTTitleBarPaneLayout());
        addAll(body, titleBar);
    }

    private Form form;
    @Override
    protected void initComponent() {
        super.initComponent();
        form = getComponentForm();
        if (form != null) {
            form.addPointerPressedListener(formPressListener);
            form.addPointerDraggedListener(formPressListener);
            form.addPointerReleasedListener(formPressListener);
        }
        
    }

    @Override
    protected void deinitialize() {
        if (form != null) {
            form.removePointerPressedListener(formPressListener);
            form.removePointerDraggedListener(formPressListener);
            form.removePointerReleasedListener(formPressListener);
            form = null;
        }
        super.deinitialize();
    }

    /**
     * Layout does a post-layout pass to line up all components with the "left-inset" tag.
     */
    @Override
    public void layoutContainer() {
        super.layoutContainer();
        
        int maxLeftX = 0;
        ComponentSelector cmps =  $(".left-inset", this);
        for (Component c : cmps) {
            Component wrap = $(c).parents(".left-edge").first().asComponent();
            if (wrap == null) {
                continue;
            }
            int thisLeftX = c.getAbsoluteX() + c.getStyle().getPaddingLeftNoRTL() - wrap.getAbsoluteX();
            maxLeftX = Math.max(maxLeftX, thisLeftX);
            
        }
        maxLeftX -= getAbsoluteX();

        for (Component c : cmps) {
            Component wrap = $(c).parents(".left-edge").first().asComponent();
            if (wrap == null) {
                continue;
            }
            int absX = c.getAbsoluteX() + c.getStyle().getPaddingLeftNoRTL() - wrap.getAbsoluteX();
            if (absX < maxLeftX) {
                int marginLeft = c.getStyle().getMarginLeftNoRTL();
                c.getAllStyles().setMarginUnitLeft(Style.UNIT_TYPE_PIXELS);
                c.getAllStyles().setMarginLeft(marginLeft + maxLeftX - absX);
            }
        }
        
    }

    @Override
    public void paint(Graphics g) {
        int color = g.getColor();
        g.setColor(0xffffff);
        g.fillRect(getX(), getY(), getWidth(), body.getY());
        g.setColor(color);
        super.paint(g);
    }
    
    
    public static interface ScrollableContainer {
        public Container getVerticalScroller();
    }
    
    
}
