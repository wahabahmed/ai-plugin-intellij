package com.turbo.dev.plugin.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionPayment {
    private TierEnum planType;
    private long price;
    private long tokens;
    private String paymentUrl;
}
