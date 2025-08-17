package aiagent.utils;

import aiagent.Trade;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TradeExporter {

    public static void exportToCSV(List<Trade> trades) {
        // Ensure the directory exists
        String directory = "history";
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdir();
        }

        // Timestamped filename
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String fileName = directory + "/trades_" + timestamp + ".csv";

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("EntryTimestamp,ExitTimestamp,EntryPrice,ExitPrice,PNL,EntryIndex,ExitIndex,Strategy,Win\n");
            for (Trade trade : trades) {
                writer.write(String.format("%d,%d,%.5f,%.5f,%.5f,%d,%d,%s,%b\n",
                        trade.entryTimestamp,
                        trade.exitTimestamp,
                        trade.entryPrice,
                        trade.exitPrice,
                        trade.pnl,
                        trade.entryIndex,
                        trade.exitIndex,
                        trade.strategy,
                        trade.isWin));
            }
            System.out.println("💾 Trade history saved to: " + fileName);
        } catch (IOException e) {
            System.err.println("❌ Failed to write trade history: " + e.getMessage());
        }
    }
}
