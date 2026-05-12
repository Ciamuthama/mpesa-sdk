package com.ciamuthama.sdkmpesa.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class STKPushRequest {
    @JsonProperty("BusinessShortCode")
    private String businessShortCode;

    @JsonProperty("Password")
    private String password;

    @JsonProperty("Timestamp")
    private String timestamp;

    @JsonProperty("TransactionType")
    private String transactionType;

    @NotBlank(message = "Amount is required")
    @Pattern(regexp = "^[1-9]\\d*$", message = "Amount must be a positive whole number")
    @JsonProperty("Amount")
    private String amount;

    @NotBlank(message = "PartyA (sender phone) is required")
    @Pattern(regexp = "^254[17]\\d{8}$", message = "PartyA must be a valid Kenyan phone number (254XXXXXXXXX)")
    @JsonProperty("PartyA")
    private String partyA;

    @JsonProperty("PartyB")
    private String partyB;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^254[17]\\d{8}$", message = "Phone number must be a valid Kenyan phone number (254XXXXXXXXX)")
    @JsonProperty("PhoneNumber")
    private String phoneNumber;

    @JsonProperty("CallBackURL")
    private String callBackURL;

    @NotBlank(message = "Account reference is required")
    @JsonProperty("AccountReference")
    private String accountReference;

    @NotBlank(message = "Transaction description is required")
    @JsonProperty("TransactionDesc")
    private String transactionDesc;
}
