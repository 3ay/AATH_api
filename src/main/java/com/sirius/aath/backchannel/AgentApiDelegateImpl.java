package com.sirius.aath.backchannel;

import com.sirius.aath.backchannel.api.AgentApiDelegate;
import com.sirius.aath.backchannel.exception.ThereIsNoInvitationException;
import com.sirius.aath.backchannel.model.*;
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

    private final ApiService apiService;
    @Autowired
    public AgentApiDelegateImpl(ContextHolder contextHolder, ApplicationEventPublisher publisher, ApiService apiService) {
        this.contextHolder = contextHolder;
        this.publisher = publisher;
        this.apiService = apiService;
    }
    @Override
    public ResponseEntity<ConnectionResponse> connectionGetById(String connectionId) {
        ConnectionResponse response = apiService.getLst().stream()
                .filter(el -> connectionId.equals(el.getConnectionId()))
                .findAny()
                .orElse(null);
        if (response == null)
            throw new ThereIsNoInvitationException("There is no invitation has this id");
        return ResponseEntity.ok(response);
    }
    @Override
    public ResponseEntity<List<ConnectionResponse>> connectionGetAll() {
        return ResponseEntity.ok(apiService.getLst());
    }
    @Override
    public ResponseEntity<ConnectionCreateInvitation200Response> createInvitation(ConnectionCreateInvitationRequest request) throws ExecutionException, InterruptedException, TimeoutException {
        apiService.setInvitation(contextHolder.getContext());
        apiService.addToContextDic(request.getData().getMediatorConnectionId(), contextHolder.getContext());
        apiService.addToInvitationDic(request.getData().getMediatorConnectionId(), apiService.getInvitation());
        return ResponseEntity.ok(apiService.invitation200Response(request.getData().getMediatorConnectionId(),apiService.getInvitationDic().get(request)));
    }
    @Override
    public ResponseEntity<ConnectionReceiveInvitation200Response> receiveInvitation(ConnectionReceiveInvitationRequest request)
    {
        return ResponseEntity.ok(apiService.getReceiveInvitationResponse(request));
    }
    @Override
    public ResponseEntity<ConnectionAcceptInvitation200Response> acceptInvitation(ConnectionAcceptInvitationRequest request)
    {
        if (apiService.findRequest(request))
            throw new ThereIsNoInvitationException("There is no invitation has this id");
        else
        {
            Pair<String,String> didVerkey = contextHolder.getContext().getDid().createAndStoreMyDid();
            Pairwise.Me inviterMe = new Pairwise.Me(didVerkey.first, didVerkey.second);
            String expectedKey = apiService.getConnectionKey();
            // use inviter listener(expectedKey, inviterMe)

        }
        return ResponseEntity.ok(apiService.initConnectionAcceptResponse(request));//in second parameter may be ConnectionState.INVITATION
    }

    public ResponseEntity<ConnectionAcceptRequest200Response> acceptRequest(ConnectionAcceptInvitationRequest request)
    {
        return AgentApiDelegate.super.connectionAcceptRequest(request);
    }
    public ResponseEntity<ConnectionSendPingRequest> sendPing()
    {
        ConnectionSendPingRequest rq = new ConnectionSendPingRequest();
        return ResponseEntity.ok(rq);
    }


}
