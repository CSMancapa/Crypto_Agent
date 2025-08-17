package aiagent.strategies;

import aiagent.*;
import aiagent.utils.TrailingStopManager;
import java.util.*;

public class MomentumSniperStrategy extends Strategy {
    public MomentumSniperStrategy(List<Candle> candles, CoinProfile profile) {
        super(candles, profile);
    }

    public static int minCandlesRequired() {
        return 25;
    }

    @Override
    public void runBacktest() {
        List<Double> closes = new ArrayList<>();
        for (Candle c : candles) closes.add(c.close);

        List<Double> rsi = Indicators.calculateRSI(closes, 14);

        boolean inPosition = false;
        double entryPrice = 0;
        long entryTimestamp = 0;
        int entryIndex = -1;
        TrailingStopManager tsm = null;

        for (int i = 14; i < candles.size(); i++) {
            double price = candles.get(i).close;
            if (!inPosition && rsi.get(i) > 70) {
                entryPrice = price;
                entryTimestamp = candles.get(i).timestamp;
                entryIndex = i;
                tsm = new TrailingStopManager(price, this.config.trailingStop);
                inPosition = true;
                System.out.printf("SNIPER BUY at %.5f on %s%n", price, entryTimestamp);
            } else if (inPosition && tsm != null) {
                tsm.update(price);
                if (tsm.shouldExit(price)) {
                    double pnl = price - entryPrice;
                    long exitTimestamp = candles.get(i).timestamp;
                    int exitIndex = i;

                    tradeHistory.add(new Trade(entryTimestamp, exitTimestamp, entryPrice, price, entryIndex, exitIndex, "MomentumSniperStrategy"));
                    capital += pnl;

                    System.out.printf("SNIPER SELL at %.5f on %s | P/L: %.4f%n", price, exitTimestamp, pnl);
                    inPosition = false;
                    tsm = null;
                }
            }
        }
    }
}



