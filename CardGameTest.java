import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class CardGameTest {
    private static final String VALID_FILE_PATH = "valid_pack.txt";
    private static final String INVALID_SIZE_FILE_PATH = "invalid_size_pack.txt";
    private static final String INVALID_FILE_PATH = "txtnon_existent_file.";

    @Before
    public void setUp() throws IOException {
        // Create a valid pack file with 8n cards (e.g., 32 cards for n=4 players)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(VALID_FILE_PATH))) {
            for (int i = 1; i <= 32; i++) {
                writer.write(String.valueOf(i));
                writer.newLine();
            }
        }

        // Create an invalid pack file with fewer than 8n cards
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(INVALID_SIZE_FILE_PATH))) {
            // the pack contains 5 cards
            writer.write("1\n2\n3\n4\n5\n"); 
        }
    }

    @Test 
    public void testReadPack(){
        // Read a valid pack file with 8*n cards (for n=4 players)
        List<Card> pack = CardGame.readPack(VALID_FILE_PATH, 4);

        //The pack should not be null and contain 8*n cards
        assertNotNull(pack);
        assertEquals(32,pack.size());

        // Try reading a valid pack file with less than 8*n cards
        List<Card> invalidPack = CardGame.readPack(INVALID_SIZE_FILE_PATH,4);

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
        CardGame.distributeCards(players, decks, pack);
 
        // Test if each player and ech deck has 4 cards 
        for(Player player : players){
            assertEquals(4, player.getHand().size());
        }
        for(Deck deck : decks){
            assertEquals(4, deck.size());
        }
    }


    @Test
    public void testGetNumberOfPlayersValidInput() {
        // Simulate valid user input for 4 players
        String simulatedInput = "4\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        // Call the method and assert the result
        int numberOfPlayers = CardGame.getNumberOfPlayers();
        assertEquals("Number of players should match user input.", 4, numberOfPlayers);
    }

    @Test
    public void testGetNumberOfPlayersInvalidInput() {
        // Simulate invalid user input followed by a valid one
        String simulatedInput = "invalid\n-1\n3\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        // Call the method and assert the result
        int numberOfPlayers = CardGame.getNumberOfPlayers();
        assertEquals("Number of players should match the first valid input >= 2.", 3, numberOfPlayers);
    }

    @Test
    public void testGetPackLocationValidPath() {
        // Simulate valid user input
        String simulatedInput = VALID_FILE_PATH + "\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        // Call the method and assert the result
        String packLocation = CardGame.getPackLocation();
        assertEquals("Pack location should match the valid file path.", VALID_FILE_PATH, packLocation);
    }

    @Test
    public void testGetPackLocationInvalidPath() {
        // Simulate invalid input followed by a valid file path
        String simulatedInput = INVALID_FILE_PATH + "\n" + VALID_FILE_PATH + "\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        // Call the method and assert the result
        String packLocation = CardGame.getPackLocation();
        assertEquals("Pack location should eventually match the valid file path.", VALID_FILE_PATH, packLocation);
    }

    @Test
    public void testInitializeGame() {
        CardGame game = new CardGame();
        game.initializeGame(4,"valid_pack.txt");
        // Test number of players
        assertEquals("Number of players should be correct.", 4, game.players.size());

        // Test size of each deck
        for (Deck deck : game.decks) {
            assertEquals("Each deck should initially have the correct number of cards.", 4, deck.size());
        }
    }

    @Test
    public void testStartGame() throws Exception {
        // Start the game
        CardGame game = new CardGame();
        game.startGame();

        // Verify each player's thread is running
        for (Player player : game.players) {
            assertTrue("Player thread should be running or runnable.",
                    player.getState() == Thread.State.RUNNABLE || player.getState() == Thread.State.TIMED_WAITING);
        }
        game.endGame();
    }

    
}
