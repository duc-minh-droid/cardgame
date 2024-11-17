import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Deck {
    private final int id;
    private final List<Card> cards = new ArrayList();
    // private final List<Integer> cards = new ArrayList<>();
    // List<Integer> cards = Collections.synchronizedList(c); 
    private Lock lock = new ReentrantLock();

    public Deck(int id) {
        this.id = id;
    }

    public int size() {
        return cards.size();
    }
    public void addCard(Card card) {
        // logDeckState();
        try {
            lock.lock();
            cards.add(card);
        } finally {
            lock.unlock();
        }
    }

    public Card drawCard() {
        // logDeckState();
        try {
            lock.lock();
            return cards.remove(0);
        } finally {
            lock.unlock();
        }

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
        List<Integer> cardsValue = new ArrayList();
        for(Card card : cards){
            cardsValue.add(card.getValue());
        }
        return cardsValue.toString().replaceAll("[\\[\\],]", "").trim();
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
