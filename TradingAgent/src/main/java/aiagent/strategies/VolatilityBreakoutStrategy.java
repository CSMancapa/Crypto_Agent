package aiagent.strategies;

import aiagent.*;
import aiagent.utils.TrailingStopManager;

import java.util.ArrayList;
import java.util.List;

public class VolatilityBreakoutStrategy extends Strategy {

    public VolatilityBreakoutStrategy(List<Candle> candles, CoinProfile profile) {
        super(candles, profile);
    }

    public static int minCandlesRequired() {
        return 20;
    }

    @Override
    public void runBacktest() {
        if (candles.size() < minCandlesRequired()) {
            System.out.println("⚠️ Not enough candles for Volatility Breakout Strategy.");
            return;
        }

        List<Double> highs = new ArrayList<>();
        List<Double> lows = new ArrayList<>();
        List<Double> closes = new ArrayList<>();

        for (Candle c : candles) {
            highs.add(c.high);
            lows.add(c.low);
            closes.add(c.close);
        }

        List<Double> atrList = Indicators.calculateATR(closes, 14);

        boolean inPosition = false;
        double entryPrice = 0;
        long entryTimestamp = 0;
        int entryIndex = -1;
        TrailingStopManager tsm = null;

        for (int i = 14; i < candles.size(); i++) {
            Candle candle = candles.get(i);
            double price = candle.close;
            double breakoutHigh = closes.get(i - 1);
            double atr = atrList.get(i);

            if (!inPosition && price > breakoutHigh + atr) {
                entryPrice = price;
                entryTimestamp = candle.timestamp;
                entryIndex = i;
                tsm = new TrailingStopManager(price, this.config.trailingStop);
                inPosition = true;
                System.out.printf("BREAKOUT BUY at %.5f on %d%n", price, entryTimestamp);
            } else if (inPosition && tsm != null) {
                tsm.update(price);
                if (tsm.shouldExit(price) || price < entryPrice - atr) {
                    long exitTimestamp = candle.timestamp;
                    int exitIndex = i;
                    double pnl = price - entryPrice;

                    tradeHistory.add(new Trade(entryTimestamp, exitTimestamp, entryPrice, price, entryIndex, exitIndex, "VolatilityBreakoutStrategy"));
                    capital += pnl;

                    System.out.printf("EXIT at %.5f on %d | P/L: %.4f%n", price, exitTimestamp, pnl);

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

            tradeHistory.add(new Trade(entryTimestamp, last.timestamp, entryPrice, exitPrice, entryIndex, exitIndex, "VolatilityBreakoutStrategy"));
            capital += pnl;

            System.out.printf("FINAL EXIT at %.5f on %d | P/L: %.4f%n", exitPrice, last.timestamp, pnl);
        }
    }
}

