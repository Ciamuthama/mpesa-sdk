package com.ciamuthama.sdkmpesa.service;

import com.ciamuthama.sdkmpesa.model.STKPushRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;


@Service
public class MpesaAgentTools {

    private static final Logger log = LoggerFactory.getLogger(MpesaAgentTools.class);
    private final MpesaService mpesaService;

    public MpesaAgentTools(MpesaService mpesaService) {
        this.mpesaService = mpesaService;
    }

    @Tool(description = "Triggers an M-Pesa STK Push to a phone number. Input requires a valid Kenyan phone number (254XXXXXXXXX format) and a positive integer amount in KES.")
    public String sendMoney(String phone, int amount) {
        try {
            if (phone == null || !phone.matches("^254[17]\\d{8}$")) {
                return "Error: Invalid phone number. Must be in format 254XXXXXXXXX (e.g., 254712345678)";
            }
            if (amount <= 0) return "Error: Amount must be a positive number";
            if (amount > 150000) return "Error: Amount exceeds maximum allowed (KES 150,000)";

            log.info("MCP tool sendMoney invoked — phone: {}XXXXX{}, amount: {}",
                    phone.substring(0, 4), phone.substring(phone.length() - 3), amount);

            STKPushRequest payload = new STKPushRequest();
            payload.setPhoneNumber(phone);
            payload.setPartyA(phone);
            payload.setAmount(String.valueOf(amount));
            payload.setAccountReference("AI Agent Payment");
            payload.setTransactionDesc("Payment via AI Agent");

            var response = mpesaService.initiateStkPush(payload);
            return "STK Push initiated successfully. CheckoutRequestID: " + response.checkoutRequestId()
                    + ". ResponseDescription: " + response.responseDescription();
        } catch (Exception e) {
            log.error("Error in sendMoney MCP tool", e);
            return "Error processing payment: " + e.getMessage();
        }
    }

    @Tool(description = "Checks the real-time status of an M-Pesa STK Push transaction. Requires the CheckoutRequestID returned from a previous sendMoney call.")
    public String checkTransactionStatus(String checkoutRequestId) {
        try {
            if (checkoutRequestId == null || checkoutRequestId.isBlank()) {
                return "Error: CheckoutRequestID is required";
            }
            log.info("MCP tool checkTransactionStatus invoked — checkoutRequestId: {}", checkoutRequestId);
            return mpesaService.checkStkPushStatus(checkoutRequestId);
        } catch (Exception e) {
            log.error("Error in checkTransactionStatus MCP tool", e);
            return "Error checking status: " + e.getMessage();
        }
    }
}
