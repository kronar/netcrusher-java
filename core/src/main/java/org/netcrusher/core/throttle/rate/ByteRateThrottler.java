package org.netcrusher.core.throttle.rate;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

/**
 * Throttler limits byte count per period
 */
public class ByteRateThrottler extends AbstractRateThrottler {

    /**
     * Create a new throttler
     * @param rate How many byte are expected per period
     * @param rateTime Period time
     * @param rateTimeUnit Period time unit
     * @param factor Division factor
     */
    public ByteRateThrottler(long rate, long rateTime, TimeUnit rateTimeUnit, int factor) {
        super(rate, rateTime, rateTimeUnit, factor);
    }

    /**
     * Create a new throttler
     * @param rate How many byte are expected per period
     * @param rateTime Period time
     * @param rateTimeUnit Period time unit
     */
    public ByteRateThrottler(long rate, long rateTime, TimeUnit rateTimeUnit) {
        super(rate, rateTime, rateTimeUnit);
    }

    @Override
    protected int events(ByteBuffer bb) {
        return bb.remaining();
    }
}
