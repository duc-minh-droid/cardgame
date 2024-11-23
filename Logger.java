import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Logger {

    private final File logFile;
    private final BufferedWriter writer;

    public Logger(String directory, String fileName) {
        File outputDir = new File(directory);
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }
        logFile = new File(outputDir, fileName);
        BufferedWriter tempWriter = null;
        try {
            tempWriter = new BufferedWriter(new FileWriter(logFile, true)); // Append mode
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer = tempWriter;
    }

    public void log(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush(); 
        } catch (IOException e) {
            System.err.println("Error writing log for " + logFile.getName() + ": " + e.getMessage());
        }
    }

    public void log(String message, int informerId) {
        log("player " + informerId + " " + message);
    }

    public String cardsToString(List<Card> cards) {
        List<Integer> cardsValue = new ArrayList<>();
        for (Card card : cards) {
            cardsValue.add(card.getValue());
        }
        return cardsValue.toString().replaceAll("[\\[\\],]", "").trim();
    }

    public void logDeckContents(int id, List<Card> cards) {
        File outputDir = new File("gameOutput");
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }
        File deckFile = new File(outputDir, "deck" + id + "_output.txt");
        try (BufferedWriter deckWriter = new BufferedWriter(new FileWriter(deckFile))) {
            deckWriter.write("deck" + id + " contents: " + cardsToString(cards));
        } catch (IOException e) {
            System.err.println("Error writing log for Deck " + id + ": " + e.getMessage());
        }
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            System.err.println("Error closing log file for " + logFile.getName() + ": " + e.getMessage());
        }
    }
}

