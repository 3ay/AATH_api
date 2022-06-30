package com.hyperledger.AATH.Backchannel.API;

import Config.ConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication(scanBasePackages = {"com.hyperledger.AATH.Backchannel.API.api.connection"})
@EnableConfigurationProperties(ConfigProperties.class)
public class AathBackchannelApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AathBackchannelApiApplication.class, args);
    }

    @Bean(initMethod = "ContextInit")
    public BeanInitMethodImpl getContextBean() {
        return new BeanInitMethodImpl();
    }

    @Bean("Listener")
    public ExecutorService Listener() {
        return Executors.newSingleThreadExecutor();
    }

}