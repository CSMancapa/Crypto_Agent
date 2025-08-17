@RestController
@RequestMapping("/api/trades")
public class TradeController {
    
    @GetMapping
    public List<Trade> getTrades() throws IOException {
        Path path = Paths.get("data/trades.csv");
        return Files.lines(path)
                   .skip(1) // Skip header
                   .map(line -> line.split(","))
                   .map(data -> new Trade(
                       data[0],  // symbol
                       Double.parseDouble(data[1]),  // entryPrice
                       Double.parseDouble(data[2]),  // exitPrice
                       Double.parseDouble(data[3])   // pnl
                   ))
                   .collect(Collectors.toList());
    }
}