package com.pagamento.common.features;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "features")
public class FeatureFlags {
    private Map<String, Boolean> flags = new HashMap<>();

    public boolean isEnabled(String feature) {
        return flags.getOrDefault(feature, false);
    }

    public Map<String, Boolean> getFlags() {
        return flags;
    }

    public void setFlags(Map<String, Boolean> flags) {
        this.flags = flags;
    }
}