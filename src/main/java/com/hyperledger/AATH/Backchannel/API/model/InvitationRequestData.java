package com.hyperledger.AATH.Backchannel.API.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class InvitationRequestData {
    @JsonProperty("agentName")
    public String agentName;
    @JsonProperty("comment")
    public String comment;
}
