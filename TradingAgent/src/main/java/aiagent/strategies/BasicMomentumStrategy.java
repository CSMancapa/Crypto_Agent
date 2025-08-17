package aiagent.strategies;

import aiagent.*;
import aiagent.utils.TrailingStopManager;
import java.util.*;

public class BasicMomentumStrategy extends Strategy {

    public BasicMomentumStrategy(List<Candle> candles, CoinProfile profile) {
        super(candles, profile);
    }

    public static int minCandlesRequired() {
        return 2;
    }

    @Override
    public void runBacktest() {
        if (candles.size() < minCandlesRequired()) {
            System.out.println("⚠️ Not enough candles for Basic Momentum Strategy.");
            return;
        }

        boolean inPosition = false;
        double entryPrice = 0;
        long entryTimestamp = 0;
        int entryIndex = -1;
        TrailingStopManager tsm = null;

        for (int i = 1; i < candles.size(); i++) {
            double price = candles.get(i).close;
            double prev = candles.get(i - 1).close;

            if (!inPosition && price > prev * 1.01) {
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

                    tradeHistory.add(new Trade(entryTimestamp, exitTimestamp, entryPrice, price, entryIndex, exitIndex, "BasicMomentumStrategy"));
                    capital += pnl;

                    System.out.printf("SELL at %.5f on %s | P/L: %.4f%n", price, exitTimestamp, pnl);
                    inPosition = false;
                    tsm = null;
                }
            }
        }

        if (inPosition && entryIndex != -1) {
            Candle last = candles.get(candles.size() - 1);
            double pnl = last.close - entryPrice;
            tradeHistory.add(new Trade(entryTimestamp, last.timestamp, entryPrice, last.close, entryIndex, candles.size() - 1, "BasicMomentumStrategy"));
            capital += pnl;
            System.out.printf("FINAL EXIT at %.5f on %s | P/L: %.4f%n", last.close, last.timestamp, pnl);
        }
    }
}
