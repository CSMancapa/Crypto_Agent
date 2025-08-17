package aiagent;

import java.util.*;

public class Indicators {

    public static List<Double> calculateEMA(List<Double> prices, int period) {
        List<Double> ema = new ArrayList<>();
        double k = 2.0 / (period + 1);
        double sum = 0;

        for (int i = 0; i < prices.size(); i++) {
            if (i < period) {
                sum += prices.get(i);
                ema.add(0.0);
            } else if (i == period) {
                double sma = sum / period;
                ema.set(i - 1, sma);
                ema.add(prices.get(i) * k + sma * (1 - k));
            } else {
                double prevEma = ema.get(i - 1);
                ema.add(prices.get(i) * k + prevEma * (1 - k));
            }
        }
        return ema;
    }

    public static List<Double> calculateRSI(List<Double> prices, int period) {
        List<Double> rsi = new ArrayList<>();
        double gain = 0;
        double loss = 0;

        for (int i = 1; i <= period; i++) {
            double change = prices.get(i) - prices.get(i - 1);
            if (change >= 0) gain += change;
            else loss -= change;
        }

        gain /= period;
        loss /= period;
        double rs = (loss == 0) ? 100 : gain / loss;
        rsi.add(100 - (100 / (1 + rs)));

        for (int i = period + 1; i < prices.size(); i++) {
            double change = prices.get(i) - prices.get(i - 1);
            if (change >= 0) {
                gain = (gain * (period - 1) + change) / period;
                loss = (loss * (period - 1)) / period;
            } else {
                gain = (gain * (period - 1)) / period;
                loss = (loss * (period - 1) - change) / period;
            }
            rs = (loss == 0) ? 100 : gain / loss;
            rsi.add(100 - (100 / (1 + rs)));
        }

        // pad with zeros to match price list length
        while (rsi.size() < prices.size()) rsi.add(0, 0.0);
        return rsi;
    }

    public static List<Double> calculateATR(List<Double> closes, int period) {
        List<Double> atr = new ArrayList<>();
        if (closes.size() < period + 1) return atr;

        // Use placeholder values for indexes before ATR starts
        for (int i = 0; i < period; i++) {
            atr.add(null); // or use atr.add(0.0) if you prefer
        }

        for (int i = period; i < closes.size(); i++) {
            double high = closes.get(i);       // simulate high
            double low = closes.get(i - 1);    // simulate low from prev close
            double prevClose = closes.get(i - 1);
            double tr = Math.max(high - low, Math.max(Math.abs(high - prevClose), Math.abs(low - prevClose)));

            if (atr.size() == period) {
                atr.add(tr); // first ATR value
            } else {
                double prevAtr = atr.get(i - 1);
                atr.add((prevAtr * (period - 1) + tr) / period); // EMA smoothing
            }
        }
        return atr;
    }

}
