package com.codename1.rad.ui;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.controllers.ActionSupport;
import com.codename1.rad.controllers.Controller;
import com.codename1.rad.events.FillSlotEvent;
import com.codename1.rad.models.Tag;
import com.codename1.rad.nodes.Node;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;

import static com.codename1.ui.ComponentSelector.$;
import com.codename1.rad.models.Entity;

/**
 * A placeholder that can be inserted into a View, that will be filled
 * at activation time by a Slot.
 */
public class Slot extends Container implements Activatable {

    /**
     * An ID for the slot which can be referenced by {@link Controller#fillSlot(Tag, ActionListener)} to fill the slot.
     */
    private final Tag id;

    /**
     * Flag to track whether the slot is activated yet or not.  Activation only occurs once, so this sort of acts
     * as a spinlock for the {@link #activate()} method.
     */
    private boolean activated;

    /**
     * The slot context.
     */
    private SlotContext context;


    /**
     * Creates a new slot with the given ID.  This slot will be filled by the controller when it (or a parent) is
     * set as the view for a controller.
     * @param id The ID for the slot.  This can be referenced by {@link Controller#fillSlot(Tag, ActionListener)} to fill
     *           the slot with custom content when activated.
     * @param context The slot context, which includes things like the Entity and Node so we know what the slot is a view
     *                for.
     */
    public Slot(Tag id, SlotContext context) {
        super(new BorderLayout());
        this.context = context;
        this.id = id;
        $(this).selectAllStyles().setPadding(0).setMargin(0).setBgTransparency(0);
    }

    public Slot(Tag id, @Inject Entity entity, @Inject Node node) {
        this(id, new SlotContext(entity, node));
    }

    public Slot(Tag id, @Inject EntityView ev) {
        this(id, new SlotContext(ev));
    }

    /**
     * Activates the slot.  This will be triggered when the Slot is added to a a Controller hierarchy.  It will
     * trigger a {@link FillSlotEvent} which can be handled by {@link Controller#fillSlot(Tag, ActionListener)} to
     * fill the slot contents.
     */
    @Override
    public void activate() {
        if (activated) return;
        activated = true;
        fill();

    }

    /**
     * Fires a {@link FillSlotEvent} to give the controller hierarchy an opportunity
     * to fill this slot.  The Controller can implement {@link Controller#fillSlot} to
     * receive and respond to such notifications.
     */
    void fill() {

        FillSlotEvent fse = new FillSlotEvent(this);
        ActionSupport.dispatchEvent(fse);
    }

    /**
     * Sets the content of this slot.
     * @param content The content to add to this slot.
     */
    public void setContent(Component content) {
        removeAll();
        add(BorderLayout.CENTER, content);
    }

    /**
     * Gets the slot ID.
     * @return
     */
    public Tag getId() {
        return id;
    }

    @Override
    protected void initComponent() {

        super.initComponent();

        // In case it hasn't been activated yet for some reason, we'll activate it now.
        activate();
    }

    /**
     * Gets the slot context which can be used to determine the Entity and Node that can be used
     * to build views in this slot.
     * @return
     */
    public SlotContext getContext() {
        return context;
    }

    /**
     * Encapsulates a context for a {@link Slot}.  This includes all of the information to know how to build views
     * inside this slot.
     */
    public static class SlotContext {
        private Entity entity;
        private Node node;

        /**
         * Creates a new SlotContext with given entity and node.
         * @param entity The model.
         * @param node The node.
         */
        public SlotContext(Entity entity, Node node) {
            this.entity = entity;
            this.node = node;
        }

        /**
         * Creates a new slot context.  This version passes an {@link EntityView} which is used to
         * extract a context (Entity and Node).
         * @param ev The EntityView which is used as the context for the slot.
         */
        public SlotContext(EntityView ev) {
            this(ev.getEntity(), ev.getViewNode());
        }

        /**
         * Gets the Entity model for the slot.
         * @return
         */
        public Entity getEntity() {
            return entity;
        }

        /**
         * Sets the Entity model for the slot.
         * @param entity
         */
        public void setEntity(Entity entity) {
            this.entity = entity;
        }

        /**
         * Gets the node for the slot.
         * @return
         */
        public Node getNode() {
            return node;
        }

        /**
         * Sets the node for the slot.
         * @param node
         */
        public void setNode(Node node) {
            this.node = node;
        }
    }
}
