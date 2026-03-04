package com.ciamuthama.sdkmpesa.config;

import com.ciamuthama.sdkmpesa.data.StkSyncPushRequest;
import com.ciamuthama.sdkmpesa.model.STKPushRequest;
import com.ciamuthama.sdkmpesa.service.MpesaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
class MpesaController {
    private final MpesaService mpesaService;

    public MpesaController(MpesaService mpesaService) {
        this.mpesaService = mpesaService;
    }

    @GetMapping("/")
    public String test(){
        StkSyncPushRequest stkPushRequest = new StkSyncPushRequest();
        stkPushRequest.setAmount(stkPushRequest.getAmount());
        stkPushRequest.setPhoneNumber(stkPushRequest.getPhoneNumber());
        stkPushRequest.setBusinessShortCode(stkPushRequest.getBusinessShortCode());
        stkPushRequest.setPassword(stkPushRequest.getPassword());
        stkPushRequest.setPartyA(stkPushRequest.getPartyA());
        stkPushRequest.setPartyB(stkPushRequest.getPartyB());
        stkPushRequest.setAccountReference(stkPushRequest.getAccountReference());
        stkPushRequest.setTimestamp(stkPushRequest.getTimestamp());
        stkPushRequest.setCallBackURL(stkPushRequest.getCallBackURL());
        stkPushRequest.setTransactionType(stkPushRequest.getTransactionType());
        stkPushRequest.setTransactionDesc(stkPushRequest.getTransactionDesc());

        return mpesaService.STKpush(stkPushRequest);
    };
}
