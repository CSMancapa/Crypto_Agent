package aiagent.utils;

import aiagent.Candle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CandleTrackerTest {
    private CandleTracker tracker;
    private List<Candle> testCandles;

    @BeforeEach
    void setUp() {
        tracker = new CandleTracker();
        testCandles = List.of(
                createCandle(1000L, 1.0),
                createCandle(2000L, 1.5),
                createCandle(3000L, 1.2),
                createCandle(4000L, 1.8)
        );
    }

    private Candle createCandle(long closeTime, double closePrice) {
        Candle candle = new Candle();
        candle.setCloseTime(closeTime);
        candle.setClose(closePrice);
        return candle;
    }

    @Test
    void shouldDetectAllCandlesAsNewInitially() {
        List<Candle> newCandles = tracker.getNewCandles(testCandles);
        assertEquals(4, newCandles.size());
    }

    @Test
    void shouldFilterAlreadyProcessedCandles() {
        tracker.updateLastProcessed(2000L);
        List<Candle> newCandles = tracker.getNewCandles(testCandles);

        assertEquals(2, newCandles.size());
        assertEquals(3000L, newCandles.get(0).getCloseTime());
        assertEquals(4000L, newCandles.get(1).getCloseTime());
    }

    @Test
    void shouldHandleEmptyInput() {
        List<Candle> newCandles = tracker.getNewCandles(List.of());
        assertTrue(newCandles.isEmpty());
    }

    @Test
    void shouldMaintainCandleOrder() {
        List<Candle> shuffledCandles = List.of(
                testCandles.get(2),
                testCandles.get(0),
                testCandles.get(3),
                testCandles.get(1)
        );

        List<Candle> newCandles = tracker.getNewCandles(shuffledCandles);
        assertEquals(1000L, newCandles.get(0).getCloseTime());
        assertEquals(2000L, newCandles.get(1).getCloseTime());
    }

    @Test
    void shouldUpdateToLatestProcessedTime() {
        tracker.updateLastProcessed(2500L);
        assertEquals(2500L, tracker.getLastProcessedTime());

        tracker.updateLastProcessed(2000L); // Should ignore older timestamps
        assertEquals(2500L, tracker.getLastProcessedTime());
    }
}