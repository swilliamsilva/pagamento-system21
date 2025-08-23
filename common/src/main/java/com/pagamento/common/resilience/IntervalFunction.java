package com.pagamento.common.resilience;

import java.time.Duration;

public interface IntervalFunction {

    static io.github.resilience4j.core.IntervalFunction ofExponentialRandomBackoff(Duration initialInterval, double multiplier, double randomizationFactor) {
        return io.github.resilience4j.core.IntervalFunction.ofExponentialRandomBackoff(initialInterval, multiplier, randomizationFactor);
    }
}

