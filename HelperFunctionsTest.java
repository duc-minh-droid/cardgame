import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HelperFunctionsTest{

    private String validPackFilePath;
    private String invalidPackFilePath;

    @Before
    public void setUp() throws IOException {
        // Create a valid pack file with 8n cards (e.g., 32 cards for n=4 players)
        validPackFilePath = "valid_pack.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(validPackFilePath))) {
            for (int i = 1; i <= 32; i++) {
                writer.write(String.valueOf(i));
                writer.newLine();
            }
        }

        // Create an invalid pack file with fewer than 8n cards
        invalidPackFilePath = "invalid_pack.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(invalidPackFilePath))) {
            // the pack contains 5 cards
            writer.write("1\n2\n3\n4\n5\n"); 
        }
    }

    }
    @Test 
    public void testReadPack(){
        // Read a valid pack file with 8*n cards (for n=4 players)
        List<Card> pack = HelperFunctions.readPack(validPackFilePath, 4);

        //The pack should not be null and contain 8*n cards
        assertNotNull(pack);
        assertEquals(32,pack.size());

        // Try reading a valid pack file with less than 8*n cards
        List<Card> invalidPack = HelperFunctions.readPack(invalidPackFilePath,4);

        // The pack should be null because the file has fewer than 8n cards
        assertNull(invalidPack);

    }

    @Test
    public void testDistributeCards(){
        // Create players and decks
        List<Player> players = new ArrayList<>();
        List<Deck> decks = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            // Create 4 players and 4 decks
            players.add(new Player(i, new Deck(i), null));
            decks.add(new Deck(i)); 
        }
        
         // Create a pack of 32 cards (8n cards for 4 players)
        List<Card> pack = new ArrayList<>();
        for (int i = 1; i <= 32; i++) {
            pack.add(new Card(i));
        }

        // Distribute cards to players and decks
        HelperFunctions.distributeCards(players, decks, pack);

        // Test if each player and ech deck has 4 cards 
        for(Player player : players){
            assertEquals(4, player.hand.size());
        }
        for(Deck deck : decks){
            assertEquals(4, deck.size());
        }
    }


    @Test
    public void testReadPackFileNotFound() {
        // Try reading a non-existent pack file
        List<Card> pack = HelperFunctions.readPack("non_existent_file.txt", 4);
        
        assertNull(pack); // The pack should be null since the file doesn't exist
    }

    @Test
    public void testInvalidPackSize() {
        // Try reading a pack with invalid size (not 8n cards)
        List<Card> pack = HelperFunctions.readPack(validPackFilePath, 3); // 3 players would require 24 cards (8*3)
        
        assertNull(pack); // The pack should be null because it contains 32 cards, not 24
    }


}