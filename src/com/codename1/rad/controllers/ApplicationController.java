/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.controllers;

import com.codename1.io.Log;
import com.codename1.ui.CN;
import static com.codename1.ui.CN.addNetworkErrorListener;
import static com.codename1.ui.CN.getCurrentForm;
import static com.codename1.ui.CN.updateNetworkThreadCount;
import com.codename1.ui.Dialog;
import com.codename1.ui.Form;
import com.codename1.ui.Toolbar;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;

/**
 * The main application controller.  This is the root controller for the entire application.
 * Applications should extend this class and use it as their main application class.  Then can
 * then override the actionPerformed() method to handle events of type {@link StartEvent}, {@link StopEvent},
 * {@link DestroyEvent}, and {@link InitEvent}.
 * 
 * The application controller is ideally suited to act as the main class for a CodenameOne application.  It implements all of the
 * lifecycle methods ({@link #init(java.lang.Object) }, {@link #start() }, {@link #stop() }, and {@link #destroy() }, and dispatches 
 * corresponding events that you can handle in your controller.
 * 
 * == Example
 * 
 * .Typical application structure, replacing the main app class with an ApplicationController
 * [source,java]
 * ----
 * public class MyApplication extends ApplicationController {
 *      public void actionPerformed(ActionEvent evt) {
 *          if (evt instance of StartEvent) {
 *              evt.consume();
 * 
 *              // Show a form
 *              new MyFormController(this).getView().show();
 *          }
 *      }
 * }
 * ----
 * @author shannah
 */
public class ApplicationController extends Controller {
    
    protected Form current;
    private Resources theme;
    
    public static ApplicationController instance;
    
    public ApplicationController() {
        super((Controller)null);
        instance = this;
    }
    
    public static ApplicationController getInstance() {
        return instance;
    }
    
    public void init(Object context) {
        // use two network threads instead of one
        
        // Disable revalidating on style changes.  This will ultimately
        // be the default for all CN1 apps.  But until then, we'll make it the default
        // for CodeRAD apps
        CN.setProperty("Component.revalidateOnStyleChange", "false");
        
        // Disable the default revalidation behaviour that triggers a revalidate
        // of the entire form when any container on the form is revalidated.
        // This is expensive and shouldn't be necessary.  Ideally this flag should
        // be set to false on *all* apps, but we don't want to break apps that depend
        // on this behaviour.  But since CodeRAD is new, we can apply this by default
        // for all CodeRAD apps.
        CN.setProperty("Form.revalidateFromRoot", "false");
        updateNetworkThreadCount(2);

        theme = UIManager.initFirstTheme("/theme");

        // Enable Toolbar on all Forms by default
        Toolbar.setGlobalToolbar(true);

        // Pro only feature
        Log.bindCrashProtection(true);

        addNetworkErrorListener(err -> {
            // prevent the event from propagating
            err.consume();
            if(err.getError() != null) {
                Log.e(err.getError());
            }
            Log.sendLogAsync();
            Dialog.show("Connection Error", "There was a networking error in the connection to " + err.getConnectionRequest().getUrl(), "OK", null);
        }); 
        dispatchEvent(new InitEvent(context));
    }
    
    protected void showCurrentForm() {
        if (current != null) {
            current.show();
        }
    }
    
    public void start() {
        showCurrentForm();
        dispatchEvent(new StartEvent());
    }
    
    public void stop() {
        dispatchEvent(new StopEvent());
        current = getCurrentForm();
        if(current instanceof Dialog) {
            ((Dialog)current).dispose();
            current = getCurrentForm();
        }
    }
    
    public void destroy() {
        dispatchEvent(new DestroyEvent());
    }
    
    public static class ApplicationEvent extends ControllerEvent {}
    public static class InitEvent extends ApplicationEvent{
        private Object context;
        
        public InitEvent(Object context) {
            this.context = context;
        }
        
        public Object getContext() {
            return context;
        }
    }
    public static class StartEvent extends ApplicationEvent{}
    public static class StopEvent extends ApplicationEvent{}
    public static class DestroyEvent extends ApplicationEvent{}
}
