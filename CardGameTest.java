import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class CardGameTest {
    private static final String VALID_FILE_PATH = "valid_pack.txt";
    private static final String INVALID_FILE_PATH = "txtnon_existent_file.";
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
        assertEquals("Invalid file path." , packLocation);
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
