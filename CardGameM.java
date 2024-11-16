import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CardGameM {
    List<Player> players;        
    List<Deck> decks;
    List<Integer> pack;
    public AtomicInteger winningPlayer = new AtomicInteger(0);

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
            players.add(new Player(i, decks.get(i), this));
        }
        HelperFunctions.distributeCards(players, decks, pack);
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
        // Assuming you have a list of players
        for (Player player : players) {
            synchronized (player) {
                player.notify();
            }
        }
    }

    public static void main(String[] args) {
        CardGameM game = new CardGameM();

        game.startGame();

        for (Player p : game.players) {
            (new Thread(p)).start();
        }
        System.out.println("Game has ended.");
    }
}
