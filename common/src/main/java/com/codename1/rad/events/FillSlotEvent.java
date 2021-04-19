package com.codename1.rad.events;

import com.codename1.rad.controllers.ControllerEvent;
import com.codename1.rad.models.Tag;
import com.codename1.rad.ui.EntityView;
import com.codename1.rad.ui.Slot;
import com.codename1.ui.events.ActionListener;

/**
 * An event that is thrown when a Slot is requesting to be filled.
 *
 * @see com.codename1.rad.controllers.Controller#fillSlot(Tag, ActionListener) To handle these events.
 */
public class FillSlotEvent extends ControllerEvent {

    public FillSlotEvent(Slot source) {
        super(source);

    }

    /**
     * Gets the slot that is requesting to be filled by this event.
     * @return
     */
    public Slot getSlot() {
        return (Slot) getSource();
    }

    


}
