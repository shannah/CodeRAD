package com.codename1.rad.models;

import com.codename1.rad.controllers.ActionSupport;
import com.codename1.rad.nodes.ActionNode;

public abstract class AbstractEntityListProvider implements EntityListProvider {





    @Override
    public void actionPerformed(ActionNode.ActionNodeEvent evt) {
        if (evt.isConsumed()) {
            return;
        }
        RequestType requestType = evt.getContext().lookupExtra(RequestType.class);
        Request request = evt.getContext().lookupExtra(Request.class);

        if (request == null) {
            if (requestType == null) {
                return;
            }
            request = createRequest(requestType);
        }

        ActionSupport.dispatchEvent(new UpdateProviderRequestEvent(evt.getSource(), this, request));


        if (request != null) {
            getEntities(request);
            if (!request.isCancelled()) {
                evt.consume();
                evt.setAsyncResource(request);
            }
        }
    }
}
