package com.turbo.dev.plugin.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.turbo.dev.plugin.dto.subscription.SubscriptionPayment;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BackendErrorDto {
    public String message;
    public List<SubscriptionPayment> paymentOptions;
}
