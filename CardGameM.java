import java.util.ArrayList;
import java.util.List;

public class CardGameM {
    public static void main(String[] args) {
        List<Player> players = new ArrayList<>();
        List<Deck> decks = new ArrayList<>();

        // Get user input 
        int n = 4;
        List<Integer> pack = HelperFunctions.readPack("four.txt", n);

        // Init decks
        for (int i = 0; i < n; i++) {
            decks.add(new Deck(i + 1));
        }

        // Init players
        for (int i = 0; i < n; i++) {
            players.add(new Player(i, decks.get(i), decks.get(i==0?decks.size()-1:i-1)));
        }

        // Distribute cards
        HelperFunctions.distributeCards(players, decks, pack);

        // All players start playing
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
}
