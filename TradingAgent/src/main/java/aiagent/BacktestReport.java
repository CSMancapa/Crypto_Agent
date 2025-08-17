package aiagent;

import java.util.*;
public class BacktestReport {

    public static void generate(List<Trade> trades, double startingCapital, double finalCapital) {
        int wins = 0;
        int losses = 0;
        double totalGain = 0;
        double totalLoss = 0;
        double maxDrawdown = 0;
        double peak = startingCapital;
        double equity = startingCapital;

        List<Double> equityCurve = new ArrayList<>();
        equityCurve.add(startingCapital);

        for (Trade trade : trades) {
            double pnl = trade.pnl;
            equity += pnl;
            peak = Math.max(peak, equity);
            maxDrawdown = Math.max(maxDrawdown, peak - equity);
            equityCurve.add(equity);

            if (pnl > 0) {
                wins++;
                totalGain += pnl;
            } else {
                losses++;
                totalLoss += Math.abs(pnl);
            }
        }

        double winRate = trades.size() > 0 ? (wins * 100.0 / trades.size()) : 0;
        double avgGain = wins > 0 ? totalGain / wins : 0;
        double avgLoss = losses > 0 ? totalLoss / losses : 0;
        double profitFactor = totalLoss > 0 ? totalGain / totalLoss : 0;
        double returnPercent = (finalCapital - startingCapital) / startingCapital * 100;

        System.out.println("\n--- PERFORMANCE METRICS ---");
        System.out.printf("Total Return: %.2f%%\n", returnPercent);
        System.out.printf("Win Rate: %.2f%% (%d wins / %d trades)\n", winRate, wins, trades.size());
        System.out.printf("Avg Gain: %.2f | Avg Loss: %.2f\n", avgGain, avgLoss);
        System.out.printf("Profit Factor: %.2f\n", profitFactor);
        System.out.printf("Max Drawdown: %.2f ZAR\n", maxDrawdown);
        System.out.printf("Final Capital: %.2f ZAR\n", finalCapital);
    }
}
