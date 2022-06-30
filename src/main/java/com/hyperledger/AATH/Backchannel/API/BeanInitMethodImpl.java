package com.hyperledger.AATH.Backchannel.API;

import Config.ConfigProperties;
import com.hyperledger.AATH.Backchannel.API.api.connection.ApiService;
import com.hyperledger.AATH.Backchannel.API.sirius.runner.Connection_160;
import com.sirius.sdk.agent.aries_rfc.feature_0160_connection_protocol.messages.ConnRequest;
import com.sirius.sdk.agent.aries_rfc.feature_0160_connection_protocol.state_machines.Inviter;
import com.sirius.sdk.agent.listener.Event;
import com.sirius.sdk.agent.listener.Listener;
import com.sirius.sdk.agent.pairwise.Pairwise;
import com.sirius.sdk.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.hyperledger.AATH.Backchannel.API.sirius.runner.Connection_160.getEndpoint;
import static com.hyperledger.AATH.Backchannel.API.sirius.runner.Connection_160.setEndpoint;

public class BeanInitMethodImpl {
    @Autowired
    private ConfigProperties props;

    public void ContextInit() throws InterruptedException, ExecutionException, TimeoutException {
        ApiService.buidContext(props.getCredentials(), props.getFromVerkey(), props.getFromSigKey(), props.getThierKey(), props.getServerUrl());
    }
}
