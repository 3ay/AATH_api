package com.hyperledger.AATH.Backchannel.API;

import Config.ConfigProperties;
import com.hyperledger.AATH.Backchannel.API.sirius.runner.Connection_160_runner;
import examples.Connection_160;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.sirius.sdk.agent.connections.Endpoint;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.*;

@SpringBootApplication(scanBasePackages = {"com.hyperledger.AATH.Backchannel.API.api.connection"})
@EnableConfigurationProperties(ConfigProperties.class)
public class AathBackchannelApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AathBackchannelApiApplication.class, args);
	}
	@Bean(initMethod="ContextInit")
	public BeanInitMethodImpl getContextBean() {
		return new BeanInitMethodImpl();
	}

	@Bean("Listener")
	public ExecutorService Listener()
	{
		return Executors.newSingleThreadExecutor();
	}

}