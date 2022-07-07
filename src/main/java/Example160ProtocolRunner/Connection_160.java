package Example160ProtocolRunner;
import com.sirius.sdk.agent.aries_rfc.feature_0160_connection_protocol.messages.ConnRequest;
import com.sirius.sdk.agent.aries_rfc.feature_0160_connection_protocol.messages.Invitation;
import com.sirius.sdk.agent.aries_rfc.feature_0160_connection_protocol.state_machines.Invitee;
import com.sirius.sdk.agent.aries_rfc.feature_0160_connection_protocol.state_machines.Inviter;
import com.sirius.sdk.agent.connections.Endpoint;
import com.sirius.sdk.agent.listener.Event;
import com.sirius.sdk.agent.listener.Listener;
import com.sirius.sdk.agent.pairwise.Pairwise;
import com.sirius.sdk.hub.Context;
import com.sirius.sdk.utils.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class Connection_160 {
    static List<Context> context = new ArrayList<>();

    public static Endpoint getEndpoint() {
        return endpoint;
    }

    static Endpoint endpoint;
    public static String getConnectionKey() {
        return connectionKey;
    }

    public static Inviter getInviter() {
        return inviter;
    }

    public static void setInviter(Inviter inviter) {
        Connection_160.inviter = inviter;
    }

    static Inviter inviter;
    public static void setConnectionKey(String connectionKey) {

        Connection_160.connectionKey = connectionKey;
    }
    public static void setEndpoint(Context context)
    {
        List<Endpoint> endpointList = context.getEndpoints();
        endpointList.forEach((Endpoint e) ->
        {
            endpoint = e.getRoutingKeys().isEmpty() ? e : null;
        });

    }

    static String  connectionKey;

    public static Invitation getInvitation() {
        return invitation;
    }

    static Invitation invitation;



    // инициализация
    public static void setInvitation(Context context)  throws InterruptedException, ExecutionException, TimeoutException
    {
        setEndpoint(context);
        String connectionKey = context.getCrypto().createKey();
        Connection_160.invitation = Invitation.builder().
                setLabel("Inviter").
                setEndpoint(getEndpoint().getAddress()).
                setRecipientKeys(Collections.singletonList(connectionKey)).
                build();
        Connection_160.setConnectionKey(connectionKey);
    }
    public static void inviterRoutine(Context context) throws InterruptedException, ExecutionException, TimeoutException {
        setEndpoint(context);
        Pair<String,String> didVerkey = context.getDid().createAndStoreMyDid();
        Pairwise.Me inviterMe = new Pairwise.Me(didVerkey.first, didVerkey.second);
        Listener listener = context.subscribe(); //??
        Event event = listener.getOne().get(30, TimeUnit.SECONDS);
        String expectedKey = Connection_160.getConnectionKey();
        String actualKey = event.getRecipientVerkey();
        if (expectedKey.equals(actualKey)) { //getConnectionKey().equals(event.getRecipientVerkey())
            if (event.message() instanceof ConnRequest) {
                ConnRequest request = (ConnRequest) event.message();
                Inviter inviter_machine = new Inviter(context, inviterMe, expectedKey, getEndpoint());
                Connection_160.setInviter(inviter_machine);
                Pairwise pairwise = inviter_machine.createConnection(request);
                context.getPairwiseList().ensureExists(pairwise);
            }
        }
    }

    public static void inviteeRoutine(Context context)
    {
        //setEndpoint(context);
        Pair<String,String> didVerkey = context.getDid().createAndStoreMyDid();
        Pairwise.Me inviteeMe = new Pairwise.Me(didVerkey.first, didVerkey.second);
        Endpoint myEndpoint = context.getEndpointWithEmptyRoutingKeys();
        Invitee machine = new Invitee(context, inviteeMe, myEndpoint);
        Pairwise pairwise = machine.createConnection(getInvitation(), "Invitee");
        context.getPairwiseList().ensureExists(pairwise);
    }


}
