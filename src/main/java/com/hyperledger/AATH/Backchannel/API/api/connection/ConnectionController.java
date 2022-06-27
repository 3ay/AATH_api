package com.hyperledger.AATH.Backchannel.API.api.connection;

import Config.ConfigProperties;
import com.hyperledger.AATH.Backchannel.API.Exception.ThereIsNoInvitationException;
import com.hyperledger.AATH.Backchannel.API.model.*;
import com.hyperledger.AATH.Backchannel.API.sirius.runner.Connection_160;
import com.sirius.sdk.hub.Context;
import org.aeonbits.owner.ConfigFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/agent/command/connection")
public class ConnectionController {
    @Autowired
    private ConfigProperties props;

    private final ApiService apiService;
    public ConnectionController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping
    public List<ConnectionResponse>getAllConnections() throws InterruptedException, ExecutionException, TimeoutException {
        return apiService.list();
    }

    @GetMapping(path = "/{connectionId}")
    public ConnectionResponse getConnectionById(@PathVariable("connectionId") String connectionId) throws InterruptedException, ExecutionException, TimeoutException {

        ConnectionResponse response = ConnectionResponse.getLst().stream()
                .filter(el -> connectionId.equals(el.getConnectionId()))
                .findAny()
                .orElse(null);
        return response;
    }
    @RequestMapping(value = "/create-invitation", method = RequestMethod.POST)
    public InvitationResponse postCreateInvitation(@RequestBody InvitationRequest invitationRequest) throws InterruptedException, ExecutionException, TimeoutException {

        ApiService.buidContext(props.getCredentials(), props.getFromVerkey(), props.getFromSigKey(), props.getThierKey(), props.getServerUrl());
        ApiService.addToContextDic(invitationRequest.getData().agentName, ApiService.getContext());
        ApiService.setListener(ApiService.getContext());
        ApiService.addToInvitationDic(invitationRequest.getData().agentName, Connection_160.getInvitation());
        return ApiService.getInvitationDic().get(invitationRequest);
    }
    @RequestMapping(value = "/receive-invitation", method = RequestMethod.POST)
        public ReceiveInvitationResponse postReceiveInvitation(@RequestBody ReceiveInvitationRequest receiveInvitationRequest)
    {
        return ApiService.getReceiveInvitationResponse(receiveInvitationRequest);
    }
    @RequestMapping(value ="/accept-invitation", method = RequestMethod.POST)
    public Accept postAcceptInvitation(@RequestBody AcceptRequest acceptRequest)
    {

        String connectionId = "";
        ArrayList<InvitationResponse> values = new ArrayList<InvitationResponse>(ApiService.getInvitationDic().values());
        InvitationResponse response = values.stream()
                .filter(el -> acceptRequest.getConnection_id().equals(el.getInvitation().id))
                .findAny()
                .orElse(null);
        if (response == null)
            throw new ThereIsNoInvitationException("There is no invitation has this id");
        else
        {
            Context context = ApiService.getContextDic().get(new InvitationRequest(response.getAgent_name()));
            Connection_160.inviteeRoutine(context);
        }
        return ApiService.getAccept(acceptRequest,"request");
    }
    @RequestMapping(value ="/accept-request", method = RequestMethod.POST)
    public Accept postAcceptRequset(@RequestBody AcceptRequest acceptRequest) throws InterruptedException, ExecutionException, TimeoutException {
        String connectionId = "";
        ArrayList<InvitationResponse> values = new ArrayList<InvitationResponse>(ApiService.getInvitationDic().values());
        InvitationResponse response = values.stream()
                .filter(el -> acceptRequest.getConnection_id().equals(el.getInvitation().id))
                .findAny()
                .orElse(null);
        if (response == null)
            throw new ThereIsNoInvitationException("There is no invitation has this id");
        else
        {
            Context context = ApiService.getContextDic().get(new InvitationRequest(response.getAgent_name()));
            Connection_160.inviterRoutine(context);
        }

        return ApiService.getAccept(acceptRequest,"response");
    }
    @RequestMapping(value="/send-ping", method = RequestMethod.POST)
    public Accept postSendPing(@RequestBody AcceptRequest acceptRequest)
    {
        return ApiService.getAccept(acceptRequest,"response");
    }
}
