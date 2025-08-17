package aiagent;

public class Candle {
    public long timestamp;
    public long closeTime;
    public double open, high, low, close, volume;

    public Candle(long timestamp, double open, double high, double low, double close, double volume, long closeTime) {
        this.timestamp = timestamp;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.closeTime = closeTime;
    }
    public Candle(){

    }
    public double getOpen() {
        return open;
    }
    public double getHigh() {
        return high;
    }
    public double getLow() {
        return low;
    }
    public double getClose() {
        return close;
    }
    public double getVolume() {
        return volume;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getCloseTime() {
        return closeTime;
    }

    public void setClose(double closePrice) {
        close = closePrice;
    }

    public void setCloseTime(long closeTime) {
        this.closeTime = closeTime;
    }
}
