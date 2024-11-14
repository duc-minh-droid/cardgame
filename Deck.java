import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Deck {
    private final int id;
    private final Queue<Integer> cards = new LinkedList<>();
    private final Lock lock = new ReentrantLock();

    public Deck(int id) {
        this.id = id;
    }

    public int size() {
        return cards.size();
    }

    // public boolean isEmpty() {
    //     return cards.isEmpty();
    // }

    public void addCard(int card) {
        lock.lock();
        try{
            cards.add(card);
        }finally{
            lock.unlock();
        }
    }

    public Integer drawCard() {
        lock.lock();
        try{
            return cards.poll();
        } finally{
            lock.unlock();
        }
    }

    public int getId() {
        return id;
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }    
}
