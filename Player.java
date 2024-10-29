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
        hand.add(leftDeck.drawCard());
    }

    private void discardCard(int id) {
        int cardToDiscard = hand.remove(id);
        rightDeck.addCard(cardToDiscard);
        System.out.println("Player" + id + "discarded card" + cardToDiscard);
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
    synchronized (leftDeck) {
        System.out.println("Player " + id + " hand before draw: " + hand);

        // Step 1: Draw a card from the left deck
        drawCard();

        synchronized (rightDeck) {
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

            // Step 6: Discard the selected card
            discardCard(discardIndex);

            System.out.println("After: " + hand.toString());
            }
        }
    }


    @Override
    public void run() {
        while (!hasWinningHand() && !GameStatus.isGameWon()) {
            playTurn();
            try{
                Thread.sleep(100);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        
        if (hasWinningHand()){
            System.out.println("Player " + id+ "has a winning hand");
        }
        GameStatus.declareWinner();
    }
}
