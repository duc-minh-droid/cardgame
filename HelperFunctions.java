import java.io.*;
import java.util.*;

public class HelperFunctions {
    public static List<Card> readPack(String filePath, int n) {
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

    public static void distributeCards(List<Player> players, List<Deck> decks, List<Card> pack) {
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
    
}
