package com.hyperledger.AATH.Backchannel.API.api.connection;

import com.google.gson.Gson;
import com.hyperledger.AATH.Backchannel.API.Exception.ThereIsNoInvitationException;
import com.hyperledger.AATH.Backchannel.API.model.*;
import com.hyperledger.AATH.Backchannel.API.sirius.runner.Connection_160;
import com.hyperledger.AATH.Backchannel.API.sirius.runner.Main;
import com.sirius.sdk.agent.aries_rfc.feature_0160_connection_protocol.messages.Invitation;
import com.sirius.sdk.agent.listener.Listener;
import com.sirius.sdk.agent.pairwise.Pairwise;
import com.sirius.sdk.encryption.P2PConnection;
import com.sirius.sdk.hub.CloudContext;
import com.sirius.sdk.hub.Context;
import com.sirius.sdk.utils.Pair;
import org.json.JSONObject;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.hyperledger.AATH.Backchannel.API.sirius.runner.Connection_160.setEndpoint;

@Service
public class ApiService {

    public static InvitationMessage InvitationMessageToModel(Invitation invitation) {
        String json = invitation.getMessageObj().toString();
        Gson gson = new Gson();
        return gson.fromJson(json, InvitationMessage.class);
    }


    public static String getInvitation_data() {
        return invitation_data;
    }

    public static void setInvitation_data(String invitation_data) {
        ApiService.invitation_data = invitation_data;
    }

    public static HashMap<InvitationRequest, InvitationResponse> getInvitationDic() {
        return invitationDic;
    }

    public static void setInvitationDic(HashMap<InvitationRequest, InvitationResponse> invitationDic) {
        ApiService.invitationDic = invitationDic;
    }

    private static HashMap<InvitationRequest, InvitationResponse> invitationDic = new HashMap();

    public static HashMap<InvitationRequest, Context> getContextDic() {
        return contextDic;
    }

    public static Listener getListener() {
        return listener;
    }

    public static void setListener(Context context) {
        setEndpoint(context);
        ApiService.listener = context.subscribe();
    }

    private static  Listener listener;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        ApiService.context = context;
    }

    private static Context context;
    public static void buidContext(String credentials, String fromVerkey, String fromSigKey, String thierKey, String serverUrl)  throws InterruptedException, ExecutionException, TimeoutException
    {
        try(
        Context context = CloudContext.builder().setServerUri(serverUrl)
                .setCredentials(credentials.getBytes(StandardCharsets.UTF_8))
                .setP2p(new P2PConnection(fromVerkey, fromSigKey, thierKey))
                .build()) {
            //Connection_160.setConnectionKey(context.getCrypto().createKey());
            Connection_160.setInvitation(context);
            setContext(context);
        }
    }
    public static void setContextDic(HashMap<InvitationRequest, Context> contextDic) {
        ApiService.contextDic = contextDic;
    }

    private static HashMap<InvitationRequest, Context> contextDic = new HashMap<>();
    public static void addToContextDic(String agentName, Context context)
    {
        HashMap<InvitationRequest, Context> map =
                new HashMap<>();
        map.put(new InvitationRequest(agentName), context);
        ApiService.setContextDic(map);
    }
    public static ReceiveInvitationResponse getReceiveInvitationResponse(ReceiveInvitationRequest receiveInvitationRequest)
    {
        String connectionId = "";
        ArrayList<InvitationResponse> values = new ArrayList<InvitationResponse>(getInvitationDic().values());
        InvitationResponse response = values.stream()
                .filter(el -> receiveInvitationRequest.getData().id.equals(el.getInvitation().id))
                .findAny()
                .orElse(null);
        if (response != null) {
            connectionId = response.getInvitation().getId();
            getInvitationDic().put(new InvitationRequest(connectionId), new InvitationResponse(
                    response.getInvitation().getType(),
                    response.getInvitation().getId(),
                    response.getInvitation().getServiceEndpoint(),
                    response.getInvitation().getRecipientKeys(),
                    response.getInvitation().getLabel(),
                    response.getAgent_name()));
        }
        else
        {
           throw new ThereIsNoInvitationException("There is no invitation has this id");
        }
        return new ReceiveInvitationResponse(connectionId, "invitation");
    }
    public static Accept getAccept(AcceptRequest request, String state)
    {
        String connectionId = "";
        ConnectionResponse response = ConnectionResponse.getLst().stream()
                .filter(el -> request.getConnection_id().equals(el.getConnectionId()))
                .findAny()
                .orElse(null);
        if (response != null)
            connectionId = response.getConnectionId();
        return new Accept(connectionId,state);
    }

    public static void addToInvitationDic(String agentName, Invitation invitation)
    {
        HashMap<InvitationRequest, InvitationResponse> map =
                new HashMap<>();
        InvitationMessage object = ApiService.InvitationMessageToModel(invitation);
        map.put(new InvitationRequest(agentName),new InvitationResponse(
                object.type,
                object.id,
                object.serviceEndpoint,
                object.recipientKeys,
                object.label,
                agentName));
        ApiService.setInvitationDic(map);
    }
    private static String invitation_data;

    public ArrayList<ConnectionResponse> list() throws InterruptedException, ExecutionException, TimeoutException {
        ArrayList<ConnectionResponse> lst = new ArrayList<ConnectionResponse>();
        //создать два списка, один с ключами, другой с values внести последовательно в объект новго списка по сущностям
        ArrayList<InvitationResponse> valuesLst = new ArrayList<>(ApiService.getInvitationDic().values());
        ArrayList <InvitationRequest> keysLst = new ArrayList<>(ApiService.getInvitationDic().keySet());

        for (int i = 0; i <keysLst.size(); i++) {
            lst.add(new ConnectionResponse(valuesLst.get(i).getInvitation().getId(),
                    keysLst.get(i).getData().getAgentName(),valuesLst.get(i).getInvitation()));
        }

        ConnectionResponse.setLst(lst);
        return lst;
    }
}
