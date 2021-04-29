/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.controllers;

import com.codename1.ui.Button;
import com.codename1.ui.CN;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.ComponentSelector;
import static com.codename1.ui.ComponentSelector.$;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.plaf.Style;

/**
 * A controller for handling application logic related to a Form.
 * 
 * == Controller Hierarchy
 * 
 * Controllers have a hierarchy similar to UI components.  The {@link ApplicationController} is generally the "root"
 * controller.  The top-level form controller is a "child" of the application controller.  If the user navigates to another
 * form from which they should be able to navigate back, then the new FormController is a "child" of the previous FormController.
 * 
 * == Back Navigation
 * 
 * If a FormController's parent controller is also a FormController, then "back" functionality will automatically be connected
 * to its Form.  When the user clicks "back", they will automatically navigate to the parent controller's form.
 * 
 * Views may also request a "back" navigation by firing a {@link FormBackEvent}, which will propagate up the controller hierarchy
 * to the first FormController, where it will be handled.
 * 
 * == Event Propagation
 * 
 * Events which are dispatched to a Controller will be propagated to the controller's parent, and its parent, and so on... until the event
 * is consumed, or until it reaches the root.  In this way, it is possible to create a catch-all event handler in the ApplicationController
 * that will catch all events in the application that haven't been consumed by another controller.
 * 
 * 
 * == Example
 * .A FormController for the ChatRoomView.  Excerpt from https://shannah.github.io/RADChatApp/getting-started-tutorial.html[this tutorial,window=top].
[source,java]
* ----
public class ChatFormController extends FormController {
    
    // Define the "SEND" action for the chat room
    public static final ActionNode send = action(
        enabledCondition(entity-> {
            return !entity.isEmpty(ChatRoom.inputBuffer);
        }),
        icon(FontImage.MATERIAL_SEND)
    );
    public ChatFormController(Controller parent) {
        super(parent);
        Form f = new Form("My First Chat Room", new BorderLayout());
        
        // Create a "view node" as a UI descriptor for the chat room.
        // This allows us to customize and extend the chat room.
        ViewNode viewNode = new ViewNode(
            actions(ChatRoomView.SEND_ACTION, send)
        );
        
        // Add the viewNode as the 2nd parameter
        ChatRoomView view = new ChatRoomView(createViewModel(), viewNode, f);
        f.add(CENTER, view);
        setView(f);
        
        addActionListener(send, evt->{
            evt.consume();
            // ... handle send event
            
        });
    }
    //...
 }
 ----
 * 
   

 * @author shannah
 */
public class FormController extends ViewController implements Runnable {
    private String title;
    private String pathName;

    private ActionListener showListener;

    private ActionListener showListener() {
        if (showListener == null) {
            showListener = evt->{
                dispatchEvent(new FormShownEvent(evt.getSource()));
            };
        }
        return showListener;
    }
    
    /**
     * Base class for FormController events.
     */
    public static class FormControllerEvent extends ControllerEvent {
        public FormControllerEvent() {
            
        }
        
        public FormControllerEvent(Object source) {
            super(source);
        }
    }
    
    /**
     * Event that can be fired by any view to request that the current form go back 
     * to the previous form.
     */
    public static class FormBackEvent extends FormControllerEvent {
        
        public FormBackEvent(Object source) {
            super(source);
        }
    }

    public static class FormShownEvent extends FormControllerEvent {
        public FormShownEvent(Object source) { super(source);}
        public Form getSourceForm() {
            return (Form)getSource();
        }

        public FormController getSourceFormController() {
            return FormController.getFormController(getSourceForm());
        }
    }
    
    public FormController(Controller parent) {
        super(parent);
    }
    
    /**
     * Sets the form associated with this controller.  This will automatically set
     * the back command of the form to call back() on this controller.
     * @param form 
     */
    public void setView(Form form) {
        super.setView(form);
        Form currView = getView();
        if (currView != null) {
            currView.removeShowListener(showListener());
        }
        if (form != null && hasBackCommand()) {
            form.setBackCommand(new Command("") {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    back();
                }
                
            });
            Toolbar tb = form.getToolbar();
            if (tb != null) {
                tb.addMaterialCommandToLeftBar("", FontImage.MATERIAL_ARROW_BACK_IOS, evt->{
                    back();
                });
            }
        }
    }
    
    public void setTitle(String title) {
        this.title = title;
        if (getView() != null) {
            getView().setTitle(title);
        }
        
    }
    
    public String getTitle() {
        return title;
    }
    
    
    Label titleLbl;
    
    /**
     * Overrides parent setView().  Delegates to {@link #setView(com.codename1.ui.Form) } if cmp is
     * a form.  Throws IllegalArgumentException otherwise.
     * @param cmp 
     */
    public void setView(Component cmp) {
        if (cmp == null) {
            setView((Form)null);
            return;
        }
        Form currView = getView();
        if (currView != null) {
            currView.removeShowListener(showListener());
        }
        if (cmp instanceof Form) {
            ((Form)cmp).addShowListener(showListener());
            setView((Form)cmp);
        } else {
            
            Form f = new Form(new BorderLayout()) {
                
                @Override
                public void setTitle(String title) {
                    super.setTitle(title);
                    if (titleLbl != null) {
                        titleLbl.setText(title);
                        revalidateLater();
                    }
                    
                    
                }
                
                
               
                @Override
                public void layoutContainer() {
                   super.layoutContainer();
                   
                   int maxLeftX = 0;
                   ComponentSelector cmps =  $(".left-inset", this);
                   for (Component c : cmps) {
                       if (!c.isVisible() || c.isHidden() || c.getWidth() == 1 || c.getHeight() == 0) {
                           continue;
                       }
                       Component wrap = $(c).parents(".left-edge").first().asComponent();
                       if (wrap == null) {
                           continue;
                       }
                       int thisLeftX = c.getAbsoluteX() + c.getStyle().getPaddingLeftNoRTL() - wrap.getAbsoluteX();
                       maxLeftX = Math.max(maxLeftX, thisLeftX);

                   }
                   //maxLeftX -= getAbsoluteX();

                   for (Component c : cmps) {
                       if (!c.isVisible() || c.isHidden() || c.getWidth() == 1 || c.getHeight() == 0) {
                           continue;
                       }
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
            };
            f.addShowListener(showListener());
            f.getToolbar().hideToolbar();
            Container titleBar = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER_ABSOLUTE));
            titleBar.setSafeArea(true);
            titleBar.setUIID("TitleArea");

            if (hasBackCommand()) {
                Button back = new Button();
                FontImage.setIcon(back, FontImage.MATERIAL_ARROW_BACK_IOS, -1);
                titleBar.add(BorderLayout.WEST, back);
                back.addActionListener(evt->{
                    evt.consume();
                    ActionSupport.dispatchEvent(new FormController.FormBackEvent(back));
                });

            }

            AppSectionController sectionCtl = getSectionController();
            if (sectionCtl != null) {
                Button done = new Button("Done");
                done.addActionListener(evt->{
                    evt.consume();
                    ActionSupport.dispatchEvent(new AppSectionController.ExitSectionEvent(done));
                });
                titleBar.add(BorderLayout.EAST, done);
            }

            titleLbl = new Label();
            titleLbl.setUIID("Title");
            if (title != null) {
                titleLbl.setText(title);
            }
            titleBar.add(BorderLayout.CENTER, titleLbl);
            f.add(BorderLayout.NORTH, titleBar);
            f.add(BorderLayout.CENTER, cmp);
            f.revalidateLater();
            setView(f);
        }
    }
    
    /**
     * Gets form associated with this controller.
     * @return 
     */
    @Override
    public Form getView() {
        return (Form)super.getView();
    }
    
    /**
     * Goes back to previous form.  The previous form is always the parent controller's
     * form, if the parent controller is a FormController.
     * 
     * If the current controller is an instance of {@link AutoCloseable},
     * then this will first attempt to call {@link AutoCloseable#close() } on the current form first.  If it throws an exception,
     * then that exception will be swallowed, but the showBack() action will be cancelled.
     */
    public void back() {
        Controller parent = getParent();
        if (parent != null) {
            FormController fc = parent.getFormController();
            if (fc != null) {
                if (this instanceof AutoCloseable) {
                    AutoCloseable ac = (AutoCloseable)this;
                    try {
                        ac.close();
                    } catch (Exception ex) {
                        return;
                    }
                }
                fc.getView().showBack();
            }
        }
    }
    
    public void show() {
        startControllerInternal();
        CN.setBookmark(this);
        getView().show();
    }
    
    /**
     * Shows this controller's form using the "back" animation.  If the current controller is an instance of {@link AutoCloseable},
     * then this will first attempt to call {@link AutoCloseable#close() } on the current form first.  If it throws an exception,
     * then that exception will be swallowed, but the showBack() action will be cancelled.
     */
    public void showBack() {
        Form currForm = CN.getCurrentForm();
        if (currForm != null) {
            ViewController currentController = getViewController(currForm);
            if (currentController instanceof AutoCloseable) {
                AutoCloseable ac = (AutoCloseable)currentController;
                try {
                    ac.close();
                } catch (Exception ex) {
                    return;
                }
            }
        }
        
        getView().showBack();
    }
    
    public boolean hasBackCommand() {
        Controller parent = getParent();
        return parent != null && parent.getFormController() != null;
    }

    /**
     * Handles Controller event.  This implementation consumes {@link FormBackEvent} events.
     * @param evt 
     */
    @Override
    public void actionPerformed(ControllerEvent evt) {
        if (evt instanceof FormBackEvent) {
            evt.consume();
            back();
            return;
        }
    }


    /**
     * Gets the pathName for this controller.
     * @return
     */
    public String getPathName() {
        return pathName;
    }

    /**
     * Sets the path name for this controller whih will be used by {@link #getPathString(String)}.
     * @param name The path name for this controller.
     */
    public void setPathName(String name) {
        this.pathName = name;
    }

    /**
     * Gets the full path string to this controller.  This allows the controller hierarchy to be described as a path,
     * like a filesystem path.
     *
     * . If the {@link #pathName} of any controller in the controller hierarchy is null, then that controller is
     * omitted from the path.
     * . Only {@link FormController} controllers are included in the path.
     * . If this FormController has no {@link #pathName} set, then this will return the same path string as the parent controller.
     * . If none of the controllers in the hierarchy (including this one) have a {@link #pathName} set then this will return an empty string.
     *
     * @param separator The path separator
     * @return The path string to this controller.
     */
    public String getPathString(String separator) {
        String name = getPathName();
        name = name == null ? "" : separator + name;
        Controller parent = getParent();
        if (parent == null) {
            return name;
        }
        FormController formController = parent.getFormController();
        if (formController == null) {
            return name;
        }
        return formController.getPathString(separator) + name;
    }


    public static FormController getFormController(Form form) {
        ViewController vc = ViewController.getViewController(form);
        if (vc == null) return null;
        FormController fc = vc.getFormController();
        if (fc == null) {
            return null;
        }
        return fc;
    }




    public void refresh() {
        boolean isShowing = CN.getCurrentForm() == getView();
        super.refresh();
        if (isShowing) {
            show();
        }
    }

    public void run() {
        if (isStarted()) {
            refresh();
        } else {
            startControllerInternal();
            show();
        }
    }

    
}
