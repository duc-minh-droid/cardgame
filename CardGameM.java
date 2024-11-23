import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class CardGameM {
    List<Player> players;        
    List<Deck> decks;
    List<Card> pack;
    public AtomicInteger winningPlayer = new AtomicInteger(0);

    public CardGameM() {
        players = new ArrayList<>();
        decks = new ArrayList<>();
    }

    public static int getNumberOfPlayers(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the number of players: ");
        int noOfPlayer = 0;
        while (noOfPlayer <= 1) { 
            try {
                String input = scanner.nextLine();
                int n = Integer.parseInt(input);
                if(n >= 2){
                    noOfPlayer = n;
                    break;
                } else{
                    System.out.println("The game required at least 2 players");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
        return noOfPlayer;
    }
    public void initializeGame() {
        int n = getNumberOfPlayers();
        pack = HelperFunctions.readPack("four.txt", n);
        for (int i = 1; i <= n; i++) {
            decks.add(new Deck(i));
        }
        for (int i = 1; i <= n; i++) {
            players.add(new Player(i, decks.get(i-1), this));
        }
        HelperFunctions.distributeCards(players, decks, pack);
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
        CardGameM game = new CardGameM();
        game.initializeGame();
        game.startGame();
        game.endGame();
    }
}
