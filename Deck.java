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
    private Lock lock = new ReentrantLock();

    public Deck(int id) {
        this.id = id;
    }

    public int size() {
        return cards.size();
    }
    public void addCard(Card card) {
        try {
            lock.lock();
            cards.add(card);
        } finally {
            lock.unlock();
        }
    }

    public Card drawCard() {
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
        Logger logger = new Logger("gameOutput", "player" + (id + 1) + "_output.txt");
        logger.logDeckContents(id, cards);
    }
}
