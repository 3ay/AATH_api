package com.hyperledger.AATH.Backchannel.API.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;

@Data
public class ReceiveInvitationRequest {
    @JsonProperty("data")
    public InvitationMessage data;
    public ReceiveInvitationRequest(String type, String id, String serviceEndpoint, ArrayList<String> recipientKeys, String label)
    {
        setData(new InvitationMessage());
        getData().type = type;
        getData().id = id;
        getData().serviceEndpoint = serviceEndpoint;
        getData().recipientKeys = recipientKeys;
        getData().label = label;

    }
}
