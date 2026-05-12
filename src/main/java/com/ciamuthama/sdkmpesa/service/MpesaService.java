package com.ciamuthama.sdkmpesa.service;

import com.ciamuthama.sdkmpesa.model.STKPushRequest;
import com.ciamuthama.sdkmpesa.model.StkSyncResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class MpesaService {

    private static final Logger log = LoggerFactory.getLogger(MpesaService.class);

    private final AuthServices authServices;
    private final RestClient restClient;

    @Value("${daraja.shortcode}")
    private String shortCode;

    @Value("${daraja.passkey}")
    private String passKey;

    @Value("${daraja.callback-url}")
    private String callbackUrl;

    public MpesaService(AuthServices authServices,
            RestClient.Builder builder,
            @Value("${daraja.base-url}") String baseUrl) {
        this.authServices = authServices;
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public StkSyncResponse initiateStkPush(STKPushRequest requestPayload) {
        String token = authServices.getAccessToken();
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String rawPassword = shortCode + passKey + timestamp;
        String password = Base64.getEncoder().encodeToString(rawPassword.getBytes());

        requestPayload.setBusinessShortCode(shortCode);
        requestPayload.setPassword(password);
        requestPayload.setTimestamp(timestamp);
        requestPayload.setPartyB(shortCode);
        requestPayload.setCallBackURL(callbackUrl);

        if (requestPayload.getTransactionType() == null) {
            requestPayload.setTransactionType("CustomerPayBillOnline");
        }

        log.info("Sending STK Push to Daraja API for shortcode: {}", shortCode);

        return restClient.post()
                .uri("/mpesa/stkpush/v1/processrequest")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestPayload)
                .retrieve()
                .body(StkSyncResponse.class);
    }

    public String checkStkPushStatus(String checkoutRequestId) {
        String token = authServices.getAccessToken();
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String password = Base64.getEncoder().encodeToString(
                (shortCode + passKey + timestamp).getBytes());

        Map<String, String> queryPayload = new HashMap<>();
        queryPayload.put("BusinessShortCode", shortCode);
        queryPayload.put("Password", password);
        queryPayload.put("Timestamp", timestamp);
        queryPayload.put("CheckoutRequestID", checkoutRequestId);

        log.info("Querying STK Push status for CheckoutRequestID: {}", checkoutRequestId);

        return restClient.post()
                .uri("/mpesa/stkpushquery/v1/query")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(queryPayload)
                .retrieve()
                .body(String.class);
    }
}