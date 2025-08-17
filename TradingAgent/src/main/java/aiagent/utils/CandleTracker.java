package aiagent.utils;

import aiagent.*;
import java.util.*;

public class CandleTracker {
    private long lastProcessedCandleTime = 0;

    public List<Candle> getNewCandles(List<Candle> freshCandles) {
        return freshCandles.stream()
                .filter(c -> c.getCloseTime() > lastProcessedCandleTime)
                .sorted(Comparator.comparing(Candle::getCloseTime))
                .toList();
    }

    public void updateLastProcessed(long timestamp) {
        this.lastProcessedCandleTime = Math.max(this.lastProcessedCandleTime, timestamp);
    }

    public long getLastProcessedTime() {
        return this.lastProcessedCandleTime;
    }
}