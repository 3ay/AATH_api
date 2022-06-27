package com.hyperledger.AATH.Backchannel.API.model;

import com.fasterxml.jackson.databind.jsonschema.JsonSerializableSchema;
import com.google.gson.annotations.SerializedName;
import com.sirius.sdk.agent.aries_rfc.feature_0160_connection_protocol.state_machines.Inviter;
import lombok.Data;

import java.util.ArrayList;
@Data
public class ConnectionResponse extends Connection{
    @SerializedName("connection_id")
    private String connectionId = null;

    private String agent_name;
    @SerializedName("state")
    private String state = null;

    @SerializedName("connection")
    private InvitationMessage connection = null;

    public ConnectionResponse (String connectionId, String agent_name, InvitationMessage connection) {
        this.connectionId = connectionId;
        this.agent_name = agent_name;
        this.connection = connection;
    }

}
