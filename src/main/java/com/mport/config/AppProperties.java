package com.mport.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Auth auth = new Auth();
    private final OAuth2 oAuth2 = new OAuth2();
    private final Datasource datasource = new Datasource();

    @Getter
    @Setter
    public static class Datasource {
        private String driverClassName;
        private String url;
        private String username;
        private String password;
    }

    @Getter
    @Setter
    public static class Auth {
        private String tokenSecret;
        private long tokenExpirationMsec;
    }

    @Getter
    @Setter
    public static class OAuth2 {
        private String authorizedUri;
        private String authorizedRedirectUris;
    }

}
