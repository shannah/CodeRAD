/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.shared.components;

import ca.weblite.shared.components.InternalFrame.InternalFrameDelegate;
import com.codename1.ui.Button;
import com.codename1.ui.ButtonGroup;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.RadioButton;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * A frame for keeping multiple documents opened in separate tabs.  Each document is contained inside its own {@link InternalFrame}.
 * @author shannah
 */
public class TabbedDocumentView extends Container {
    
    public static interface TabbedDocumentViewDelegate {
        public void close(int index);
    }
    
    private class FrameHolder implements InternalFrameDelegate {
        RadioButton tabButton;
        Button closeTabButton;
        InternalFrame frame;
        
        FrameHolder(String title, Component mainContent) {
            frame = new InternalFrame(title, mainContent);
            tabButton = RadioButton.createToggle(title, tabButtonGroup);
            tabButton.setUIID("FrameHolderTab");
            tabButton.addActionListener(e->{
                showDocument(tabs.indexOf(this));
            });
            
        }

        @Override
        public void close(InternalFrame src) {
            if (delegate != null) {
                delegate.close(tabs.indexOf(this));
            }
        }
        
        public Container getTopLeftButtonsContainer() {
            return frame.getTopLeftButtonsContainer();
        }
        
        public Container getTopRightButtonsContainer() {
            return frame.getTopRightButtonsContainer();
        }
    }
    
    
    public TabbedDocumentView() {
        super(new BorderLayout());
        initUI();
    }
    
    public void showDocument(int index) {
        if (currentIndex >= 0) {
            FrameHolder curr = tabs.get(currentIndex);
            curr.frame.remove();
        }
        currentIndex = index;
        if (currentIndex >= 0) {
            FrameHolder curr = tabs.get(currentIndex);
            add(BorderLayout.CENTER, curr.frame);
            curr.tabButton.setSelected(true);
        }
        revalidateLater();
    }
    
    public int getDocumentCount() {
        return tabs.size();
    }
    
    public String getDocumentTitle(int index) {
        return tabs.get(index).frame.getTitle();
    }
    
    public Component getDocumentView(int index) {
        return tabs.get(index).frame.getMainContent();
    }
    
    public Container getTopLeftButtonsContainer(int index) {
        return tabs.get(index).getTopLeftButtonsContainer();
    }
    
    public Container getTopRightButtonsContainer(int index) {
        return tabs.get(index).getTopRightButtonsContainer();
    }
    
    public int findIndexWithContent(Component mainContent) {
        for (FrameHolder holder : tabs) {
            if (holder.frame.getMainContent() == mainContent) {
                return tabs.indexOf(holder);
            }
        }
        return -1;
    }
    
    public int findIndexWithFrameName(String name) {
        for (FrameHolder holder : tabs) {
            if (name.equals(holder.frame.getTitle())) {
                return tabs.indexOf(holder);
            }
        }
        return -1;
    }
    
    public int getSelectedIndex() {
        return currentIndex;
    }
    
    public void addDocumentView(String title, Component mainContent) {
        FrameHolder holder = new FrameHolder(title, mainContent);
        //tabButtonGroup.add(holder.tabButton);
        tabs.add(holder);
        tabsCnt.add(holder.tabButton);
        
        revalidateLater();
    }
    
    public void removeDocument(int index) {
        FrameHolder curr = tabs.get(index);
        if (index == currentIndex) {
            curr.frame.remove();
        }
        curr.tabButton.remove();
        tabButtonGroup.remove(curr.tabButton);
        tabs.remove(curr);
        
        if (index <= currentIndex) {
            currentIndex--;
            showDocument(currentIndex);
        }
        revalidateLater();
        
        
        
        
    }
    
    private void initUI() {
        add(BorderLayout.NORTH, tabsCnt);
        tabsCnt.setUIID("FrameHolderTabs");
        
    }
    
    public void setDelegate(TabbedDocumentViewDelegate del) {
        this.delegate = del;
    }
    
    public TabbedDocumentViewDelegate getDelegate() {
        return this.delegate;
    }
    
    private int currentIndex=-1;
    private List<FrameHolder> tabs = new ArrayList<>();
    private TabbedDocumentViewDelegate delegate;
    private ButtonGroup tabButtonGroup = new ButtonGroup();
    private Container tabsCnt=new Container(BoxLayout.x());
    
    
    
    
}
