package com.codename1.rad.models;

import com.codename1.util.AsyncResource;

/**
 * An interface used to load an entity list asynchronously.
 */
public interface EntityListProvider {

    public static interface IEntityListRequestData {

    }

    public static class RequestData extends Entity implements IEntityListRequestData {

    }

    /**
     * The response of a call to {@link #getEntities(Request)}
     */
    public static class Request extends AsyncResource<EntityList> {
        private RequestType requestType = RequestType.REFRESH;
        private Entity requestData;
        private Request nextRequest;

        public Request(RequestType type) {
            this(type, new RequestData());
        }

        public <T extends Entity & IEntityListRequestData> Request(RequestType type, T requestData) {
            this.requestType = type;
            this.requestData = null;
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


}
