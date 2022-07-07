package Config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Data
@Configuration
@ConfigurationProperties(prefix = "env1")
public class ConfigProperties {
    private String credentials;
    private String fromVerkey;
    private String fromSigKey;
    private String thierKey;
    private String serverUrl;
}
