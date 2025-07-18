package com.pagamento.common.resilience;

import java.time.Duration;

import io.github.resilience4j.core.IntervalFunction;

public interface IntervalFunctionHelper {
    
    static IntervalFunction ofExponentialRandomBackoff(Duration initialInterval, double multiplier, double randomizationFactor) {
        return IntervalFunction.ofExponentialRandomBackoff(
            initialInterval, 
            multiplier, 
            randomizationFactor
        );
    }
}