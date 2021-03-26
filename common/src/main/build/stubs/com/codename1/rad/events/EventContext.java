/**
 *  This package contains utility classes for CodeRAD events.
 */
package com.codename1.rad.events;


/**
 *  Encapsulates a context in which an {@link ActionNode}'s event is fired.  The context
 *  includes the Component that the event originated from, the {@link Entity} that the 
 *  event relates to (usually the value obtained from {@link com.codename1.rad.ui.EntityView#getEntity() } of the 
 *  view in which the event is fired), and the {@link ActionNode} that the event pertains to.
 *  
 *  
 *  @author shannah
 */
public class EventContext {

	public EventContext(com.codename1.rad.models.Entity entity, Component source, com.codename1.rad.nodes.ActionNode action) {
	}

	public EventContext() {
	}

	public EventContext copyWithNewAction(com.codename1.rad.nodes.ActionNode action) {
	}

	/**
	 *  @return the entity
	 */
	public com.codename1.rad.models.Entity getEntity() {
	}

	/**
	 *  @param entity the entity to set
	 */
	public void setEntity(com.codename1.rad.models.Entity entity) {
	}

	/**
	 *  @return the eventSource
	 */
	public Component getEventSource() {
	}

	/**
	 *  @param eventSource the eventSource to set
	 */
	public void setEventSource(Component eventSource) {
	}

	/**
	 *  @return the action
	 */
	public com.codename1.rad.nodes.ActionNode getAction() {
	}

	/**
	 *  @param action the action to set
	 */
	public void setAction(com.codename1.rad.nodes.ActionNode action) {
	}

	public void putExtra(Object key, Object val) {
	}

	public Object getExtra(Object key) {
	}

	public Iterable getExtraDataKeys() {
	}

	public boolean hasExtraData() {
	}
}
