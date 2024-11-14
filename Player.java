import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private void drawCard() {
        leftDeck.lock();
        try{
            Integer card = leftDeck.drawCard();
            if (card != null) {
                hand.add(card);
                System.out.println("Player " + (id + 1) + " draw a " + card);
            } else {
                System.out.println("Player " + (id + 1) + " could not draw a card as the deck is empty.");
            }
        } finally{
            leftDeck.unlock();
        }
    }

    private void discardCard(int id) {
        rightDeck.lock();
        try{
            int cardToDiscard = hand.remove(id);
            rightDeck.addCard(cardToDiscard);
            System.out.println("Player " + (id + 1) + " discard a " + cardToDiscard);

        } finally{
            rightDeck.unlock();
        }
    }

    public Boolean hasWinningHand() {
        int firstCardValue = hand.get(0);
        for (int i = 1; i < hand.size(); i++) {
            if (hand.get(i) != firstCardValue) {
                return false; // Found a card that is different
            }
        }
        return true;
    }

    public void playTurn() {
        if (GameStatus.isGameWon()) {
            System.out.println("Player " + (id + 1) + " stops playing because the game has ended.");
            return;
        }

        System.out.println("Player " + (id + 1) + " is playing...\n");
        // synchronized (leftDeck) {
            
            // Step 1: Draw a card from the left deck
            if (GameStatus.isGameWon() || Thread.currentThread().isInterrupted()) {
                return;
            }
        
            synchronized (leftDeck) {
                // Game status check before drawing a card
                if (GameStatus.isGameWon() || Thread.currentThread().isInterrupted()) {
                    return;
                }
            }
            drawCard();

            // synchronized (rightDeck) {
                // Step 2: Count the frequency of each card in the hand
                Map<Integer, Integer> frequencyMap = new HashMap<>();
                for (int card : hand) {
                    frequencyMap.put(card, frequencyMap.getOrDefault(card, 0) + 1);
                }

                // Step 3: Determine the most frequent card value
                int preferredCard = hand.get(0);
                int maxFrequency = 0;

                for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
                    if (entry.getValue() > maxFrequency) {
                        preferredCard = entry.getKey();
                        maxFrequency = entry.getValue();
                    }
                }

                // Step 4: Find a card to discard that is not the preferred card
                int discardIndex = -1;
                for (int i = 0; i < hand.size(); i++) {
                    if (hand.get(i) != preferredCard) {
                        discardIndex = i;
                        break;
                    }
                }

                // Step 5: If no non-preferred card found, discard the first card
                if (discardIndex == -1) {
                    discardIndex = 0;
                }
                
                if (GameStatus.isGameWon() || Thread.currentThread().isInterrupted()) {
                    return;
                }
        
                discardCard(discardIndex);
                System.out.println("Player " + (id + 1) + " ends turn with hand: " + hand);
            // }
        }
    // }

    @Override
    public void run() {
        while (!hasWinningHand() && !GameStatus.isGameWon()) {
            playTurn();
        }
        if (hasWinningHand()) {
            GameStatus.declareWinner(id + 1);
        }
    }
}

