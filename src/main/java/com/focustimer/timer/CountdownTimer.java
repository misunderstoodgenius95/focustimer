package com.focustimer.timer;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * A blocking countdown timer. Ticks once per second and reports the
 * remaining duration through {@code onTick} until it reaches zero.
 */
public class CountdownTimer {

    private volatile boolean cancelled = false;

    public void start(Duration duration, Consumer<Duration> onTick) throws InterruptedException {
        long remainingSeconds = duration.getSeconds();
        while (remainingSeconds >= 0 && !cancelled) {
            onTick.accept(Duration.ofSeconds(remainingSeconds));
            if (remainingSeconds == 0) {
                break;
            }
            Thread.sleep(1000);
            remainingSeconds--;
        }
    }

    public void cancel() {
        cancelled = true;
    }
}
