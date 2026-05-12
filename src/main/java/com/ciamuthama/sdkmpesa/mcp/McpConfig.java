package com.ciamuthama.sdkmpesa.mcp;

import com.ciamuthama.sdkmpesa.model.STKPushRequest;
import com.ciamuthama.sdkmpesa.service.MpesaAgentTools;
import com.ciamuthama.sdkmpesa.service.MpesaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;
@Configuration
public class McpConfig {

    @Bean
    public ToolCallbackProvider mpesaTools(MpesaAgentTools mpesaAgentTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(mpesaAgentTools)
                .build();
    }
}