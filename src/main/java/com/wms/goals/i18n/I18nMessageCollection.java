package com.wms.goals.i18n;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.MessageSource;

@Getter
@AllArgsConstructor
public enum I18nMessageCollection {
    GOAL_CREATED("GOAL_CREATED"),
    GOAL_UPDATED("GOAL_UPDATED"),
    GOAL_LIST_FETCHED("GOAL_LIST_FETCHED"),
    GOAL_DETAIL_FETCHED("GOAL_DETAIL_FETCHED"),
    PROJECTION_DONE("PROJECTION_DONE"),
    VALIDATION_FAILED("VALIDATION_FAILED"),
    INTERNAL_ERROR("INTERNAL_ERROR"),
    MISSING_OR_INVALID_TOKEN("MISSING_OR_INVALID_TOKEN"),
    CUSTOMER_NOT_FOUND("CUSTOMER_NOT_FOUND"),
    KYC_REQUIRED("KYC_REQUIRED"),
    CRP_REQUIRED("CRP_REQUIRED"),
    RISK_PROFILE_NOT_FOUND("RISK_PROFILE_NOT_FOUND"),
    PROFILE_ASSUMPTION_NOT_CONFIGURED("PROFILE_ASSUMPTION_NOT_CONFIGURED"),
    TARGET_AGE_INVALID("TARGET_AGE_INVALID"),
    SIMULATION_DONE("SIMULATION_DONE");

    private final String key;

    public String localized(MessageSource ms, java.util.Locale locale){
        return ms.getMessage(key, null, locale);
    }
}
