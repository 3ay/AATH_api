package com.sirius.aath.backchannel;

import com.sirius.aath.backchannel.api.AgentApiDelegate;
import com.sirius.aath.backchannel.exception.ThereIsNoInvitationException;
import com.sirius.aath.backchannel.model.*;
import com.sirius.aath.backchannel.services.ConnectionService;
import com.sirius.aath.backchannel.services.InvitationService;
import com.sirius.sdk.agent.pairwise.Pairwise;
import com.sirius.sdk.utils.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Service
public class AgentApiDelegateImpl implements AgentApiDelegate {

    private final ContextHolder contextHolder;

    private final ApplicationEventPublisher publisher;

    private final InvitationService invitationService;

    private final ConnectionService connectionService;

    @Autowired
    public AgentApiDelegateImpl(ContextHolder contextHolder, ApplicationEventPublisher publisher, InvitationService invitationService, ConnectionService connectionService) {
        this.contextHolder = contextHolder;
        this.publisher = publisher;
        this.invitationService = invitationService;
        this.connectionService = connectionService;
    }

    @Override
    public ResponseEntity<ConnectionResponse> connectionGetById(String connectionId) {

        try {
            return connectionService.listOfConnectionResponse().stream()
                    .filter(el -> connectionId.equals(el.getConnectionId()))
                    .findAny()
                    .map(ResponseEntity::ok)
                    .orElseThrow(() -> new ThereIsNoInvitationException("There is no invitation has this id"));
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<List<ConnectionResponse>> connectionGetAll() {
        try {
            return ResponseEntity.ok(connectionService.listOfConnectionResponse());
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public ResponseEntity<ConnectionCreateInvitation200Response> connectionCreateInvitation(ConnectionCreateInvitationRequest request){
        try {
            invitationService.setInvitation(contextHolder.getContext());
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
        invitationService.addToContextDic(request.getData().getMediatorConnectionId(), contextHolder.getContext());
        invitationService.addToInvitationDic(request.getData().getMediatorConnectionId(), invitationService.getInvitation());
        return ResponseEntity.ok(invitationService.invitation200Response(request.getData().getMediatorConnectionId(), invitationService.getInvitationDic().get(request)));
    }

    @Override
    public ResponseEntity<ConnectionReceiveInvitation200Response> connectionReceiveInvitation(ConnectionReceiveInvitationRequest request) {
        return ResponseEntity.ok(invitationService.getReceiveInvitationResponse(request));
    }

    @Override
    public ResponseEntity<ConnectionAcceptInvitation200Response> connectionAcceptInvitation(ConnectionAcceptInvitationRequest request) {
        if (invitationService.findRequest(request))
            throw new ThereIsNoInvitationException("There is no invitation has this id");
        else {
            Pair<String, String> didVerkey = contextHolder.getContext().getDid().createAndStoreMyDid();
            Pairwise.Me inviterMe = new Pairwise.Me(didVerkey.first, didVerkey.second);
            String expectedKey = invitationService.getConnectionKey();
            // use inviter listener(expectedKey, inviterMe)

        }
        return ResponseEntity.ok(invitationService.initConnectionAcceptResponse(request));//in second parameter may be ConnectionState.INVITATION
    }

    public ResponseEntity<ConnectionAcceptRequest200Response> acceptRequest(ConnectionAcceptInvitationRequest request) {
        return AgentApiDelegate.super.connectionAcceptRequest(request);
    }

    public ResponseEntity<ConnectionSendPingRequest> sendPing() {
        ConnectionSendPingRequest rq = new ConnectionSendPingRequest();
        return ResponseEntity.ok(rq);
    }


}
