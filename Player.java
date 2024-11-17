import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Player extends Thread{
    private final int id;
    private final List<Integer> hand = new ArrayList<>();
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

    public void addCard(int card) {
        hand.add(card);
    }

    public Boolean checkWinningHand() {
        int firstCardValue = hand.get(0);
        for (int i = 1; i < hand.size(); i++) {
            if (hand.get(i) != firstCardValue) {
                return false; 
            }
        }
        return true;
    }

    public synchronized void playTurn() {
        Deck nextPlayerDeck = game.getNextPlayer(this).deck;
        Integer card = deck.drawCard();
        if (card != null) {
            hand.add(card);
            logAction("draws a " + card + " from deck " + deck.getId());
        } else {
            logAction("draws no card as deck is empty");
        }

        int index = findDiscardIndex();
        int cardToDiscard = hand.remove(index);
        nextPlayerDeck.addCard(cardToDiscard);
        logAction("discards a " + cardToDiscard + " to deck " + nextPlayerDeck.getId());

        logAction("current hand " + handToString());
    }

    private int findDiscardIndex() {
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (int card : hand) {
            frequencyMap.put(card, frequencyMap.getOrDefault(card, 0) + 1);
        }

        int preferredCard = hand.get(0);
        int maxFrequency = 0;

        for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() > maxFrequency) {
                preferredCard = entry.getKey();
                maxFrequency = entry.getValue();
            }
        }

        int discardIndex = -1;
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i) != preferredCard) {
                discardIndex = i;
                break;
            }
        }

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
        return hand.toString().replaceAll("[\\[\\],]", "").trim();
    }
}
