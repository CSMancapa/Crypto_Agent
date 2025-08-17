package aiagent;

import aiagent.utils.CandleTracker;
import aiagent.utils.TradeExporter;
import java.util.*;

public class App {
    public static void main(String[] args) {
        final String symbol = "FLOKIUSDT";
        final double startingCapital = 400.0;
        final long durationMs = 1000L * 60 * 60 * 24; // 24 hours
        final long sleepIntervalMs = 1000L * 60 * 5;  // 5 minutes
        final long endTime = System.currentTimeMillis() + durationMs;

        BinanceClient client = new BinanceClient();
        CandleTracker candleTracker = new CandleTracker();
        CoinProfile config = new CoinProfile();
        config.initialCapital = startingCapital;
        config.transactionFee = 0.001;
        config.trailingStop = 0.02;

        List<Trade> allTrades = new ArrayList<>();
        double capital = startingCapital;

        System.out.println("🚀 Starting paper trading session for " + symbol);
        System.out.println("⏰ Duration: 24 hours | Starting capital: $" + startingCapital);

        while (System.currentTimeMillis() < endTime) {
            try {
                // Fetch and process new candles
                List<Candle> freshCandles = client.fetchRecentCandles(symbol);
                List<Candle> newCandles = candleTracker.getNewCandles(freshCandles);

                if (newCandles.isEmpty()) {
                    System.out.println("⏳ No new candles available. Waiting...");
                    Thread.sleep(sleepIntervalMs);
                    continue;
                }

                // Process each new candle
                for (Candle candle : newCandles) {
                    System.out.printf("\n📊 New Candle [%s] O:%.4f H:%.4f L:%.4f C:%.4f%n",
                            new Date(candle.getCloseTime()),
                            candle.getOpen(),
                            candle.getHigh(),
                            candle.getLow(),
                            candle.getClose());

                    // Analyze market condition
                    String marketCondition = MarketAnalyzer.classify(Collections.singletonList(candle));
                    System.out.println("🏷️ Market Condition: " + marketCondition);

                    // Get appropriate strategy
                    Strategy strategy = StrategyRegistry.getStrategy(
                            marketCondition,
                            Collections.singletonList(candle),
                            config
                    );

                    // Execute strategy
                    strategy.setCapital(capital);
                    strategy.setTradeHistory(allTrades);
                    strategy.runBacktest();

                    // Update state
                    capital = strategy.getCapital();
                    allTrades.addAll(strategy.getTrades());
                    candleTracker.updateLastProcessed(candle.getCloseTime());

                    // Log results
                    System.out.printf("💵 Capital: $%.2f | Trades: %d%n",
                            capital,
                            allTrades.size());
                }

                // Export trades periodically
                if (System.currentTimeMillis() % (15 * 60 * 1000) < sleepIntervalMs) {
                    TradeExporter.exportToCSV(allTrades);
                    System.out.println("💾 Saved trade history to CSV");
                }

                // Sleep until next interval
                long remainingTime = Math.max(1000, sleepIntervalMs - (System.currentTimeMillis() % sleepIntervalMs));
                System.out.printf("⏸️ Sleeping for %.1f seconds%n", remainingTime / 1000.0);
                Thread.sleep(remainingTime);

            } catch (Exception e) {
                System.err.println("❌ Error: " + e.getMessage());
                e.printStackTrace();
                try {
                    Thread.sleep(sleepIntervalMs);
                } catch (InterruptedException ignored) {}
            }
        }

        System.out.println("\n✅ 24-Hour Paper Trading Session Complete");
        System.out.printf("💰 Final Capital: $%.2f | Total Trades: %d%n",
                capital,
                allTrades.size());
        TradeExporter.exportToCSV(allTrades);
    }
}