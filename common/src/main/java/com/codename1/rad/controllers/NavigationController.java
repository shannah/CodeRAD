package com.codename1.rad.controllers;

import com.codename1.ui.Component;
import com.codename1.util.promise.Promise;

public class NavigationController extends Controller {

    /**
     * Creates a controller with a given parent controller.
     *
     * @param parent The parent controller of this controller.
     */
    public NavigationController(Controller parent) {
        super(parent);
    }

    public static class NavigationEvent extends ControllerEvent {
        private boolean cancelled;

        public NavigationEvent(Component source) {
            super(source);
        }

        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }

        public boolean isCancelled() {
            return cancelled;
        }

    }



    public Promise<NavigationEvent> push() {
        return new Promise<NavigationEvent>((resolve, reject) -> {
            addEventListener(evt -> {
                if (evt instanceof NavigationEvent) {
                    evt.consume();
                    resolve.call((NavigationEvent)evt);
                    return;
                }
                if (evt instanceof FormController.FormBackEvent) {
                    evt.consume();
                    NavigationEvent nevt = new NavigationEvent(evt.getComponent());
                    nevt.setCancelled(true);
                    resolve.call(nevt);
                }
            });
        });

    }
}
