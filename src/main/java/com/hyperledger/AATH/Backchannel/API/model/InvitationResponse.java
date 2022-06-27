package com.hyperledger.AATH.Backchannel.API.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.ArrayList;
@Data
public class InvitationResponse extends Connection{
    @JsonProperty("agent_name")
    private String agent_name;
    @JsonProperty("invitation")
    @SerializedName("invitation")
    private InvitationMessage invitation = null;

    public InvitationResponse(String type, String id, String serviceEndpoint, ArrayList<String> recipientKeys, String label, String agent_name) {
        setInvitation(new InvitationMessage());
        getInvitation().type = type;
        getInvitation().id = id;
        getInvitation().serviceEndpoint = serviceEndpoint;
        getInvitation().recipientKeys = recipientKeys;
        getInvitation().label = label;
        this.agent_name = agent_name;
    }
}
