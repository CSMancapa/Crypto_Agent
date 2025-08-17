package aiagent.strategies;

import aiagent.*;
import aiagent.utils.TrailingStopManager;
import java.util.*;

public class RangeMeanReversionStrategy extends Strategy {

    public RangeMeanReversionStrategy(List<Candle> candles, CoinProfile profile) {
        super(candles, profile);
    }

    public static int minCandlesRequired() {
        return 60;
    }

    @Override
    public void runBacktest() {
        if (candles.size() < minCandlesRequired()) {
            System.out.println("⚠️ Not enough candles for Range Mean Reversion Strategy.");
            return;
        }

        List<Double> closes = new ArrayList<>();
        for (Candle c : candles) closes.add(c.close);

        List<Double> rsi = Indicators.calculateRSI(closes, 14);

        double support = closes.subList(closes.size() - 50, closes.size()).stream().min(Double::compare).orElse(0.0);
        double resistance = closes.subList(closes.size() - 50, closes.size()).stream().max(Double::compare).orElse(0.0);

        boolean inPosition = false;
        double entryPrice = 0;
        long entryTimestamp = 0;
        int entryIndex = -1;
        TrailingStopManager tsm = null;

        for (int i = 14; i < candles.size(); i++) {
            Candle candle = candles.get(i);
            double price = candle.close;

            if (!inPosition && price <= support * 1.01 && rsi.get(i) < 40) {
                entryPrice = price;
                entryTimestamp = candle.timestamp;
                entryIndex = i;
                tsm = new TrailingStopManager(price, this.config.trailingStop);
                inPosition = true;
                System.out.printf("BUY at %.5f on %d%n", price, entryTimestamp);
            } else if (inPosition && tsm != null) {
                tsm.update(price);
                if (tsm.shouldExit(price) || (price >= resistance * 0.99 && rsi.get(i) > 60)) {
                    long exitTimestamp = candle.timestamp;
                    int exitIndex = i;
                    double pnl = price - entryPrice;

                    tradeHistory.add(new Trade(entryTimestamp, exitTimestamp, entryPrice, price, entryIndex, exitIndex, "RangeMeanReversionStrategy"));
                    capital += pnl;

                    System.out.printf("SELL at %.5f on %d | P/L: %.4f%n", price, exitTimestamp, pnl);

                    inPosition = false;
                    tsm = null;
                }
            }
        }

        // Final forced exit if still in position
        if (inPosition && entryIndex != -1) {
            Candle last = candles.get(candles.size() - 1);
            double exitPrice = last.close;
            double pnl = exitPrice - entryPrice;
            int exitIndex = candles.size() - 1;

            tradeHistory.add(new Trade(entryTimestamp, last.timestamp, entryPrice, exitPrice, entryIndex, exitIndex, "RangeMeanReversionStrategy"));
            capital += pnl;

            System.out.printf("FINAL SELL at %.5f on %d | P/L: %.4f%n", exitPrice, last.timestamp, pnl);
        }
    }
}
