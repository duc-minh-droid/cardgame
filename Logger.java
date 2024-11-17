import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Logger {

    private final File logFile;

    public Logger(String directory, String fileName) {
        File outputDir = new File(directory);
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }
        logFile = new File(outputDir, fileName);

        // Clear existing content
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile))) {
            writer.write(""); // Clear file
        } catch (IOException e) {
            System.err.println("Error clearing log file for " + fileName + ": " + e.getMessage());
        }
    }

    public void log(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing log for " + logFile.getName() + ": " + e.getMessage());
        }
    }

    public void log(String message, int informerId) {
        log("player " + informerId + " " + message);
    }

    public String cardsToString(List<Card> cards) {
        List<Integer> cardsValue = new ArrayList();
        for(Card card : cards){
            cardsValue.add(card.getValue());
        }
        return cardsValue.toString().replaceAll("[\\[\\],]", "").trim();
    }

    public void logDeckContents(int id, List<Card> cards) {
        File outputDir = new File("gameOutput");
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }
        File logFile = new File(outputDir, "deck" + id + "_output.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile))) {
            writer.write("deck" + id + " contents: " + cardsToString(cards));
        } catch (IOException e) {
            System.err.println("Error writing log for Deck " + id + ": " + e.getMessage());
        }
    }
}
