import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerTest {
    private static final String OUTPUT_DIR = "gameOutput";
    private CardGame game;
    private List<Deck> decks;
    private List<Player> players;
    private File logDirectory;

    @Before
    public void setUp() {
        logDirectory = new File(OUTPUT_DIR);
    }

    @After
    public void deleteOutput() {
        if (logDirectory.exists()) {
            deleteDirectory(logDirectory);
        }
    }

    // Helper method to create a test pack of cards
    private List<Card> createTestPack(int playerCount, int cardsPerPlayer) {
        List<Card> pack = new ArrayList<>();
        for (int i = 0; i < playerCount * cardsPerPlayer; i++) {
            pack.add(new Card(i + 1));
        }
        return pack;
    }

    @Test
    public void testPlayerInitialiation() {
        game = new CardGame();
        game.decks.add(new Deck(1));
        Player player1 = new Player(1, game.decks.get(0), game);
        game.players.add(player1);
        
        assertEquals(1, player1.getId());
    }

    @Test
    public void testLogInitialHand() {
        // Create a game with 2 players
        game = new CardGame();
        List<Card> testPack = createTestPack(2, 8);
        
        // Mock pack reading and distribution
        game.pack = testPack;
        game.decks.add(new Deck(1));
        game.decks.add(new Deck(2));
        
        Player player1 = new Player(1, game.decks.get(0), game);
        game.players.add(player1);
        
        // Add some cards to hand
        player1.addCard(new Card(1));
        player1.addCard(new Card(1));
        player1.logInitialHand();

        // Verify log file content
        String logContent = readLogFile(new File(OUTPUT_DIR, "player1_output.txt"));
        assertTrue(logContent.contains("initial hand 1 1"));
    }

    @Test
    public void testCheckWinningHand() {
        // Create a game with  players
        game = new CardGame();
        
        game.decks.add(new Deck(1));

        
        Player player1 = new Player(1, game.decks.get(0), game);
        game.players.add(player1);
        
        // Add matching cards
        player1.addCard(new Card(1));
        player1.addCard(new Card(1));
        player1.addCard(new Card(1));
        assertTrue(player1.checkWinningHand());

        // Add a different card
        player1.addCard(new Card(2));
        assertFalse(player1.checkWinningHand());
    }

    @Test
    public void testPlayTurn() {
        // Create a game with 2 players
        game = new CardGame();
        List<Card> testPack = createTestPack(2, 8);
        
        // Mock pack reading and distribution
        game.pack = testPack;
        Deck deck1 = new Deck(1);
        Deck deck2 = new Deck(2);
        game.decks.add(deck1);
        game.decks.add(deck2);
        
        // Manually add cards to deck1
        deck1.addCard(new Card(5));
        deck1.addCard(new Card(6));
        
        Player player1 = new Player(1, deck1, game);
        Player player2 = new Player(2, deck2, game);
        game.players.add(player1);
        game.players.add(player2);

        // Ensure initial conditions
        assertTrue(player1.getHand().isEmpty());
        assertFalse(player1.getDeck().isEmpty());

        // Simulate a turn
        player1.playTurn();

        // Verify log file
        String logContent = readLogFile(new File(OUTPUT_DIR, "player1_output.txt"));
        assertTrue(logContent.contains("draws a"));
        assertTrue(logContent.contains("discards a"));
        assertTrue(logContent.contains("current hand"));
        
        // Verify hand changes
        assertEquals(1, player1.getHand().size());
        assertFalse(player2.getDeck().isEmpty());
    }

    // Helper method to create a temporary pack file
    private String createPackFile(List<Card> cards) {
        try {
            File tempPack = File.createTempFile("testpack", ".txt");
            tempPack.deleteOnExit();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempPack))) {
                for (Card card : cards) {
                    writer.write(String.valueOf(card.getValue()));
                    writer.newLine();
                }
            }

            return tempPack.getAbsolutePath();
        } catch (IOException e) {
            fail("Could not create temporary pack file");
            return null;
        }
    }

    // Utility method to read log file contents
    private String readLogFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
            return content.toString();
        } catch (IOException e) {
            fail("Could not read log file: " + e.getMessage());
            return "";
        }
    }

    // Utility method to recursively delete directory
    private void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }

    @Test
    public void testRun() {
        // Create a game with at least 2 players
        game = new CardGame();

        // Create decks and players
        Deck deck1 = new Deck(1);
        Deck deck2 = new Deck(2);
        game.decks.add(deck1);
        game.decks.add(deck2);

        // Create the player to test
        Player player = new Player(1, deck1, game);
        
        // Set up a winning hand for the player
        player.getHand().add(new Card(10));
        player.getHand().add(new Card(10));
        player.getHand().add(new Card(10));
        player.getHand().add(new Card(10));

        // Simulate another player in the game
        Player player2 = new Player(2, deck2, game);
        game.players.add(player);
        game.players.add(player2);

        // Prepare decks with some cards to prevent immediate emptiness
        deck1.addCard(new Card(5));
        deck2.addCard(new Card(5));

        // Start the player's thread
        player.start();

        // Wait for the thread to finish
        try {
            player.join(1000); // Add a timeout to prevent hanging
        } catch (InterruptedException e) {
            fail("Thread was interrupted");
        }

        // Verify that the player wins
        assertEquals(1, game.winningPlayer.get());

        // Verify log files
        File winnerLogFile = new File(OUTPUT_DIR, "player1_output.txt");
        assertTrue("Winner log file should exist", winnerLogFile.exists());
        
        String logContent = readLogFile(winnerLogFile);
        assertTrue("Log should contain 'wins'", logContent.contains("wins"));
        assertTrue("Log should contain 'exits'", logContent.contains("exits"));
        assertTrue("Log should contain final hand", logContent.contains("final hand"));
    }
}