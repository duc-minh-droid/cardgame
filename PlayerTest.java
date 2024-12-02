
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;

public class PlayerTest {

    @Test
    public void testImmediateWin() {
        // Set up the game
        CardGame game = new CardGame();
        Deck deck = new Deck(1);
        Player player = new Player(1, deck, game);

        // Initialize the player's hand with a winning hand
        player.addCard(new Card(1));
        player.addCard(new Card(1));
        player.addCard(new Card(1));
        player.addCard(new Card(1));

        // Check that the player immediately wins
        assertTrue("Player should have a winning hand at the start", player.checkWinningHand());

        // Simulate notifying other threads
        game.winningPlayer = new AtomicInteger(1);
        assertEquals("Player 1 should be declared as the winner", 1, game.winningPlayer.get());
    }

    @Test
    public void testMidGameWin() throws InterruptedException {
        // Set up the game
        CardGame game = new CardGame();
        Deck playerDeck = new Deck(1);
        Deck nextPlayerDeck = new Deck(2);

        for (int i = 1; i <= 4; i++) {
            playerDeck.addCard(new Card(0)); 
            nextPlayerDeck.addCard(new Card(0)); 
        }

        // Create players
        Player player1 = new Player(1, playerDeck, game);
        Player player2 = new Player(2, nextPlayerDeck, game);

        // Set up initial hands
        player1.addCard(new Card(1));
        player1.addCard(new Card(1));
        player1.addCard(new Card(2));
        player1.addCard(new Card(3));

        // Add a winning card to the player's deck
        playerDeck.addCard(new Card(1)); // This will give player1 a winning hand

        // Create threads
        Thread player1Thread = new Thread(player1);
        Thread player2Thread = new Thread(player2);

        // Start threads
        player1Thread.start();
        player2Thread.start();

        // Wait for threads to complete
        player1Thread.join();
        player2Thread.join();

        // Check if player 1 is the winner
        assertEquals("Player 1 should be declared as the winner", 1, game.winningPlayer.get()+1);
    }

    @Test
    public void testCardDrawAndDiscard() {
        // Setup decks
        Deck deck1 = new Deck(0);
        Deck deck2 = new Deck(1);

        // Populate left deck with some cards
        deck1.addCard(new Card(3));
        deck1.addCard(new Card(5));
        CardGame c = new CardGame();

        // Create player and assign leftDeck
        Player player = new Player(0, deck1, c);
        Player player2 = new Player(1, deck2, c);
        c.players.add(player);
        c.players.add(player2);
        player.addCard(new Card(1));
        player.addCard(new Card(2));
        player.addCard(new Card(3));
        player.addCard(new Card(4));

        // Simulate play turn
        player.playTurn();

        // Validate card movement
        assertEquals(1, deck1.size()); // One card removed from left deck
        assertEquals(1, deck2.size()); // One card added to right deck
        assertEquals(4, player.getHand().size()); // Player's hand should remain at 4 cards
    }

}