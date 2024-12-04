import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Logger {

    private final File logFile;
    private BufferedWriter writer;

    public Logger(String directory, String fileName, boolean clearFile) {
        File outputDir = new File(directory);
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }
        logFile = new File(outputDir, fileName);
        try {
            if (clearFile) {
                BufferedWriter clearWriter = new BufferedWriter(new FileWriter(logFile, false));
                clearWriter.write(""); // Clear file content
                clearWriter.close();
            }
            writer = new BufferedWriter(new FileWriter(logFile, true)); // Append mode
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}

