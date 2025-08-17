package aiagent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonTest {
    public static void main(String[] args) throws Exception {
        String json = "{\"symbol\": \"BTCUSDT\", \"price\": \"100.0\"}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);

        System.out.println("Symbol: " + node.get("symbol").asText());
        System.out.println("Price: " + node.get("price").asDouble());
    }
}

