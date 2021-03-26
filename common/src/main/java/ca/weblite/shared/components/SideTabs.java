/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.shared.components;

import com.codename1.ext.ui.FontAwesome;
import com.codename1.ui.Button;
import com.codename1.ui.ButtonGroup;
import static com.codename1.ui.CN.convertToPixels;
import com.codename1.ui.CheckBox;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.Image;
import com.codename1.ui.RadioButton;
import com.codename1.ui.Tabs;
import com.codename1.ui.events.SelectionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;

/**
 * A {@link Tabs} component that displays the tab buttons on the left side.
 * @author shannah
 */
public class SideTabs extends Container {
    private class TabsImpl extends Tabs {
        public Tabs addTabFA(String title, char materialIcon, float iconSize, Component component) {
            int index = getTabsContainer().getComponentCount();
            Image i = FontAwesome.createBrand(materialIcon, "Tab", iconSize);
            insertTab(title, i, component, index);
            Style sel = getUIManager().getComponentCustomStyle("Tab", "press");
            i = FontAwesome.createBrand(materialIcon, sel, iconSize);
            setTabSelectedIcon(index, i);
            return this;
        }
    }
    private TabsImpl tabs = new TabsImpl();
    private Container sideMenu = new Container(BoxLayout.y());
    private ButtonGroup menuButtonGroup;
    
    public SideTabs() {
        super(new BorderLayout());
        initUI();
    }
    
    private void initUI() {
        tabs.hideTabs();
        tabs.setTabUIID("SideTab");
        tabs.addSelectionListener(new SelectionListener() {
            @Override
            public void selectionChanged(int oldSelected, int newSelected) {
                RadioButton cb = (RadioButton)sideMenu.getComponentAt(newSelected);
                cb.setSelected(true);
            }
            
        });
        sideMenu.setUIID("SideTabsMenu");
        sideMenu.setPreferredW(convertToPixels(56.5f));
        
        add(BorderLayout.WEST, sideMenu);
        add(BorderLayout.CENTER, tabs);
    }
            
    public SideTabs setTabUIID(String uiid) {
        tabs.setTabUIID(uiid);
        return this;
    }
    
    public String getTabUIID() {
        return tabs.getTabUIID();
    }
    
    public int getTabCount() {
        return tabs.getTabCount();
    }
    
    public String getTabTitle(int index) {
        return tabs.getTabTitle(index);
    }
    
    public Image getTabIcon(int index) {
        return tabs.getTabIcon(index);
    }
    
    public Component getTabComponentAt(int index) {
        return tabs.getTabComponentAt(index);
    }
    
    public SideTabs addTab(String title, Component content) {
        tabs.addTab(title, content);
        update();
        return this;
    }
    
    
    
    public SideTabs addTabFA(String title, char icon, float iconSize, Component content) {
        tabs.addTabFA(title, icon, iconSize, content);
        update();
        return this;
        
    }
    
    public SideTabs addTab(String title, char materialIcon, float iconSize, Component content) {
        
        tabs.addTab(title, materialIcon, iconSize, content);
        
        update();
        return this;
    }
    
    public void update() {
        menuButtonGroup = new ButtonGroup();
        sideMenu.removeAll();
        int len = tabs.getTabCount();
        for (int i=0; i<len; i++) {
            RadioButton mb = createMenuButton(i);
            sideMenu.add(mb);
        }
    }
    
    private RadioButton createMenuButton(int index) {
        RadioButton out = RadioButton.createToggle(tabs.getTabTitle(index), tabs.getTabIcon(index), menuButtonGroup);
        out.setUIID(tabs.getTabUIID());
        out.setGap(convertToPixels(3f));
        //out.setText(tabs.getTabTitle(index));
        //out.setIcon(tabs.getTabIcon(index));
        out.addActionListener(e->{
            tabs.setSelectedIndex(index);
        });
        return out;
    }
    
}
