package com.wms.goals.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "goal.assumptions")
@Data
public class GoalAssumptionProperties {
    private double inflationAnnual = 0.03;
    private String compounding = "MONTHLY";
    private Map<String, Double> profiles;
    private double trackingThresholdPct = 0.10;
    private double depositAnnual = 0.03;

    public Double findReturnAnnual(String profileType) {
        if (profiles == null || profileType == null) return null;
        Double v = profiles.get(profileType);
        if (v != null) return v;
        String key = profileType.trim().toUpperCase(Locale.ROOT);
        return profiles.get(key);
    }
}

