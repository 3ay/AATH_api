package com.sirius.aath.backchannel.services;

import com.sirius.aath.backchannel.model.ConnectionCreateInvitationRequest;
import com.sirius.aath.backchannel.model.ConnectionResponse;
import com.sirius.aath.backchannel.model.ConnectionState;
import com.sirius.aath.backchannel.model.InvitationMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Service
public class ConnectionService {
    private final InvitationService invitationService;

    public ConnectionService(InvitationService invitationService) {
        this.invitationService = invitationService;
    }
    public ArrayList<ConnectionResponse> getLst() {
        return lst;
    }

    public void setLst(ArrayList<ConnectionResponse> lst) {
        this.lst = lst;
    }

    private ArrayList<ConnectionResponse> lst = new ArrayList<ConnectionResponse>();
    public ArrayList<ConnectionResponse> listOfConnectionResponse() throws InterruptedException, ExecutionException, TimeoutException {
        ArrayList<ConnectionResponse> lst = new ArrayList<ConnectionResponse>();
        //создать два списка, один с ключами, другой с values внести последовательно в объект новго списка по сущностям
        ArrayList<InvitationMessage> valuesLst = new ArrayList<>(invitationService.getInvitationDic().values());
        ArrayList <ConnectionCreateInvitationRequest> keysLst = new ArrayList<>(invitationService.getInvitationDic().keySet());
        ConnectionResponse connectionResponse = new ConnectionResponse();
        for (int i = 0; i <keysLst.size(); i++) {
            lst.add(invitationService.initConnectionResponse(connectionResponse,valuesLst.get(i).getId(),
                    ConnectionState.INVITATION,valuesLst.get(i)));
            // lst.add(new ConnectionResponse(valuesLst.get(i).getInvitation().getId(),
            //         keysLst.get(i).getData().getConnection_id(),valuesLst.get(i).getInvitation()));
        }
        setLst(lst);
        return lst;
    }
}
