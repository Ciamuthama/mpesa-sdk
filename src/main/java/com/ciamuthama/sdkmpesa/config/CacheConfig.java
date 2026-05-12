package com.ciamuthama.sdkmpesa.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("darajaToken");
        manager.setCaffeine(Caffeine.newBuilder()
                // Daraja tokens expire in 3599s, refresh a bit early
                .expireAfterWrite(55, TimeUnit.MINUTES)
                .maximumSize(1));
        return manager;
    }
}
