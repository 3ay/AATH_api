package com.sirius.aath.backchannel.model;

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

    public InvitationMessage(String type, String id, String serviceEndpoint, ArrayList<String> recipientKeys, String label) {
        this.type = type;
        this.id = id;
        this.serviceEndpoint = serviceEndpoint;
        this.recipientKeys = recipientKeys;
        this.label = label;
    }
}
