package com.test.cache.ignitepoc.config;

import com.test.cache.ignitepoc.constant.IgniteConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.IgniteSystemProperties;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration(IgniteConstants.IGNITE_SYSTEM_PROPERTIES_CONFIGURATION)
@EnableConfigurationProperties(IgniteConfigProperties.class)
@AutoConfigureBefore(CacheAutoConfiguration.class)
@Slf4j
public class IgniteSystemPropertiesConfig {

    private final IgniteConfigProperties properties;

    public IgniteSystemPropertiesConfig(IgniteConfigProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        if (properties != null && properties.getAddresses() != null) {

            String addressString = properties.getAddresses().stream().filter(Objects::nonNull).collect(Collectors.joining(","));

            if (addressString != null) {
                System.setProperty(IgniteSystemProperties.IGNITE_TCP_DISCOVERY_ADDRESSES, addressString);
            }
            Map<String, String> systemProperties = properties.getSystemProperties();
            if (systemProperties != null) {
                systemProperties.entrySet().forEach(e -> {
                    log.info("Setting up Ignite system properties Name:={} value:={}", e.getKey(), e.getValue());
                });
            }
        }

    }

}
