package com.turbo.dev.plugin.exception;

import com.turbo.dev.plugin.dto.subscription.SubscriptionPayment;

import java.util.List;

public class LimitException extends RuntimeException {
    private final List<SubscriptionPayment> paymentOptions;

    public LimitException(String message, List<SubscriptionPayment> paymentOptions) {
        super(message);
        this.paymentOptions = paymentOptions;
    }

    public List<SubscriptionPayment> getPaymentOptions() {
        return paymentOptions;
    }
}
