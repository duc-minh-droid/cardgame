import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.*;

public class CardGameM {
    List<Player> players;        
    List<Deck> decks;
    List<Integer> pack;
    private static final Lock lock = new ReentrantLock();
    private static volatile boolean gameWon = false;
    private static int winningPlayerId = -1;

    public CardGameM() {
        players = new ArrayList<>();
        decks = new ArrayList<>();
    }

    public void startGame() {
        int n = 4;
        pack = HelperFunctions.readPack("four.txt", n);
        for (int i = 0; i < n; i++) {
            decks.add(new Deck(i + 1));
        }
        for (int i = 0; i < n; i++) {
            players.add(new Player(i, decks.get(i), decks.get(i==0?decks.size()-1:i-1), this));
        }
        HelperFunctions.distributeCards(players, decks, pack);
    }

    public synchronized boolean isGameWon() {
        return gameWon;
    }

    public synchronized int getWinningPlayerId() {
        return winningPlayerId;
    }

    public synchronized void declareWinner(int playerId) {
        if (!gameWon) {
            gameWon = true;
            winningPlayerId = playerId;
            System.out.println("Player " + playerId + " has won the game!");
        }
    }

    public static void main(String[] args) {
        CardGameM game = new CardGameM();

        game.startGame();

        // All players start playing
        List<Thread> threads = new ArrayList<>();

        for (Player player : game.players) {
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
}
