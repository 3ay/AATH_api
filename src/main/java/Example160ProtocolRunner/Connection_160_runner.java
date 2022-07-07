package Example160ProtocolRunner;

import com.sirius.sdk.encryption.P2PConnection;
import com.sirius.sdk.hub.CloudContext;
import com.sirius.sdk.hub.Context;

import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Connection_160_runner {
    @Value("${credentials}")
    private static String credentials;

    @Value("${fromVerkey}")
    private static String fromVerkey_agent1;

    @Value("${fromSigKey}")
    private static String fromSigKey_agent_1;

    @Value("${thierKey}")
    private static String thierKey_agent1;

    @Value("${serverUrl}")
    private static String serverUrl;

    public static void Runner()throws InterruptedException, ExecutionException, TimeoutException {
        try (Context context = CloudContext.builder().setServerUri(serverUrl)
                .setCredentials(credentials.getBytes(StandardCharsets.UTF_8))
                .setP2p(new P2PConnection(fromVerkey_agent1, fromSigKey_agent_1, thierKey_agent1))
                .build()) {
            Connection_160.setInvitation(context);
        }
        CompletableFuture<Boolean> runInviterFeature = CompletableFuture.supplyAsync(() -> {
            try (Context context = CloudContext.builder().setServerUri(serverUrl)
                    .setCredentials(credentials.getBytes(StandardCharsets.UTF_8))
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

        runInviterFeature.get(60, TimeUnit.SECONDS);
    }
}
