/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.shared.components;

import com.codename1.components.InteractionDialog;
import com.codename1.components.SpanLabel;
import com.codename1.ui.Button;
import com.codename1.ui.CN;
import com.codename1.ui.FontImage;
import com.codename1.ui.layouts.BorderLayout;

/**
 * A button that, when clicked on, displays some help text in a popup bubble.
 * @author shannah
 */
public class HelpButton extends Button {

    /**
     * Help button types.
     */
    public static enum HelpButtonType {
        Info(FontImage.MATERIAL_HELP, "HelpButtonInfo"),
        Error(FontImage.MATERIAL_ERROR, "HelpButtonError");
        
        HelpButtonType(char icon, String uiid) {
            this.icon = icon;
            this.uiid = uiid;
        }
        
        void apply(HelpButton btn) {
            btn.setUIID(uiid);
            btn.setMaterialIcon(icon);
        }
        
        private char icon;
        private String uiid;
    }
    
    /**
     * @return the helpText
     */
    public String getHelpText() {
        return helpText;
    }

    /**
     * @param helpText the helpText to set
     */
    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }
    private String helpText;
    private InteractionDialog dlg;
    private HelpButtonType type = HelpButtonType.Info;
    
    public HelpButton(String helpText) {
        this(helpText, "Button");
    }
    
    public HelpButton(String helpText, String UUID) {
        super("");
        this.helpText = helpText;
        setMaterialIcon(FontImage.MATERIAL_HELP);
        addActionListener(e->{
            if (dlg != null && dlg.isShowing()) {
                dlg.dispose();
                dlg = null;
                return;
            }
            dlg = new InteractionDialog();
            dlg.setFormMode(true);
            dlg.setLayout(new BorderLayout());
            SpanLabel sl = new SpanLabel(getHelpText());
            if (popupPreferredW > 0) {
                sl.setPreferredW(popupPreferredW);
            }
            dlg.add(BorderLayout.CENTER, sl);
            dlg.setDisposeWhenPointerOutOfBounds(true);
            dlg.setAnimateShow(false);
            dlg.showPopupDialog(this);
        });
    }
    
    public void setType(HelpButtonType type) {
        if (this.type != type) {
            this.type = type;
            this.type.apply(this);
        }
    }
    
    private int popupPreferredW = Math.min(CN.getDisplayWidth(), CN.convertToPixels(100));
    public void setPopupPreferredW(int w) {
        this.popupPreferredW = w;
    }
}