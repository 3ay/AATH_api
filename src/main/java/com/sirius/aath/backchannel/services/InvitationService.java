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
    static Endpoint endpoint;

    static Invitation invitation;

    static String  connectionKey;

    public InvitationService(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    public String getConnectionKey() {
        return connectionKey;
    }

    public void setConnectionKey(String connectionKey) {
        InvitationService.connectionKey = connectionKey;
    }
    public Invitation getInvitation() {
        return invitation;
    }

    public Endpoint getEndpoint() {
        return endpoint;
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
        setConnectionKey(connectionKey);
    }
    public boolean findRequest(ConnectionAcceptInvitationRequest request)
    {
        ArrayList<InvitationMessage> values = new ArrayList<InvitationMessage>(getInvitationDic().values());
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

    public static String getInvitation_data() {
        return invitation_data;
    }

    public static void setInvitation_data(String invitation_data) {
        InvitationService.invitation_data = invitation_data;
    }

    public ConcurrentHashMap<ConnectionCreateInvitationRequest, InvitationMessage> getInvitationDic() {
        return invitationDic;
    }

    public void setInvitationDic(ConcurrentHashMap<ConnectionCreateInvitationRequest, InvitationMessage> invitationDic) {
        InvitationService.invitationDic = invitationDic;
    }

    private static ConcurrentHashMap<ConnectionCreateInvitationRequest, InvitationMessage> invitationDic = new ConcurrentHashMap();

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
        ArrayList<InvitationMessage> values = new ArrayList<InvitationMessage>(getInvitationDic().values());
        InvitationMessage response = values.stream()
                .filter(el -> receiveInvitationRequest.getData().getMediatorConnectionId().equals(el.getId()))
                .findAny()
                .orElse(null);
        if (response != null) {
            connectionId = response.getId();
            getInvitationDic().put(initCreateInvitationRequest(connectionId),
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

    public void addToInvitationDic(String id, Invitation invitation)
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
        setInvitationDic(map);
    }
    private static String invitation_data;


}
