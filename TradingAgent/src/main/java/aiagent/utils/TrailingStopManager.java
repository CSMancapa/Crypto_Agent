package aiagent.utils;

public class TrailingStopManager {
    private double highestPrice;
    private final double trailPercent;

    public TrailingStopManager(double entryPrice, double trailPercent) {
        this.highestPrice = entryPrice;
        this.trailPercent = trailPercent;
    }

    public void update(double currentPrice) {
        if (currentPrice > highestPrice) {
            highestPrice = currentPrice;
        }
    }

    public double getTrailingStop() {
        return highestPrice * (1 - trailPercent);
    }

    public boolean shouldExit(double currentPrice) {
        return currentPrice < getTrailingStop();
    }
}
