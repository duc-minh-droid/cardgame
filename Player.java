import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.*;

public class Player extends Thread {
    private final int id;
    private final List<Integer> hand = new ArrayList<>();
    private final Deck leftDeck;
    private final Deck rightDeck;
    private final CardGameM game;
    private static final Lock turnLock = new ReentrantLock();

    public Player(int id, Deck leftDeck, Deck rightDeck, CardGameM game) {
        this.id = id;
        this.leftDeck = leftDeck;
        this.rightDeck = rightDeck;
        this.game = game;
    }

    public void addCard(int card) {
        hand.add(card);
    }

    private void drawCard() {
        if (leftDeck.isEmpty()) {
            System.out.println("Player " + (id + 1) + " could not draw a card as the deck is empty.");
            return;
        }
        Integer card = leftDeck.drawCard();
        if (card != null) {
            hand.add(card);
            System.out.println("Player " + (id + 1) + " draws a " + card);
        } else {
            System.out.println("Player " + (id + 1) + " could not draw a card as the deck is empty.");
        }
    }

    private void discardCard(int index) {
        // rightDeck.lock();
        // try {
        //     int cardToDiscard = hand.remove(index);
        //     rightDeck.addCard(cardToDiscard);
        //     System.out.println("Player " + (id + 1) + " discards a " + cardToDiscard);
        // } finally {
        //     rightDeck.unlock();
        // }
        int cardToDiscard = hand.remove(index);
        rightDeck.addCard(cardToDiscard);
        System.out.println("Player " + (id + 1) + " discards a " + cardToDiscard);
    }

    public Boolean checkWinningHand() {
        int firstCardValue = hand.get(0);
        for (int i = 1; i < hand.size(); i++) {
            if (hand.get(i) != firstCardValue) {
                return false; // Found a card that is different
            }
        }
        return true;
    }

    public void exitGame() {
        if (!game.isGameWon()) {
            System.out.println("Player " + id + " is exiting the game.");
        }
    }

    public void playTurn() {
        // turnLock.lock();
        if (leftDeck.getId() < rightDeck.getId()) {
            leftDeck.lock();
            rightDeck.lock();
        } else {
            rightDeck.lock();
            leftDeck.lock();
        }
        try {
            if (game.isGameWon()) {
                System.out.println("Player " + (id + 1) + " stops playing because the game has ended.");
                return;
            }

            System.out.println("Player " + (id + 1) + " is playing...");

            drawCard();
            discardCard(findDiscardIndex());
            System.out.println("Player " + (id + 1) + " ends turn with hand: " + hand);
        } finally {
            // turnLock.unlock();
            rightDeck.unlock();
            leftDeck.unlock();
        }
    }
    private int findDiscardIndex() {
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

        return discardIndex;
    }

    @Override
    public void run() {
        while (!game.isGameWon() && !Thread.interrupted()) {
            playTurn();
            if (checkWinningHand()) {
                game.declareWinner(id + 1);
                break;
            }
        }
    }
}