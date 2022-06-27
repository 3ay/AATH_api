package com.hyperledger.AATH.Backchannel.API.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class AcceptRequest {
    @JsonProperty("id")
    private String connection_id;
    @JsonProperty("data")
    InvitationRequestData data;
    public AcceptRequest()
    {
        super();
    }
}
