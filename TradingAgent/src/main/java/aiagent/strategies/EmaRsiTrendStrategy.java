package aiagent.strategies;

import aiagent.*;
import aiagent.utils.TrailingStopManager;
import java.util.*;

public class EmaRsiTrendStrategy extends Strategy {
    public EmaRsiTrendStrategy(List<Candle> candles, CoinProfile profile) {
        super(candles, profile);
    }

    public static int minCandlesRequired() {
        return 50;
    }

    @Override
    public void runBacktest() {
        List<Double> closes = new ArrayList<>();
        for (Candle c : candles) closes.add(c.close);

        List<Double> ema = Indicators.calculateEMA(closes, 20);
        List<Double> rsi = Indicators.calculateRSI(closes, 14);

        boolean inPosition = false;
        double entryPrice = 0;
        long entryTimestamp = 0;
        int entryIndex = -1;
        TrailingStopManager tsm = null;

        for (int i = 20; i < candles.size(); i++) {
            double price = candles.get(i).close;

            if (!inPosition && price > ema.get(i) && rsi.get(i) > 50) {
                entryPrice = price;
                entryTimestamp = candles.get(i).timestamp;
                entryIndex = i;
                tsm = new TrailingStopManager(price, this.config.trailingStop);
                inPosition = true;
                System.out.printf("BUY at %.5f on %s%n", price, entryTimestamp);
            } else if (inPosition && tsm != null) {
                tsm.update(price);
                if (tsm.shouldExit(price)) {
                    double pnl = price - entryPrice;
                    long exitTimestamp = candles.get(i).timestamp;
                    int exitIndex = i;

                    tradeHistory.add(new Trade(entryTimestamp, exitTimestamp, entryPrice, price, entryIndex, exitIndex, "EmaRsiTrendStrategy"));
                    capital += pnl;

                    System.out.printf("SELL at %.5f on %s | P/L: %.4f%n", price, exitTimestamp, pnl);
                    inPosition = false;
                    tsm = null;
                }
            }
        }
    }
}


