package com.sirius.aath.backchannel.event;

import com.sirius.aath.backchannel.service.MessageService;
import com.sirius.sdk.agent.aries_rfc.feature_0015_acks.Ack;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AckEventHandler {

    private final MessageService messageService;

    public AckEventHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @EventListener(Ack.class)
    public void handle(Ack event) {
        messageService.getAndRemove(event.getAckMessageId()).complete(event);
    }
}
