import java.util.ArrayList;
import java.util.List;

public class CardGameM {
    public static void main(String[] args) {
        List<Player> players = new ArrayList<>();
        List<Deck> decks = new ArrayList<>();

        int n = 4;

        // Init decks
        for (int i = 0; i < n; i++) {
            decks.add(new Deck(i + 1));
        }

        // Init players
        for (int i = 0; i < n; i++) {
            players.add(new Player(i, decks.get(i), decks.get(i==0?decks.size()-1:i-1)));
            int j = i+1;
            System.out.println(
                    "Player " + j + " has left deck: " + decks.get(i).getId() + " and right deck: "
                            + decks.get(i + 1 < n ? i + 1 : 0).getId());
        }
    }
}
