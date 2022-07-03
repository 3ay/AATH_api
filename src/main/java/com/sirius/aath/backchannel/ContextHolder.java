package com.sirius.aath.backchannel;


import com.sirius.aath.backchannel.config.Env;
import com.sirius.sdk.encryption.P2PConnection;
import com.sirius.sdk.hub.CloudContext;
import com.sirius.sdk.hub.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class ContextHolder {

    private final Env env;

    private Context context;

    @Autowired
    public ContextHolder(Env env) {
        this.env = env;
    }

    @PostConstruct
    void init() {
        context = buildContext();
    }

    private Context buildContext() {
        return CloudContext.builder().
                setServerUri(env.getServerUrl()).
                setCredentials(env.getCredentials().getBytes()).
                setP2p(new P2PConnection(
                        env.getFromVerkey(),
                        env.getFromSigKey(),
                        env.getThierKey()
                )).build();
    }

    @PreDestroy
    void destroy() {
        context.close();
    }

    public Context getContext() {
        return context;
    }

}