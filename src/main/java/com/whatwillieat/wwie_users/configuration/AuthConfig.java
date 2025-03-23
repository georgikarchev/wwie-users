package com.whatwillieat.wwie_users.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfig {

    private static String authToken;
    private static int wwieUiPort;

    @Value("${app.api-key}")
    public void setAuthToken(String token) {
        authToken = token;
    }

    public static String getAuthToken() {
        return authToken;
    }

    @Value("${app.wwie-ui.port}")
    public void setPort(int port) {
        wwieUiPort = port;
    }

    public static int getPort() {return wwieUiPort;}
}