package com.hyperledger.AATH.Backchannel.API.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

@Data
public class InvitationRequest {
    @JsonProperty("data")
    public InvitationRequestData data;
    public InvitationRequest(String  agentName)
    {
        setData(new InvitationRequestData());
        getData().agentName = agentName;
    }

    public InvitationRequest() {
       super();
    }
}
