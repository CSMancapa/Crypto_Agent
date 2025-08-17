package aiagent;

import java.time.LocalDateTime;

public class Trade {
    public long entryTimestamp;
    public long exitTimestamp;
    public double entryPrice;
    public double exitPrice;
    public double pnl;
    public int entryIndex;
    public int exitIndex;
    public String strategy;
    public boolean isWin;

    public Trade(long entryTimestamp, long exitTimestamp, double entryPrice, double exitPrice,
                 int entryIndex, int exitIndex, String strategy) {
        this.entryTimestamp = entryTimestamp;
        this.exitTimestamp = exitTimestamp;
        this.entryPrice = entryPrice;
        this.exitPrice = exitPrice;
        this.pnl = exitPrice - entryPrice;
        this.isWin = pnl > 0;
        this.entryIndex = entryIndex;
        this.exitIndex = exitIndex;
        this.strategy = strategy;
    }

    public String toString() {
        return String.format("%s | BUY at %.5f (%d) → SELL at %.5f (%d) | P/L: %.4f",
                strategy, entryPrice, entryTimestamp, exitPrice, exitTimestamp, pnl);
    }
}

