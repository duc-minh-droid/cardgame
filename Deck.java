import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Deck {
    private final int id;
    private final Queue<Integer> cards = new LinkedList<>();
    // private final List<Integer> cards = new ArrayList<>();
    private final Lock lock = new ReentrantLock();
    // List<Integer> cards = Collections.synchronizedList(c); 

    public Deck(int id) {
        this.id = id;
    }

    public int size() {
        return cards.size();
    }
    public void addCard(int card) {
        // lock.lock();
        // try{
        //     cards.add(card);
        // }finally{
        //     lock.unlock();
        // }
        cards.add(card);
    }

    public Integer drawCard() {
        // lock.lock();
        // try{
        //     return cards.poll();
        // } finally{
        //     lock.unlock();
        // }
        return cards.poll();

    }

    public boolean isEmpty(){
        return cards.isEmpty();
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
}
