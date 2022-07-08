package com.sirius.aath.backchannel.service;

import com.sirius.aath.backchannel.ContextHolder;
import com.sirius.sdk.agent.aries_rfc.feature_0015_acks.Ack;
import com.sirius.sdk.agent.aries_rfc.feature_0095_basic_message.Message;
import com.sirius.sdk.agent.pairwise.Pairwise;
import com.sirius.sdk.hub.Context;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@Service
public class MessageService {

    private final ContextHolder contextHolder;

    private final Map<String, CompletableFuture<Ack>> futures = new ConcurrentHashMap<>();

    public MessageService(ContextHolder contextHolder) {
        this.contextHolder = contextHolder;
    }

    public Future<Ack> sendMessage(String content, String therDid, String theirKey) {
        Context context = contextHolder.getContext();
        Message message = Message.builder().setContent(content).build();
        message.setPleaseAck(true);
        context.sendTo(message,
                new Pairwise(
                        new Pairwise.Me("", ""),
                        new Pairwise.Their(therDid, "label", "endpoint", theirKey)
                ));
        CompletableFuture<Ack> future = new CompletableFuture<>();
        futures.put(message.getAckMessageId(), future);
        return future;
    }

    public CompletableFuture<Ack> getAndRemove(String messageId) {
        return futures.remove(messageId);
    }
}
