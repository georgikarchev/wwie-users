package com.whatwillieat.wwie_users.service;

import com.whatwillieat.wwie_users.configuration.AuthConfig;
import com.whatwillieat.wwie_users.security.AuthenticationMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private static final String AUTH_TOKEN_HEADER_NAME = "X-API-KEY";

    public static Authentication getAuthentication(HttpServletRequest request) {
        String requestApiKey = request.getHeader(AUTH_TOKEN_HEADER_NAME);
        String appApiKey = AuthConfig.getAuthToken();
        int port = AuthConfig.getPort();
        System.out.println(port);

        if (requestApiKey == null || !requestApiKey.equals(appApiKey)) {
            throw new BadCredentialsException("Invalid API Key");
        }

        return new AuthenticationMapper(requestApiKey, AuthorityUtils.NO_AUTHORITIES);
    }
}
