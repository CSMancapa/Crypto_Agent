package aiagent;

import java.util.*;

public class MarketAnalyzer {

    // Example configurable thresholds
    private static final int TREND_EMA_PERIOD = 10;
    private static final double TREND_SLOPE_THRESHOLD = 0.0005;
    private static final int VOLATILITY_LOOKBACK = 14;
    private static final double VOLATILITY_THRESHOLD = 0.03; // 3% average range

    public static String classify(List<Candle> candles) {
        if (candles.size() < TREND_EMA_PERIOD + 1) {
            return "unknown";
        }

        boolean trendingUp = isTrendingUp(candles);
        boolean volatileMarket = isVolatile(candles);

        if (trendingUp && volatileMarket) return "volatile_trending_up";
        if (trendingUp) return "trending_up";
        if (isTrendingDown(candles)) return "trending_down";
        if (volatileMarket) return "volatile";

        return "ranging";
    }

    private static boolean isTrendingUp(List<Candle> candles) {
        double emaPrev = calculateEMA(candles, TREND_EMA_PERIOD, candles.size() - TREND_EMA_PERIOD - 1);
        double emaNow = calculateEMA(candles, TREND_EMA_PERIOD, candles.size() - 1);
        return (emaNow - emaPrev) / emaPrev > TREND_SLOPE_THRESHOLD;
    }

    private static boolean isTrendingDown(List<Candle> candles) {
        double emaPrev = calculateEMA(candles, TREND_EMA_PERIOD, candles.size() - TREND_EMA_PERIOD - 1);
        double emaNow = calculateEMA(candles, TREND_EMA_PERIOD, candles.size() - 1);
        return (emaNow - emaPrev) / emaPrev < -TREND_SLOPE_THRESHOLD;
    }

    private static boolean isVolatile(List<Candle> candles) {
        if (candles.size() < VOLATILITY_LOOKBACK + 1) return false;
        double totalRange = 0;
        for (int i = candles.size() - VOLATILITY_LOOKBACK; i < candles.size(); i++) {
            double high = candles.get(i).getHigh();
            double low = candles.get(i - 1).getLow();
            totalRange += Math.abs(high - low) / low;
        }
        double avgRange = totalRange / VOLATILITY_LOOKBACK;
        return avgRange > VOLATILITY_THRESHOLD;
    }

    public static double calculateEMA(List<Candle> candles, int period, int endIndex) {
        if (endIndex - period + 1 < 0) {
            return candles.get(endIndex).close; // Not enough data, fallback to current close
        }

        double k = 2.0 / (period + 1);
        double ema = candles.get(endIndex - period + 1).close; // initial seed

        for (int i = endIndex - period + 2; i <= endIndex; i++) {
            double close = candles.get(i).close;
            ema = close * k + ema * (1 - k);
        }

        return ema;
    }


}
