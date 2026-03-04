package com.ciamuthama.sdkmpesa.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service // <--- detailed annotation is important!
public class MpesaService {

    private final AuthServices authServices;
    private final RestClient restClient;

    public MpesaService(AuthServices authServices, RestClient.Builder builder) {
        this.authServices = authServices;
        this.restClient = builder.baseUrl("https://sandbox.safaricom.co.ke").build();
    }

    public String initiateStkPush(StkPushRequest requestPayload) {
        String token = authServices.getAccessToken();

        return restClient.post()
                .uri("/mpesa/stkpush/v1/processrequest")
                .header("Authorization", "Bearer " + token) // FIXED: Correct spelling + Added space
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestPayload) // Spring automatically converts the object to JSON here
                .retrieve()
                .body(String.class);
    }
}