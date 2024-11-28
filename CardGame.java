import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.*;
import java.util.*;

public class CardGame {
    public List<Player> players;        
    public List<Deck> decks;
    public List<Card> pack;
    public AtomicInteger winningPlayer = new AtomicInteger(0);

    public CardGame() {
        players = new ArrayList<>();
        decks = new ArrayList<>();
    }

    public static int getNumberOfPlayers() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the number of players: ");
        while (true) { 
            try {
                String input = scanner.nextLine();
                int n = Integer.parseInt(input);
                if (n >= 2) {
                    return n;
                } else {
                    System.out.println("The game requires at least 2 players.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
    }
    
    public static String getPackLocation(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter location of pack to load ");
        while (true) { 
            String filePath = scanner.nextLine();
            File file = new File(filePath);
            if (file.exists()) {
                return filePath; 
            } else {
                System.out.println("Invalid file path.");
            }
        }
    }

    public List<Card> readPack(String filePath, int n) {
        List<Card> pack = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                Card newCard = new Card(Integer.valueOf(line));
                pack.add(newCard);
            }
        } catch (IOException | NumberFormatException e) {
            return null; // Return null if an error occurs
        }
        Collections.shuffle(pack);
        return pack.size() == 8 * n ? pack : null; // Check if the list size is as expected

    }
    public void distributeCards(List<Player> players, List<Deck> decks, List<Card> pack) {
        int n = players.size();
        Iterator<Card> iterator = pack.iterator();

        // Distribute cards to players
        for (int i = 0; i < 4 * n; i++) {
            Card card = iterator.next();
            players.get(i % n).addCard(card);
        }

        // Distribute remaining cards to decks
        for (int i = 0; i < 4 * n; i++) {
            Card card = iterator.next();
            decks.get(i % n).addCard(card);
        }
    }
    
    public void initializeGame(int n, String filePath) {
        pack = readPack(filePath, n);
        for (int i = 1; i <= n; i++) {
            decks.add(new Deck(i));
        }
        for (int i = 1; i <= n; i++) {
            players.add(new Player(i, decks.get(i-1), this));
        }
        distributeCards(players, decks, pack);
        for (Player player : players) {
            player.logInitialHand();
        }
    }

    public Player getNextPlayer(Player p) {
        int i = players.indexOf(p) + 1;
        if (i > players.size() - 1) {
            return players.get(0);
        } else {
            return players.get(i);
        }
    }

    public void notifyAllPlayers() {
        // First interrupt all other players
        for (Player player : players) {
            if (player != Thread.currentThread()) {
                player.interrupt();
                synchronized (player) {
                    player.notify();  // Wake up any waiting threads
                }
            }
        }
    }

    public void logDecks() {
        for (Deck deck : decks) {
            deck.logDeckContents();
        }
    }

    public void startGame() {
        for (Player p : players) {
            p.start();
        }
    }

    public void endGame() throws Exception {
        for (Player p : players) {
            p.join();
        }
        logDecks();
        System.out.println("Player " + (winningPlayer.get()) + " wins!");
    }


    public static void main(String[] args) throws Exception {
        CardGame game = new CardGame();
        game.initializeGame(getNumberOfPlayers(), getPackLocation());
        game.startGame();
        game.endGame();
    }
}
