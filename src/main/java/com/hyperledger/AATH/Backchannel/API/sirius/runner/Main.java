package com.hyperledger.AATH.Backchannel.API.sirius.runner;

import com.sirius.sdk.agent.listener.Event;
import com.sirius.sdk.agent.listener.Listener;
import com.sirius.sdk.agent.aries_rfc.feature_0095_basic_message.Message;
import com.sirius.sdk.agent.aries_rfc.feature_0160_connection_protocol.state_machines.Inviter;
import com.sirius.sdk.agent.aries_rfc.feature_0160_connection_protocol.messages.ConnRequest;
import com.sirius.sdk.agent.aries_rfc.feature_0160_connection_protocol.messages.Invitation;
import com.sirius.sdk.agent.connections.Endpoint;
import com.sirius.sdk.agent.pairwise.Pairwise;
import com.sirius.sdk.agent.wallet.abstract_wallet.model.RetrieveRecordOptions;
import com.sirius.sdk.encryption.P2PConnection;
import com.sirius.sdk.hub.CloudContext;
import com.sirius.sdk.hub.Context;
import com.sirius.sdk.hub.CloudHub;
import com.sirius.sdk.utils.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
@Component
public class Main {


    static Context context;

    public static Pair<String, String> qrCode() {
        String namespace = "samples";
        String storeId = "qr";
        RetrieveRecordOptions opts = new RetrieveRecordOptions();
        // Сохраняем инфо... о QR в самом Wallet чтобы не генерировать ключ при каждом запуске samples
        String retStr = null;
        try {
            retStr = context.getNonSecrets().getWalletRecord(namespace, storeId, opts);
        } catch (Exception ignored) {

        }
        if (retStr != null) {
            JSONObject ret = new JSONObject(retStr);
            JSONArray vals = new JSONArray(ret.optString("value"));
            String connectionKey = vals.getString(0);
            String qrContent = vals.getString(1);
            String qrUrl = vals.getString(2);
            return new Pair<>(connectionKey, qrUrl);
        } else { // WalletItemNotFound
            // Ключ установки соединения. Аналог Bob Pre-key
            //см. [2.4. Keys] https://signal.org/docs/specifications/x3dh/
            String connectionKey = context.getCrypto().createKey();
            // Теперь сформируем приглашение для других через 0160
            // шаг 1 - определимся какой endpoint мы возьмем, для простоты возьмем endpoint без доп шифрования
            List<Endpoint> endpoints = context.getEndpoints();
            Endpoint myEndpoint = null;
            for (Endpoint e : endpoints) {
                if (e.getRoutingKeys().isEmpty()) {
                    myEndpoint = e;
                    break;
                }
            }
            if (myEndpoint == null)
                return null;
            // шаг 2 - создаем приглашение
            Invitation invitation = Invitation.builder().
                    setLabel("0160 Sample J").
                    setRecipientKeys(Collections.singletonList(connectionKey)).
                    setEndpoint(myEndpoint.getAddress()).
                    build();

            // шаг 3 - согласно Aries-0160 генерируем URL
            String qrContent = invitation.invitationUrl();

            // шаг 4 - создаем QR
            String qrUrl = context.generateQrCode(qrContent);
            if (qrUrl == null)
                return null;
            // Кладем в Wallet для повторного использования
            JSONArray dump = new JSONArray();
            dump.put(connectionKey).put(qrContent).put(qrUrl);
            context.getNonSecrets().addWalletRecord(namespace, storeId, dump.toString());
            return new Pair<>(connectionKey, qrUrl);
        }
    }

    final static String serverUrl = "http://localhost";
    final static byte[] credentials_agent1 = "Ftp1Lx2Y9uVqM5Q1fzAgy3kngGMWqOsUGylPGc3oYD6a3o5vSxMHC4NKE8+f1HCdo3T+ZJEBZq50BaJy9rSGh18/iBQBSUoI8MAF402kYjQ=".getBytes(StandardCharsets.UTF_8);
    final static String fromVerkey_agent1 = "6QvQ3Y5pPMGNgzvs86N3AQo98pF5WrzM1h6WkKH3dL7f";
    final static String fromSigKey_agent_1 = "28Au6YoU7oPt6YLpbWkzFryhaQbfAcca9KxZEmz22jJaZoKqABc4UJ9vDjNTtmKSn2Axfu8sT52f5Stmt7JD4zzh";
    final static String thierKey_agent1 = "4PQsX8fuBguerSaBbTQuGhtS8rgCz82XJmeTFM9VGPFj";
    final static String did_agent1 = "Th7MpTaRZVRYnPiabds81Y";
    final static String seed_agent1 = "000000000000000000000000Steward1";

    final static byte[] credentials_agent2 = "Ftp1Lx2Y9uVqM5Q1fzAgy9SPlHTb9tyVt5Zk58/XUkrdvfRFx+M0b9/3wF4FVb5do3T+ZJEBZq50BaJy9rSGh7pcY1PFBxsuxFqKNevrM90=".getBytes(StandardCharsets.UTF_8);
    final static String fromVerkey_agent2 = "5o6wXAYT3A8svdog2t4M3gk15iXNW8yvxVu3utJHAD7g";
    final static String fromSigKey_agent_2 = "2xsAzx4URZGY8imWRL5jFAbQqvdFHw4ZbuxxoAADSqVCFTbiwZYhw4gPVA5dsqbJSsLxbac7ath4sFiHYzyVsEDY";
    final static String thierKey_agent2 = "9mXcH7KmRit9qSamWr29owAoB85CvTyzDX3bShHf4XQq";
    final static String did_agent2 = "T8MtAB98aCkgNLtNfQx6WG";
    final static String seed_agent2 = "000000000000000000000000Trustee0";
    @PostConstruct
    public static void init () throws InterruptedException, ExecutionException, TimeoutException {

        try (Context context = CloudContext.builder().setServerUri(serverUrl)
                .setCredentials(credentials_agent1)
                .setP2p(new P2PConnection(fromVerkey_agent1, fromSigKey_agent_1, thierKey_agent1))
                .build()) {
            Connection_160.setInvitation(context);
        }
        CompletableFuture<Boolean> runInviterFeature = CompletableFuture.supplyAsync(() -> {
            try (Context context = CloudContext.builder().setServerUri(serverUrl)
                    .setCredentials(credentials_agent1)
                    .setP2p(new P2PConnection(fromVerkey_agent1, fromSigKey_agent_1, thierKey_agent1))
                    .build()) {
                try {
                    Connection_160.inviterRoutine(context);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }, r -> new Thread(r).start());
        CompletableFuture<Boolean> runInviteeFeature = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try (Context context = CloudContext.builder().setServerUri(serverUrl)
                    .setCredentials(credentials_agent2)
                    .setP2p(new P2PConnection(fromVerkey_agent2, fromSigKey_agent_2, thierKey_agent2))
                    .build()) {
                Connection_160.inviteeRoutine(context);
            }
            return true;
        }, r -> new Thread(r).start());
        runInviterFeature.get(60, TimeUnit.SECONDS);
        runInviteeFeature.get(60, TimeUnit.SECONDS);


    }
}
