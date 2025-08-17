package aiagent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.*;

public class BinanceClient {

    private static final String BASE_URL = "https://api.binance.com";
    private static final String INTERVAL = "4h"; // or "1m", "5m", etc.
    private static final int LIMIT = 100; // Adjust depending on your candle requirements

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Candle> fetchRecentCandles(String symbol) throws IOException, InterruptedException {
        String url = String.format("%s/api/v3/klines?symbol=%s&interval=%s&limit=%d", BASE_URL, symbol.toUpperCase(), INTERVAL, LIMIT);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = mapper.readTree(response.body());

        List<Candle> candles = new ArrayList<>();
        for (JsonNode node : root) {
            try {
                long timestamp = node.get(0).asLong();
                double open = node.get(1).asDouble();
                double high = node.get(2).asDouble();
                double low = node.get(3).asDouble();
                double close = node.get(4).asDouble();
                double volume = node.get(5).asDouble();
                long closeTime = node.get(6).asLong();
                candles.add(new Candle(timestamp, open, high, low, close, volume,  closeTime));
            } catch (Exception e) {
                System.err.println("Skipping malformed candle.");
            }
        }
        return candles;
    }
}
