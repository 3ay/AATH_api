package com.sirius.aath.backchannel;

import com.sirius.aath.backchannel.api.AgentApiDelegate;
import com.sirius.aath.backchannel.model.ConnectionAcceptInvitation200Response;
import com.sirius.aath.backchannel.model.ConnectionAcceptInvitationRequest;
import com.sirius.aath.backchannel.service.MessageService;
import com.sirius.sdk.agent.aries_rfc.feature_0015_acks.Ack;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class AgentApiDelegateImpl implements AgentApiDelegate {


    private final MessageService messageService;

    public AgentApiDelegateImpl(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public ResponseEntity<ConnectionAcceptInvitation200Response> connectionAcceptInvitation(
            ConnectionAcceptInvitationRequest connectionAcceptInvitationRequest) {
        //TODO implement logic here
        return AgentApiDelegate.super.connectionAcceptInvitation(connectionAcceptInvitationRequest);
    }

    public ResponseEntity<MessageAck> sendMessage(MessageRequest messageRequest) {
        Future<Ack> future =
                messageService.sendMessage(messageRequest.content, messageRequest.theirDid, messageRequest.theirKey);
        Ack ack;
        try {
            ack = future.get(30, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Could not get ACK", e);
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(new MessageAck(ack.getId()));
    }

    public record MessageRequest(String content, String theirDid, String theirKey) {

    }


    public record MessageAck(String id) {

    }

}
