package com.hyperledger.AATH.Backchannel.API.model;

import java.util.ArrayList;

public class Connection {
    private static ArrayList<ConnectionResponse> lst = new ArrayList<ConnectionResponse>();
    public static ArrayList<ConnectionResponse> getLst() {
        return lst;
    }
    public static void setLst(ArrayList<ConnectionResponse> lst) {
        Connection.lst = lst;
    }
}
