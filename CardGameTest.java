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
    private static final String FILE_PATH = "pack.txt";


    @Before
    public void setUp() throws IOException {
        // Create a valid pack file with 8n cards (e.g., 32 cards for n=4 players)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (int i = 1; i <= 32; i++) {
                writer.write(String.valueOf(i));
                writer.newLine();
            }
        }
    }

    @Test 
    public void testReadPack(){
        // Read a valid pack file with 8*n cards (for n=4 players)
        List<Card> pack = CardGame.readPack(FILE_PATH, 4);

        //The pack should not be null and contain 8*n cards
        assertNotNull(pack);
        assertEquals(32,pack.size());
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
    public void testGetNumberOfPlayers() {
        // Simulate valid user input for 4 players
        String simulatedInput = "4\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        // Call the method and assert the result
        int numberOfPlayers = CardGame.getNumberOfPlayers();
        assertEquals("Number of players should match user input.", 4, numberOfPlayers);
    }

    @Test
    public void testGetPackLocation() {
        // Simulate valid user input
        String simulatedInput = FILE_PATH + "\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        // Call the method and assert the result
        String packLocation = CardGame.getPackLocation();
        assertEquals("Pack location should match the valid file path.", FILE_PATH, packLocation);
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
