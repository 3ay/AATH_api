package com.hyperledger.AATH.Backchannel.API.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.ArrayList;
@Data
public class InvitationMessage {
    @JsonProperty("@type")
    @SerializedName("@type")
    public String type = null;

    @JsonProperty("@id")
    @SerializedName ("@id")
    public String id = null;

    @JsonProperty("serviceEndpoint")
    @SerializedName ("serviceEndpoint")
    public String serviceEndpoint = null;

    @JsonProperty("recipientKeys")
    @SerializedName("recipientKeys")
    public ArrayList<String> recipientKeys = null;

    @JsonProperty("label")
    @SerializedName ("label")
    public String label = null;

}
