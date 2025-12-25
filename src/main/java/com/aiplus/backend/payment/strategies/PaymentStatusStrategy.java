package com.aiplus.backend.payment.strategies;

import java.util.Map;

public interface PaymentStatusStrategy {
    void process(String token, Map<String, Object> data);

}
