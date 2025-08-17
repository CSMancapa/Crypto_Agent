package aiagent;

import java.util.*;

public abstract class Strategy {
    protected List<Candle> candles;
    protected CoinProfile config;
    protected double startingCapital = 40.0;
    protected double capital = startingCapital;
    protected double positionSize = 1.0; // Will calculate per trade
    protected List<Trade> tradeHistory = new ArrayList<>();
    protected double riskPerTrade = 0.02; // 2% of capital

    public Strategy(List<Candle> candles, CoinProfile config) {
        this.candles = candles;
        this.config = config;
    }

    protected double calculatePositionSize(double capital, double entryPrice, double stopLossPrice) {
        double riskAmount = capital * riskPerTrade;
        double stopDistance = Math.abs(entryPrice - stopLossPrice);
        return stopDistance > 0 ? riskAmount / stopDistance : 0;
    }

    public List<Trade> getTrades() {
        return tradeHistory;
    }

    public double getCapital() {
        return capital;
    }

    public void setStartingCapital(double capital) {
        this.startingCapital = capital;
        this.capital = capital;
    }


    public void setTradeHistory(List<Trade> trades) {
        this.tradeHistory = trades;
    }

    public void setCapital(double capital) {
        this.capital = capital;
    }


    public abstract void runBacktest();


    // Utility method for normalized position sizing, if needed
    protected double calculatePositionSize(double balance, double entryPrice) {
        return balance / entryPrice;
    }

    // Optional: Add common helper methods here (e.g., for logging, stats)
}
