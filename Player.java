import java.util.ArrayList;
import java.util.List;

public class Player implements Runnable {
    private final int id;
    private final List<Integer> hand = new ArrayList<>();
    private final Deck leftDeck;
    private final Deck rightDeck;
    // private boolean active = true;

    public Player(int id, Deck leftDeck, Deck rightDeck) {
        this.id = id;
        this.leftDeck = leftDeck;
        this.rightDeck = rightDeck;
    }

    public void addCard(int card) {
        hand.add(card);
    }

    @Override
    public void run() {
        
    }
}
