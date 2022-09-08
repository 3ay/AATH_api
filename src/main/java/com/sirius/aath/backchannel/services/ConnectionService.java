package com.sirius.aath.backchannel.services;

import com.sirius.aath.backchannel.exception.ThereIsNoInvitationException;
import com.sirius.aath.backchannel.model.*;
import com.sirius.sdk.agent.connections.Endpoint;
import com.sirius.sdk.hub.Context;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Service
public class ConnectionService {
    private final InvitationService invitationService;
    public ConnectionService(InvitationService invitationService) {
        this.invitationService = invitationService;
    }
    public ArrayList<ConnectionResponse> getLst() {
        return lst;
    }

    public void setLst(ArrayList<ConnectionResponse> lst) {
        this.lst = lst;
    }

    private ArrayList<ConnectionResponse> lst = new ArrayList<ConnectionResponse>();

    public ArrayList<ConnectionResponse> listOfConnectionResponse() throws InterruptedException, ExecutionException, TimeoutException {
        ArrayList<ConnectionResponse> lst = new ArrayList<ConnectionResponse>();
        //создать два списка, один с ключами, другой с values внести последовательно в объект новго списка по сущностям
        ArrayList<InvitationMessage> valuesLst = new ArrayList<>(invitationService.getInvitationRequestMessageDic().values());
        ArrayList <ConnectionCreateInvitationRequest> keysLst = new ArrayList<>(invitationService.getInvitationRequestMessageDic().keySet());
        ConnectionResponse connectionResponse = new ConnectionResponse();
        for (int i = 0; i <keysLst.size(); i++) {
            lst.add(invitationService.initConnectionResponse(connectionResponse,valuesLst.get(i).getId(),
                    ConnectionState.INVITATION,valuesLst.get(i)));
            // lst.add(new ConnectionResponse(valuesLst.get(i).getInvitation().getId(),
            //         keysLst.get(i).getData().getConnection_id(),valuesLst.get(i).getInvitation()));
        }
        setLst(lst);
        return lst;
    }
    public ConnectionAcceptInvitation200Response initConnectionAcceptResponse(ConnectionAcceptInvitationRequest request)
    {
        ConnectionAcceptInvitation200Response acceptInvitationResponse = new ConnectionAcceptInvitation200Response();
        String connectionId = "";
        ConnectionResponse response = getLst().stream()
                .filter(el -> request.getId().equals(el.getConnectionId()))
                .findAny()
                .orElse(null);
        if (response != null)
            connectionId = response.getConnectionId();
        else
        {
            throw new ThereIsNoInvitationException("There is no invitation has this id");
        }
        acceptInvitationResponse.setConnectionId(connectionId);
        return acceptInvitationResponse;
    }
    public ConnectionAcceptRequest200Response initConnectionAcceptRequest(ConnectionAcceptInvitationRequest request)
    {
        ConnectionAcceptRequest200Response acceptInvitationRequest = new ConnectionAcceptRequest200Response();
        String connectionId = "";
        ConnectionResponse response = getLst().stream()
                .filter(el -> request.getId().equals(el.getConnectionId()))
                .findAny()
                .orElse(null);
        if (response != null)
            connectionId = response.getConnectionId();
        else
        {
            throw new ThereIsNoInvitationException("There is no invitation has this id");
        }
        acceptInvitationRequest.setConnectionId(connectionId);
        return acceptInvitationRequest;
    }
}
