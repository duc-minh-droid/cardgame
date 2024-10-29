import java.io.*;
import java.util.*;

public class HelperFunctions {
    public static List<Integer> readPack(String filePath, int n) {
        List<Integer> pack = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                pack.add(Integer.parseInt(line));
            }
        } catch (IOException | NumberFormatException e) {
            return null; // Return null if an error occurs
        }
        return pack.size() == 8 * n ? pack : null; // Check if the list size is as expected
    }

    public static void distributeCards(List<Player> players, List<Deck> decks, List<Integer> pack) {
        int n = players.size();
        Iterator<Integer> iterator = pack.iterator();

        // Distribute cards to players
        for (int i = 0; i < 4 * n; i++) {
            int card = iterator.next();
            players.get(i % n).addCard(card);
        }

        // Distribute remaining cards to decks
        for (int i = 0; i < 4 * n; i++) {
            int card = iterator.next();
            decks.get(i % n).addCard(card);
        }
    }
    
}
