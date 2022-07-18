package com.sirius.aath.backchannel.services;

import com.google.gson.Gson;
import com.sirius.aath.backchannel.exception.ThereIsNoInvitationException;
import com.sirius.aath.backchannel.model.*;
import com.sirius.aath.backchannel.model.ConnectionResponse;
import com.sirius.sdk.agent.aries_rfc.feature_0160_connection_protocol.messages.Invitation;
import com.sirius.sdk.agent.connections.Endpoint;
import com.sirius.sdk.hub.Context;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Service
public class InvitationService {
    private final ConnectionService connectionService;
    private Endpoint endpoint;

    private Invitation invitation;

    private String  connectionKey;
    private String invitation_data;

    private ArrayList<String> ConnectionKeysList;
    public ArrayList<String> getConnectionKeysList() {
        return ConnectionKeysList;
    }

    public void addToConnectionKeysList(String connectionKey) {
        this.ConnectionKeysList.add(connectionKey);
    }

    public InvitationService(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    public String getConnectionKey() {
        return this.connectionKey;
    }

    public void setConnectionKey(String connectionKey) {
        this.connectionKey = connectionKey;
    }
    public Invitation getInvitation() {
        return this.invitation;
    }

    public Endpoint getEndpoint() {
        return this.endpoint;
    }
    public ConnectionCreateInvitationRequest invitationRequest(String id){
        ConnectionCreateInvitationRequest request = new ConnectionCreateInvitationRequest();
        request.setData(new ConnectionCreateInvitationRequestData().mediatorConnectionId(id));
        return request;
    }
    public void setInvitation(Context context)  throws InterruptedException, ExecutionException, TimeoutException
    {
        setEndpoint(context);
        String connectionKey = context.getCrypto().createKey();
        invitation = Invitation.builder().
                setLabel("Inviter").
                setEndpoint(getEndpoint().getAddress()).
                setRecipientKeys(Collections.singletonList(connectionKey)).
                build();
       addToConnectionKeysList(connectionKey);
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
    public void setEndpoint(Context context)
    {
        List<Endpoint> endpointList = context.getEndpoints();
        endpointList.forEach((Endpoint e) ->
        {
            endpoint = e.getRoutingKeys().isEmpty() ? e : null;
        });

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

    public String getInvitation_data() {
        return this.invitation_data;
    }

    public void setInvitation_data(String invitation_data) {
        this.invitation_data = invitation_data;
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

    private ConcurrentHashMap<String, Invitation> invitationIdInvitationDic;

    public ConcurrentHashMap<ConnectionCreateInvitationRequest, Context> getContextDic() {
        return contextDic;
    }

    public void setContextDic(ConcurrentHashMap<ConnectionCreateInvitationRequest, Context> contextDic) {
        InvitationService.contextDic = contextDic;
    }

    private static ConcurrentHashMap<ConnectionCreateInvitationRequest, Context> contextDic = new ConcurrentHashMap<>();
    public void addToContextDic(String id, Context context)
    {
        ConcurrentHashMap<ConnectionCreateInvitationRequest, Context> map =
                new ConcurrentHashMap<>();
        map.put(invitationRequest(id), context);
        setContextDic(map);
    }
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
    public ConnectionAcceptInvitation200Response initConnectionAcceptResponse(ConnectionAcceptInvitationRequest request)
    {
        ConnectionAcceptInvitation200Response acceptInvitationResponse = new ConnectionAcceptInvitation200Response();
        String connectionId = "";
        ConnectionResponse response = connectionService.getLst().stream()
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
        ConnectionResponse response = connectionService.getLst().stream()
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
