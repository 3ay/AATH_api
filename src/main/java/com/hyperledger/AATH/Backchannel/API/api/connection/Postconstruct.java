package com.hyperledger.AATH.Backchannel.API.api.connection;

import com.hyperledger.AATH.Backchannel.API.sirius.runner.Connection_160;
import com.hyperledger.AATH.Backchannel.API.sirius.runner.Connection_160_runner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class Postconstruct {

    public void init() throws InterruptedException, ExecutionException, TimeoutException {
        //Connection_160_runner.Runner();
        System.out.println("postconstruct is running");
    }
}
