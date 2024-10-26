import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class CardGame {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of players:");
        int n = scanner.nextInt();
        System.out.println("Enter the location of a valid input pack:");
        String packFilePath = scanner.next();

        List<Integer> pack = readPack(packFilePath, n);
        if (pack == null) {
            System.out.println("Invalid pack file. Please provide a valid pack.");
            return;
        }

        List<Player> players = new ArrayList<>();
        List<Deck> decks = new ArrayList<>();

        // Initialize players and decks
        for (int i = 0; i < n; i++) {
            decks.add(new Deck(i + 1));
            players.add(new Player(i + 1, decks.get(i), decks.get((i + 1 < decks.size()) ? (i + 1) : 0)));
        }

        // Distribute cards to players
        distributeCards(players, decks, pack);

        System.out.println("Cards have been distributed.");

        // Start the game threads
        List<Thread> threads = new ArrayList<>();
        for (Player player : players) {
            Thread thread = new Thread(player);
            threads.add(thread);
            thread.start();
        }

        // Wait for all player threads to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Game has ended.");
    }

    private static List<Integer> readPack(String filePath, int n) {
        List<Integer> pack = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                pack.add(Integer.parseInt(line));
            }
        } catch (IOException | NumberFormatException e) {
            return null;
        }
        return pack.size() == 8 * n ? pack : null;
    }

    private static void distributeCards(List<Player> players, List<Deck> decks, List<Integer> pack) {
        int n = players.size();
        Iterator<Integer> iterator = pack.iterator();

        // Distribute cards to players
        for (int i = 0; i < 4 * n; i++) {
            int card = iterator.next();
            players.get((i < n) ? i : (i - n)).addCard(card);
            System.out.println("Distributed card " + card + " to player " + (i % n + 1));
        }

        // Distribute remaining cards to decks
        for (int i = 0; i < 4 * n; i++) {
            int card = iterator.next();
            decks.get((i < n) ? i : (i - n)).addCard(card);
            System.out.println("Distributed card " + card + " to deck " + (i % n + 1));
        }
    }
}

class Player implements Runnable {
    private final int id;
    private final List<Integer> hand = new ArrayList<>();
    private final Deck leftDeck;
    private final Deck rightDeck;
    private boolean active = true;

    public Player(int id, Deck leftDeck, Deck rightDeck) {
        this.id = id;
        this.leftDeck = leftDeck;
        this.rightDeck = rightDeck;
    }

    public void addCard(int card) {
        hand.add(card);
        System.out.println("Player " + id + " received card " + card);
    }

    @Override
    public void run() {
        System.out.println("Player " + id + " has started.");
        try (PrintWriter writer = new PrintWriter(new FileWriter("player" + id + "_output.txt"))) {
            writer.println("player " + id + " initial hand " + handToString());
            while (!GameStatus.isGameWon() && active) {
                if (hand.isEmpty()) {
                    System.out.println("Player " + id + " has no cards to discard.");
                    active = false;
                    break;
                }
                // Draw a card from the left deck
                int drawnCard = leftDeck.drawCard();
                if (drawnCard == -1) {
                    System.out.println("Player " + id + " could not draw a card (deck empty).");
                    active = false;
                    break;
                }
                hand.add(drawnCard);
                System.out.println("Player " + id + " draws a card: " + drawnCard);

                // Discard a card to the right deck
                int discardedCard = discardCard();
                rightDeck.addCard(discardedCard);
                System.out.println("Player " + id + " discards a card: " + discardedCard);
                writer.println("player " + id + " draws a " + drawnCard);
                writer.println("player " + id + " discards a " + discardedCard);
                writer.println("player " + id + " current hand is " + handToString());
                System.out.println("Player " + id + " current hand: " + handToString());

                // Check if the current player has a winning hand
                if (hasWinningHand() && !GameStatus.isGameWon()) {
                    writer.println("player " + id + " wins");
                    System.out.println("Player " + id + " wins!");
                    GameStatus.declareWinner();
                    active = false;
                    break;
                }
            }
            if (!active) {
                writer.println("player " + id + " exits");
                System.out.println("Player " + id + " exits.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean hasWinningHand() {
        return hand.stream().distinct().count() == 1 && !hand.isEmpty();
    }

    private int discardCard() {
        if (hand.isEmpty()) {
            throw new IllegalStateException("Cannot discard from an empty hand");
        }
        // Simulate discarding a card (for simplicity, discard the first card)
        return hand.remove(0);
    }

    private String handToString() {
        StringBuilder sb = new StringBuilder();
        for (int card : hand) {
            sb.append(card).append(" ");
        }
        return sb.toString().trim();
    }
}

class Deck {
    private final int id;
    private final Queue<Integer> cards = new LinkedList<>();
    private final Lock lock = new ReentrantLock();

    public Deck(int id) {
        this.id = id;
    }

    public void addCard(int card) {
        lock.lock();
        try {
            cards.add(card);
            System.out.println("Deck " + id + " received card " + card);
        } finally {
            lock.unlock();
        }
    }

    public int drawCard() {
        lock.lock();
        try {
            if (cards.isEmpty()) {
                return -1; // Indicates no card to draw
            }
            int card = cards.poll();
            System.out.println("Deck " + id + " gives card " + card);
            return card;
        } finally {
            lock.unlock();
        }
    }
}

class GameStatus {
    private static final Lock lock = new ReentrantLock();
    private static final Condition gameWonCondition = lock.newCondition();
    private static boolean gameWon = false;

    public static boolean isGameWon() {
        lock.lock();
        try {
            return gameWon;
        } finally {
            lock.unlock();
        }
    }

    public static void declareWinner() {
        lock.lock();
        try {
            if (!gameWon) {
                gameWon = true;
                gameWonCondition.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }
}
