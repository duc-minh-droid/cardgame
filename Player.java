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
        logFile = new File(outputDir, "player" + (id + 1) + ".txt");

        // Clear existing content in the log file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile))) {
            // Writing an empty string clears the file
            writer.write("");
        } catch (IOException e) {
            System.err.println("Error clearing log file for Player " + (id + 1) + ": " + e.getMessage());
        }
    }

    public void addCard(int card) {
        hand.add(card);
    }

    private synchronized void drawCard() {
        if (deck.isEmpty()) {
            logAction("Could not draw a card as the deck is empty.");
            return;
        }
        Integer card = deck.drawCard();
        if (card != null) {
            hand.add(card);
            logAction("Draws a " + card);
        } else {
            logAction("Could not draw a card as the deck is empty.");
        }
    }

    private synchronized void discardCard(int index) {
        int cardToDiscard = hand.remove(index);
        game.getNextPlayer(this).deck.addCard(cardToDiscard);
        logAction("Discards a " + cardToDiscard);
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

    public void playTurn() {
        drawCard();
        discardCard(findDiscardIndex());
        logAction("Ends turn with hand: " + hand);
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
        while (game.winningPlayer.get() == 0) {
            if (checkWinningHand()) {
                if (game.winningPlayer.compareAndSet(0, id + 1)) {
                    logAction("Wins the game!");
                    game.notifyAllPlayers();
                    break;
                }
            } else if (deck.isEmpty()) {
                synchronized (this) {
                    try {
                        logAction("Waiting for the deck to be refilled.");
                        wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                playTurn();
                synchronized (game.getNextPlayer(this)) {
                    game.getNextPlayer(this).notify();
                }
            }
        }
        
        // Log game end for the player
        logAction("Game has ended.");
        
        // Notify the next player to prevent deadlock
        synchronized (game.getNextPlayer(this)) {
            game.getNextPlayer(this).notify();
        }
    }


    private synchronized void logAction(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write("Player " + (id + 1) + ": " + message);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing log for Player " + (id + 1) + ": " + e.getMessage());
        }
    }
}
