package com.hyperledger.AATH.Backchannel.API.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Accept {
    @SerializedName("connection_id")
    @JsonProperty("connection_id")
    private String connectionId;
    @SerializedName("state")
    @JsonProperty("state")
    private String state;
    public Accept(String connectionId, String state)
    {
        this.connectionId = connectionId;
        this.state = state;
    }
}
