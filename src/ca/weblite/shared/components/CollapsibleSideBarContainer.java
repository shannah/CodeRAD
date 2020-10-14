/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.shared.components;

import com.codename1.rad.controllers.ControllerEvent;
import com.codename1.rad.controllers.ViewController;
import com.codename1.ui.CN;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Graphics;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.Layout;

/**
 * A Pane that includes a side bar and main content, which the sidebar would "overlap".
 * @author shannah
 */
public class CollapsibleSideBarContainer extends Container {
    
    public static class SideBarEvent extends ControllerEvent {
        public SideBarEvent(Component source) {
            super(source);
        }
    }
    
    public static class ShowSideBarEvent extends SideBarEvent {
        public ShowSideBarEvent(Component source) {
            super(source);
        }
    }
    
    public static class HideSideBarEvent extends SideBarEvent {
        public HideSideBarEvent(Component source) {
            super(source);
        }
    }
    
    private final Component left, center;
    private int sideMenuWidth;
    private int slidePos = 0;
    
    private int startDragX, startDragY, startSlidePos;
    private boolean dragging, pressed, dragLock;
    private class SideBarLayout extends Layout {

        @Override
        public void layoutContainer(Container parent) {
            sideMenuWidth = left.getPreferredW();
            left.setX(-sideMenuWidth + slidePos);
            left.setY(0);
            left.setWidth(sideMenuWidth);
            left.setHeight(parent.getHeight());
            
            center.setWidth(parent.getWidth());
            
            center.setHeight(parent.getHeight());
            center.setX(slidePos);
            center.setY(0);
        }

        @Override
        public Dimension getPreferredSize(Container arg0) {
            return new Dimension(CN.getDisplayWidth(), CN.getDisplayHeight());
        }

        @Override
        public boolean isOverlapSupported() {
            return true;
        }
        
        
        
    }
    
    
    
    
    public CollapsibleSideBarContainer(Component left, Component center) {
        this.left = left;
        this.center = center;
        setLayout(new SideBarLayout());
        addAll(center, left);
        
    }
    
    
    

    private Form form;
    private ViewController vc;
    @Override
    protected void initComponent() {
        super.initComponent();
        form = getComponentForm();
        if (form != null) {
            form.addPointerPressedListener(formPointerListener);
            form.addPointerDraggedListener(formPointerListener);
            form.addPointerReleasedListener(formPointerListener);
            
            
        }
       vc = ViewController.getViewController(this);
        if (vc != null) {
            vc.addEventListener(vcListener);
        }
        
    }

    @Override
    protected void deinitialize() {
        if (vc != null) {
            vc.removeEventListener(vcListener);
        }
        if (form != null) {
            form.removePointerPressedListener(formPointerListener);
            form.removePointerReleasedListener(formPointerListener);
            form.removePointerDraggedListener(formPointerListener);
        }
        super.deinitialize(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        
        
    }

    @Override
    protected void paintGlass(Graphics g) {
        super.paintGlass(g);
        if (left.getX() + left.getWidth() > 0) {
            int color = g.getColor();
            g.setColor(0xcccdce);
            int alpha = g.getAlpha();
            double sideMenuRatio = (left.getWidth() + left.getX()) / (double)Math.max(1,Math.max(left.getPreferredW(), left.getWidth() + left.getX()));
            
            g.setAlpha((int)Math.round(sideMenuRatio * alpha * 0.8));
            g.fillRect(left.getWidth() + left.getX(), getY(), getWidth()-left.getWidth()-left.getX(), getHeight());
            g.setAlpha(alpha);
            g.setColor(color);
        }
    }
    
    
    
    

    
    
    /**
     * Wraps a component in a container that will calculate an appropriate preferred size for a side menu.
     * @param cmp
     * @return 
     */
    public static Component wrapSideMenu(Component cmp) {
        Container out = new Container(new BorderLayout()) {
            @Override
            protected Dimension calcPreferredSize() {
                return new Dimension(Math.min(CN.convertToPixels(60), CN.getDisplayWidth() - CN.convertToPixels(8)), CN.getDisplayHeight());
            }
            
        };
        out.setGrabsPointerEvents(true);
        out.getAllStyles().stripMarginAndPadding();
        out.add(BorderLayout.CENTER, cmp);
        return out;
    }
    
    private ActionListener vcListener = evt -> {
        if (evt instanceof ShowSideBarEvent) {
            evt.consume();
            showSideBar();
            return;
        }
        if (evt instanceof HideSideBarEvent) {
            evt.consume();
            hideSideBar();;
        }
    };
    
    private ActionListener formPointerListener = evt -> {
        if (pressed && dragging && dragLock) {
            evt.consume();
        }
        if (evt.getEventType() == ActionEvent.Type.PointerPressed) {
            startDragX = evt.getX();
            
            startDragY = evt.getY();
            startSlidePos = slidePos;
            Component cmp = getComponentAt(evt.getX(), evt.getY());
            if (cmp == null) {
                return;
            }
            if (cmp != this && cmp.blocksSideSwipe()) {
                return;
            }
            Form f = getComponentForm();
            
            if (f != null) {
                if (cmp != f.getContentPane() && !f.getContentPane().contains(cmp)) {
                    // This must be in a layered pane
                    return;
                }
            }
            
            pressed = true;
        } else {
            if (!pressed) {
                return;
            }
            
            if (evt.getEventType() == ActionEvent.Type.PointerDrag) {
                
                int diffX = Math.abs(evt.getX() - startDragX);
                int diffY = Math.abs(evt.getY() - startDragY);
                if (diffX < diffY) {
                    if (dragging) {
                        if (slidePos > sideMenuWidth/2) {
                            slidePos = sideMenuWidth;
                            animateLayout(200);
                        } else {
                            slidePos = 0;
                            animateLayout(200);
                        }
                    }
                    dragging = false;
                    pressed = false;
                    return;
                }
                dragging = true;
                
                if (diffX> CN.convertToPixels(3) && diffY < CN.convertToPixels(1) ) {
                    dragLock = true;
                    Form f = getComponentForm();
                    if (f != null) {
                        
                        f.clearComponentsAwaitingRelease();
                    }
                    evt.consume();
                } else if (dragLock) {
                    evt.consume();
                } else {
                    
                }
                
                sideMenuWidth = getLeft().getPreferredW();
                slidePos = Math.min(sideMenuWidth, Math.max(0, startSlidePos + evt.getX() - startDragX));
                revalidateLater();
            } else if (evt.getEventType() == ActionEvent.Type.PointerReleased) {
                if (dragging) {
                    if (Display.getInstance().getDragSpeed(false) < -1) {
                        
                        if (slidePos < sideMenuWidth) {
                            slidePos = sideMenuWidth;
                            animateLayout(200);
                        }
                    } else if (Display.getInstance().getDragSpeed(false) > 1) {
                        
                        if (slidePos > 0) {
                            slidePos = 0;
                            animateLayout(200);
                        }
                    } else if (slidePos < sideMenuWidth && slidePos > sideMenuWidth/2) {
                        
                        slidePos = sideMenuWidth;
                        animateLayout(200);
                    } else if (slidePos > 0 && slidePos < sideMenuWidth/2) {
                        
                        slidePos = 0;
                        animateLayout(200);
                    }
                }
                dragging = false;
                pressed = false;
                
            }
        }
        
        
    };

    
    private Component getLeft() {
        return left;
    }
    
    
    public void showSideBar() {
        if (slidePos < left.getPreferredW()) {
            slidePos = left.getPreferredW();
            animateLayout(200);
        }
    }
    
    public void hideSideBar() {
        if (slidePos > 0) {
            slidePos = 0;
            animateLayout(200);
        }
    }
    
    public void install(Form form) {
        form.getToolbar().setTitle("foo");
        form.getToolbar().hideToolbar();
        form.getContentPane().setLayout(new BorderLayout());
        form.getContentPane().setScrollableY(false);
        form.getContentPane().setScrollableX(false);
        form.getContentPane().removeAll();
        form.getContentPane().getStyle().stripMarginAndPadding();
        form.getContentPane().add(BorderLayout.CENTER, this);
    }
}
