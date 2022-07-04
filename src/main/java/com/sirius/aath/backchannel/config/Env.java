package com.sirius.aath.backchannel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "env")
public class Env {

    private String serverUrl;
    private String credentials;
    private String fromVerkey;
    private String fromSigKey;
    private String thierKey;

}
