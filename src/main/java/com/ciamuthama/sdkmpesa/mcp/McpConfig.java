package com.ciamuthama.sdkmpesa.mcp;

import com.ciamuthama.sdkmpesa.model.STKPushRequest;
import com.ciamuthama.sdkmpesa.service.MpesaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Configuration
public class MpesaAgentTools {

    private static final Logger log = LoggerFactory.getLogger(MpesaAgentTools.class);

    private final MpesaService mpesaService;

    public MpesaAgentTools(MpesaService mpesaService) {
        this.mpesaService = mpesaService;
    }

    @Bean
    @Description("Triggers an M-Pesa STK Push to a phone number. Input requires a valid Kenyan phone number (254XXXXXXXXX format) and a positive integer amount in KES.")
    public Function<AgentStkRequest, String> sendMoney() {
        return (request) -> {
            try {
                // Validate phone number format
                if (request.phone() == null || !request.phone().matches("^254[17]\\d{8}$")) {
                    return "Error: Invalid phone number. Must be in format 254XXXXXXXXX (e.g., 254712345678)";
                }

                // Validate amount
                if (request.amount() <= 0) {
                    return "Error: Amount must be a positive number";
                }
                if (request.amount() > 150000) {
                    return "Error: Amount exceeds maximum allowed (KES 150,000)";
                }

                log.info("MCP tool sendMoney invoked — phone: {}XXXXX{}, amount: {}",
                        request.phone().substring(0, 4),
                        request.phone().substring(request.phone().length() - 3),
                        request.amount());

                STKPushRequest payload = new STKPushRequest();
                payload.setPhoneNumber(request.phone());
                payload.setPartyA(request.phone());
                payload.setAmount(String.valueOf(request.amount()));
                payload.setAccountReference("AI Agent Payment");
                payload.setTransactionDesc("Payment via AI Agent");

                var response = mpesaService.initiateStkPush(payload);
                return "STK Push initiated successfully. CheckoutRequestID: " + response.checkoutRequestId()
                        + ". ResponseDescription: " + response.responseDescription();
            } catch (Exception e) {
                log.error("Error in sendMoney MCP tool", e);
                return "Error processing payment: " + e.getMessage();
            }
        };
    }

    @Bean
    @Description("Checks the real-time status of an M-Pesa STK Push transaction. Requires the CheckoutRequestID returned from a previous sendMoney call.")
    public Function<AgentQueryRequest, String> checkTransactionStatus() {
        return (request) -> {
            try {
                if (request.checkoutRequestId() == null || request.checkoutRequestId().isBlank()) {
                    return "Error: CheckoutRequestID is required";
                }

                log.info("MCP tool checkTransactionStatus invoked — checkoutRequestId: {}",
                        request.checkoutRequestId());

                return mpesaService.checkStkPushStatus(request.checkoutRequestId());
            } catch (Exception e) {
                log.error("Error in checkTransactionStatus MCP tool", e);
                return "Error checking status: " + e.getMessage();
            }
        };
    }

    public record AgentStkRequest(String phone, int amount) {
    }

    public record AgentQueryRequest(String checkoutRequestId) {
    }
}
