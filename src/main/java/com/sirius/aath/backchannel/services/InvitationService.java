package com.sirius.aath.backchannel.services;

import com.google.gson.Gson;
import com.sirius.aath.backchannel.exception.ThereIsNoInvitationException;
import com.sirius.aath.backchannel.model.*;
import com.sirius.aath.backchannel.model.ConnectionResponse;
import com.sirius.sdk.agent.aries_rfc.feature_0160_connection_protocol.messages.Invitation;
import com.sirius.sdk.hub.Context;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Service
public class InvitationService {
    public ConcurrentHashMap<String, Invitation> getConnectionKeyInvitationDic() {
        return connectionKeyInvitationDic;
    }

    private final ConcurrentHashMap<String, Invitation> connectionKeyInvitationDic = new ConcurrentHashMap<>();
    public ConnectionCreateInvitationRequest invitationRequest(String id){
        ConnectionCreateInvitationRequest request = new ConnectionCreateInvitationRequest();
        request.setData(new ConnectionCreateInvitationRequestData().mediatorConnectionId(id));
        return request;
    }
    public void addToConnectionKeyInvitationDic(Context context)  throws InterruptedException, ExecutionException, TimeoutException
    {
        String connectionKey = context.getCrypto().createKey();
        Invitation invitation = Invitation.builder().
                setLabel("Inviter").
                setEndpoint(context.getEndpointWithEmptyRoutingKeys().getAddress()).
                setRecipientKeys(Collections.singletonList(connectionKey)).
                build();
        connectionKeyInvitationDic.put(connectionKey, invitation);
       //add invitation to dic
    }
    public boolean findRequest(ConnectionAcceptInvitationRequest request)
    {
        ArrayList<InvitationMessage> values = new ArrayList<InvitationMessage>(getInvitationRequestMessageDic().values());
        InvitationMessage response = values.stream()
                .filter(el -> request.getId().equals(el.getId()))
                .findAny()
                .orElse(null);
        return response == null;
    }

    public ConnectionCreateInvitationRequest initCreateInvitationRequest(String id)
    {
        ConnectionCreateInvitationRequest request= new ConnectionCreateInvitationRequest();
        ConnectionCreateInvitationRequestData data = new ConnectionCreateInvitationRequestData();
        data.setMediatorConnectionId(id);
        request.setData(data);
        return request;
    }
    public ConnectionReceiveInvitation200Response initConnectionReceiveInvitationResponse(String id, ConnectionState state)
    {
        ConnectionReceiveInvitation200Response response = new ConnectionReceiveInvitation200Response();
        response.setConnectionId(id);
        response.setState(state);
        return response;
    }
    public ConnectionResponse initConnectionResponse(ConnectionResponse connectionResponse, String connection_id,ConnectionState state, InvitationMessage invitationMessage )
    {
        connectionResponse.setConnectionId(connection_id);
        connectionResponse.setState(state);
        connectionResponse.setConnection(invitationMessage);
        return connectionResponse;
    }
    public ConnectionCreateInvitation200Response invitation200Response(String id, InvitationMessage message)
    {
        ConnectionCreateInvitation200Response response = new ConnectionCreateInvitation200Response();
        response.setConnectionId(id);
        response.setInvitation(message);
        return response;
    }


    public static InvitationMessage InvitationMessageToModel(Invitation invitation) {
        String json = invitation.getMessageObj().toString();
        Gson gson = new Gson();
        return gson.fromJson(json, InvitationMessage.class);
    }

    public ConcurrentHashMap<ConnectionCreateInvitationRequest, InvitationMessage> getInvitationRequestMessageDic() {
        return invitationRequestMessageDic;
    }

    public void setInvitationRequestMessageDic(ConcurrentHashMap<ConnectionCreateInvitationRequest, InvitationMessage> invitationDic) {
        InvitationService.invitationRequestMessageDic = invitationDic;
    }

    private static ConcurrentHashMap<ConnectionCreateInvitationRequest, InvitationMessage> invitationRequestMessageDic = new ConcurrentHashMap();

    public ConcurrentHashMap<String, Invitation> getInvitationIdInvitationDic() {
        return invitationIdInvitationDic;
    }

    public void setInvitationIdInvitationDic(String id, Invitation invitation) {
        this.invitationIdInvitationDic.put(id, invitation);
    }

    private ConcurrentHashMap<String, Invitation> invitationIdInvitationDic = new ConcurrentHashMap<>();

    public ConnectionReceiveInvitation200Response getReceiveInvitationResponse(ConnectionReceiveInvitationRequest receiveInvitationRequest)
    {
        String connectionId = "";
        ArrayList<InvitationMessage> values = new ArrayList<InvitationMessage>(getInvitationRequestMessageDic().values());
        InvitationMessage response = values.stream()
                .filter(el -> receiveInvitationRequest.getData().getMediatorConnectionId().equals(el.getId()))
                .findAny()
                .orElse(null);
        if (response != null) {
            connectionId = response.getId();
            getInvitationRequestMessageDic().put(initCreateInvitationRequest(connectionId),
                    new InvitationMessage(
                    response.getType(),
                    response.getId(),
                    response.getServiceEndpoint(),
                    response.getRecipientKeys(),
                    response.getLabel()));
        }
        else
        {
           throw new ThereIsNoInvitationException("There is no invitation has this id");
        }
        return initConnectionReceiveInvitationResponse(connectionId, ConnectionState.INVITATION);
    }


    public void addToInvitationRequestMessageDic(String id, Invitation invitation)
    {
        ConcurrentHashMap<ConnectionCreateInvitationRequest, InvitationMessage> map =
                new ConcurrentHashMap<>();
        InvitationMessage object = InvitationService.InvitationMessageToModel(invitation);
        map.put(invitationRequest(id),new InvitationMessage(
                object.type,
                object.id,
                object.serviceEndpoint,
                object.recipientKeys,
                object.label));
        setInvitationRequestMessageDic(map);
        setInvitationIdInvitationDic(id, invitation);
    }



}
