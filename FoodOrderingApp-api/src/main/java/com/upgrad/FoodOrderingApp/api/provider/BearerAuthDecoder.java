package com.upgrad.FoodOrderingApp.api.provider;

import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;

public class BearerAuthDecoder {

    public final String BEARER_AUTH_PREFIX = "Bearer ";
    private final String accessToken;

    public BearerAuthDecoder(final String bearerToken) throws AuthorizationFailedException {
        //if (!bearerToken.startsWith(BEARER_AUTH_PREFIX)) {
        //    throw new AuthorizationFailedException("ATHR-004", "Invalid Authorization header format");
        //}
        //final String[] bearerTokens = bearerToken.split(BEARER_AUTH_PREFIX);
        //if(bearerTokens.length != 2) {
        //    throw new AuthorizationFailedException("ATHR-004", "Invalid Authorization header format");
        //}
        //this.accessToken = bearerTokens[1];
        this.accessToken = bearerToken.split(BEARER_AUTH_PREFIX)[1];
    }

    public String getAccessToken() {
        return accessToken;
    }
}
