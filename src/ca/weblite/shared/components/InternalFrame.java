/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.shared.components;

import com.codename1.ui.Button;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.Label;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.FlowLayout;

/**
 * An internal frame, which is used inside the {@link TabbedDocumentView}
 * @author shannah
 */
public class InternalFrame extends Container {
    
    public static interface InternalFrameDelegate {
        public void close(InternalFrame src);
        
    }
    
    public InternalFrame(String title, Component mainContent) {
        
        this.mainContent = mainContent;
        initUI();
        setTitle(title);
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        Container topBar = new Container(new BorderLayout());
        topBar.setUIID("InternalFrameTopBar");
        title = new Label();
        title.setUIID("InternalFrameTitle");
        closeButton = new Button();
        closeButton.setMaterialIcon(FontImage.MATERIAL_CLOSE);
        closeButton.addActionListener(e->{
            if (delegate != null) {
                delegate.close(this);
            }
        });
        
        topBar.add(BorderLayout.CENTER, FlowLayout.encloseCenter(title));
        
        topLeftButtons = FlowLayout.encloseIn();
        topRightButtons = FlowLayout.encloseRight();
        topRightButtons.add(closeButton);
        topBar.add(BorderLayout.EAST, topRightButtons);
        topBar.add(BorderLayout.WEST, topLeftButtons);
        add(BorderLayout.NORTH, topBar);
        if (mainContent != null) {
            add(BorderLayout.CENTER, mainContent);
        }
    }
    
    public void setTitle(String title) {
        this.title.setText(title);
    }
    
    public String getTitle() {
        return this.title.getText();
    }
    
    public void setMainContent(Component cmp) {
        if (mainContent != null) {
            mainContent.remove();
        }
        mainContent = cmp;
        if (mainContent != null) {
            add(BorderLayout.CENTER, mainContent);
        }
    }
    
    public Component getMainContent() {
        return mainContent;
    }
    
    public Container getTopLeftButtonsContainer() {
        return topLeftButtons;
    }
    
    public Container getTopRightButtonsContainer() {
        return topRightButtons;
    }
    
    private Button closeButton;
    private Component mainContent;
    private Container topLeftButtons, topRightButtons;
    private Label title;
    private InternalFrameDelegate delegate;
    
    
}
