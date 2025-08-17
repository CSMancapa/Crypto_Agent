package aiagent;

import aiagent.strategies.*;
import java.util.*;

public class StrategyRegistry {

    public static Strategy getStrategy(String condition, List<Candle> candles, CoinProfile config) {
        int candleCount = candles.size();

        switch (condition) {
            case "trending_down":
            case "trending_up":
                if (candleCount >= EmaRsiTrendStrategy.minCandlesRequired()) {
                    return new EmaRsiTrendStrategy(candles, config);
                }
                break;

            case "ranging":
                if (candleCount >= RangeMeanReversionStrategy.minCandlesRequired()) {
                    return new RangeMeanReversionStrategy(candles, config);
                }
                break;


            case "volatile":
            case "volatile_trending_up":
                if (candleCount >= VolatilityBreakoutStrategy.minCandlesRequired()) {
                    return new VolatilityBreakoutStrategy(candles, config);
                }
                break;

            case "pump_and_dump":
                if (candleCount >= MomentumSniperStrategy.minCandlesRequired()) {
                    return new MomentumSniperStrategy(candles, config);
                }
                break;
        }

        System.out.println("⚠️ Not enough candles for advanced strategy. Using VolatilityBreakoutStrategy.");
        return new VolatilityBreakoutStrategy(candles, config);
    }
}

