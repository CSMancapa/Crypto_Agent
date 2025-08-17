package aiagent;

import aiagent.StrategyRegistry;

import java.util.*;

public class PaperTrader {

    private final BinanceClient binanceClient;
    private final CoinProfile profile;
    private final double startingCapital;
    private double capital;
    private final List<Trade> tradeHistory = new ArrayList<>();

    public PaperTrader(BinanceClient binanceClient, CoinProfile profile, double startingCapital) {
        this.binanceClient = binanceClient;
        this.profile = profile;
        this.startingCapital = startingCapital;
        this.capital = startingCapital;
    }

    public void run(String symbol) {
        try {
            List<Candle> candles = binanceClient.fetchRecentCandles(symbol);
            if (candles.size() < 50) {
                System.out.println("⚠️ Not enough candles for trading.");
                return;
            }

            String condition = MarketAnalyzer.classify(candles);
            System.out.println("📊 Market Condition: " + condition);

            Strategy strategy = StrategyRegistry.getStrategy(condition, candles, profile);
            strategy.setStartingCapital(startingCapital);
            strategy.setCapital(capital);
            strategy.setTradeHistory(tradeHistory);  // Track trades across sessions
            strategy.runBacktest();

            // Print updated results
            BacktestReport.generate(tradeHistory, startingCapital, strategy.getCapital());
            this.capital = strategy.getCapital();

        } catch (Exception e) {
            System.err.println("PaperTrader Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
