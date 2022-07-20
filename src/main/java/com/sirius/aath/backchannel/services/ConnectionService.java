package com.sirius.aath.backchannel.services;

import com.sirius.aath.backchannel.model.ConnectionCreateInvitationRequest;
import com.sirius.aath.backchannel.model.ConnectionResponse;
import com.sirius.aath.backchannel.model.ConnectionState;
import com.sirius.aath.backchannel.model.InvitationMessage;
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
    private Endpoint endpoint;
    private boolean connectionKeyIsExist;

    public boolean isConnectionKeyIsExist() {
        return connectionKeyIsExist;
    }

    public void setConnectionKeyIsExist(boolean connectionKeyIsExist) {
        this.connectionKeyIsExist = connectionKeyIsExist;
    }



    public void setEndpoint(Context context) {
        List<Endpoint> endpointList = context.getEndpoints();
        endpointList.forEach((Endpoint e) ->
        {
            endpoint = e.getRoutingKeys().isEmpty() ? e : null;
        });
    }

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
}
