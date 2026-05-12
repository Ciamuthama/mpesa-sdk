package com.ciamuthama.sdkmpesa.controller;

import com.ciamuthama.sdkmpesa.model.CallbackResponse;
import com.ciamuthama.sdkmpesa.model.STKPushRequest;
import com.ciamuthama.sdkmpesa.model.StkSyncResponse;
import com.ciamuthama.sdkmpesa.service.MpesaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class MpesaController {

    private static final Logger log = LoggerFactory.getLogger(MpesaController.class);
    private final MpesaService mpesaService;

    public MpesaController(MpesaService mpesaService) {
        this.mpesaService = mpesaService;
    }
    
    @GetMapping("")
    public String Entry(){
        return "this is home";
    }

    @PostMapping("/stk-push")
    public ResponseEntity<StkSyncResponse> stkPush(@RequestBody @Valid STKPushRequest request) {
        log.info("STK Push request received for phone: {}", maskPhone(request.getPhoneNumber()));
        StkSyncResponse response = mpesaService.initiateStkPush(request);
        log.info("STK Push initiated. CheckoutRequestID: {}", response.checkoutRequestId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-status")
    public ResponseEntity<String> checkStatus(@RequestParam String checkoutRequestId) {
        log.info("Status check for CheckoutRequestID: {}", checkoutRequestId);
        String result = mpesaService.checkStkPushStatus(checkoutRequestId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/callback")
    public ResponseEntity<Map<String, Object>> callback(@RequestBody CallbackResponse callBackData) {
        try {
            var callback = callBackData.getBody().getStkCallback();
            String checkoutId = callback.getCheckoutRequestID();
            int resultCode = callback.getResultCode();
            String resultDesc = callback.getResultDesc();

            log.info("Daraja callback received — CheckoutID: {}, ResultCode: {}, Desc: {}",
                    checkoutId, resultCode, resultDesc);

            if (resultCode == 0) {
                log.info("Payment SUCCESSFUL for CheckoutID: {}", checkoutId);
                callback.getCallbackMetadata().getItem()
                        .forEach(item -> log.info("  {} = {}", item.getName(), item.getValue()));
            } else {
                log.warn("Payment FAILED for CheckoutID: {} — {}", checkoutId, resultDesc);
            }

            return ResponseEntity.ok(Map.of("ResultCode", 0, "ResultDesc", "Accepted"));
        } catch (Exception e) {
            log.error("Error processing Daraja callback", e);
            return ResponseEntity.ok(Map.of("ResultCode", 1, "ResultDesc", "Error processing callback"));
        }
    }

    /**
     * Masks a phone number for safe logging: 2547XXXXX863
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 6)
            return "****";
        return phone.substring(0, 4) + "XXXXX" + phone.substring(phone.length() - 3);
    }
}
