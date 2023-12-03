package com.elk.Utils;

import java.util.concurrent.atomic.AtomicLong;

public class Utils {
    private static final AtomicLong LAST_TIME_MS = new AtomicLong();
    public static long uniqueCurrentTimeNS() {
        long now = System.nanoTime();
        while(true) {
            long lastTime = LAST_TIME_MS.get();
            if (lastTime >= now)
                now = lastTime+1;
            if (LAST_TIME_MS.compareAndSet(lastTime, now))
                return now;
        }
    }
}
