package com.hyperledger.AATH.Backchannel.API.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReceiveInvitationResponse {
    @JsonProperty("connection_id")
    String connection_id;
    @JsonProperty("state")
    String state;

    public ReceiveInvitationResponse(String connection_id, String state) {
        this.connection_id = connection_id;
        this.state = state;
    }
}
