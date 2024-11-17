import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Player extends Thread{
    private final int id;
    private final List<Card> hand = new ArrayList<>();
    public final Deck deck;
    private final CardGameM game;
    private final File logFile;


    public Player(int id, Deck deck, CardGameM game) {
        this.id = id;
        this.deck = deck;
        this.game = game;

        File outputDir = new File("playerOutput");
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }
        logFile = new File(outputDir, "player" + (id + 1) + "_output.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile))) {
            writer.write("");
        } catch (IOException e) {
            System.err.println("Error clearing log file for Player " + (id + 1) + ": " + e.getMessage());
        }
    }

    public void logInitialHand() {
        logAction("initial hand " + handToString());
    }

    public void addCard(Card card) {
        hand.add(card);
    }

    public Boolean checkWinningHand() {
        int firstCardValue = hand.get(0).getValue();
        for (int i = 1; i < hand.size(); i++) {
            if (hand.get(i).getValue() != firstCardValue) {
                return false; 
            }
        }
        return true;
    }

    public synchronized void playTurn() {
        Deck nextPlayerDeck = game.getNextPlayer(this).deck;
        Card drawedCard = deck.drawCard();
        if (drawedCard != null) {
            hand.add(drawedCard);
            logAction("draws a " + drawedCard.getValue() + " from deck " + deck.getId());
        } else {
            logAction("draws no card as deck is empty");
        }

        int index = findDiscardIndex();
        Card cardToDiscard = hand.remove(index);
        nextPlayerDeck.addCard(cardToDiscard);
        logAction("discards a " + cardToDiscard.getValue() + " to deck " + nextPlayerDeck.getId());

        logAction("current hand " + handToString());
    }

    private int findDiscardIndex() {
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (Card card : hand) {
            frequencyMap.put(card.getValue(), frequencyMap.getOrDefault(card.getValue(), 0) + 1);
        }

        // Card preferredCard = hand.get(0);
        // int maxFrequency = 0;

        // for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
        //     if (entry.getValue() > maxFrequency) {
        //         preferredCard = entry.getKey();
        //         maxFrequency = entry.getValue();
        //     }
        // }

        int preferredValue = hand.get(0).getValue();
        int maxFrequency = 0;
        for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() > maxFrequency) {
                preferredValue = entry.getKey();
                maxFrequency = entry.getValue();
            }
        }
        
        int discardIndex = -1;
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).getValue() != preferredValue) {
                discardIndex = i;
                break;
                
            }
        }

        // for (int i = 0; i < hand.size(); i++) {
        //     if (hand.get(i) != preferredCard) {
        //         discardIndex = i;
        //         break;
        //     }
        // }

        if (discardIndex == -1) {
            discardIndex = 0;
        }

        return discardIndex;
    }

    @Override
    public void run() {
        try {
            while (game.winningPlayer.get() == 0) {

                if (deck.isEmpty()) {
                    synchronized (this) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                } else {
                    playTurn();
                    if (checkWinningHand()) {
                        if (game.winningPlayer.compareAndSet(0, id + 1)) {
                            logAction("wins");
                            break;
                        }
                    }
                }
            }
        } finally {
            if (game.winningPlayer.get() == id + 1) {
                logAction("exits");
                logAction("final hand: " + handToString());
            } else {
                int winnerId = game.winningPlayer.get();
                logAction("has informed player " + (id + 1) + " that player " + winnerId + " has won", winnerId);
                logAction("exits");
                logAction("hand: " + handToString());
            }

            // Final notify to prevent deadlock
            synchronized (game.getNextPlayer(this)) {
                game.getNextPlayer(this).notify();
            }
        }
    }


    private synchronized void logAction(String message) {
        logAction(message, id + 1); // Default to current player
    }
    
    private synchronized void logAction(String message, int informerId) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write("player " + informerId + " " + message);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing log for Player " + (id + 1) + ": " + e.getMessage());
        }
    }

    private String handToString() {
        List<Integer> handValue = new ArrayList();
        for(Card card : hand){
            handValue.add(card.getValue());
        }

        return handValue.toString().replaceAll("[\\[\\],]", "").trim();
    }
}
