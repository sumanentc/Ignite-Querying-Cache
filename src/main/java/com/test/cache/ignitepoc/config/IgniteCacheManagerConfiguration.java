package com.test.cache.ignitepoc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ResourceLoader;

import javax.cache.CacheManager;
import javax.cache.Caching;
import java.io.IOException;

import static com.test.cache.ignitepoc.constant.IgniteConstants.IGNITE_CONFIG_PROP_SPRING;
import static com.test.cache.ignitepoc.constant.IgniteConstants.IGNITE_SYSTEM_PROPERTIES_CONFIGURATION;

@Configuration
public class IgniteCacheManagerConfiguration {
    private final String igniteConfigPath;


    public IgniteCacheManagerConfiguration(@Value(IGNITE_CONFIG_PROP_SPRING) String igniteConfigPath) {
        this.igniteConfigPath = igniteConfigPath;
    }

    @DependsOn(IGNITE_SYSTEM_PROPERTIES_CONFIGURATION)
    @Bean
    public CacheManager cacheManager(ResourceLoader resourceLoader) throws IOException {
        return Caching.getCachingProvider().getCacheManager(resourceLoader
                .getResource(igniteConfigPath).getURI(), getClass().getClassLoader());

    }
}
