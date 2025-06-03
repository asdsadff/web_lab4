package com.partsey.ssl.openidconnect;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "casdoor")
@Data
public class OpenIdConnectProperties {

    private String connectEndpoint;
    private String connectClientId;
    private String connectClientSecret;

}
