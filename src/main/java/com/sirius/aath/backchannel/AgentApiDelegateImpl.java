package com.sirius.aath.backchannel;

import com.sirius.aath.backchannel.api.AgentApiDelegate;
import com.sirius.aath.backchannel.model.ConnectionAcceptInvitation200Response;
import com.sirius.aath.backchannel.model.ConnectionAcceptInvitationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AgentApiDelegateImpl implements AgentApiDelegate {

    @Override
    public ResponseEntity<ConnectionAcceptInvitation200Response> connectionAcceptInvitation(
            ConnectionAcceptInvitationRequest connectionAcceptInvitationRequest) {
        //TODO implement logic here
        return AgentApiDelegate.super.connectionAcceptInvitation(connectionAcceptInvitationRequest);
    }
}
