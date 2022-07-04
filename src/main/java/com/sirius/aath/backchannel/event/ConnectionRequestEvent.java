package com.sirius.aath.backchannel.event;

import com.sirius.sdk.agent.aries_rfc.feature_0160_connection_protocol.messages.ConnRequest;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

public record ConnectionRequestEvent(ConnRequest connRequest, String metadata) {

    @Component
    public static class Handler {

        @EventListener(ConnectionRequestEvent.class)
        public void handle(ConnectionRequestEvent event) {
            //TODO handle event
        }

    }

}
