/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.shared.components;

import com.codename1.components.InteractionDialog;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import static com.codename1.ui.ComponentSelector.$;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.events.ActionSource;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.Layout;
import java.util.ArrayList;
import java.util.List;

/**
 * A popup-menu with commands that the user can click on.
 * @author shannah
 */
public class PopupMenu extends InteractionDialog {
    
    /**
     * @return the commandLabel
     */
    public String getCommandLabel() {
        return commandLabel;
    }

    /**
     * @param commandLabel the commandLabel to set
     */
    public void setCommandLabel(String commandLabel) {
        this.commandLabel = commandLabel;
    }

    /**
     * @return the materialIcon
     */
    public char getMaterialIcon() {
        return materialIcon;
    }

    /**
     * @param materialIcon the materialIcon to set
     */
    public void setMaterialIcon(char materialIcon) {
        this.materialIcon = materialIcon;
    }
    private List<Command> commands = new ArrayList<>();
    private Command cmd;
    private char materialIcon = FontImage.MATERIAL_MORE_VERT;
    private String commandLabel = "";
    private Container commandsCnt;
    public PopupMenu() {
        super(new BorderLayout());
        setUIID("PopupMenu");
        setDialogUIID("PopupMenuContent");
        setTitle("Menu");
        $("DialogTitle", this).setUIID("PopupMenuTitle").remove();
        commandsCnt = new Container(BoxLayout.y());
        commandsCnt.setScrollableY(true);
        setDisposeWhenPointerOutOfBounds(true);
        add(BorderLayout.CENTER, commandsCnt);
        this.setAnimateShow(false);
        
    }
    
    public void setCommandsLayout(Layout layout) {
        commandsCnt.setLayout(layout);
        //System.out.println("Setting commands layout to "+layout);
    }
    
    public Command getCommand() {
        if (cmd != null) {
            return cmd;
        }
        Command out = Command.createMaterial(getCommandLabel(), getMaterialIcon(),  e->{
            if (!isInitialized()) {
                
                showPopupDialog(e.getComponent());
            } else {
                dispose();
            }
        });
        cmd = out;
        return out;
    }

    @Override
    public void showPopupDialog(Component c) {
        super.showPopupDialog(c);
    }
    
    public PopupMenu addCommand(Component component) {
        commandsCnt.add(component);
        if (component instanceof ActionSource) {
            ((ActionSource)component).addActionListener(e->{
                dispose();
            });
        }
        return this;
    }
    
    public PopupMenu addCommand(Command command) {
        commands.add(command);
        Button b = new Button(command);
        b.addActionListener(e->{
            dispose();
        });
        commandsCnt.add(b);
        return this;
    }
    
    public void removeAllCommands() {
        commandsCnt.removeAll();
    }
    
    
}