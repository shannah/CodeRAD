/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.shared.components;

import com.codename1.ui.CN;
import com.codename1.ui.Component;
import static com.codename1.ui.ComponentSelector.$;
import com.codename1.ui.Container;
import com.codename1.ui.events.ScrollListener;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.Layout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author shannah
 */
public class OverflowContainer extends Container {
    private Component mainContents, overflowContent;
    
    boolean inScrollListener;
    
    private class OverflowLayout extends Layout {

        @Override
        public void layoutContainer(Container parent) {
            int x = 0;
            int y = 0;
            int w = parent.getWidth();
            int h = parent.getHeight();

            Component c = mainContents;
            
            c.setWidth(w);
            c.setHeight(h);
            c.setX(x);
            c.setY(y);
            overflowContent.setX(parent.getLayoutWidth() + 2);
            overflowContent.setY(0);
            overflowContent.setHeight(parent.getLayoutHeight());
            overflowContent.setWidth(overflowContent.getPreferredW());
            
        }

        @Override
        public Dimension getPreferredSize(Container parent) {
            Dimension dim = new Dimension(
                    mainContents.getPreferredW(), 
                    mainContents.getPreferredH()
            );
            
            return dim;
        }
        
    }

    @Override
    public int getLayoutHeight() {
        int height = getHeight();
        if (height <= 1) {
            return getPreferredH();
        }
        return height;
    }

    @Override
    public int getLayoutWidth() {
        int width = getWidth();
        if (width <= 0) {
            return getPreferredW();
        }
        return width;
    }
    
    
    
    @Override
    protected Dimension calcScrollSize() {
        return new Dimension(mainContents.getWidth(), mainContents.getHeight());
        
    }
    
    public OverflowContainer(Component mainContents, Component overflowContent) {
        setLayout(new OverflowLayout());
        //setLayout(new BorderLayout());
        getStyle().setMargin(0,0,0,0);
        setScrollableX(true);
        
        this.mainContents = mainContents;
        if (mainContents.getName() == null) {
            mainContents.setName("OverflowContainer#MainContents");
        }
        if (overflowContent.getName() == null) {
            overflowContent.setName("OverflowContainer#OverflowContent");
        }
        this.overflowContent = overflowContent;
        addAll(mainContents, overflowContent);
        setTensileDragEnabled(true);
        setTensileLength(overflowContent.getPreferredW() + CN.convertToPixels(5));
        setAlwaysTensile(true);
        
        
        
    }

    
    @Override
    public boolean isScrollableX() {
        return true;
    }

    @Override
    protected boolean constrainWidthWhenScrollable() {
        return true;
    }

    

    

    
    

    @Override
    public void layoutContainer() {
        super.layoutContainer();
        setTensileLength(overflowContent.getPreferredW() + CN.convertToPixels(5));
    }
    
    
    public static class OverflowGroup {
        private static final String KEY = "$$OverflowGroup";
        
        private HashSet<OverflowContainer> components = new HashSet<>();
        private boolean inScrollListener;
        private ScrollListener l = (scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (inScrollListener) {
                return;
            }
            inScrollListener = true;
            try {
                for (OverflowContainer cnt : components) {
                    if (cnt.getScrollX() != scrollX) {
                        cnt.setScrollX(scrollX);
                    }
                }
            } finally {
                inScrollListener = false;
            }
        };
        
        public void add(OverflowContainer cnt) {
            if (components.contains(cnt)) {
                return;
            }
            cnt.addScrollListener(l);
            components.add(cnt);
        }
        
        public void remove(OverflowContainer cnt) {
            cnt.removeScrollListener(l);
            components.remove(cnt);
        }
        
        public void clear() {
            List<OverflowContainer> toRemove = new ArrayList<>(components);
            for (OverflowContainer cnt : toRemove) {
                remove(cnt);
            }
        }
        
        public static OverflowGroup findGroup(OverflowContainer cnt) {
            Component cmp = cnt.getParent();
            while (cmp != null) {
                OverflowGroup grp = (OverflowGroup)cmp.getClientProperty(KEY);
                if (grp != null){
                    return grp;
                }
                cmp = cmp.getParent();
            }
            return null;
        }
        
        public Component findGroupContainer(Component searchStart) {
            while (searchStart != null) {
                OverflowGroup grp = (OverflowGroup)searchStart.getClientProperty(KEY);
                if (grp != null && grp == this) {
                    return searchStart;
                }
                searchStart = searchStart.getParent();
            }
            return null;
        }
        
        public static OverflowGroup createGroup(Container cnt) {
            OverflowGroup group = new OverflowGroup();
            cnt.putClientProperty(KEY, group);
            return group;
        }
        
        public static void removeGroup(Container cnt) {
            OverflowGroup group = (OverflowGroup)cnt.getClientProperty(KEY);
            if (group != null) {
                group.clear();
                cnt.putClientProperty(KEY, null);
            }
            
        }
    }
    
            
}
