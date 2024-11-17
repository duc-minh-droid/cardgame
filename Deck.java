import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Deck {
    private final int id;
    private final List<Integer> cards = new ArrayList();
    // private final List<Integer> cards = new ArrayList<>();
    // List<Integer> cards = Collections.synchronizedList(c); 

    public Deck(int id) {
        this.id = id;
    }

    public int size() {
        return cards.size();
    }
    public void addCard(int card) {
        // logDeckState();
        cards.add(card);
    }

    public Integer drawCard() {
        // logDeckState();
        return cards.remove(0);

    }

    public boolean isEmpty(){
        return cards.isEmpty();
    }

    public int getId() {
        return id;
    }

    public void logDeckContents() {
        File outputDir = new File("deckOutput");
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }
        File logFile = new File(outputDir, "deck" + id + "_output.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile))) {
            writer.write("deck" + id + " contents: " + cardsToString());
        } catch (IOException e) {
            System.err.println("Error writing log for Deck " + id + ": " + e.getMessage());
        }
    }

    private String cardsToString() {
        return cards.toString().replaceAll("[\\[\\],]", "").trim();
    }

    private void logDeckState() {
        File outputDir = new File("deckOutput");
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }

        File logFile = new File(outputDir, "deck" + id + "_output.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write("deck" + id + " contents: " + cardsToString());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing log for Deck " + id + ": " + e.getMessage());
        }
    }
}
