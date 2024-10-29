import java.util.ArrayList;
import java.util.List;

public class CardGameM {
    public static void main(String[] args) {
        List<Player> players = new ArrayList<>();
        List<Deck> decks = new ArrayList<>();
        List<Thread> playThreads = new ArrayList<>(); 

        // Get user input 
        int n = 4;
        List<Integer> pack = HelperFunctions.readPack("four.txt", n);
        System.out.println(pack.size());

        // Init decks
        for (int i = 0; i < n; i++) {
            decks.add(new Deck(i + 1));
        }

        // Init players
        for (int i = 0; i < n; i++) {
            Player player = new Player(i + 1, decks.get(i), decks.get(i == 0 ? decks.size() - 1 : i - 1) );
            players.add(player);
            Thread playerThread = new Thread(player, "Player-" + (i + 1));
            playThreads.add(playerThread);
        }

        // Distribute cards
        HelperFunctions.distributeCards(players, decks, pack);

        for(Thread thread : playThreads){
            thread.start();
        }

        for(Thread thread : playThreads){
            try{
                thread.join();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }   
        
        System.out.println("Game over");
        }
}
