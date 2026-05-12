package com.ciamuthama.sdkmpesa.service;

import com.ciamuthama.sdkmpesa.model.AuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.Objects;

@Service
public class AuthServices {

    private static final Logger log = LoggerFactory.getLogger(AuthServices.class);

    @Value("${daraja.consumer.key}")
    private String consumerKey;

    @Value("${daraja.consumer.secret}")
    private String consumerSecret;

    private final RestClient restClient;

    public AuthServices(RestClient.Builder builder,
            @Value("${daraja.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @Cacheable("darajaToken")
    public String getAccessToken() {
        log.info("Fetching new Daraja OAuth token...");
        String key = consumerKey + ":" + consumerSecret;
        String authHeader = "Basic " + Base64.getEncoder().encodeToString(key.getBytes());

        String token = Objects.requireNonNull(restClient.get()
                .uri("/oauth/v1/generate?grant_type=client_credentials")
                .header("Authorization", authHeader)
                .retrieve()
                .body(AuthResponse.class))
                .getAccess_token();

        log.info("Daraja OAuth token obtained successfully");
        return token;
    }
}
