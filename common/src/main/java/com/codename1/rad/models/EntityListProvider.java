package com.codename1.rad.models;

import com.codename1.rad.controllers.ControllerEvent;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.ui.events.ActionListener;
import com.codename1.util.AsyncResource;

/**
 * An interface used to load an entity list asynchronously.
 */
public interface EntityListProvider extends ActionListener<ActionNode.ActionNodeEvent> {

    public static class UpdateProviderRequestEvent extends ControllerEvent {
        private Request request;
        private EntityListProvider provider;

        public UpdateProviderRequestEvent(Object source, EntityListProvider provider, Request request) {
            super(source);
            this.provider = provider;
            this.request = request;
        }

        public Request getRequest() {
            return request;
        }

        public EntityListProvider getProvider() {
            return provider;
        }
    }



    /**
     * The response of a call to {@link #getEntities(Request)}
     */
    public static class Request extends AsyncResource<EntityList> {
        private RequestType requestType = RequestType.REFRESH;
        private Request nextRequest;

        public Request(RequestType type) {
            this.requestType = type;
        }

        public Request() {
            this(RequestType.REFRESH);
        }


        public boolean hasMore() {
            return nextRequest != null;
        }

        public void setNextRequst(Request request) {
            this.nextRequest = request;
        }

        public Request getNextRequest() {
            return nextRequest;
        }



    }

    /**
     * Enum with values that can be used in a {@link Request} to specify to the provider
     * whether to "load more", or refresh the contents.
     */
    public static enum RequestType {
        REFRESH,
        LOAD_MORE;
    }

    /**
     * Gets an entity list asynchronously.
     * @param request A request.  This can be any type of entity.  As long as the provider and the consumer
     *                agree on the tags or properties that it can contain, and their meaning.
     * @return Async result.
     */
    public Request getEntities(Request request);
    public Request createRequest(RequestType type);


}
