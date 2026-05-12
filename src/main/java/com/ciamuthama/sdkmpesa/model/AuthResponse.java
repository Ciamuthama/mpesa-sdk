package com.ciamuthama.sdkmpesa.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuthResponse {
    @JsonProperty("access_token")
    private String access_token;

    @JsonProperty("expires_in")
    private String expire_in;
}
