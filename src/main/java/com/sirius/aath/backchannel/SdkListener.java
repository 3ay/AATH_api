package com.sirius.aath.backchannel;

import com.sirius.aath.backchannel.event.ConnectionRequestEvent;
import com.sirius.sdk.agent.aries_rfc.feature_0160_connection_protocol.messages.ConnRequest;
import com.sirius.sdk.agent.aries_rfc.feature_0160_connection_protocol.state_machines.Inviter;
import com.sirius.sdk.agent.listener.Event;
import com.sirius.sdk.agent.listener.Listener;
import com.sirius.sdk.hub.Context;
import com.sirius.sdk.messaging.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
public class SdkListener {

    private final ContextHolder contextHolder;

    private final ExecutorService executorService;

    private final ApplicationEventPublisher publisher;


    @Autowired
    public SdkListener(ContextHolder contextHolder, ExecutorService executorService, ApplicationEventPublisher publisher) {
        this.contextHolder = contextHolder;
        this.executorService = executorService;
        this.publisher = publisher;
    }

    @PostConstruct
    public void init() {
        log.info("The SdkListener is starting...");
        executorService.execute(this::listen);
    }

    @PreDestroy
    public void destroy() {
        log.info("The KycListener is stopping...");
        executorService.shutdownNow();
    }

    public void listen() {
        try {
            Context context = contextHolder.getContext();

            Listener listener = context.subscribe();
            while (true) {
                Event event = listener.getOne().get();
                Message message = event.message();
                log.info("received: {}", message.getMessageObj());
                if (message instanceof ConnRequest) {
                    publisher.publishEvent(new ConnectionRequestEvent((ConnRequest) message, "metadata"));
                }
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            log.info("Stop listener");
        }
    }
}
